/*
 * Copyright 2019 Michel Davit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.davit.akka.http.scaladsl.marshallers.thrift.scrooge

import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.unmarshalling.Unmarshaller.UnsupportedContentTypeException
import akka.util.ByteString
import com.twitter.scrooge.ThriftStructCodec
import fr.davit.akka.http.scaladsl.marshallers.thrift.{ThriftBinarySupport, ThriftCompactSupport, ThriftJsonSupport}
import org.apache.thrift.protocol.{TBinaryProtocol, TProtocol, TProtocolFactory}
import org.apache.thrift.transport.TIOStreamTransport
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}
import thrift.TestMessage

class ScroogeSupportSpec extends FlatSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  val scrooge                                                   = TestMessage("test", 42)
  implicit val testMessageCodec: ThriftStructCodec[TestMessage] = TestMessage

  val binary  = new TBinaryProtocol.Factory()
  val compact = new TBinaryProtocol.Factory()
  val json    = new TBinaryProtocol.Factory()

  def serialize(factory: TProtocolFactory): ByteString = {
    val builder = ByteString.newBuilder
    scrooge.write(factory.getProtocol(new TIOStreamTransport(builder.asOutputStream)))
    builder.result()
  }

  val dataForContentType = ThriftBinarySupport.contentTypes.map(_ -> serialize(binary)).toMap ++
    ThriftCompactSupport.contentTypes.map(_ -> serialize(compact)).toMap ++
    ThriftJsonSupport.contentTypes.map(_    -> serialize(json)).toMap

  class ScroogeTestSuite(scroogeSupport: ScroogeAbstractSupport) {

    import scroogeSupport.{scroogeMarshaller, scroogeUnmarshaller}

    it should "marshall scrooge message with default content type" in {
      Get() ~> get(complete(scrooge)) ~> check {
        contentType shouldBe scroogeSupport.contentTypes.head
        responseAs[Array[Byte]] shouldBe dataForContentType(scroogeSupport.contentTypes.head)
      }
    }

    it should "marshall scrooge message with requested content type" in {
      scroogeSupport.contentTypes.foreach { ct =>
        Get().withHeaders(Accept(ct.mediaType)) ~> get(complete(scrooge)) ~> check {
          contentType shouldBe ct
          responseAs[Array[Byte]] shouldBe dataForContentType(ct)
        }
      }
    }

    it should "unmarshall to scrooge message" in {
      scroogeSupport.contentTypes.foreach { ct =>
        val entity = HttpEntity(ct, dataForContentType(ct))
        Unmarshal(entity).to[TestMessage].futureValue shouldBe scrooge
      }
    }

    it should "fail unmarshalling if the content type is not valid" in {
      val entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, "")
      Unmarshal(entity).to[TestMessage].failed.futureValue shouldBe an[UnsupportedContentTypeException]
    }
  }

  "ScroogeBinarySupport" should behave like new ScroogeTestSuite(ScroogeBinarySupport)
  "ScroogeCompactSupport" should behave like new ScroogeTestSuite(ScroogeCompactSupport)
  "ScroogeJsonSupport" should behave like new ScroogeTestSuite(ScroogeJsonSupport)
  "ScroogeSupport" should behave like new ScroogeTestSuite(ScroogeSupport)
}

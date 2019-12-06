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

package fr.davit.akka.http.scaladsl.marshallers.thrift

import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.model.{ContentType, ContentTypes, HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.UnacceptedResponseContentTypeRejection
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.unmarshalling.Unmarshaller.UnsupportedContentTypeException
import fr.davit.thrift.TestMessage
import org.apache.thrift.TSerializer
import org.apache.thrift.protocol.{TBinaryProtocol, TCompactProtocol, TJSONProtocol}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

class ThriftSupportSpec extends FlatSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  val thrift  = new TestMessage("test", 42)
  val binary  = new TSerializer(new TBinaryProtocol.Factory())
  val compact = new TSerializer(new TCompactProtocol.Factory())
  val json    = new TSerializer(new TJSONProtocol.Factory())

  val dataForContentType = ThriftBinarySupport.contentTypes.map(_ -> binary.serialize(thrift)).toMap ++
    ThriftCompactSupport.contentTypes.map(_ -> compact.serialize(thrift)).toMap ++
    ThriftJsonSupport.contentTypes.map(_    -> json.serialize(thrift)).toMap

  class ThriftTestSuite(thriftSupport: ThriftAbstractSupport) {

    import thriftSupport.{thriftMarshaller, thriftUnmarshaller}

    it should "marshall thrift message with default content type" in {
      Get() ~> get(complete(thrift)) ~> check {
        contentType shouldBe thriftSupport.contentTypes.head
        responseAs[Array[Byte]] shouldBe dataForContentType(thriftSupport.contentTypes.head)
      }
    }

    it should "marshall thrift message with requested content type" in {
      thriftSupport.contentTypes.foreach { ct =>
        Get().withHeaders(Accept(ct.mediaType)) ~> get(complete(thrift)) ~> check {
          contentType shouldBe ct
          responseAs[Array[Byte]] shouldBe dataForContentType(ct)
        }
      }
    }

    it should "unmarshall to thrift message with default content type" in {
      thriftSupport.contentTypes.foreach { ct =>
        val entity = HttpEntity(ct, dataForContentType(ct))
        Unmarshal(entity).to[TestMessage].futureValue shouldBe thrift
      }
    }

    it should "fail unmarshalling if the content type is not valid" in {
      val entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, "")
      Unmarshal(entity).to[TestMessage].failed.futureValue shouldBe an[UnsupportedContentTypeException]
    }
  }

  "ThriftBinarySupport" should behave like new ThriftTestSuite(ThriftBinarySupport)
  "ThriftCompactSupport" should behave like new ThriftTestSuite(ThriftCompactSupport)
  "ThriftJsonSupport" should behave like new ThriftTestSuite(ThriftJsonSupport)
  "ThriftSupport" should behave like new ThriftTestSuite(ThriftSupport)
}

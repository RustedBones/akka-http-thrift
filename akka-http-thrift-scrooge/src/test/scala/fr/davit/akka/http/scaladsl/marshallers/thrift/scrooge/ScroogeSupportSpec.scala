package fr.davit.akka.http.scaladsl.marshallers.thrift.scrooge

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.unmarshalling.Unmarshaller.UnsupportedContentTypeException
import akka.util.ByteString
import com.twitter.scrooge.ThriftStructCodec
import org.apache.thrift.transport.TIOStreamTransport
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}
import thrift.TestMessage

class ScroogeSupportSpec
  extends FlatSpec
    with Matchers
    with ScalaFutures
    with ScalatestRouteTest {

  class ScroogeTestSuite(scroogeSupport: ScroogeSupport) {

    import scroogeSupport.{scroogeMarshaller, scroogeUnmarshaller}

    val scrooge = TestMessage("test", 42)
    implicit val testMessageCodec: ThriftStructCodec[TestMessage] = TestMessage

    val builder = ByteString.newBuilder
    scrooge.write(scroogeSupport.protocolFactory.getProtocol(new TIOStreamTransport(builder.asOutputStream)))
    val data = builder.result()

    it should "marshall scrooge message" in  {
      Get() ~> get(complete(scrooge)) ~> check {
        contentType shouldBe scroogeSupport.contentType
        responseAs[Array[Byte]] shouldBe data
      }
    }

    it should "unmarshall to scrooge message" in {
      val entity = HttpEntity(scroogeSupport.contentType, data)
      Unmarshal(entity).to[TestMessage].futureValue shouldBe scrooge
    }

    it should "fail unmarshalling if the content type is not valid" in {
      val entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, data)
      Unmarshal(entity).to[TestMessage].failed.futureValue shouldBe an[UnsupportedContentTypeException]
    }
  }

  "ScroogeBinarySupport" should behave like new ScroogeTestSuite(ScroogeBinarySupport)
  "ScroogeCompactSupport" should behave like new ScroogeTestSuite(ScroogeCompactSupport)
  "ScroogeJsonSupport" should behave like new ScroogeTestSuite(ScroogeJsonSupport)

}

package fr.davit.akka.http.scaladsl.marshallers.thrift.scrooge

import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.UnacceptedResponseContentTypeRejection
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.unmarshalling.Unmarshaller.UnsupportedContentTypeException
import akka.util.ByteString
import com.twitter.scrooge.ThriftStructCodec
import org.apache.thrift.transport.TIOStreamTransport
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}
import thrift.TestMessage

class ScroogeSupportSpec extends FlatSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  val scrooge                                                   = TestMessage("test", 42)
  implicit val testMessageCodec: ThriftStructCodec[TestMessage] = TestMessage

  class ScroogeTestSuite(scroogeSupport: ScroogeAbstractSupport) {

    import scroogeSupport.{scroogeMarshaller, scroogeUnmarshaller}

    val builder = ByteString.newBuilder
    scrooge.write(scroogeSupport.protocolFactory.getProtocol(new TIOStreamTransport(builder.asOutputStream)))
    val data = builder.result()

    it should "marshall scrooge message" in {
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

  "ScroogeSupport" should "marshall thrift message with requested type" in {
    import ScroogeSupport._

    scroogeSupports.foreach { support =>
      val builder = ByteString.newBuilder
      scrooge.write(support.protocolFactory.getProtocol(new TIOStreamTransport(builder.asOutputStream)))
      val data = builder.result()

      Get().withHeaders(Accept(support.contentType.mediaType)) ~> get(complete(scrooge)) ~> check {
        contentType shouldBe support.contentType
        responseAs[Array[Byte]] shouldBe data
        responseAs[TestMessage] shouldBe scrooge
      }
    }
  }

  it should "marshall thrift message to json by default" in {
    import ScroogeSupport._

    val builder = ByteString.newBuilder
    scrooge.write(ScroogeJsonSupport.protocolFactory.getProtocol(new TIOStreamTransport(builder.asOutputStream)))
    val data = builder.result()

    Get() ~> get(complete(scrooge)) ~> check {
      contentType shouldBe ScroogeJsonSupport.contentType
      responseAs[Array[Byte]] shouldBe data
    }
  }

  it should "fail when Accept doesnt' match supported type" in {
    import ScroogeSupport._

    Get().withHeaders(Accept(MediaTypes.`text/html`)) ~> get(complete(scrooge)) ~> check {
      rejection shouldBe a[UnacceptedResponseContentTypeRejection]
    }
  }
}

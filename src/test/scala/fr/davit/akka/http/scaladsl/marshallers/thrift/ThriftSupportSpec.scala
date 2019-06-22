package fr.davit.akka.http.scaladsl.marshallers.thrift

import akka.http.javadsl.server.UnacceptedResponseContentTypeRejection
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.unmarshalling.Unmarshaller.UnsupportedContentTypeException
import fr.davit.thrift.TestMessage
import org.apache.thrift.TSerializer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

class ThriftSupportSpec extends FlatSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  val thrift     = new TestMessage("test", 42)

  class ThriftTestSuite(thriftSupport: ThriftAbstractSupport) {

    import thriftSupport.{thriftMarshaller, thriftUnmarshaller}

    val serializer = new TSerializer(thriftSupport.protocolFactory)
    val data       = serializer.serialize(thrift)

    it should "marshall thrift message" in {
      Get() ~> get(complete(thrift)) ~> check {
        contentType shouldBe thriftSupport.contentType
        responseAs[Array[Byte]] shouldBe data
      }
    }

    it should "unmarshall to thrift message" in {
      val entity = HttpEntity(thriftSupport.contentType, data)
      Unmarshal(entity).to[TestMessage].futureValue shouldBe thrift
    }

    it should "fail unmarshalling if the content type is not valid" in {
      val entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, data)
      Unmarshal(entity).to[TestMessage].failed.futureValue shouldBe an[UnsupportedContentTypeException]
    }
  }

  "ThriftBinarySupport" should behave like new ThriftTestSuite(ThriftBinarySupport)
  "ThriftCompactSupport" should behave like new ThriftTestSuite(ThriftCompactSupport)
  "ThriftJsonSupport" should behave like new ThriftTestSuite(ThriftJsonSupport)

  "ThriftSupport" should "marshall thrift message with requested type" in {
    import ThriftSupport._

    thriftSupports.foreach { support =>
      val serializer = new TSerializer(support.protocolFactory)
      val data       = serializer.serialize(thrift)

      Get().withHeaders(Accept(support.contentType.mediaType)) ~> get(complete(thrift)) ~> check {
        contentType shouldBe support.contentType
        responseAs[Array[Byte]] shouldBe data
        responseAs[TestMessage] shouldBe thrift
      }
    }
  }

  it should "marshall thrift message to json by default" in {
    import ThriftSupport._

    val serializer = new TSerializer(ThriftJsonSupport.protocolFactory)
    val data       = serializer.serialize(thrift)

    Get() ~> get(complete(thrift)) ~> check {
      contentType shouldBe ThriftJsonSupport.contentType
      responseAs[Array[Byte]] shouldBe data
    }
  }

  it should "fail when Accept doesnt' match supported type" in {
    import ThriftSupport._

    Get().withHeaders(Accept(MediaTypes.`text/html`)) ~> get(complete(thrift)) ~> check {
      rejection shouldBe a[UnacceptedResponseContentTypeRejection]
    }
  }
}

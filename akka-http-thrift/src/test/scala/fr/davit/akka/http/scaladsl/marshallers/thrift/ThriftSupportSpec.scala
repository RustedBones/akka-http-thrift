package fr.davit.akka.http.scaladsl.marshallers.thrift

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.unmarshalling.Unmarshaller.UnsupportedContentTypeException
import fr.davit.thrift.TestMessage
import org.apache.thrift.TSerializer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

class ThriftSupportSpec
  extends FlatSpec
    with Matchers
    with ScalaFutures
    with ScalatestRouteTest {

  class ThriftTestSuite(thriftSupport: ThriftSupport) {

    import thriftSupport.{thriftMarshaller, thriftUnmarshaller}

    val serializer = new TSerializer(thriftSupport.protocolFactory)
    val thrift = new TestMessage("test", 42)
    val data = serializer.serialize(thrift)

    it should "marshall thrift message" in  {
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
}

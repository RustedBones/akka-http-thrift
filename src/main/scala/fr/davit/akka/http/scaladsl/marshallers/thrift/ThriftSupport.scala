package fr.davit.akka.http.scaladsl.marshallers.thrift

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import org.apache.thrift.TBase
import org.apache.thrift.protocol.{TBinaryProtocol, TCompactProtocol, TJSONProtocol, TProtocolFactory}
import org.apache.thrift.transport.{TByteBuffer, TIOStreamTransport}

import scala.reflect.ClassTag

trait ThriftAbstractSupport {

  def protocolFactory: TProtocolFactory

  def contentType: ContentType

  //--------------------------------------------------------------------------------------------------------------------
  // Unmarshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def thriftUnmarshaller[T <: TBase[_, _] : ClassTag]: FromEntityUnmarshaller[T] = {
    Unmarshaller.byteStringUnmarshaller.forContentTypes(contentType).map { data =>
      // this is not so nice but as long as thrift as default constructor we should be fine
      val message = implicitly[ClassTag[T]].runtimeClass.newInstance().asInstanceOf[T]
      message.read(protocolFactory.getProtocol(new TByteBuffer(data.asByteBuffer)))
      message
    }
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Marshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def thriftMarshaller[T <: TBase[_, _]]: ToEntityMarshaller[T] = {
    Marshaller.ByteStringMarshaller.wrap[T, MessageEntity](contentType.mediaType) { thrift =>
      val builder = ByteString.newBuilder
      thrift.write(protocolFactory.getProtocol(new TIOStreamTransport(builder.asOutputStream)))
      builder.result()
    }
  }
}


//----------------------------------------------------------------------------------------------------------------------
// Binary
//----------------------------------------------------------------------------------------------------------------------
trait ThriftBinarySupport extends ThriftAbstractSupport {
  override def protocolFactory: TProtocolFactory = new TBinaryProtocol.Factory()

  override def contentType: ContentType = MediaType.applicationBinary("vnd.apache.thrift.binary", MediaType.NotCompressible)
}

object ThriftBinarySupport extends ThriftBinarySupport

//----------------------------------------------------------------------------------------------------------------------
// Compact
//----------------------------------------------------------------------------------------------------------------------
trait ThriftCompactSupport extends ThriftAbstractSupport {
  override def protocolFactory: TProtocolFactory = new TCompactProtocol.Factory()

  override def contentType: ContentType = MediaType.applicationBinary("vnd.apache.thrift.compact", MediaType.NotCompressible)
}

object ThriftCompactSupport extends ThriftCompactSupport

//----------------------------------------------------------------------------------------------------------------------
// JSON
//----------------------------------------------------------------------------------------------------------------------
trait ThriftJsonSupport extends ThriftAbstractSupport {
  override def protocolFactory: TProtocolFactory = new TJSONProtocol.Factory()

  override def contentType: ContentType = MediaType.applicationBinary("vnd.apache.thrift.json", MediaType.NotCompressible)
}

object ThriftJsonSupport extends ThriftJsonSupport

//----------------------------------------------------------------------------------------------------------------------
// Generic
//----------------------------------------------------------------------------------------------------------------------
trait ThriftSupport {

  private[thrift] val thriftSupports = Seq(ThriftJsonSupport, ThriftBinarySupport, ThriftCompactSupport)

  //--------------------------------------------------------------------------------------------------------------------
  // Unmarshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def thriftUnmarshaller[T <: TBase[_, _] : ClassTag]: FromEntityUnmarshaller[T] = {
    Unmarshaller.firstOf(thriftSupports.map(_.thriftUnmarshaller[T]): _*)
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Marshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def scalaPbMarshaller[T <: TBase[_, _] : ClassTag]: ToEntityMarshaller[T] = {
    Marshaller.oneOf(thriftSupports.map(_.thriftMarshaller[T]): _*)
  }
}

object ThriftSupport extends ThriftSupport
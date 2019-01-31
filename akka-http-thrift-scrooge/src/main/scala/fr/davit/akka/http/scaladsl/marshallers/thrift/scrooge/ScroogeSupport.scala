package fr.davit.akka.http.scaladsl.marshallers.thrift.scrooge

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import com.twitter.scrooge.{ThriftStruct, ThriftStructCodec}
import fr.davit.akka.http.scaladsl.marshallers.thrift.{ThriftBinarySupport, ThriftCompactSupport, ThriftJsonSupport, ThriftSupport}
import org.apache.thrift.transport.{TByteBuffer, TIOStreamTransport}

trait ScroogeSupport extends ThriftSupport {

  //--------------------------------------------------------------------------------------------------------------------
  // Unmarshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def scroogeUnmarshaller[T <: ThriftStruct](implicit codec: ThriftStructCodec[T]): FromEntityUnmarshaller[T] = {
    Unmarshaller.byteStringUnmarshaller.forContentTypes(contentType).map { data =>
      codec.decode(protocolFactory.getProtocol(new TByteBuffer(data.asByteBuffer)))
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  // Marshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def scroogeMarshaller[T <: ThriftStruct](implicit codec: ThriftStructCodec[T]): ToEntityMarshaller[T] = {
    Marshaller.ByteStringMarshaller.wrap(contentType.mediaType) { thrift =>
      val builder = ByteString.newBuilder
      codec.encode(thrift, protocolFactory.getProtocol(new TIOStreamTransport(builder.asOutputStream)))
      builder.result()
    }
  }

}

//----------------------------------------------------------------------------------------------------------------------
// Binary
//----------------------------------------------------------------------------------------------------------------------
trait ScroogeBinarySupport extends ThriftBinarySupport with ScroogeSupport

object ScroogeBinarySupport extends ScroogeBinarySupport

//----------------------------------------------------------------------------------------------------------------------
// Compact
//----------------------------------------------------------------------------------------------------------------------
trait ScroogeCompactSupport extends ThriftCompactSupport with ScroogeSupport

object ScroogeCompactSupport extends ScroogeCompactSupport

//----------------------------------------------------------------------------------------------------------------------
// JSON
//----------------------------------------------------------------------------------------------------------------------
trait ScroogeJsonSupport extends ThriftJsonSupport with ScroogeSupport

object ScroogeJsonSupport extends ScroogeJsonSupport
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

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{ContentType, ContentTypeRange, MessageEntity}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import com.twitter.scrooge.{ThriftStruct, ThriftStructCodec}
import fr.davit.akka.http.scaladsl.marshallers.thrift._
import org.apache.thrift.protocol.TProtocolFactory
import org.apache.thrift.transport.{TByteBuffer, TIOStreamTransport}

trait ScroogeAbstractSupport extends ThriftAbstractSupport {


  protected def serialize[T <: ThriftStruct](thrift: T)(implicit codec: ThriftStructCodec[T]): ByteString = {
    val builder = ByteString.newBuilder
    codec.encode(thrift, protocolFactory.getProtocol(new TIOStreamTransport(builder.asOutputStream)))
    builder.result()
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Unmarshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def scroogeUnmarshaller[T <: ThriftStruct](implicit codec: ThriftStructCodec[T]): FromEntityUnmarshaller[T] = {
    Unmarshaller.byteStringUnmarshaller.forContentTypes(contentTypes.map(ContentTypeRange.apply): _*).map { data =>
      codec.decode(protocolFactory.getProtocol(new TByteBuffer(data.asByteBuffer)))
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  // Marshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def scroogeMarshaller[T <: ThriftStruct](implicit codec: ThriftStructCodec[T]): ToEntityMarshaller[T] = {
    Marshaller.oneOf(contentTypes.map(ct => Marshaller.ByteStringMarshaller.wrap[T, MessageEntity](ct.mediaType)(serialize)): _*)
  }
}

//----------------------------------------------------------------------------------------------------------------------
// Binary
//----------------------------------------------------------------------------------------------------------------------
trait ScroogeBinarySupport extends ThriftBinarySupport with ScroogeAbstractSupport

object ScroogeBinarySupport extends ScroogeBinarySupport

//----------------------------------------------------------------------------------------------------------------------
// Compact
//----------------------------------------------------------------------------------------------------------------------
trait ScroogeCompactSupport extends ThriftCompactSupport with ScroogeAbstractSupport

object ScroogeCompactSupport extends ScroogeCompactSupport

//----------------------------------------------------------------------------------------------------------------------
// JSON
//----------------------------------------------------------------------------------------------------------------------
trait ScroogeJsonSupport extends ThriftJsonSupport with ScroogeAbstractSupport

object ScroogeJsonSupport extends ScroogeJsonSupport

//----------------------------------------------------------------------------------------------------------------------
// Generic
//----------------------------------------------------------------------------------------------------------------------
trait ScroogeSupport extends ScroogeAbstractSupport {

  override protected def protocolFactory: TProtocolFactory = throw new Exception("No protocol factory defined for ScroogeSupport")

  private val scroogeSupports = Seq(ScroogeJsonSupport, ScroogeBinarySupport, ScroogeCompactSupport)

  override val contentTypes: Seq[ContentType] = scroogeSupports.flatMap(_.contentTypes)

  //--------------------------------------------------------------------------------------------------------------------
  // Unmarshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit override def scroogeUnmarshaller[T <: ThriftStruct](implicit codec: ThriftStructCodec[T]): FromEntityUnmarshaller[T] = {
    Unmarshaller.firstOf(scroogeSupports.map(_.scroogeUnmarshaller[T]): _*)
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Marshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit override def scroogeMarshaller[T <: ThriftStruct](implicit codec: ThriftStructCodec[T]): ToEntityMarshaller[T] = {
    Marshaller.oneOf(scroogeSupports.map(_.scroogeMarshaller[T]): _*)
  }

}

object ScroogeSupport extends ScroogeSupport

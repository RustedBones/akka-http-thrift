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

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import org.apache.thrift.TBase
import org.apache.thrift.protocol.{TBinaryProtocol, TCompactProtocol, TJSONProtocol, TProtocolFactory}
import org.apache.thrift.transport.{TByteBuffer, TIOStreamTransport}

import scala.reflect.ClassTag

trait ThriftAbstractSupport {

  protected def protocolFactory: TProtocolFactory

  private def serialize[T <: TBase[_, _]](thrift: T): ByteString = {
    val builder = ByteString.newBuilder
    thrift.write(protocolFactory.getProtocol(new TIOStreamTransport(builder.asOutputStream)))
    builder.result()
  }

  def contentTypes: Seq[ContentType]

  //--------------------------------------------------------------------------------------------------------------------
  // Unmarshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def thriftUnmarshaller[T <: TBase[_, _]: ClassTag]: FromEntityUnmarshaller[T] = {
    Unmarshaller.byteStringUnmarshaller.forContentTypes(contentTypes.map(ContentTypeRange.apply): _*).map { data =>
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
    Marshaller.oneOf(contentTypes.map(ct => Marshaller.ByteStringMarshaller.wrap(ct.mediaType)(serialize)): _*)
  }
}

//----------------------------------------------------------------------------------------------------------------------
// Binary
//----------------------------------------------------------------------------------------------------------------------
trait ThriftBinarySupport extends ThriftAbstractSupport {
  override protected val protocolFactory: TProtocolFactory = new TBinaryProtocol.Factory()

  override val contentTypes: Seq[ContentType] = List(
    MediaType.applicationBinary("vnd.apache.thrift.binary", MediaType.NotCompressible)
  )
}

object ThriftBinarySupport extends ThriftBinarySupport

//----------------------------------------------------------------------------------------------------------------------
// Compact
//----------------------------------------------------------------------------------------------------------------------
trait ThriftCompactSupport extends ThriftAbstractSupport {
  override protected val protocolFactory: TProtocolFactory = new TCompactProtocol.Factory()

  override val contentTypes: Seq[ContentType] = List(
    MediaType.applicationBinary("vnd.apache.thrift.compact", MediaType.NotCompressible)
  )
}

object ThriftCompactSupport extends ThriftCompactSupport

//----------------------------------------------------------------------------------------------------------------------
// JSON
//----------------------------------------------------------------------------------------------------------------------
trait ThriftJsonSupport extends ThriftAbstractSupport {
  override protected val protocolFactory: TProtocolFactory = new TJSONProtocol.Factory()

  override val contentTypes: Seq[ContentType] = List(
    ContentTypes.`application/json`,
    MediaType.applicationWithFixedCharset("vnd.apache.thrift.json", HttpCharsets.`UTF-8`)
  )
}

object ThriftJsonSupport extends ThriftJsonSupport

//----------------------------------------------------------------------------------------------------------------------
// Generic
//----------------------------------------------------------------------------------------------------------------------
trait ThriftSupport extends ThriftAbstractSupport {

  override protected def protocolFactory: TProtocolFactory =
    throw new Exception("No protocol factory defined for ThriftSupport")

  private val thriftSupports = Seq(ThriftJsonSupport, ThriftBinarySupport, ThriftCompactSupport)

  override val contentTypes: Seq[ContentType] = thriftSupports.flatMap(_.contentTypes)

  //--------------------------------------------------------------------------------------------------------------------
  // Unmarshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit override def thriftUnmarshaller[T <: TBase[_, _]: ClassTag]: FromEntityUnmarshaller[T] = {
    Unmarshaller.firstOf(thriftSupports.map(_.thriftUnmarshaller[T]): _*)
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Marshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit override def thriftMarshaller[T <: TBase[_, _]]: ToEntityMarshaller[T] = {
    Marshaller.oneOf(thriftSupports.map(_.thriftMarshaller[T]): _*)
  }
}

object ThriftSupport extends ThriftSupport

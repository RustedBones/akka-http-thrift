# akka-http-thrift

[![Continuous Integration](https://github.com/RustedBones/akka-http-thrift/actions/workflows/ci.yml/badge.svg)](https://github.com/RustedBones/akka-http-thrift/actions/workflows/ci.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/fr.davit/akka-http-thrift_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/fr.davit/akka-http-thrift_2.12)
[![Software License](https://img.shields.io/badge/license-Apache%202-brightgreen.svg?style=flat)](LICENSE)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

akka-http thrift and json marshalling/unmarshalling for Thrift structs and Scrooge generated classes

After the akka [licencing change](https://www.lightbend.com/blog/why-we-are-changing-the-license-for-akka),
no further development is expected on `akka-http-thrift`.
If you're migrating to pekko-http, see [pekko-http-thrift](https://github.com/RustedBones/pekko-http-thrift).

## Versions

| Version | Release date | Akka Http version | Thrift version | Scrooge version | Scala versions                 |
|---------|--------------|-------------------|----------------|---------------- |--------------------------------|
| `0.2.5` | 2022-06-01   | `10.2.9`          | `0.16.0`       | `22.4.0`        | `2.13.8`, `2.12.15`            |
| `0.2.4` | 2021-09-21   | `10.2.6`          | `0.15.0`       | `21.8.0`        | `2.13.6`, `2.12.15`            |
| `0.2.3` | 2021-08-11   | `10.2.6`          | `0.14.2`       | `21.6.0`        | `2.13.6`, `2.12.14`            |
| `0.2.2` | 2021-03-06   | `10.1.14`         | `0.14.0`       | `19.12.0`       | `2.13.5`, `2.12.13`            |
| `0.2.1` | 2019-12-06   | `10.1.11`         | `0.13.0`       | `19.11.0`       | `2.13.1`, `2.12.10`, `2.11.12` |
| `0.2.0` | 2019-07-13   | `10.1.8`          | `0.11.0`       | `19.1.0`        | `2.12.8`, `2.11.12`            |
| `0.1.0` | 2019-01-31   | `10.1.7`          | `0.11.0`       | `19.1.0`        | `2.12.8`, `2.11.12`,           |

The complete list can be found in the [CHANGELOG](CHANGELOG.md) file.

## Getting akka-http-thrift

Libraries are published to Maven Central. Add to your `build.sbt`:

```scala
libraryDependencies += "fr.davit" %% "akka-http-thrift"         % <version> // thrift support
libraryDependencies += "fr.davit" %% "akka-http-thrift-scrooge" % <version> // srooge support
```

**Important**: Since akka-http 10.1.0, akka-stream transitive dependency is marked as provided. You should now explicitly
include it in your build.

> [...] we changed the policy not to depend on akka-stream explicitly anymore but mark it as a provided dependency in our build. 
That means that you will always have to add a manual dependency to akka-stream. Please make sure you have chosen and 
added a dependency to akka-stream when updating to the new version

```scala
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % <version> // Only Akka 2.5 supported
```

For more details, see the akka-http 10.1.x [release notes](https://doc.akka.io/docs/akka-http/current/release-notes/10.1.x.html)

## Quick start

For the examples, we are using the following thrift domain model 

```thrift
struct Item {
  1: string name
  2: i64 id
}

struct Order {
  1: list<Item> items
}
```

Marshalling/Unmarshalling of the generated classes depends on the `Accept`/`Content-Type` header sent by the client:
- `Content-Type: application/json`: json
- `Content-Type: application/vnd.apache.thrift.json`: json
- `Content-Type: application/vnd.apache.thrift.binary`: binary
- `Content-Type: application/vnd.apache.thrift.compact`: compact

-No `Accept` header or matching several (eg `Accept: application/*`) will take the 1st matching type from the above list.

### Thrift

The implicit marshallers and unmarshallers for your generated thrift classes are defined in 
`ThriftSupport`. Specific (un)marshallers can be imported from `ThriftBinarySupport`, `ThriftCompactSupport` and `ThriftJsonSupport`. 
You simply need to have them in scope.

```scala
import akka.http.scaladsl.server.Directives
import fr.davit.akka.http.scaladsl.marshallers.thrift.ThriftSupport


class MyThriftService extends Directives with ThriftSupport {

  // format: OFF
  val route =
    get {
      pathSingleSlash {
        complete(Item("thing", 42))
      }
    } ~
    post {
      entity(as[Order]) { order =>
        val itemsCount = order.items.size
        val itemNames = order.items.map(_.name).mkString(", ")
        complete(s"Ordered $itemsCount items: $itemNames")
      }
    }
  // format: ON
}
```

### Scrooge

The implicit marshallers and unmarshallers for your generated thrift classes are defined in 
`ScroogeSupport`. Specific (un)marshallers can be imported from `ScroogeBinarySupport`, `ScroogeCompactSupport` and `ScroogeJsonSupport`. 
You need to have them in scope as well as the `ThriftStructCodec` for the classes (The companion object generated by scrooge
is the codec).

```scala
import akka.http.scaladsl.server.Directives
import fr.davit.akka.http.scaladsl.marshallers.thrift.scrooge.ScroogeSupport
import com.twitter.scrooge.ThriftStructCodec

class MyScroogeService extends Directives with ScroogeSupport {

  implicit val itemCodec: ThriftStructCodec[Item] = Item
  implicit val orderCodec: ThriftStructCodec[Order] = Order

  // format: OFF
  val route =
    get {
      pathSingleSlash {
        complete(Item("thing", 42))
      }
    } ~
    post {
      entity(as[Order]) { order =>
        val itemsCount = order.items.size
        val itemNames = order.items.map(_.name).mkString(", ")
        complete(s"Ordered $itemsCount items: $itemNames")
      }
    }
  // format: ON
}
```

## Limitation

Entity streaming (http chunked transfer) is at the moment not supported by the library.

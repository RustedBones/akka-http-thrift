import sbt._

object Dependencies {

  object Versions {
    val akka      = "2.5.23"
    val akkaHttp  = "10.1.8"
    val logback   = "1.2.3"
    val scalaTest = "3.0.8"
    val thrift    = "0.12.0"
    val scrooge   = "19.1.0"
  }

  val akkaHttp = "com.typesafe.akka" %% "akka-http"    % Versions.akkaHttp
  val thrift   = "org.apache.thrift" % "libthrift"     % Versions.thrift
  val scrooge  = "com.twitter"       %% "scrooge-core" % Versions.scrooge exclude ("com.twitter", "libthrift")
  // "com.twitter" %% "finagle-thrift" % "6.34.0" exclude("com.twitter", "libthrift")

  object Provided {
    val akkaStream = "com.typesafe.akka" %% "akka-stream"    % Versions.akka    % "provided"
    val logback    = "ch.qos.logback"    % "logback-classic" % Versions.logback % "provided"
  }

  object Test {
    val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp  % "test"
    val akkaTestkit     = "com.typesafe.akka" %% "akka-testkit"      % Versions.akka      % "test"
    val scalaTest       = "org.scalatest"     %% "scalatest"         % Versions.scalaTest % "test"
  }
}
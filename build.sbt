// General info
val username = "RustedBones"
val repo     = "akka-http-thrift"

lazy val commonSettings = Seq(
  organization := "fr.davit",
  version := "0.1.0",
  crossScalaVersions := Seq("2.11.12", "2.12.8"),
  scalaVersion := (ThisBuild / crossScalaVersions).value.last,
  Compile / compile / scalacOptions ++= Settings.scalacOptions(scalaVersion.value),
  homepage := Some(url(s"https://github.com/$username/$repo")),
  licenses += "APACHE" -> url(s"https://github.com/$username/$repo/blob/master/LICENSE"),
  scmInfo := Some(ScmInfo(url(s"https://github.com/$username/$repo"), s"git@github.com:$username/$repo.git")),
  developers := List(
    Developer(
      id = s"$username",
      name = "Michel Davit",
      email = "michel@davit.fr",
      url = url(s"https://github.com/$username"))
  ),
  publishMavenStyle := true,
  Test / publishArtifact := false,
  publishTo := Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging),
  credentials ++= (for {
    username <- sys.env.get("SONATYPE_USERNAME")
    password <- sys.env.get("SONATYPE_PASSWORD")
  } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq,
)

lazy val root = (project in file("."))
  .aggregate(`akka-http-thrift`, `akka-http-thrift-scrooge`)
  .settings(commonSettings: _*)
  .settings(
    publish / skip := true
  )

lazy val `akka-http-thrift` = (project in file("akka-http-thrift"))
  .settings(commonSettings: _*)
  .disablePlugins(ScroogeSBT)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.akkaHttp,
      Dependencies.thrift,
      Dependencies.Provided.akkaStream,
      Dependencies.Provided.logback,
      Dependencies.Test.akkaTestkit,
      Dependencies.Test.akkaHttpTestkit,
      Dependencies.Test.scalaTest
    )
  )

lazy val `akka-http-thrift-scrooge` = (project in file("akka-http-thrift-scrooge"))
  .dependsOn(`akka-http-thrift`)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.scrooge,
      Dependencies.Provided.akkaStream,
      Dependencies.Provided.logback,
      Dependencies.Test.akkaTestkit,
      Dependencies.Test.akkaHttpTestkit,
      Dependencies.Test.scalaTest
    )
  )

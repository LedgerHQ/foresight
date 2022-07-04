scalaVersion := "2.13.8"

val AkkaVersion      = "2.6.18"
val AkkaHttpVersion  = "10.2.9"
val AkkaSlickVersion = "3.0.4"

enablePlugins(JavaServerAppPackaging)

libraryDependencies ++= Seq(
  "com.typesafe.akka"  %% "akka-stream"               % AkkaVersion,
  "com.typesafe.akka"  %% "akka-http"                 % AkkaHttpVersion,
  "com.typesafe.akka"  %% "akka-http-spray-json"      % AkkaHttpVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % AkkaSlickVersion,
  "ch.qos.logback"      % "logback-classic"           % "1.0.9",
  "org.postgresql"      % "postgresql"                % "42.4.0"
)
mainClass in (Compile, run) := Some("foresight.indexer.Indexer")
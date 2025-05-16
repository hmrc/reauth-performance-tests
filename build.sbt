name := "reauth-performance-tests"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.16"

enablePlugins(GatlingPlugin)

val gatlingVersion: String = "3.6.1"

libraryDependencies ++= Seq(
  "uk.gov.hmrc"                   %% "performance-test-runner"   % "6.1.0"        % "test",
  "io.rest-assured"                % "rest-assured"              % "5.4.0"        % "test",
  "com.nimbusds"                   % "nimbus-jose-jwt"           % "9.31"         % "test",
  "com.softwaremill.sttp.client3" %% "core"                      % "3.10.1"
)

// Enabling sbtAutoBuildPlugin provides default `testOptions` from `sbt-settings` plugin.
// These testOptions are not compatible with `sbt gatling:test`. So we have to override testOptions here.

Test / testOptions := Seq.empty

//-feature: surfaces warning when advanced features are used without being enabled.
//-language:implicitConversions", "-language:postfixOps are recommended by Gatling
scalacOptions ++= Seq("-feature", "-language:implicitConversions", "-language:postfixOps")

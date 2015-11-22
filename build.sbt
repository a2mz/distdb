import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

val project = Project(
  id = "distkv",
  base = file("."),
  settings = Project.defaultSettings ++ SbtMultiJvm.multiJvmSettings ++ Seq(
    name := "akka-sample-multi-node-scala",
    version := "1.0",
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.4.0",
      "com.typesafe.akka" %% "akka-remote" % "2.4.0",
      "com.typesafe.akka" %% "akka-multi-node-testkit" % "2.4.0",
      "org.scalatest" %% "scalatest" % "2.2.1" % "test"),
    // make sure that MultiJvm test are compiled by the default test compilation
    compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),
    // disable parallel tests
    parallelExecution in Test := false,
    // make sure that MultiJvm tests are executed by the default test target,
    // and combine the results from ordinary test and multi-jvm tests
    executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
      case (testResults, multiNodeResults)  =>
        val overall =
          if (testResults.overall.id < multiNodeResults.overall.id)
            multiNodeResults.overall
          else
            testResults.overall
        Tests.Output(overall,
          testResults.events ++ multiNodeResults.events,
          testResults.summaries ++ multiNodeResults.summaries)
    }
  )
) configs (MultiJvm)



name := "distkv"

version := "1.0"

scalaVersion := "2.11.7"

parallelExecution in Test := false

compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test)

executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
  case (testResults, multiNodeResults)  =>
    val overall =
      if (testResults.overall.id < multiNodeResults.overall.id)
        multiNodeResults.overall
      else
        testResults.overall
    Tests.Output(overall,
      testResults.events ++ multiNodeResults.events,
      testResults.summaries ++ multiNodeResults.summaries)
}

unmanagedSourceDirectories in MultiJvm <<=
  Seq(baseDirectory(_ / "src/test")).join

seq(SbtMultiJvm.multiJvmSettings :_*)

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.0"

libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.4.0"

libraryDependencies += "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.0"

libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % "1.0"

libraryDependencies += "com.typesafe" % "config" % "1.3.0"

libraryDependencies += "com.github.pathikrit" %% "better-files" % "2.13.0"
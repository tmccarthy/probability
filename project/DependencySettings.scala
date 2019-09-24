import sbt.Keys.libraryDependencies
import sbt._
import sbt.librarymanagement.{CrossVersion, ModuleID}

object DependencySettings {

  val commonDependencies: Seq[Def.Setting[Seq[ModuleID]]] = Seq(
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test,
    libraryDependencies += "com.github.ghik" %% "silencer-lib" % "1.4.1" % Provided,
    libraryDependencies += compilerPlugin("com.github.ghik" %% "silencer-plugin" % "1.4.1"),
  )

  val catsDependency = libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"

  val catsEffectDependency = libraryDependencies += "org.typelevel" %% "cats-effect" % "2.0.0"

  val circeDependency = libraryDependencies += "io.circe" %% "circe-core" % "0.12.1"

  val spireDependency = libraryDependencies += "org.typelevel" %% "spire" % "0.17.0-M1"

  val catsTestKitDependency = libraryDependencies ++= List(
    "org.typelevel" %% "cats-testkit" % "2.0.0" % Test,
    "org.typelevel" %% "cats-testkit-scalatest" % "1.0.0-M2" % Test,
    "org.scalatest" %% "scalatest" % "3.1.0-RC2" % Test,
  )

}

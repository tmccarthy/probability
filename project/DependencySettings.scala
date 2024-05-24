import sbt.Keys.libraryDependencies
import sbt._
import sbt.librarymanagement.ModuleID

object DependencySettings {

  val scalatestDependency: ModuleID = "org.scalatest" %% "scalatest" % "3.0.8"

  val commonDependencies: Seq[Def.Setting[Seq[ModuleID]]] = Seq(
    libraryDependencies += scalatestDependency % Test,
    libraryDependencies += "com.github.ghik" %% "silencer-lib" % "1.4.1" % Provided,
    libraryDependencies += compilerPlugin("com.github.ghik" %% "silencer-plugin" % "1.4.1"),
    libraryDependencies += "au.id.tmm.tmm-utils"   %% "tmm-utils-testing" % "0.3.3" % Test,
  )

  val catsDependency = libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"

  val catsEffectDependency = libraryDependencies += "org.typelevel" %% "cats-effect" % "2.0.0"

  val circeDependency = libraryDependencies += "io.circe" %% "circe-core" % "0.12.1"

  val spireDependency = libraryDependencies += "org.typelevel" %% "spire" % "0.18.0"

  val catsTestKitDependency = libraryDependencies ++= List(
    "org.typelevel" %% "cats-testkit" % "2.0.0-M4" % Test,
  )

  val scalacheckDependency = libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.2"

}

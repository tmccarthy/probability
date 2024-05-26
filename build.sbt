name := "probability"

ThisBuild / tlBaseVersion := "0.4"

xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName := "au.id.tmm"
ThisBuild / organization := "au.id.tmm.probability"
ThisBuild / organizationName := "Timothy McCarthy"
ThisBuild / startYear := Some(2019)
ThisBuild / developers := List(
  tlGitHubDev("tmccarthy", "Timothy McCarthy"),
)

val Scala213 = "2.13.13"
val Scala3   = "3.2.1"
ThisBuild / scalaVersion := Scala3
ThisBuild / crossScalaVersions := Seq(
  //  Scala3,
  Scala213,
)

ThisBuild / githubWorkflowJavaVersions := List(
  JavaSpec.temurin("17"),
)

ThisBuild / tlCiHeaderCheck := false
ThisBuild / tlCiScalafmtCheck := true
ThisBuild / tlCiMimaBinaryIssueCheck := false
ThisBuild / tlCiDependencyGraphJob := false
ThisBuild / tlFatalWarnings := true

addCommandAlias("check", ";githubWorkflowCheck;scalafmtSbtCheck;+scalafmtCheckAll;+test")
addCommandAlias("fix", ";githubWorkflowGenerate;scalafmtSbt;+scalafmtAll")

val scalatestVersion   = "3.2.18"
val tmmUtilsVersion    = "0.10.0"
val catsVersion        = "2.10.0"
val spireVersion       = "0.18.0"
val catsTestkitVersion = "2.1.5"
val scalacheckVersion  = "1.18.0"
val circeVersion       = "0.14.3"

lazy val root = tlCrossRootProject
  .settings(console := (core / Compile / console).value)
  .settings(xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName := "au.id.tmm")
  .aggregate(
    core,
    coreCirce,
    coreScalacheck,
    coreCats,
    coreApacheMath,
    coreTesting,
    exhaustiveDistribution,
    exhaustiveDistributionCirce,
    exhaustiveDistributionScalacheck,
    exhaustiveDistributionCats,
  )

lazy val core = project
  .in(file("core/core"))
  .settings(name := "probability-core")
  .settings(xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName := "au.id.tmm")
  .settings(
  )
  .settings(
    libraryDependencies += "au.id.tmm.tmm-utils" %% "tmm-utils-testing-core" % tmmUtilsVersion  % Test,
    libraryDependencies += "org.scalatest"       %% "scalatest"              % scalatestVersion % Test,
  )

lazy val coreCirce = project
  .in(file("core/circe"))
  .settings(name := "probability-core-circe")
  .settings(xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName := "au.id.tmm")
  .settings(
    libraryDependencies += "io.circe" %% "circe-core" % circeVersion,
  )
  .settings(
    libraryDependencies += "au.id.tmm.tmm-utils" %% "tmm-utils-testing-core" % tmmUtilsVersion  % Test,
    libraryDependencies += "org.scalatest"       %% "scalatest"              % scalatestVersion % Test,
  )
  .dependsOn(core)

lazy val coreScalacheck = project
  .in(file("core/scalacheck"))
  .settings(name := "probability-core-scalacheck")
  .settings(xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName := "au.id.tmm")
  .settings(
    libraryDependencies += "org.scalacheck" %% "scalacheck" % scalacheckVersion,
  )
  .settings(
    libraryDependencies += "au.id.tmm.tmm-utils" %% "tmm-utils-testing-core" % tmmUtilsVersion  % Test,
    libraryDependencies += "org.scalatest"       %% "scalatest"              % scalatestVersion % Test,
  )
  .dependsOn(core)

lazy val coreCats = project
  .in(file("core/cats"))
  .settings(name := "probability-core-cats")
  .settings(xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName := "au.id.tmm")
  .settings(
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.10.0",
  )
  .settings(
    libraryDependencies += "au.id.tmm.tmm-utils" %% "tmm-utils-testing-core" % tmmUtilsVersion    % Test,
    libraryDependencies += "org.scalatest"       %% "scalatest"              % scalatestVersion   % Test,
    libraryDependencies += "org.typelevel"       %% "cats-testkit-scalatest" % catsTestkitVersion % Test,
  )
  .dependsOn(core, coreScalacheck % "test->compile")

lazy val coreApacheMath = project
  .in(file("core/apache-math"))
  .settings(name := "probability-core-apache-math")
  .settings(xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName := "au.id.tmm")
  .settings(
    libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1",
  )
  .settings(
    libraryDependencies += "au.id.tmm.tmm-utils" %% "tmm-utils-testing-core" % tmmUtilsVersion  % Test,
    libraryDependencies += "org.scalatest"       %% "scalatest"              % scalatestVersion % Test,
  )
  .dependsOn(core)

lazy val coreTesting = project
  .in(file("core/testing"))
  .settings(name := "probability-core-testing")
  .settings(xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName := "au.id.tmm")
  .settings(
    libraryDependencies += "org.scalatest" %% "scalatest" % scalatestVersion,
  )
  .settings(
    libraryDependencies += "au.id.tmm.tmm-utils" %% "tmm-utils-testing-core" % tmmUtilsVersion  % Test,
    libraryDependencies += "org.scalatest"       %% "scalatest"              % scalatestVersion % Test,
  )
  .dependsOn(core, coreApacheMath, core % "test->test")

lazy val exhaustiveDistribution = project
  .in(file("distribution-exhaustive/core"))
  .settings(name := "probability-distribution-exhaustive")
  .settings(xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName := "au.id.tmm")
  .settings(
    libraryDependencies += "org.typelevel" %% "spire" % spireVersion,
  )
  .settings(
    libraryDependencies += "au.id.tmm.tmm-utils" %% "tmm-utils-testing-core" % tmmUtilsVersion  % Test,
    libraryDependencies += "org.scalatest"       %% "scalatest"              % scalatestVersion % Test,
  )
  .dependsOn(core, core % "test->test")

lazy val exhaustiveDistributionCirce = project
  .in(file("distribution-exhaustive/circe"))
  .settings(name := "probability-distribution-exhaustive-circe")
  .settings(xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName := "au.id.tmm")
  .settings(
    libraryDependencies += "io.circe" %% "circe-core" % circeVersion,
  )
  .settings(
    libraryDependencies += "au.id.tmm.tmm-utils" %% "tmm-utils-testing-core" % tmmUtilsVersion  % Test,
    libraryDependencies += "org.scalatest"       %% "scalatest"              % scalatestVersion % Test,
  )
  .dependsOn(exhaustiveDistribution)

lazy val exhaustiveDistributionScalacheck = project
  .in(file("distribution-exhaustive/scalacheck"))
  .settings(name := "probability-distribution-exhaustive-scalacheck")
  .settings(xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName := "au.id.tmm")
  .settings(
    libraryDependencies += "org.scalacheck" %% "scalacheck" % scalacheckVersion,
  )
  .settings(
    libraryDependencies += "au.id.tmm.tmm-utils" %% "tmm-utils-testing-core" % tmmUtilsVersion  % Test,
    libraryDependencies += "org.scalatest"       %% "scalatest"              % scalatestVersion % Test,
    libraryDependencies += "org.scalatestplus"   %% "scalacheck-1-17"        % "3.2.18.0"       % Test,
  )
  .dependsOn(exhaustiveDistribution)

lazy val exhaustiveDistributionCats = project
  .in(file("distribution-exhaustive/cats"))
  .settings(name := "probability-distribution-exhaustive-cats")
  .settings(xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName := "au.id.tmm")
  .settings(
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.10.0",
  )
  .settings(
    libraryDependencies += "au.id.tmm.tmm-utils" %% "tmm-utils-testing-core"       % tmmUtilsVersion    % Test,
    libraryDependencies += "au.id.tmm.tmm-utils" %% "tmm-utils-testing-cats"       % tmmUtilsVersion    % Test,
    libraryDependencies += "au.id.tmm.tmm-utils" %% "tmm-utils-testing-scalacheck" % tmmUtilsVersion    % Test,
    libraryDependencies += "org.scalatest"       %% "scalatest"                    % scalatestVersion   % Test,
    libraryDependencies += "org.typelevel"       %% "cats-testkit-scalatest"       % catsTestkitVersion % Test,
  )
  .dependsOn(exhaustiveDistribution, coreCats, exhaustiveDistributionScalacheck % "test->compile")

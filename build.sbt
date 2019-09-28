import DependencySettings._

val settingsHelper = ProjectSettingsHelper("au.id.tmm","probability")(
  otherScalaVersions = List.empty,
)

settingsHelper.settingsForBuild

lazy val root = project
  .in(file("."))
  .settings(settingsHelper.settingsForRootProject)
  .settings(console := (console in Compile in core).value)
  .aggregate(
    core,
    distribution,
    distributionCats,
    distributionApacheMath,
    measure,
    measureCirce,
    measureCats,
  )

// TODO rename to shared
lazy val core = project
  .in(file("core"))
  .settings(settingsHelper.settingsForSubprojectCalled("core"))
  .settings(spireDependency)

// TODO core-cats
// TODO core-circe

lazy val distribution = project
  .in(file("distribution/core"))
  .settings(settingsHelper.settingsForSubprojectCalled("distribution"))
  .dependsOn(core)

lazy val distributionCats = project
  .in(file("distribution/cats"))
  .settings(settingsHelper.settingsForSubprojectCalled("distribution-cats"))
  .settings(
    catsDependency,
    catsTestKitDependency,
    libraryDependencies += "au.id.tmm.intime" %% "intime-scalacheck" % "1.0.2" % "test",
    libraryDependencies += "au.id.tmm.intime" %% "intime-cats"       % "1.0.2" % "test",
  )
  .dependsOn(distribution)

lazy val distributionApacheMath = project
  .in(file("distribution/apache-math"))
  .settings(settingsHelper.settingsForSubprojectCalled("distribution-apache-math"))
  .settings(
    libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1",
  )
  .dependsOn(distribution)

lazy val measure = project
  .in(file("measure/core"))
  .settings(settingsHelper.settingsForSubprojectCalled("measure"))
  .settings(spireDependency)
  .dependsOn(core, core % "test->test")

lazy val measureCirce = project
  .in(file("measure/circe"))
  .settings(settingsHelper.settingsForSubprojectCalled("measure-circe"))
  .settings(circeDependency)
  .dependsOn(measure)

lazy val measureCats = project
  .in(file("measure/cats"))
  .settings(settingsHelper.settingsForSubprojectCalled("measure-cats"))
  .settings(
    catsDependency,
    catsTestKitDependency,
    libraryDependencies += "au.id.tmm.intime" %% "intime-scalacheck" % "1.0.2" % "test",
    libraryDependencies += "au.id.tmm.intime" %% "intime-cats"       % "1.0.2" % "test",
  )
  .dependsOn(measure)

addCommandAlias("check", ";+test;scalafmtCheckAll")

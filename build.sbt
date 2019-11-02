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
    coreCirce,
    coreScalacheck,
    coreCats,
    coreApacheMath,
    measure,
    measureCirce,
    measureCats,
  )

lazy val core = project
  .in(file("core/core"))
  .settings(settingsHelper.settingsForSubprojectCalled("core"))

lazy val coreCirce = project
  .in(file("core/circe"))
  .settings(settingsHelper.settingsForSubprojectCalled("core-circe"))
  .settings(circeDependency)
  .dependsOn(core)

lazy val coreScalacheck = project
  .in(file("core/scalacheck"))
  .settings(settingsHelper.settingsForSubprojectCalled("core-scalacheck"))
  .settings(scalacheckDependency)
  .dependsOn(core)

lazy val coreCats = project
  .in(file("core/cats"))
  .settings(settingsHelper.settingsForSubprojectCalled("core-cats"))
  .settings(
    catsDependency,
    catsTestKitDependency,
    libraryDependencies += "au.id.tmm.intime" %% "intime-scalacheck" % "1.0.2" % "test",
    libraryDependencies += "au.id.tmm.intime" %% "intime-cats"       % "1.0.2" % "test",
  )
  .dependsOn(core, coreScalacheck % "test->compile")

lazy val coreApacheMath = project
  .in(file("core/apache-math"))
  .settings(settingsHelper.settingsForSubprojectCalled("core-apache-math"))
  .settings(
    libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1",
  )
  .dependsOn(core)

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

lazy val measureScalacheck = project
  .in(file("measure/scalacheck"))
  .settings(settingsHelper.settingsForSubprojectCalled("measure-scalacheck"))
  .settings(scalacheckDependency)
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
  .dependsOn(measure, coreCats, measureScalacheck % "test->compile")

addCommandAlias("check", ";+test;scalafmtCheckAll")
addCommandAlias("cover", ";clean;coverage;test;coverageAggregate")

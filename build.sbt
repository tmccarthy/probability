import DependencySettings._

val settingsHelper = ProjectSettingsHelper("au.id.tmm","probability")()

settingsHelper.settingsForBuild

lazy val root = project
  .in(file("."))
  .settings(settingsHelper.settingsForRootProject)
  .settings(console := (console in Compile in probabilityMeasureCore).value)
  .aggregate(
    probabilityMeasureCore,
    probabilityMeasureCirce,
    probabilityMeasureCats,
  )

lazy val probabilityMeasureCore = project
  .in(file("probability-measure/core"))
  .settings(settingsHelper.settingsForSubprojectCalled("measure-core"))
  .settings(spireDependency)

lazy val probabilityMeasureCirce = project
  .in(file("probability-measure/circe"))
  .settings(settingsHelper.settingsForSubprojectCalled("measure-circe"))
  .settings(circeDependency)
  .dependsOn(probabilityMeasureCore)

lazy val probabilityMeasureCats = project
  .in(file("probability-measure/cats"))
  .settings(settingsHelper.settingsForSubprojectCalled("measure-cats"))
  .settings(
    catsDependency,
    catsTestKitDependency,
    libraryDependencies += "au.id.tmm.intime" %% "intime-scalacheck" % "1.0.2" % "test",
    libraryDependencies += "au.id.tmm.intime" %% "intime-cats"       % "1.0.2" % "test",
  )
  .dependsOn(probabilityMeasureCore)

lazy val probabilityDistributionCore = project
  .in(file("probability-distribution/core"))
  .settings(settingsHelper.settingsForSubprojectCalled("distribution-core"))

addCommandAlias("check", ";+test;scalafmtCheckAll")

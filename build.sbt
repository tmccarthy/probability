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
    circe,
    cats,
    apacheMath,
  )

lazy val core = project
  .in(file("core"))
  .settings(settingsHelper.settingsForSubprojectCalled("core"))
  .settings(spireDependency)

lazy val apacheMath = project
  .in(file("apache-math"))
  .settings(settingsHelper.settingsForSubprojectCalled("apache-math"))
  .settings(
    libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1",
  )
  .dependsOn(core)

lazy val circe = project
  .in(file("circe"))
  .settings(settingsHelper.settingsForSubprojectCalled("circe"))
  .settings(circeDependency)
  .dependsOn(core)

lazy val cats = project
  .in(file("cats"))
  .settings(settingsHelper.settingsForSubprojectCalled("cats"))
  .settings(
    catsDependency,
    catsTestKitDependency,
    libraryDependencies += "au.id.tmm.intime" %% "intime-scalacheck" % "1.0.2" % "test",
    libraryDependencies += "au.id.tmm.intime" %% "intime-cats"       % "1.0.2" % "test",
  )
  .dependsOn(core)

addCommandAlias("check", ";+test;scalafmtCheckAll")

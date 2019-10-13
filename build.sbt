import DependencySettings._

val settingsHelper = ProjectSettingsHelper("au.id.tmm","probability")(
  otherScalaVersions = List.empty,
)

settingsHelper.settingsForBuild

lazy val root = project
  .in(file("."))
  .settings(settingsHelper.settingsForRootProject)
  .settings(console := (console in Compile in shared).value)
  .aggregate(
    shared,
    sharedCirce,
    sharedScalacheck,
    sharedCats,
    distribution,
    distributionCats,
    distributionApacheMath,
    measure,
    measureCirce,
    measureCats,
  )

lazy val shared = project
  .in(file("shared/core"))
  .settings(settingsHelper.settingsForSubprojectCalled("shared"))
  .settings(spireDependency)

lazy val sharedCirce = project
  .in(file("shared/circe"))
  .settings(settingsHelper.settingsForSubprojectCalled("shared-circe"))
  .settings(circeDependency)
  .dependsOn(shared)

lazy val sharedScalacheck = project
  .in(file("shared/scalacheck"))
  .settings(settingsHelper.settingsForSubprojectCalled("shared-scalacheck"))
  .settings(scalacheckDependency)
  .dependsOn(shared)

lazy val sharedCats = project
  .in(file("shared/cats"))
  .settings(settingsHelper.settingsForSubprojectCalled("shared-cats"))
  .settings(
    catsDependency,
    catsTestKitDependency,
    libraryDependencies += "au.id.tmm.intime" %% "intime-scalacheck" % "1.0.2" % "test",
    libraryDependencies += "au.id.tmm.intime" %% "intime-cats"       % "1.0.2" % "test",
  )
  .dependsOn(shared, sharedScalacheck % "test->compile")

lazy val distribution = project
  .in(file("distribution/core"))
  .settings(settingsHelper.settingsForSubprojectCalled("distribution"))
  .dependsOn(shared)

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
  .dependsOn(shared, shared % "test->test")

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
  .dependsOn(measure, sharedCats, measureScalacheck % "test->compile")

addCommandAlias("check", ";+test;scalafmtCheckAll")

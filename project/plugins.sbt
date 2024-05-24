val sbtTypelevelVersion = "0.6.7"

addSbtPlugin("org.typelevel" % "sbt-typelevel-ci"         % sbtTypelevelVersion)
addSbtPlugin("org.typelevel" % "sbt-typelevel-settings"   % sbtTypelevelVersion)
addSbtPlugin("org.typelevel" % "sbt-typelevel-versioning" % sbtTypelevelVersion)
addSbtPlugin("org.typelevel" % "sbt-typelevel-github"     % sbtTypelevelVersion)

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")

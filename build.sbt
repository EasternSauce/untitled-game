ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.18"

// Enable SemanticDB for Scalafix
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := "4.15.0" // a published SemanticDB version for Scala 2.13.18

lazy val root = (project in file("."))
  .settings(
    name := "untitled-game",

    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-Wunused"
    )
  )

// ------------------- Dependencies -------------------
val libGdxVersion = "1.14.0"

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies ++= Seq(
  "com.badlogicgames.gdx" % "gdx" % libGdxVersion,
  "com.badlogicgames.gdx" % "gdx-box2d" % libGdxVersion,
  "com.badlogicgames.gdx" % "gdx-backend-lwjgl3" % libGdxVersion,
  "com.badlogicgames.gdx" % "gdx-freetype" % libGdxVersion,

  "com.badlogicgames.gdx" % "gdx-platform" % libGdxVersion classifier "natives-desktop",
  "com.badlogicgames.gdx" % "gdx-box2d-platform" % libGdxVersion classifier "natives-desktop",
  "com.badlogicgames.gdx" % "gdx-freetype-platform" % libGdxVersion classifier "natives-desktop",

  "com.softwaremill.quicklens" %% "quicklens" % "1.9.7",
  "com.esotericsoftware" % "kryonet" % "2.22.0-RC1",
  "space.earlygrey" % "shapedrawer" % "2.6.0",
  "com.twitter" %% "chill" % "0.10.0",
  "org.typelevel" %% "cats-core" % "2.12.0"
)
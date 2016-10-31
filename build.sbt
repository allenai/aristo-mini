val sharedSettings = Seq(
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
  libraryDependencies := Seq(
    "com.datasift.dropwizard.scala" %% "dropwizard-scala-core" % "1.0.0-1",
    "org.scalactic" %% "scalactic" % "3.0.0",
    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  )
)

lazy val common = Project(
  id = "common",
  base = file("common")
).settings(
  sharedSettings ++ Seq(
    name := "common",
    version := "1.0",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
    )
  )
)

lazy val evalui = Project(
  id = "evalui",
  base = file("evalui")
).settings(
  sharedSettings ++ Seq(
    name := "evalui",
    version := "1.0",
    libraryDependencies ++= Seq(
      "com.squareup.okhttp3" % "okhttp" % "3.4.1",
      "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
    ),
    mainClass in Compile := Some("org.allenai.aristomini.evalui.EvalUIServer"),
    bashScriptExtraDefines += """addApp "server"""",
    bashScriptExtraDefines += """addApp "conf/eval-server.yaml""""
  )
).dependsOn(common).enablePlugins(JavaAppPackaging)

lazy val random = Project(
  id = "solver-random",
  base = file("solvers/random")
).settings(
   sharedSettings ++ Seq(
    name := "solver-random",
    version := "1.0",
    mainClass in Compile := Some("org.allenai.aristomini.solver.random.RandomSolverServer"),
    bashScriptExtraDefines += """addApp "server"""",
    bashScriptExtraDefines += """addApp "conf/solver-server.yaml""""
  )
).dependsOn(common).enablePlugins(JavaAppPackaging)

lazy val textsearch = Project(
  id = "solver-textsearch",
  base = file("solvers/textsearch")
).settings(
  sharedSettings ++ Seq(
    name := "solver-textsearch",
    version := "1.0",
    libraryDependencies ++= Seq(
      "org.elasticsearch" % "elasticsearch" % "2.3.3"
    ),
    mainClass in Compile := Some("org.allenai.aristomini.solver.textsearch.TextSearchSolverServer"),
    bashScriptExtraDefines += """addApp "server"""",
    bashScriptExtraDefines += """addApp "conf/solver-server.yaml""""
  )
).dependsOn(common).enablePlugins(JavaAppPackaging)

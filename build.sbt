import play.sbt.routes.RoutesKeys
import sbt.Def
import scoverage.ScoverageKeys
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "automated-export-system-frontend"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.3.8"
ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:implicitConversions",
  "-Wconf:src=target/.*:s,msg=Flag.*repeatedly:s"
).distinct
ThisBuild / scalafmtOnCompile := true

lazy val microservice = (project in file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(inConfig(Test)(testSettings): _*)
  .settings(ThisBuild / useSuperShell := false)
  .settings(
    name := appName,
    RoutesKeys.routesImport := Seq(
      "controllers.Assets.Asset",
      "uk.gov.hmrc.play.bootstrap.binders.RedirectUrl"
    ).distinct,
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "uk.gov.hmrc.hmrcfrontend.views.config._",
      "uk.gov.hmrc.automatedexportsystemfrontend.views.html.templates._",
      "uk.gov.hmrc.automatedexportsystemfrontend.views.ViewUtils._",
      "uk.gov.hmrc.automatedexportsystemfrontend.models.Mode",
      "uk.gov.hmrc.automatedexportsystemfrontend.controllers.routes._",
      "uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all._"
    ),
    PlayKeys.playDefaultPort := 5002,
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*handlers.*;.*components.*;" +
      ".*Routes.*;.*viewmodels.govuk.*;",
    ScoverageKeys.coverageMinimumStmtTotal := 78,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    libraryDependencies ++= AppDependencies(),
    retrieveManaged := true,
    pipelineStages := Seq(digest),
    Assets / pipelineStages := Seq(concat)
  )

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork := true,
  unmanagedSourceDirectories += baseDirectory.value / "test-utils"
)

lazy val it =
  (project in file("it"))
    .enablePlugins(PlayScala)
    .dependsOn(microservice % "test->test")

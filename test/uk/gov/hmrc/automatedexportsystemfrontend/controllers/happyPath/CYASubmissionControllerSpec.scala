/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.happyPath

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.auth.core.retrieve.{~, Credentials}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.happyPath.routes as happyRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.TestFixture.{testAuthorityId, testGroupId}
import uk.gov.hmrc.automatedexportsystemfrontend.models.{OfficeOfExit, PartOfConsolidationAnswer}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.happyPath.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.HappyPath.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.SummaryListViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.happyPath.CYASubmissionView
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.Future

class CYASubmissionControllerSpec extends SpecBase {

  "CYASubmissionController" - {

    "must return OK and the correct view for a GET" in {
      val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
      val enrolmentIdentifier = uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")
      val enrolments = Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      val userAnswers = emptyUserAnswers
        .set(EnterMrnPage, "MRN")
        .get
        .set(IsSplitExitPage, false)
        .get
        .set(EnterDucrPage, "DUCR")
        .get
        .set(PartOfConsolidationPage, PartOfConsolidationAnswer(true, Some("123")))
        .get
        .set(OfficeOfExitPage, OfficeOfExit.Belfast)
        .get
        .set(AnyDiscrepanciesPage, false)
        .get

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      val exportOperationList = SummaryListViewModel(
        Seq(EnterMrnSummary.row(userAnswers)(messages(application)), IsSplitExitSummary.row(userAnswers)(messages(application))).flatten
      )

      val consignmentList = SummaryListViewModel(
        Seq(EnterDucrSummary.row(userAnswers)(messages(application)), PartOfConsolidationSummary.row(userAnswers)(messages(application))).flatten
      )

      val customsOfficeExitList = SummaryListViewModel(Seq(OfficeOfExitSummary.row(userAnswers)(messages(application))).flatten)

      val extraRowsList = SummaryListViewModel(Seq(AnyDiscrepanciesSummary.row(userAnswers)(messages(application))).flatten)

      running(application) {
        val request = FakeRequest(GET, happyRoute.CYASubmissionController.onPageLoad().url)
          .withSession(SessionKeys.sessionId -> "some-session-id")
        val result = route(application, request).value

        val view = application.injector.instanceOf[CYASubmissionView]
        status(result) shouldBe OK
        val body = contentAsString(result)
        body should include("MRN")
        body should include("Is this a split exit?")
        body should include("DUCR")
        body should include("Yes - MUCR: 123")
        body should include("Belfast")
        body should include("Are there any discrepancies with this consignment?")
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {
      val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
      val enrolmentIdentifier = uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")
      val enrolments = Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, happyRoute.CYASubmissionController.onPageLoad().url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}

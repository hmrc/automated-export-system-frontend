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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.auth.core.retrieve.{~, Credentials}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes as amendRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.TestFixture.{testAuthorityId, testGroupId}
import uk.gov.hmrc.automatedexportsystemfrontend.models.{OfficeOfExit, PartOfConsolidationAnswer}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Amend.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.SummaryListViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.amend.AmendCYASubmissionView
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.Future

class AmendCYASubmissionControllerSpec extends SpecBase {

//  "CYASubmissionController" - {
//
//    "must return OK and the correct view for a GET" in {
//      val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
//      val enrolmentIdentifier = uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")
//      val enrolments = Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))
//
//      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
//        .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))
//
//      val userAnswers = emptyUserAnswers
//        .set(AmendEnterMrnPage("submissionId"), "MRN")
//        .get
//        .set(AmendIsSplitExitPage("submissionId"), false)
//        .get
//        .set(AmendEnterDucrPage("submissionId"), "DUCR")
//        .get
//        .set(AmendPartOfConsolidationPage("submissionId"), PartOfConsolidationAnswer(true, Some("123")))
//        .get
//        .set(AmendOfficeOfExitPage("submissionId"), OfficeOfExit.Belfast)
//        .get
//        .set(AmendAnyDiscrepanciesPage("submissionId"), false)
//        .get
//
//      val application = applicationBuilder(userAnswers = Some(userAnswers))
//        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
//        .build()
//
//      val exportOperationList = SummaryListViewModel(
//        Seq(
//          AmendEnterMrnSummary.row(userAnswers)("submissionId")(messages(application)),
//          AmendIsSplitExitSummary.row(userAnswers)("submissionId")(messages(application))
//        ).flatten
//      )
//
//      val consignmentList = SummaryListViewModel(
//        Seq(
//          AmendEnterDucrSummary.row(userAnswers)("submissionId")(messages(application)),
//          AmendPartOfConsolidationSummary.row(userAnswers)(messages(application))
//        ).flatten
//      )
//
//      val customsOfficeExitList = SummaryListViewModel(Seq(AmendOfficeOfExitSummary.row(userAnswers)("submissionId")(messages(application))).flatten)
//
//      val extraRowsList = SummaryListViewModel(Seq(AmendAnyDiscrepanciesSummary.row(userAnswers)("submissionId")(messages(application))).flatten)
//
//      running(application) {
//        val request = FakeRequest(GET, amendRoute.AmendCYASubmissionController.onPageLoad().url)
//          .withSession(SessionKeys.sessionId -> "some-session-id")
//        val result = route(application, request).value
//
//        val view = application.injector.instanceOf[AmendCYASubmissionView]
//        status(result) shouldBe OK
//        val body = contentAsString(result)
//        body should include("MRN")
//        body should include("Is this a split exit?")
//        body should include("DUCR")
//        body should include("Yes - MUCR: 123")
//        body should include("Belfast")
//        body should include("Are there any discrepancies with this consignment?")
//      }
//    }
//
//    "must redirect to Journey Recovery for a GET if no existing data is found" in {
//      val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
//      val enrolmentIdentifier = uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")
//      val enrolments = Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))
//
//      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
//        .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))
//
//      val application = applicationBuilder(userAnswers = None)
//        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
//        .build()
//
//      running(application) {
//        val request = FakeRequest(GET, amendRoute.AmendCYASubmissionController.onPageLoad().url)
//
//        val result = route(application, request).value
//
//        status(result) shouldBe SEE_OTHER
//        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
//      }
//    }
//  }
}

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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.create

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.auth.core.retrieve.{~, Credentials}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as happyRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.TestFixture.{testAuthorityId, testGroupId}
import uk.gov.hmrc.automatedexportsystemfrontend.models.LocationQualifier.AuthorisationNumber
import uk.gov.hmrc.automatedexportsystemfrontend.models.ModeOfTransportAtBorder.Sea
import uk.gov.hmrc.automatedexportsystemfrontend.models.{
  ContainerDetails,
  DocumentDetails,
  LocationDetails,
  LocationType,
  ModeOfTransportAtBorder,
  OfficeOfExit,
  PackingDetails,
  PartOfConsolidationAnswer,
  TransportAcrossBorderDetails,
  WhatHasChangedDetails
}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Create.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.SummaryListViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.CYASubmissionView
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.Future

class CYASubmissionControllerSpec extends SpecBase {

  "CYASubmissionController" - {

    "must return OK and the correct view for a GET with no discrepancies present" in {
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
        .set(LocationTypePage, LocationType.DesignatedLocation)
        .get
        .set(LocationIdPage, LocationDetails(AuthorisationNumber, "unlocode", "1234", "authorisationReferenceNumber"))
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
        body should include("Export operation")
        body should include("MRN")
        body should include("Is this a split exit?")
        body should include("DUCR")
        body should include("Yes - MUCR: 123")
        body should include("Are there any discrepancies with this consignment?")
        body should include("Location of goods")
        body should include("Type of location")
        body should include("Designated location")
        body should include("Location identifier")
        body should include("Authorisation number")
        body should include("UN/LOCODE")
        body should include("unlocode")
        body should include("Location additional identifier")
        body should include("1234")
        body should include("Authorisation reference number")
        body should include("authorisationReferenceNumber")
        body should include("Customs office of exit")
        body should include("Belfast")
        body should not include "Discrepancy details"
      }
    }

    "must return OK and the correct view for a GET with discrepancies present" in {
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
        .set(AnyDiscrepanciesPage, true)
        .get
        .set(LocationTypePage, LocationType.DesignatedLocation)
        .get
        .set(LocationIdPage, LocationDetails(AuthorisationNumber, "unlocode", "1234", "authorisationReferenceNumber"))
        .get
        .set(DiscrepancyConsignmentPage, Sea)
        .get
        .set(DiscrepancyTransportPage, ContainerDetails("containerId123", 99))
        .get
        .set(DiscrepancySealsPage, "GB12345678")
        .get
        .set(DiscrepancyReferencePage, "12")
        .get
        .set(DiscrepancyTransportMeansPage, TransportAcrossBorderDetails("transportType", "transportIdNumber", "countryOfRegistration"))
        .get
        .set(DiscrepancyTransportDocPage, DocumentDetails("documentType", "documentReferenceNumber"))
        .get
        .set(DiscrepancyPackingPage, PackingDetails("BX", "10", "MARKS123"))
        .get
        .set(DiscrepancyGoodsPage, WhatHasChangedDetails("1234", Some("2GB647298735290-S569"), "20", "10"))
        .get

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, happyRoute.CYASubmissionController.onPageLoad().url)
          .withSession(SessionKeys.sessionId -> "some-session-id")
        val result = route(application, request).value

        val view = application.injector.instanceOf[CYASubmissionView]
        status(result) shouldBe OK
        val body = contentAsString(result)
        body should include("Export operation")
        body should include("MRN")
        body should include("Is this a split exit?")
        body should include("DUCR")
        body should include("Yes - MUCR: 123")
        body should include("Are there any discrepancies with this consignment?")
        body should include("Location of goods")
        body should include("Type of location")
        body should include("Designated location")
        body should include("Location identifier")
        body should include("Authorisation number")
        body should include("UN/LOCODE")
        body should include("unlocode")
        body should include("Location additional identifier")
        body should include("1234")
        body should include("Authorisation reference number")
        body should include("authorisationReferenceNumber")
        body should include("Customs office of exit")
        body should include("Belfast")
        body should include("Discrepancy details")
        body should include("How will the goods cross the border?")
        body should include("Sea")
        body should include("Container identification number")
        body should include("containerId123")
        body should include("Number of seals")
        body should include("99")
        body should include("Seal identifier")
        body should include("GB12345678")
        body should include("Declaration Goods Reference")
        body should include("12")
        body should include("Transport means type")
        body should include("transportType")
        body should include("Transport means ID")
        body should include("transportIdNumber")
        body should include("Transport means nationality")
        body should include("countryOfRegistration")
        body should include("Transport document type")
        body should include("documentType")
        body should include("Transport document reference")
        body should include("documentReferenceNumber")
        body should include("Package type")
        body should include("BX")
        body should include("Number of packages")
        body should include("10")
        body should include("Shipping marks")
        body should include("MARKS123")
        body should include("Declaration goods item number")
        body should include("1234")
        body should include("Declaration Unique Consignment Reference (DUCR)")
        body should include("2GB647298735290-S569")
        body should include("New gross mass")
        body should include("20")
        body should include("New net mass")
        body should include("10")
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

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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.submission

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.must.Matchers.mustEqual
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.auth.core.retrieve.{~, Credentials}
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.submission.routes
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.TestFixture.{testAuthorityId, testGroupId}
import uk.gov.hmrc.automatedexportsystemfrontend.models.SingleSubmissionResponseParser
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.submission.ViewSingleSubmissionView
import uk.gov.hmrc.http.SessionKeys
import scala.xml.XML

import scala.concurrent.Future

class ViewSingleSubmissionControllerSpec extends SpecBase with MockitoSugar {

  "ViewSingleSubmission Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.ViewSingleSubmissionController.onPageLoad("12345").url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ViewSingleSubmissionView]

        status(result) mustEqual OK
        val body = contentAsString(result)
        body should include("automated-export-system-frontend")
        body should include("submission-detail")
      }
    }

    "must return OK and the correct view for a parsed XML 'full' submission" in {

      val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
      val mockAutomatedExportSystemConnector = mock[AutomatedExportSystemConnector]

      val enrolmentIdentifier =
        uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")

      val enrolments =
        Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      val mockResponseBody = """<Submission>
                                         |            <submissionId>12345</submissionId>
                                         |            <ExportOperation>
                                         |              <type>1</type>
                                         |              <MRN>mrn12345</MRN>
                                         |              <discrepanciesExist>1</discrepanciesExist>
                                         |              <splitIndicator>1</splitIndicator>
                                         |            </ExportOperation>
                                         |            <CustomsOfficeOfExitActual>
                                         |              <referenceNumber>GB000051</referenceNumber>
                                         |            </CustomsOfficeOfExitActual>
                                         |            <GoodsShipment>
                                         |              <Consignment>
                                         |                <modeOfTransportAtTheBorder>1</modeOfTransportAtTheBorder>
                                         |                <referenceNumberUCR>referenceNumberUcr</referenceNumberUCR>
                                         |                <parentUCRID>parentUcrId</parentUCRID>
                                         |                <TransportEquipment>
                                         |                  <sequenceNumber>1</sequenceNumber>
                                         |                  <containerIdentificationNumber>1</containerIdentificationNumber>
                                         |                  <numberOfSeals>1</numberOfSeals>
                                         |                  <Seal>
                                         |                    <sequenceNumber>1</sequenceNumber>
                                         |                    <identifier>sealIdentifier1</identifier>
                                         |                  </Seal>
                                         |                  <GoodsReference>
                                         |                    <sequenceNumber>1</sequenceNumber>
                                         |                    <declarationGoodsItemNumber>1</declarationGoodsItemNumber>
                                         |                  </GoodsReference>
                                         |                </TransportEquipment>
                                         |                <TransportEquipment>
                                         |                  <sequenceNumber>2</sequenceNumber>
                                         |                  <containerIdentificationNumber>2</containerIdentificationNumber>
                                         |                  <numberOfSeals>1</numberOfSeals>
                                         |                  <Seal>
                                         |                    <sequenceNumber>2</sequenceNumber>
                                         |                    <identifier>sealIdentifier2</identifier>
                                         |                  </Seal>
                                         |                  <GoodsReference>
                                         |                    <sequenceNumber>2</sequenceNumber>
                                         |                    <declarationGoodsItemNumber>2</declarationGoodsItemNumber>
                                         |                  </GoodsReference>
                                         |                </TransportEquipment>
                                         |                <LocationOfGoods>
                                         |                  <qualifierOfIdentification>qualifierOfIdentification</qualifierOfIdentification>
                                         |                  <authorisationNumber>authorisationNumber</authorisationNumber>
                                         |                  <additionalIdentifier>additionalIdentifier</additionalIdentifier>
                                         |                  <UNLocode>unLocode</UNLocode>
                                         |                </LocationOfGoods>
                                         |                <ActiveBorderTransportMeans>
                                         |                  <typeOfIdentification>typeOfIdentification</typeOfIdentification>
                                         |                  <identificationNumber>identificationNumber</identificationNumber>
                                         |                  <nationality>nationality</nationality>
                                         |                </ActiveBorderTransportMeans>
                                         |                <TransportDocument>
                                         |                  <sequenceNumber>1</sequenceNumber>
                                         |                  <type>1</type>
                                         |                  <referenceNumber>referenceNumber1</referenceNumber>
                                         |                </TransportDocument>
                                         |                <TransportDocument>
                                         |                  <sequenceNumber>2</sequenceNumber>
                                         |                  <type>2</type>
                                         |                  <referenceNumber>referenceNumber2</referenceNumber>
                                         |                </TransportDocument>
                                         |              </Consignment>
                                         |              <GoodsItem>
                                         |                <declarationGoodsItemNumber>1</declarationGoodsItemNumber>
                                         |                <referenceNumberUCR>referenceNumberUcr</referenceNumberUCR>
                                         |                <Commodity>
                                         |                  <GoodsMeasure>
                                         |                    <grossMass>100.55</grossMass>
                                         |                    <netMass>80.45</netMass>
                                         |                  </GoodsMeasure>
                                         |                </Commodity>
                                         |                <Packaging>
                                         |                  <sequenceNumber>1</sequenceNumber>
                                         |                  <typeOfPackages>typeOfPackages</typeOfPackages>
                                         |                  <numberOfPackages>1</numberOfPackages>
                                         |                  <shippingMarks>shippingMarks</shippingMarks>
                                         |                </Packaging>
                                         |                <Packaging>
                                         |                  <sequenceNumber>2</sequenceNumber>
                                         |                  <typeOfPackages>typeOfPackages</typeOfPackages>
                                         |                  <numberOfPackages>1</numberOfPackages>
                                         |                  <shippingMarks>shippingMarks</shippingMarks>
                                         |                </Packaging>
                                         |              </GoodsItem>
                                         |              <GoodsItem>
                                         |                <declarationGoodsItemNumber>2</declarationGoodsItemNumber>
                                         |                <referenceNumberUCR>referenceNumberUcr</referenceNumberUCR>
                                         |                <Commodity>
                                         |                  <GoodsMeasure>
                                         |                    <grossMass>100.55</grossMass>
                                         |                    <netMass>80.45</netMass>
                                         |                  </GoodsMeasure>
                                         |                </Commodity>
                                         |                <Packaging>
                                         |                  <sequenceNumber>3</sequenceNumber>
                                         |                  <typeOfPackages>typeOfPackages</typeOfPackages>
                                         |                  <numberOfPackages>1</numberOfPackages>
                                         |                  <shippingMarks>shippingMarks</shippingMarks>
                                         |                </Packaging>
                                         |                <Packaging>
                                         |                  <sequenceNumber>4</sequenceNumber>
                                         |                  <typeOfPackages>typeOfPackages</typeOfPackages>
                                         |                  <numberOfPackages>1</numberOfPackages>
                                         |                  <shippingMarks>shippingMarks</shippingMarks>
                                         |                </Packaging>
                                         |              </GoodsItem>
                                         |            </GoodsShipment>
                                         |            <updatedAt>2026-08-11T00:00:00</updatedAt>
                                         |          </Submission>""".stripMargin

      when(mockAutomatedExportSystemConnector.getSingleSubmissionTestOnly(any[String])(any()))
        .thenReturn(Future.successful(SingleSubmissionResponseParser.parse(XML.loadString(mockResponseBody))))

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
          bind[AutomatedExportSystemConnector].toInstance(mockAutomatedExportSystemConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.ViewSingleSubmissionController.onPageLoad("12345").url)
          .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) mustEqual OK
        val body = contentAsString(result)
        body should include("automated-export-system-frontend")
        body should include("submission-detail")

        body should include("IE507(a) pre-notification details")
        body should include("Accepted")
        body should include("Submitted 2026-08-11")

        body should include("Export operation")
        body should include("MRN")
        body should include("mrn12345")
        body should include("Are there any discrepancies with this consignment?")
        body should include("Yes")
        body should include("Is this a split exit?")
        body should include("Yes")

        body should include("Consignment")
        body should include("How will the goods cross the border?")
        body should include("Sea")
        body should include("DUCR")
        body should include("referenceNumberUcr")
        body should include("Is this part of a consolidation?")
        body should include("Yes - MUCR: parentUcrId")

        body should include("Customs office of exit")
        body should include("Where do you expect the goods to exit the UK?")
        body should include("Belfast (GB000051)")

        body should include("Exit carrier")

        body should include("Transport equipment")
        body should include("Container identification number")
        body should include("1")
        body should include("Number of seals")
        body should include("Seal identifier")
        body should include("sealIdentifier1")
        body should include("Declaration Goods Reference")

        body should include("2")
        body should include("sealIdentifier2")

        body should include("Location of goods")
        body should include("Type of location")
        body should include("locationType.")
        body should include("Location identifier")
        body should include("qualifierOfIdentification")
        body should include("Authorisation reference number")
        body should include("authorisationNumber")
        body should include("Location additional identifier")
        body should include("additionalIdentifier")
        body should include("UN/LOCODE")
        body should include("unLocode")

        body should include("Transport across the border")
        body should include("Transport means type")
        body should include("typeOfIdentification")
        body should include("Transport means ID")
        body should include("identificationNumber")
        body should include("Transport means nationality")
        body should include("nationality")

        body should include("Document Details")
        body should include("Transport document type")
        body should include("Transport document reference")
        body should include("referenceNumber1")
        body should include("referenceNumber2")

        body should include("Goods Item")
        body should include("Declaration goods item number")
        body should include("Declaration Unique Consignment Reference (DUCR)")
        body should include("New gross mass")
        body should include("100.55")
        body should include("New net mass")
        body should include("80.45")
        body should include("Package type")
        body should include("typeOfPackages")
        body should include("Number of packages")
        body should include("Shipping marks")
        body should include("shippingMarks")

        body should include("Amend this submission")
        body should include("Cancel this submission")
      }
    }
  }
}

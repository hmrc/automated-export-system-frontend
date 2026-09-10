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

package uk.gov.hmrc.automatedexportsystemfrontend.connectors

import com.google.inject.*
import org.apache.pekko.Done
import play.api.Logging
import play.api.http.Status.{ACCEPTED, OK}
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}
import play.api.libs.ws.writeableOf_String
import uk.gov.hmrc.automatedexportsystemfrontend.config.FrontendAppConfig
import uk.gov.hmrc.automatedexportsystemfrontend.models.{
  SingleSubmissionResponse,
  SingleSubmissionResponseParser,
  SubmissionSummaryResponseList,
  SubmissionSummaryResponseParser
}
import play.api.http.Status.NO_CONTENT

import scala.concurrent.{ExecutionContext, Future}
import scala.xml.XML

@Singleton
class AutomatedExportSystemConnector @Inject() (frontendAppConfig: FrontendAppConfig, httpClient: HttpClientV2)(implicit ec: ExecutionContext)
    extends Logging {

  def submitIE507a(submission: String)(implicit hc: HeaderCarrier): Future[Done] =
    httpClient
      .post(url"${frontendAppConfig.automatedExportSystemApi}/message")
      .setHeader("Content-Type" -> "application/xml; charset=UTF-8")
      .withBody(submission)
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case ACCEPTED =>
            Future.successful(Done)
          case _ =>
            logger.error(s"Failed to submit IE507a to /automated-export-system/message with status : ${response.status}")
            Future.failed(UpstreamErrorResponse("Unexpected response from /automated-export-system/message", response.status))
        }
      }

  def getSubmissionSummaryResponses()(implicit hc: HeaderCarrier): Future[SubmissionSummaryResponseList] =
    httpClient
      .get(url"${frontendAppConfig.automatedExportSystemApi}/submissions")
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(SubmissionSummaryResponseParser.parse(XML.loadString(response.body)))
          case _ =>
            logger.error(s"Failed to retrieve submissions from /automated-export-system/submissions with status : ${response.status}")
            Future.failed(UpstreamErrorResponse("Unexpected response from /automated-export-system/submissions", response.status))
        }
      }

  def getSingleSubmission(submissionId: String)(implicit hc: HeaderCarrier): Future[SingleSubmissionResponse] =
    httpClient
      .get(url"${frontendAppConfig.automatedExportSystemApi}/submission/$submissionId")
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(SingleSubmissionResponseParser.parse(XML.loadString(response.body)))

          case _ =>
            Future.failed(UpstreamErrorResponse(s"Unexpected response from /submission/$submissionId", response.status))
        }
      }

  def getSingleSubmissionTestOnly(submissionId: String)(implicit hc: HeaderCarrier): Future[SingleSubmissionResponse] =
    val body = """<Submission>
                      |            <submissionId>12345</submissionId>
                      |            <ExportOperation>
                      |              <type>1</type>
                      |              <MRN>mrn12345</MRN>
                      |              <discrepanciesExist>1</discrepanciesExist>
                      |              <splitIndicator>1</splitIndicator>
                      |            </ExportOperation>
                      |            <CustomsOfficeOfExitActual>
                      |              <referenceNumber>referenceNumber</referenceNumber>
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
                      |                  <typeOfLocation>typeOfLocation</typeOfLocation>
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

    Future.successful(SingleSubmissionResponseParser.parse(XML.loadString(body)))

  def cancelSubmission(submissionId: String)(implicit hc: HeaderCarrier): Future[Done] =
    httpClient
      .get(url"${frontendAppConfig.automatedExportSystemApi}/cancel/$submissionId")
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case NO_CONTENT =>
            Future.successful(Done)

          case _ =>
            Future.failed(UpstreamErrorResponse(s"Unexpected response from cancel submission $submissionId", response.status))
        }
      }
}

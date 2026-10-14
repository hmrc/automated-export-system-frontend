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

package uk.gov.hmrc.automatedexportsystemfrontend.utils

import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.*
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.*

import java.time.LocalDateTime

class AmendmentAnswersMapperSpec extends SpecBase with Matchers {

  private val mapper = new AmendmentAnswersMapper
  private val sessionId = "sessionId"
  private val submissionId = "submissionId"

  "toUserAnswers" - {

    "populate the amend answers from a submission with consignment data" in {
      val submission = SingleSubmissionResponse(
        submissionId = submissionId,
        status = 1,
        exportOperation =
          SingleSubmissionExportOperation(exportOperationType = "1", mrn = "24GB12345678901234", discrepanciesExist = 1, splitIndicator = 1),
        customsOfficeOfExitActual = SingleSubmissionCustomsOfficeOfExitActual("GB000051"),
        goodsShipment = Some(
          SingleSubmissionGoodsShipment(
            consignment = SingleSubmissionConsignment(
              modeOfTransportAtTheBorder = Some(3),
              referenceNumberUCR = "DUCR123456",
              parentUCRID = Some("MUCR123"),
              transportEquipment = None,
              locationOfGoods = SingleSubmissionLocationOfGoods(
                typeOfLocation = "designatedLocation",
                qualifierOfIdentification = "unlocode",
                authorisationNumber = None,
                additionalIdentifier = Some("Additional ID"),
                UNLocode = Some("GBABC")
              ),
              activeBorderTransportMeans = Some(
                SingleSubmissionActiveBorderTransportMeans(
                  typeOfIdentification = Some("11"),
                  identificationNumber = Some("ID123"),
                  nationality = Some("GB")
                )
              ),
              transportDocument = None
            ),
            goodsItems = Some(
              Seq(
                SingleSubmissionGoodsItem(
                  declarationGoodsItemNumber = Some(1),
                  referenceNumberUCR = Some("GRN1"),
                  commodity = SingleSubmissionCommodity(BigDecimal(10), BigDecimal(9)),
                  packaging = None
                )
              )
            )
          )
        ),
        updatedAt = LocalDateTime.now()
      )

      val result = mapper.toUserAnswers(sessionId, submission).success.value

      result.id shouldBe sessionId
      result.get(AmendEnterMrnPage(submissionId)) shouldBe Some("24GB12345678901234")
      result.get(AmendIsSplitExitPage(submissionId)) shouldBe Some(true)
      result.get(AmendAnyDiscrepanciesPage(submissionId)) shouldBe Some(true)
      result.get(AmendOfficeOfExitPage(submissionId)) shouldBe Some(OfficeOfExit.fromCode("GB000051"))
      result.get(AmendEnterDucrPage(submissionId)) shouldBe Some("DUCR123456")
      result.get(AmendLocationTypePage(submissionId)) shouldBe Some(LocationType.DesignatedLocation)
      result.get(AmendLocationIdPage(submissionId)) shouldBe Some(
        LocationDetails(
          locationType = LocationQualifier.UnLocode,
          unlocode = Some("GBABC"),
          locationAdditionalIdentifier = Some("Additional ID"),
          authorisationReferenceNumber = None
        )
      )
      result.get(AmendPartOfConsolidationPage(submissionId)) shouldBe Some(PartOfConsolidationAnswer(boolean = true, mucr = Some("MUCR123")))
      result.get(AmendDiscrepancyConsignmentPage(submissionId)) shouldBe Some(ModeOfTransportAtBorder.fromCode(3).value)
    }

    "populate only the header answers when there is no consignment" in {
      val submission = SingleSubmissionResponse(
        submissionId = submissionId,
        status = 1,
        exportOperation =
          SingleSubmissionExportOperation(exportOperationType = "1", mrn = "24GB12345678901234", discrepanciesExist = 0, splitIndicator = 0),
        customsOfficeOfExitActual = SingleSubmissionCustomsOfficeOfExitActual("GB000051"),
        goodsShipment = None,
        updatedAt = LocalDateTime.now()
      )

      val result = mapper.toUserAnswers(sessionId, submission).success.value

      result.id shouldBe sessionId
      result.get(AmendEnterMrnPage(submissionId)) shouldBe Some("24GB12345678901234")
      result.get(AmendIsSplitExitPage(submissionId)) shouldBe Some(false)
      result.get(AmendAnyDiscrepanciesPage(submissionId)) shouldBe Some(false)
      result.get(AmendOfficeOfExitPage(submissionId)) shouldBe Some(OfficeOfExit.fromCode("GB000051"))

      result.get(AmendEnterDucrPage(submissionId)) shouldBe None
      result.get(AmendLocationTypePage(submissionId)) shouldBe None
      result.get(AmendLocationIdPage(submissionId)) shouldBe None
      result.get(AmendPartOfConsolidationPage(submissionId)) shouldBe None
      result.get(AmendDiscrepancyConsignmentPage(submissionId)) shouldBe None
    }
  }
}

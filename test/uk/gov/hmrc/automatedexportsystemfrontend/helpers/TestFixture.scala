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

package uk.gov.hmrc.automatedexportsystemfrontend.helpers
import uk.gov.hmrc.automatedexportsystemfrontend.models.{
  SingleSubmissionConsignment,
  SingleSubmissionCustomsOfficeOfExitActual,
  SingleSubmissionExportOperation,
  SingleSubmissionGoodsShipment,
  SingleSubmissionLocationOfGoods,
  SingleSubmissionResponse
}

import java.time.{Clock, Instant, LocalDateTime, ZoneOffset}

object TestFixture {
  val testGroupId = "test-group-id"
  val testAuthorityId = "test-authority-id"
  val testMrn = "24GB12345678901234"
  private val fixedInstant = Instant.parse("2025-06-03T00:00:00Z")
  private val clock = Clock.fixed(fixedInstant, ZoneOffset.UTC)

  val testLocationOfGoods = SingleSubmissionLocationOfGoods(
    typeOfLocation = "test",
    qualifierOfIdentification = "test",
    authorisationNumber = None,
    additionalIdentifier = None,
    UNLocode = None
  )

  val testConsignment = SingleSubmissionConsignment(
    modeOfTransportAtTheBorder = None,
    referenceNumberUCR = "test",
    parentUCRID = None,
    transportEquipment = None,
    locationOfGoods = testLocationOfGoods,
    activeBorderTransportMeans = None,
    transportDocument = None
  )

  def singleSubmission(submissionId: String, mrn: String = testMrn): SingleSubmissionResponse = SingleSubmissionResponse(
    submissionId = submissionId,
    exportOperation = SingleSubmissionExportOperation(exportOperationType = "test", splitIndicator = 0, mrn = mrn, discrepanciesExist = 0),
    customsOfficeOfExitActual = SingleSubmissionCustomsOfficeOfExitActual(referenceNumber = "testRef"),
    goodsShipment = Some(SingleSubmissionGoodsShipment(consignment = testConsignment, goodsItems = None)),
    updatedAt = LocalDateTime.now(clock)
  )
}

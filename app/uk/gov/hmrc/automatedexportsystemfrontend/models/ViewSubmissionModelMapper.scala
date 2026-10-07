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

package uk.gov.hmrc.automatedexportsystemfrontend.models

import uk.gov.hmrc.automatedexportsystemfrontend.views.submission.lookups.SubmissionLookups

import java.time.format.DateTimeFormatter

object ViewSubmissionViewModelMapper {

  def toViewModel(response: SingleSubmissionResponse): ViewSubmissionViewModel =
    ViewSubmissionViewModel(
      submissionId = response.submissionId,
      mrn = response.exportOperation.mrn,
      ducr = response.goodsShipment.map(_.consignment.referenceNumberUCR).getOrElse(""),
      officeOfExit = response.customsOfficeOfExitActual.referenceNumber,
      submittedDate = response.updatedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
      status = SubmissionLookups.mapStatus(response.exportOperation.exportOperationType),
      discrepancyConsignment = response.goodsShipment.flatMap(_.consignment.modeOfTransportAtTheBorder.map(_.toString)),
      locationType = response.goodsShipment.map(_.consignment.locationOfGoods.typeOfLocation),
      locationUnlocode = response.goodsShipment.flatMap(_.consignment.locationOfGoods.UNLocode),
      locationAdditionalIdentifier = response.goodsShipment.flatMap(_.consignment.locationOfGoods.additionalIdentifier),
      locationAuthorisationReferenceNumber = response.goodsShipment.flatMap(_.consignment.locationOfGoods.authorisationNumber),
      partOfConsolidation = None,
      anyDiscrepancies = Some(response.exportOperation.discrepanciesExist == 1),
      isSplitExit = Some(response.exportOperation.splitIndicator == 1)
    )
}

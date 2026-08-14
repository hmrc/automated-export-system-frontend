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

package uk.gov.hmrc.automatedexportsystemfrontend.forms.unhappyPath

import play.api.data.Form
import play.api.data.Forms.*
import uk.gov.hmrc.automatedexportsystemfrontend.forms.mappings.Mappings
import uk.gov.hmrc.automatedexportsystemfrontend.models.PackingDetails

import javax.inject.Inject

class DiscrepancyPackingFormProvider @Inject() extends Mappings {

  def apply(): Form[PackingDetails] = Form(
    mapping(
      "packagingCode" -> text("discrepancyPacking.error.packagingCode.required")
        .verifying(maxLength(100, "discrepancyPacking.error.packagingCode.length")),
      "numberOfPackages" -> text("discrepancyPacking.error.numberOfPackages.required")
        .verifying(maxLength(100, "discrepancyPacking.error.numberOfPackages.length")),
      "shippingMarks" -> text("discrepancyPacking.error.shippingMarks.required")
        .verifying(maxLength(100, "discrepancyPacking.error.shippingMarks.length"))
    )(PackingDetails.apply)(x => Some((x.packagingCode, x.numberOfPackages, x.shippingMarks)))
  )
}

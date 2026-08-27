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

package uk.gov.hmrc.automatedexportsystemfrontend.forms.create

import play.api.data.Form
import play.api.data.Forms.*
import uk.gov.hmrc.automatedexportsystemfrontend.forms.Constants.{ducrMaxLength, ducrRegex, goodsItemNumberMaxValue, grossMassRegex, netMassRegex}
import uk.gov.hmrc.automatedexportsystemfrontend.forms.mappings.Mappings
import uk.gov.hmrc.automatedexportsystemfrontend.models.WhatHasChangedDetails

import javax.inject.Inject

class DiscrepancyGoodsFormProvider @Inject() extends Mappings {

  def apply(): Form[WhatHasChangedDetails] = Form(
    mapping(
      "goodsItemNumber" -> int("discrepancyGoods.error.goodsItemNumber.required")
        .verifying(
          minimumValue(0, "discrepancyGoods.error.goodsItemNumber.length"),
          maximumValue(goodsItemNumberMaxValue, "discrepancyGoods.error.goodsItemNumber.length")
        ),
      "declarationUniqueConsignmentReference" -> optional(
        text().verifying(
          firstError(
            maxLength(ducrMaxLength, "discrepancyGoods.error.declarationUniqueConsignmentReference.length"),
            regexp(ducrRegex, "discrepancyGoods.error.declarationUniqueConsignmentReference.invalid")
          )
        )
      ),
      "newGrossMass" -> text("discrepancyGoods.error.newGrossMass.required")
        .verifying(
          firstError(
            maxLength(100, "discrepancyGoods.error.newGrossMass.length"),
            regexp(grossMassRegex, "discrepancyGoods.error.newGrossMass.invalid")
          )
        ),
      "newNetMass" -> text("discrepancyGoods.error.newNetMass.required")
        .verifying(
          firstError(maxLength(100, "discrepancyGoods.error.newNetMass.length"), regexp(netMassRegex, "discrepancyGoods.error.newNetMass.invalid"))
        )
    )(WhatHasChangedDetails.apply)(x => Some((x.declarationGoodsItemNumber, x.declarationUniqueConsignmentReference, x.newGrossMass, x.newNetMass)))
  )
}

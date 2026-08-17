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
import uk.gov.hmrc.automatedexportsystemfrontend.models.WhatHasChangedDetails

import javax.inject.Inject

class DiscrepancyGoodsFormProvider @Inject() extends Mappings {

  def apply(): Form[WhatHasChangedDetails] = Form(
    mapping(
      "goodsItemNumber" -> text("discrepancyGoods.error.goodsItemNumber.required")
        .verifying(maxLength(100, "discrepancyGoods.error.goodsItemNumber.length")),
      "declarationUniqueConsignmentReference" -> optional(
        text().verifying(maxLength(100, "discrepancyGoods.error.declarationUniqueConsignmentReference.length"))
      ),
      "newGrossMass" -> text("discrepancyGoods.error.newGrossMass.required")
        .verifying(maxLength(100, "discrepancyGoods.error.newGrossMass.length")),
      "newNetMass" -> text("discrepancyGoods.error.newNetMass.required")
        .verifying(maxLength(100, "discrepancyGoods.error.newNetMass.length"))
    )(WhatHasChangedDetails.apply)(x => Some((x.goodsItemNumber, x.declarationUniqueConsignmentReference, x.newGrossMass, x.newNetMass)))
  )
}

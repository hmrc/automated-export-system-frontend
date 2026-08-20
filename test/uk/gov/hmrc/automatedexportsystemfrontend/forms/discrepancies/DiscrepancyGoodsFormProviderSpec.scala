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

package uk.gov.hmrc.automatedexportsystemfrontend.forms.discrepancies

import play.api.data.FormError
import uk.gov.hmrc.automatedexportsystemfrontend.forms.behaviours.StringFieldBehaviours
import uk.gov.hmrc.automatedexportsystemfrontend.forms.discrepancies.DiscrepancyGoodsFormProvider

class DiscrepancyGoodsFormProviderSpec extends StringFieldBehaviours {

  val form = new DiscrepancyGoodsFormProvider()()

  ".goodsItemNumber" - {

    val fieldName = "goodsItemNumber"
    val requiredKey = "discrepancyGoods.error.goodsItemNumber.required"
    val lengthKey = "discrepancyGoods.error.goodsItemNumber.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  ".declarationUniqueConsignmentReference" - {

    val fieldName = "declarationUniqueConsignmentReference"
    val lengthKey = "discrepancyGoods.error.declarationUniqueConsignmentReference.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    "bind successfully when no ducr value is provided" in {
      val data = Map("goodsItemNumber" -> "reference", "declarationUniqueConsignmentReference" -> "", "newGrossMass" -> "20", "newNetMass" -> "10")

      val result = form.bind(data)

      result.errors mustBe empty
      result.value.value.declarationUniqueConsignmentReference mustBe None
    }
  }

  ".newGrossMass" - {

    val fieldName = "newGrossMass"
    val requiredKey = "discrepancyGoods.error.newGrossMass.required"
    val lengthKey = "discrepancyGoods.error.newGrossMass.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  ".newNetMass" - {

    val fieldName = "newNetMass"
    val requiredKey = "discrepancyGoods.error.newNetMass.required"
    val lengthKey = "discrepancyGoods.error.newNetMass.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }
}

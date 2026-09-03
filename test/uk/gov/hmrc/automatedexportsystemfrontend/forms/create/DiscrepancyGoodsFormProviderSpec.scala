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

import play.api.data.{Field, FormError}
import uk.gov.hmrc.automatedexportsystemfrontend.forms.Constants.{ducrRegex, grossMassRegex, netMassRegex}
import uk.gov.hmrc.automatedexportsystemfrontend.forms.behaviours.{IntFieldBehaviours, StringFieldBehaviours}
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyGoodsFormProvider

class DiscrepancyGoodsFormProviderSpec extends StringFieldBehaviours with IntFieldBehaviours {

  val form = new DiscrepancyGoodsFormProvider()()

  ".declarationGoodsItemNumber" - {

    val fieldName = "declarationGoodsItemNumber"
    val requiredKey = "discrepancyGoods.error.goodsItemNumber.required"
    val lengthKey = "discrepancyGoods.error.goodsItemNumber.length"
//    val invalidKey = "discrepancyGoods.error.goodsItemNumber.invalid"
    val maxLength = 9999

    behave like intFieldWithMaximum(form, fieldName, maxLength, FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    "bind successfully when no goods item number is provided" in {
      val data =
        Map(fieldName -> "", "declarationUniqueConsignmentReference" -> "5GB000000000000-12345", "newGrossMass" -> "20", "newNetMass" -> "10")

      val result = form.bind(data)

      result.errors mustBe empty
      result.value.value.declarationGoodsItemNumber mustBe None
    }

    // TODO possibly readd or delete as needed
//    "must not bind invalid data" in {
//
//      val invalidValues: Seq[String] = Seq("abc123!", "abc?123")
//
//
//      invalidValues.foreach { invalidValue =>
//        val result: Field = form.bind(Map(fieldName -> invalidValue)).apply(fieldName)
//        result.errors must contain(expectedError)
//      }
//    }
  }

  ".declarationUniqueConsignmentReference" - {

    val fieldName = "declarationUniqueConsignmentReference"
    val lengthKey = "discrepancyGoods.error.declarationUniqueConsignmentReference.length"
    val invalidKey = "discrepancyGoods.error.declarationUniqueConsignmentReference.invalid"
    val maxLength = 35

    behave like fieldThatBindsValidData(form, fieldName, ducrGen)

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    "bind successfully when no ducr value is provided" in {
      val data =
        Map("declarationGoodsItemNumber" -> "1234", "declarationUniqueConsignmentReference" -> "", "newGrossMass" -> "20", "newNetMass" -> "10")

      val result = form.bind(data)

      result.errors mustBe empty
      result.value.value.declarationUniqueConsignmentReference mustBe None
    }

    "must not bind invalid data" in {

      val invalidValues: Seq[String] = Seq("abc123!", "abc?123")

      val expectedError = FormError(fieldName, invalidKey, Seq(ducrRegex))

      invalidValues.foreach { invalidValue =>
        val result: Field = form.bind(Map(fieldName -> invalidValue)).apply(fieldName)
        result.errors must contain(expectedError)
      }
    }
  }

  ".newGrossMass" - {

    val fieldName = "newGrossMass"
    val requiredKey = "discrepancyGoods.error.newGrossMass.required"
    val lengthKey = "discrepancyGoods.error.newGrossMass.length"
    val invalidKey = "discrepancyGoods.error.newGrossMass.invalid"
    val maxLength = 100

    behave like fieldThatBindsValidData(form, fieldName, validWeight(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    "must not bind invalid data" in {

      val invalidValues: Seq[String] = Seq("abc123!", "abc?123")

      val expectedError = FormError(fieldName, invalidKey, Seq(grossMassRegex))

      invalidValues.foreach { invalidValue =>
        val result: Field = form.bind(Map(fieldName -> invalidValue)).apply(fieldName)
        result.errors must contain(expectedError)
      }
    }
  }

  ".newNetMass" - {

    val fieldName = "newNetMass"
    val requiredKey = "discrepancyGoods.error.newNetMass.required"
    val lengthKey = "discrepancyGoods.error.newNetMass.length"
    val invalidKey = "discrepancyGoods.error.newNetMass.invalid"
    val maxLength = 100

    behave like fieldThatBindsValidData(form, fieldName, validWeight(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    "must not bind invalid data" in {

      val invalidValues: Seq[String] = Seq("abc123!", "abc?123")

      val expectedError = FormError(fieldName, invalidKey, Seq(netMassRegex))

      invalidValues.foreach { invalidValue =>
        val result: Field = form.bind(Map(fieldName -> invalidValue)).apply(fieldName)
        result.errors must contain(expectedError)
      }
    }
  }
}

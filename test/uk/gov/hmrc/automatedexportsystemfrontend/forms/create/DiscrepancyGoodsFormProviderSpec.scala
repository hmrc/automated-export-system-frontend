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
import uk.gov.hmrc.automatedexportsystemfrontend.models.WhatHasChangedDetails

class DiscrepancyGoodsFormProviderSpec extends StringFieldBehaviours with IntFieldBehaviours {

  val form = new DiscrepancyGoodsFormProvider()()
  private def key(name: String): String = s"goodsItem.$name"

  private val validData: Map[String, String] = Map(
    key("declarationGoodsItemNumber") -> "1234",
    key("declarationUniqueConsignmentReference") -> "5GB000000000000-12345",
    key("newGrossMass") -> "20",
    key("newNetMass") -> "10"
  )

  "the form as a whole" - {

    "must bind to None when every field is blank" in {
      val data =
        Map("declarationGoodsItemNumber" -> "", "declarationUniqueConsignmentReference" -> "", "newGrossMass" -> "", "newNetMass" -> "")

      val result = form.bind(data)

      result.errors mustBe empty
      result.value.value mustBe None
    }

    "must bind to None when no fields are submitted" in {
      val result = form.bind(Map.empty[String, String])

      result.errors mustBe empty
      result.value.value mustBe None
    }

    "must bind to Some(details) when all fields are valid" in {
      val result = form.bind(validData)

      result.errors mustBe empty
      result.value.value mustBe Some(WhatHasChangedDetails(Some(1234), Some("5GB000000000000-12345"), "20", "10"))
    }

    "must require both masses when only the DUCR is entered" in {
      val data = Map(key("declarationUniqueConsignmentReference") -> "5GB000000000000-12345")

      val result = form.bind(data)

      result.errors.map(e => e.key -> e.message) must contain allOf (
        key("newGrossMass") -> "discrepancyGoods.error.newGrossMass.required",
        key("newNetMass") -> "discrepancyGoods.error.newNetMass.required"
      )
    }

    "must fill the prefixed keys" in {
      val filled = form.fill(Some(WhatHasChangedDetails(Some(1234), Some("5GB000000000000-12345"), "20", "10")))

      filled.data mustBe validData
      form.fill(None).data mustBe empty
    }
  }

  ".declarationGoodsItemNumber" - {
    val fieldName = key("declarationGoodsItemNumber")
    val requiredKey = "discrepancyGoods.error.goodsItemNumber.required"
    val lengthKey = "discrepancyGoods.error.goodsItemNumber.length"
    val maxLength = 9999

    behave like intFieldWithMaximum(form, fieldName, maxLength, FormError(fieldName, lengthKey, Seq(maxLength)))

    "bind successfully when no goods item number is provided" in {
      val result = form.bind(validData.updated(fieldName, ""))
      result.errors mustBe empty
      result.value.value.value.declarationGoodsItemNumber mustBe None
    }
  }

  ".declarationUniqueConsignmentReference" - {

    val fieldName = key("declarationUniqueConsignmentReference")
    val optionalKey = "discrepancyGoods.error.declarationUniqueConsignmentReference.optional"
    val lengthKey = "discrepancyGoods.error.declarationUniqueConsignmentReference.length"
    val invalidKey = "discrepancyGoods.error.declarationUniqueConsignmentReference.invalid"
    val maxLength = 35

    behave like fieldThatBindsValidData(form, fieldName, ducrGen)

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    "bind successfully when no ducr value is provided" in {
      val result = form.bind(validData.updated(fieldName, ""))

      result.errors mustBe empty
      result.value.value.value.declarationUniqueConsignmentReference mustBe None
    }

    "must not bind invalid data" in {

      val invalidValues: Seq[String] = Seq("abc123!", "abc?123")

      val expectedError = FormError(fieldName, invalidKey, Seq(ducrRegex))

      invalidValues.foreach { invalidValue =>
        val result: Field = form.bind(Map(fieldName -> invalidValue)).apply(fieldName)
        result.errors must contain(expectedError)
      }
    }

    "not bind when only spaces are entered, prompting an optional ducr error" in {
      val result = form.bind(validData.updated(fieldName, " "))

      result.errors must contain(FormError(fieldName, optionalKey))
    }
  }

  ".newGrossMass" - {

    val fieldName = key("newGrossMass")
    val requiredKey = "discrepancyGoods.error.newGrossMass.required"
    val lengthKey = "discrepancyGoods.error.newGrossMass.length"
    val invalidKey = "discrepancyGoods.error.newGrossMass.invalid"
    val maxLength = 100

    behave like fieldThatBindsValidData(form, fieldName, validWeight(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    "must be required when the field is missing but another field is entered" in {
      val result = form.bind(validData - fieldName).apply(fieldName)

      result.errors mustBe Seq(FormError(fieldName, requiredKey))
    }

    "must be required when the field is blank but another field is entered" in {
      val result = form.bind(validData.updated(fieldName, "")).apply(fieldName)

      result.errors mustBe Seq(FormError(fieldName, requiredKey))
    }

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

    val fieldName = key("newNetMass")
    val requiredKey = "discrepancyGoods.error.newNetMass.required"
    val lengthKey = "discrepancyGoods.error.newNetMass.length"
    val invalidKey = "discrepancyGoods.error.newNetMass.invalid"
    val maxLength = 100

    behave like fieldThatBindsValidData(form, fieldName, validWeight(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    "must be required when the field is missing but another field is entered" in {
      val result = form.bind(validData - fieldName).apply(fieldName)

      result.errors mustBe Seq(FormError(fieldName, requiredKey))
    }

    "must be required when the field is blank but another field is entered" in {
      val result = form.bind(validData.updated(fieldName, "")).apply(fieldName)

      result.errors mustBe Seq(FormError(fieldName, requiredKey))
    }

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

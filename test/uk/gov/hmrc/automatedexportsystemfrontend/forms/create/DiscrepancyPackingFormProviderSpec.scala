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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.Constants.shippingMarksRegex
import uk.gov.hmrc.automatedexportsystemfrontend.forms.behaviours.{IntFieldBehaviours, StringFieldBehaviours}
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyPackingFormProvider

class DiscrepancyPackingFormProviderSpec extends StringFieldBehaviours with IntFieldBehaviours {

  val form = new DiscrepancyPackingFormProvider()()

  ".packagingCode" - {

    val fieldName = "packagingCode"
    val requiredKey = "discrepancyPacking.error.packagingCode.required"
    val lengthKey = "discrepancyPacking.error.packagingCode.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  ".numberOfPackages" - {

    val fieldName = "numberOfPackages"
    val requiredKey = "discrepancyPacking.error.numberOfPackages.required"
    val lengthKey = "discrepancyPacking.error.numberOfPackages.length"
    val maxLength = 99999999

    behave like intFieldWithMaximum(form, fieldName, maxLength, FormError(fieldName, lengthKey, Seq(maxLength)))

//    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  ".shippingMarks" - {

    val fieldName = "shippingMarks"
    val requiredKey = "discrepancyPacking.error.shippingMarks.required"
    val lengthKey = "discrepancyPacking.error.shippingMarks.length"
    val invalidKey = "discrepancyPacking.error.shippingMarks.invalid"
    val maxLength = 512

    behave like fieldThatBindsValidData(form, fieldName, alphaNumStringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    "must not bind invalid data" in {

      val invalidValues: Seq[String] = Seq(" abc123", "abc123 ")

      val expectedError = FormError(fieldName, invalidKey, Seq(shippingMarksRegex))

      invalidValues.foreach { invalidValue =>
        val result: Field = form.bind(Map(fieldName -> invalidValue)).apply(fieldName)
        result.errors must contain(expectedError)
      }
    }
  }
}

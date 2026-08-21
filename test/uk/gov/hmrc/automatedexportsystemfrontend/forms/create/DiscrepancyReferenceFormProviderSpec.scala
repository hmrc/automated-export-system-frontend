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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.Constants.sealIdentifierRegex
import uk.gov.hmrc.automatedexportsystemfrontend.forms.behaviours.StringFieldBehaviours
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyReferenceFormProvider

class DiscrepancyReferenceFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "discrepancySeals.error.required"
  val lengthKey = "discrepancySeals.error.length"
  val invalidKey = "discrepancySeals.error.invalid"
  val maxLength = 20

  val form = new DiscrepancyReferenceFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(form, fieldName, alphaNumStringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    "must not bind invalid data" in {

      val invalidValues: Seq[String] = Seq(" abc123", "abc123 ")

      val expectedError = FormError(fieldName, invalidKey, Seq(sealIdentifierRegex))

      invalidValues.foreach { invalidValue =>
        val result: Field = form.bind(Map(fieldName -> invalidValue)).apply(fieldName)
        result.errors must contain(expectedError)
      }
    }
  }
}

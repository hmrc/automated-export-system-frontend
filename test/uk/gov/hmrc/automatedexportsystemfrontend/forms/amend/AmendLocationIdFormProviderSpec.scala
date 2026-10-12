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

package uk.gov.hmrc.automatedexportsystemfrontend.forms.amend

import play.api.data.FormError
import uk.gov.hmrc.automatedexportsystemfrontend.forms.Constants.{additionalIdentifierRegex, authorisationNumberRegex}
import uk.gov.hmrc.automatedexportsystemfrontend.forms.behaviours.{OptionFieldBehaviours, StringFieldBehaviours}
import uk.gov.hmrc.automatedexportsystemfrontend.forms.amend.AmendLocationIdFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{LocationDetails, LocationQualifier}

class AmendLocationIdFormProviderSpec extends OptionFieldBehaviours, StringFieldBehaviours {

  val form = new AmendLocationIdFormProvider()()

  ".locationType" - {

    val fieldName = "locationType"

    behave like optionsField[LocationQualifier](
      form,
      fieldName,
      validValues = LocationQualifier.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, "locationId.error.locationType.required"))
  }

  ".unlocode" - {

    val fieldName = "unlocode"
    val lengthKey = "locationId.error.unlocode.length"
    val maxLength = 17

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))
  }

  ".locationAdditionalIdentifier" - {

    val fieldName = "locationAdditionalIdentifier"
    val lengthKey = "locationId.error.locationAdditionalIdentifier.length"
    val invalidKey = "locationId.error.locationAdditionalIdentifier.invalid"
    val maxLength = 4

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    "must not bind invalid data" in {

      val invalidValues: Seq[String] = Seq(" ab1", "ab1 ")

      val expectedError = FormError(fieldName, invalidKey, Seq(additionalIdentifierRegex))

      invalidValues.foreach { invalidValue =>
        val result = form.bind(Map(fieldName -> invalidValue)).apply(fieldName)
        result.errors must contain(expectedError)
      }
    }
  }

  ".authorisationReferenceNumber" - {

    val fieldName = "authorisationReferenceNumber"
    val lengthKey = "locationId.error.authorisationReferenceNumber.length"
    val invalidKey = "locationId.error.authorisationReferenceNumber.invalid"
    val maxLength = 35

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    "must not bind invalid data" in {

      val invalidValues: Seq[String] = Seq(" abcd1", "abcd1 ")

      val expectedError = FormError(fieldName, invalidKey, Seq(authorisationNumberRegex))

      invalidValues.foreach { invalidValue =>
        val result = form.bind(Map(fieldName -> invalidValue)).apply(fieldName)
        result.errors must contain(expectedError)
      }
    }
  }

  "must accept a qualifier with optional fields missing" in {
    val result = form.bind(Map("locationType" -> "unlocode"))

    assert(result.value.contains(LocationDetails(LocationQualifier.UnLocode, None, None, None)))
  }

  "must accept a qualifier with optional fields blank" in {
    val result =
      form.bind(Map("locationType" -> "authnumber", "unlocode" -> "", "locationAdditionalIdentifier" -> "", "authorisationReferenceNumber" -> ""))

    assert(result.value.contains(LocationDetails(LocationQualifier.AuthorisationNumber, None, None, None)))
  }
}

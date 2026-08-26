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

import play.api.data.FormError
import uk.gov.hmrc.automatedexportsystemfrontend.forms.behaviours.{OptionFieldBehaviours, StringFieldBehaviours}
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.LocationIdFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.LocationQualifier

class LocationIdFormProviderSpec extends OptionFieldBehaviours, StringFieldBehaviours {

  val form = new LocationIdFormProvider()()

  ".locationType" - {

    val fieldName = "locationType"

    behave like optionsField[LocationQualifier](
      form,
      fieldName,
      validValues = LocationQualifier.values,
      invalidError = FormError(fieldName, "error.invalid")
    )
  }

  ".unlocode" - {

    val fieldName = "unlocode"
    val requiredKey = "locationId.error.unlocode.required"
    val lengthKey = "locationId.error.unlocode.length"
    val maxLength = 17

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  ".locationAdditionalIdentifier" - {

    val fieldName = "locationAdditionalIdentifier"
    val requiredKey = "locationId.error.locationAdditionalIdentifier.required"
    val lengthKey = "locationId.error.locationAdditionalIdentifier.length"
    val maxLength = 4

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  ".authorisationReferenceNumber" - {

    val fieldName = "authorisationReferenceNumber"
    val requiredKey = "locationId.error.authorisationReferenceNumber.required"
    val lengthKey = "locationId.error.authorisationReferenceNumber.length"
    val maxLength = 35

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }
}

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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.behaviours.StringFieldBehaviours
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyTransportFormProvider

class DiscrepancyTransportFormProviderSpec extends StringFieldBehaviours {

  val form = new DiscrepancyTransportFormProvider()()

  ".containerId" - {

    val fieldName = "containerId"
    val requiredKey = "discrepancyTransport.error.containerId.required"
    val lengthKey = "discrepancyTransport.error.containerId.length"
    val maxLength = 17

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  ".numberOfSeals" - {

    val fieldName = "numberOfSeals"
    val requiredKey = "discrepancyTransport.error.numberOfSeals.required"
    val minimum = 0
    val maximum = 99

    val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

    behave like fieldThatBindsValidData(form, fieldName, validDataGenerator)

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }
}

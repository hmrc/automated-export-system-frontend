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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.behaviours.OptionFieldBehaviours
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.LocationTypeFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.LocationType

class LocationTypeFormProviderSpec extends OptionFieldBehaviours {

  val form = new LocationTypeFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "locationType.error.required"

    behave like optionsField[LocationType](form, fieldName, validValues = LocationType.values, invalidError = FormError(fieldName, "error.invalid"))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }
}

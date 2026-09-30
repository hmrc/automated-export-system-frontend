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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.behaviours.BooleanFieldBehaviours

class AddAnotherPackagingDetailFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "addAnotherPackagingDetail.error.required"
  val invalidKey = "error.boolean"

  val form = new AddAnotherPackagingDetailFormProvider()

  ".value" - {

    val fieldName = "value"

    behave like booleanField(form(true), fieldName, invalidError = FormError(fieldName, invalidKey))

    behave like mandatoryField(form(true), fieldName, requiredError = FormError(fieldName, requiredKey))

    "must bind an empty field when condition is false" in {
      val result = form(false).bind(Map(fieldName -> ""))
      result.value.value mustBe false
      result.errors mustBe empty
    }
  }
}

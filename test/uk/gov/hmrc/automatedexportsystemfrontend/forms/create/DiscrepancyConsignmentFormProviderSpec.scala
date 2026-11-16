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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyConsignmentFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.codelists.CodeListEntry

import play.api.libs.json.Json

class DiscrepancyConsignmentFormProviderSpec extends OptionFieldBehaviours {

  private val entries = Seq(CodeListEntry("1", "Sea transport", Json.obj()), CodeListEntry("7", "Fixed transport installations", Json.obj()))

  val form = new DiscrepancyConsignmentFormProvider().forCodeList(entries)

  ".value" - {

    val fieldName = "value"
    val requiredKey = "discrepancyConsignment.error.required"

    behave like optionsField[String](
      form,
      fieldName,
      validValues = entries.map(_.key),
      invalidError = FormError(fieldName, "discrepancyConsignment.error.invalid")
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }
}

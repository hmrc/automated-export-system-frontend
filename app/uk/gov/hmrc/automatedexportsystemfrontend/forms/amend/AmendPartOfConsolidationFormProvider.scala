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

import uk.gov.hmrc.automatedexportsystemfrontend.forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}
import uk.gov.hmrc.automatedexportsystemfrontend.forms.Constants.{mucrMaxLength, mucrRegex}
import uk.gov.hmrc.automatedexportsystemfrontend.models.PartOfConsolidationAnswer

import javax.inject.Inject

class AmendPartOfConsolidationFormProvider @Inject() extends Mappings {

  def apply(): Form[PartOfConsolidationAnswer] =
    Form(
      mapping("boolean" -> boolean("partOfConsolidation.error.required"), "mucr" -> optional(text()))(PartOfConsolidationAnswer.apply)(answer =>
        Some((answer.boolean, answer.mucr))
      )
    )

  def validateAnswer(answer: PartOfConsolidationAnswer): Form[PartOfConsolidationAnswer] = {
    val form: Form[PartOfConsolidationAnswer] = apply().fill(answer)
    val mucr: String = answer.mucr.getOrElse("")

    if (!answer.boolean) {
      form
    } else if (mucr.trim.isEmpty) {
      form.withError("mucr", "partOfConsolidation.mucr.required")
    } else if (mucr.length > mucrMaxLength) {
      form.withError("mucr", "partOfConsolidation.mucr.length")
    } else if (!mucr.matches(mucrRegex)) {
      form.withError("mucr", "partOfConsolidation.mucr.invalid")
    } else {
      form
    }
  }

}

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

import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.data.FormError
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.PartOfConsolidationFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.PartOfConsolidationAnswer

class PartOfConsolidationFormProviderSpec extends AnyFreeSpec with Matchers {

  private val form = new PartOfConsolidationFormProvider()()

  "PartOfConsolidationFormProvider" - {

    "bind when false and no MUCR supplied" in {
      val result = form.bind(Map("boolean" -> "false"))

      result.value.value mustEqual
        PartOfConsolidationAnswer(false, None)
    }

    "bind when true and MUCR supplied" in {
      val result = form.bind(Map("boolean" -> "true", "mucr" -> "123456"))

      result.value.value mustEqual
        PartOfConsolidationAnswer(true, Some("123456"))
    }

    "return an error when no boolean selected" in {
      val result = form.bind(Map.empty)

      result.errors must contain(FormError("boolean", "partOfConsolidation.error.required"))
    }

    "allow no selected with MUCR omitted" in {
      val result = form.bind(Map("boolean" -> "false"))

      result.errors mustBe empty
    }
  }
}

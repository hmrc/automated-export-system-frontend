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

import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import uk.gov.hmrc.automatedexportsystemfrontend.forms.Constants.mrnRegex
import uk.gov.hmrc.automatedexportsystemfrontend.forms.amend.AmendEnterMrnFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.forms.behaviours.StringFieldBehaviours

class AmendEnterMrnFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "enterMrn.error.required"
  val lengthKey = "enterMrn.error.length"
  val invalidKey = "enterMrn.error.invalid"
  val maxLength = 18
  val validValues: Seq[String] = Seq("24AB123456789012A1", "99YZA1B2C3D4E5F6E9")

  val form = new AmendEnterMrnFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(form, fieldName, Gen.oneOf(validValues))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    "must not bind invalid data" in {

      val invalidValues: Seq[String] = Seq("23AB123456789012A1", "99YZA1B2C3D4E5F6F9")

      val expectedError = FormError(fieldName, invalidKey, Seq(mrnRegex))

      invalidValues.foreach { invalidValue =>
        val result: Field = form.bind(Map(fieldName -> invalidValue)).apply(fieldName)
        result.errors must contain(expectedError)
      }
    }
  }
}

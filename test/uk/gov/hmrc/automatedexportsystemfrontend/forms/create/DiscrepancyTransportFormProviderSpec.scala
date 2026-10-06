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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.behaviours.StringFieldBehaviours
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyTransportFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.ContainerDetails
import uk.gov.hmrc.automatedexportsystemfrontend.forms.Constants.{containerIdMaxLength, containerIdRegex, numberOfSealsMaxValue}

class DiscrepancyTransportFormProviderSpec extends StringFieldBehaviours {

  val form = new DiscrepancyTransportFormProvider()()

  ".containerId" - {

    val fieldName = "containerId"
    val lengthKey = "discrepancyTransport.error.containerId.length"
    val invalidKey = "discrepancyTransport.error.containerId.invalid"

    behave like fieldThatBindsValidData(form, fieldName, alphaNumStringsWithMaxLength(containerIdMaxLength))

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = containerIdMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(containerIdMaxLength))
    )

    "must bind a missing value as None" in {
      form.bind(Map("numberOfSeals" -> "1")).value mustBe
        Some(ContainerDetails(None, Some(1)))
    }

    "must bind an empty value as None" in {
      form.bind(Map("containerId" -> "", "numberOfSeals" -> "1")).value mustBe
        Some(ContainerDetails(None, Some(1)))
    }

    "must bind a valid value when number of seals is absent" in {
      form.bind(Map(fieldName -> "CONT123")).value mustBe
        Some(ContainerDetails(Some("CONT123"), None))
    }

    "must not bind invalid data" in {
      val invalidValues = Seq(" abc123", "abc123 ", "abc123 ")
      val expectedError = FormError(fieldName, invalidKey, Seq(containerIdRegex))

      invalidValues.foreach { invalidValue =>
        val result: Field = form.bind(Map(fieldName -> invalidValue)).apply(fieldName)

        result.errors must contain(expectedError)
      }
    }
  }

  ".numberOfSeals" - {

    val fieldName = "numberOfSeals"

    behave like fieldThatBindsValidData(form, fieldName, intsInRangeWithCommas(0, numberOfSealsMaxValue))

    "must bind a missing value as None" in {
      form.bind(Map("containerId" -> "CONT123")).value mustBe
        Some(ContainerDetails(Some("CONT123"), None))
    }

    "must bind an empty value as None" in {
      form.bind(Map("containerId" -> "CONT123", fieldName -> "")).value mustBe
        Some(ContainerDetails(Some("CONT123"), None))
    }

    "must bind a valid value when container ID is absent" in {
      form.bind(Map(fieldName -> "1")).value mustBe
        Some(ContainerDetails(None, Some(1)))
    }

    "must bind zero as Some(0)" in {
      form.bind(Map(fieldName -> "0")).value mustBe
        Some(ContainerDetails(None, Some(0)))
    }

    "must bind the maximum allowed value" in {
      form.bind(Map(fieldName -> numberOfSealsMaxValue.toString)).value mustBe
        Some(ContainerDetails(None, Some(numberOfSealsMaxValue)))
    }

    "must reject a negative value" in {
      val result = form.bind(Map(fieldName -> "-1")).apply(fieldName)

      result.errors must contain(FormError(fieldName, "discrepancyTransport.error.numberOfSeals.negative", Seq(0)))
    }

    "must reject a value above the maximum" in {
      val result =
        form.bind(Map(fieldName -> (numberOfSealsMaxValue + 1).toString)).apply(fieldName)

      result.errors must contain(FormError(fieldName, "discrepancyTransport.error.numberOfSeals.maximum", Seq(numberOfSealsMaxValue)))
    }

    "must reject a non-numeric value" in {
      val result = form.bind(Map(fieldName -> "abc")).apply(fieldName)

      result.errors must contain(FormError(fieldName, "error.nonNumeric"))
    }

    "must reject a decimal value" in {
      val result = form.bind(Map(fieldName -> "1.5")).apply(fieldName)

      result.errors must contain(FormError(fieldName, "error.wholeNumber"))
    }
  }

  "must bind both missing fields as None" in {
    form.bind(Map.empty[String, String]).value mustBe
      Some(ContainerDetails(None, None))
  }

  "must bind both empty fields as None" in {
    form.bind(Map("containerId" -> "", "numberOfSeals" -> "")).value mustBe
      Some(ContainerDetails(None, None))
  }

  "must bind both fields when valid values are entered" in {
    form.bind(Map("containerId" -> "CONT123", "numberOfSeals" -> "1")).value mustBe
      Some(ContainerDetails(Some("CONT123"), Some(1)))
  }
}

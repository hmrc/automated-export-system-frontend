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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.Constants.{
  documentTypeMaxLength,
  documentTypeRegex,
  referenceNumberMaxLength,
  referenceNumberRegex
}
import uk.gov.hmrc.automatedexportsystemfrontend.forms.behaviours.StringFieldBehaviours
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyTransportDocFormProvider

class DiscrepancyTransportDocFormProviderSpec extends StringFieldBehaviours {

  val form = new DiscrepancyTransportDocFormProvider()()

  ".documentType" - {

    val fieldName = "documentType"
    val requiredKey = "discrepancyTransportDoc.error.documentType.required"
    val lengthKey = "discrepancyTransportDoc.error.documentType.length"
    val invalidKey = "discrepancyTransportDoc.error.documentType.invalid"
    val maxLength = documentTypeMaxLength

    behave like fieldThatBindsValidData(form, fieldName, validDocumentTypes(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    "must not bind invalid data" in {

      val invalidValues: Seq[String] = Seq("000", "abc")

      val expectedError = FormError(fieldName, invalidKey, Seq(documentTypeRegex))

      invalidValues.foreach { invalidValue =>
        val result: Field = form.bind(Map(fieldName -> invalidValue)).apply(fieldName)
        result.errors must contain(expectedError)
      }
    }
  }

  ".referenceNumber" - {

    val fieldName = "referenceNumber"
    val requiredKey = "discrepancyTransportDoc.error.referenceNumber.required"
    val lengthKey = "discrepancyTransportDoc.error.referenceNumber.length"
    val invalidKey = "discrepancyTransportDoc.error.referenceNumber.invalid"
    val maxLength = referenceNumberMaxLength

    behave like fieldThatBindsValidData(form, fieldName, validDocumentTypes(maxLength))

    behave like fieldWithMaxLength(form, fieldName, maxLength = maxLength, lengthError = FormError(fieldName, lengthKey, Seq(maxLength)))

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    "must not bind invalid data" in {

      val invalidValues: Seq[String] = Seq("000 ", " 1234")

      val expectedError = FormError(fieldName, invalidKey, Seq(referenceNumberRegex))

      invalidValues.foreach { invalidValue =>
        val result: Field = form.bind(Map(fieldName -> invalidValue)).apply(fieldName)
        result.errors must contain(expectedError)
      }
    }

  }
}

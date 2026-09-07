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
import org.scalatest.matchers.should.Matchers.shouldBe
import uk.gov.hmrc.automatedexportsystemfrontend.forms.behaviours.StringFieldBehaviours
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyTransportDocFormProvider

class DiscrepancyTransportDocFormProviderSpec extends StringFieldBehaviours {

  val form = new DiscrepancyTransportDocFormProvider()()

  ".documentType" - {

    val fieldName = "documentType"
    "must bind valid values" in {
      form.bind(Map(fieldName -> "1")).errors shouldBe empty
      form.bind(Map(fieldName -> "1234")).errors shouldBe empty
    }

    "must bind empty value as None" in {
      form.bind(Map.empty).errors shouldBe empty
      form.bind(Map(fieldName -> "")).errors shouldBe empty
    }

    "must reject invalid values" in {
      form.bind(Map(fieldName -> "12345")).errors.head.message shouldBe
        "discrepancyTransportDoc.error.documentType.invalid"
    }
  }

  ".referenceNumber" - {

    val fieldName = "referenceNumber"
    "must bind valid values" in {
      form.bind(Map(fieldName -> "1")).errors shouldBe empty
      form.bind(Map(fieldName -> "1234")).errors shouldBe empty
    }

    "must bind empty value as None" in {
      form.bind(Map.empty).errors shouldBe empty
      form.bind(Map(fieldName -> "")).errors shouldBe empty
    }

    "must reject invalid values" in {
      form.bind(Map(fieldName -> "12345")).errors.head.message shouldBe
        "discrepancyTransportDoc.error.referenceNumber.invalid"
    }
  }
}

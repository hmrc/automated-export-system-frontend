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

package uk.gov.hmrc.automatedexportsystemfrontend.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SubmissionErrorMapperSpec extends AnyWordSpec with Matchers {

  "SubmissionErrorMapper" should {

    "return technical error when a technical error exists" in {

      val errors = Seq(
        SingleSubmissionError(code = "RULE_VIOLATION", description = None, path = None, originalValue = None),
        SingleSubmissionError(code = "INVALID_MRN", description = None, path = None, originalValue = None)
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe
        Seq(SubmissionErrorMessage("viewSubmission.error.technical"))
    }

    "remove duplicate errors" in {

      val errors = Seq(
        SingleSubmissionError(code = "INVALID_MRN", description = None, path = None, originalValue = None),
        SingleSubmissionError(code = "INVALID_MRN", description = None, path = None, originalValue = None)
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe
        Seq(SubmissionErrorMessage("viewSubmission.error.invalidMrn"))
    }

    "return multiple validation errors" in {

      val errors = Seq(
        SingleSubmissionError(code = "INVALID_MRN", description = None, path = None, originalValue = None),
        SingleSubmissionError(code = "UNKNOWN_MRN", description = None, path = None, originalValue = None)
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe
        Seq(SubmissionErrorMessage("viewSubmission.error.invalidMrn"), SubmissionErrorMessage("viewSubmission.error.unknownMrn"))
    }

    "ignore unknown errors" in {

      val errors = Seq(SingleSubmissionError(code = "SOME_NEW_CODE", description = None, path = None, originalValue = None))

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq.empty
    }

    "return technical error for new HMRC async technical error codes" in {

      val technicalCodes =
        Seq("INCORRECT_ENUMERATION", "NOT_SUPPORTED_IN_POSITION", "UNSPECIFIED_ERROR_OTHER", "TOO_MANY_REPETITIONS", "INVALID_XML_FORMAT")

      technicalCodes.foreach { code =>
        val errors = Seq(SingleSubmissionError(code = code, description = None, path = None, originalValue = None))

        SubmissionErrorMapper.toMessageKeys(errors) shouldBe
          Seq(SubmissionErrorMessage("viewSubmission.error.technical"))
      }
    }

    "return element too long error with its field label and maximum length" in {
      val errors = Seq(
        SingleSubmissionError(
          code = "ELEMENT_TOO_LONG",
          description = None,
          path = Some("Body.GoodsShipment.Consignment.LocationOfGoods.additionalIdentifier"),
          originalValue = None
        )
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq(
        SubmissionErrorMessage("viewSubmission.error.elementTooLong", Seq("Location of goods additional identifier", "4"))
      )
    }

    "return element too short error with its field label and minimum length" in {
      val errors = Seq(
        SingleSubmissionError(
          code = "ELEMENT_TOO_SHORT",
          description = None,
          path = Some("Body.GoodsShipment.Consignment.LocationOfGoods.additionalIdentifier"),
          originalValue = None
        )
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq(
        SubmissionErrorMessage("viewSubmission.error.elementTooShort", Seq("Location of goods additional identifier", "1"))
      )
    }

    "return missing field error with its field label" in {
      val errors = Seq(
        SingleSubmissionError(
          code = "MISSING_FIELD",
          description = None,
          path = Some("Body.GoodsShipment.Consignment.parentUCRID"),
          originalValue = None
        )
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq(SubmissionErrorMessage("viewSubmission.error.missingField", Seq("MUCR")))
    }

    "return invalid characters error with its field label" in {
      val errors = Seq(
        SingleSubmissionError(
          code = "INVALID_CHARACTERS",
          description = None,
          path = Some("Body.GoodsShipment.Consignment.parentUCRID"),
          originalValue = None
        )
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq(SubmissionErrorMessage("viewSubmission.error.invalidCharacters", Seq("MUCR")))
    }

    "return invalid specific type error with its field label" in {
      val errors = Seq(
        SingleSubmissionError(
          code = "INVALID_VALUE_SPECIFIC_TYPE",
          description = None,
          path = Some("Body.CustomsOfficeOfExitActual.referenceNumber"),
          originalValue = None
        )
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq(
        SubmissionErrorMessage("viewSubmission.error.invalidValueSpecificType", Seq("Reference number"))
      )
    }

    "return invalid value pattern error with its field label" in {
      val errors = Seq(
        SingleSubmissionError(
          code = "INVALID_VALUE_PATTERN",
          description = None,
          path = Some("Body.GoodsShipment.Consignment.TransportEquipment.numberOfSeals"),
          originalValue = None
        )
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq(
        SubmissionErrorMessage("viewSubmission.error.invalidValuePattern", Seq("Number of seals"))
      )
    }

    "return lower than minimum error with its field label and minimum value" in {
      val errors = Seq(
        SingleSubmissionError(
          code = "LOWER_THAN_MIN",
          description = None,
          path = Some("Body.GoodsItem.Commodity.GoodsMeasure.grossMass"),
          originalValue = None
        )
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq(SubmissionErrorMessage("viewSubmission.error.lowerThanMin", Seq("Gross mass", "0")))
    }

    "return greater than maximum error with its field label and maximum value" in {
      val errors = Seq(
        SingleSubmissionError(
          code = "GREATER_THAN_MAX",
          description = None,
          path = Some("Body.GoodsItem.Commodity.GoodsMeasure.netMass"),
          originalValue = None
        )
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq(
        SubmissionErrorMessage("viewSubmission.error.greaterThanMax", Seq("Net mass", "9999999999.999999"))
      )
    }

    "return equal or lower than minimum error with its field label and minimum value" in {
      val errors = Seq(
        SingleSubmissionError(
          code = "EQUAL_OR_LOWER_THAN_MIN",
          description = None,
          path = Some("Body.GoodsItem.Commodity.GoodsMeasure.grossMass"),
          originalValue = None
        )
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq(
        SubmissionErrorMessage("viewSubmission.error.equalOrLowerThanMin", Seq("Gross mass", "0"))
      )
    }

    "return equal or greater than maximum error with its field label and maximum value" in {
      val errors = Seq(
        SingleSubmissionError(
          code = "EQUAL_OR_GREATER_THAN_MAX",
          description = None,
          path = Some("Body.GoodsItem.Commodity.GoodsMeasure.netMass"),
          originalValue = None
        )
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq(
        SubmissionErrorMessage("viewSubmission.error.equalOrGreaterThanMax", Seq("Net mass", "9999999999.999999"))
      )
    }

    "ignore a field validation error when its path is missing" in {
      val errors = Seq(SingleSubmissionError(code = "ELEMENT_TOO_LONG", description = None, path = None, originalValue = None))

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq.empty
    }

    "ignore a field validation error when its path is not mapped" in {
      val errors =
        Seq(SingleSubmissionError(code = "ELEMENT_TOO_LONG", description = None, path = Some("Body.Unknown.unmappedField"), originalValue = None))

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq.empty
    }
  }
}

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

object SubmissionErrorMapper {

  private val technicalErrors = Set(
    "CODELIST_VIOLATION",
    "CONDITION_VIOLATION_MISSING",
    "RULE_VIOLATION",
    "CONDITION_VIOLATION_NOT_ALLOWED",
    "DUPLICATED_MESSAGE_ID",
    "ROLEBASED_AUTH_FAILED",
    "TRANSITIONAL_CONSTRAINT_VIOLATION",
    "EDI_VIOLATION_POST_DOWNGRADE",
    "FUNCTIONAL_VIOLATION_POST_DOWNGRADE",
    "MESSAGE_OUT_OF_SEQUENCE",
    "INCORRECT_ENUMERATION",
    "NOT_SUPPORTED_IN_POSITION",
    "UNSPECIFIED_ERROR_OTHER",
    "TOO_MANY_REPETITIONS",
    "INVALID_XML_FORMAT"
  )

  private val errorMappings: Map[String, String] = Map(
    "INVALID_FORMAT_DUCR" -> "viewSubmission.error.invalidFormatDucr",
    "MUCR_SHUT" -> "viewSubmission.error.mucrShut",
    "UNKNOWN_MRN" -> "viewSubmission.error.unknownMrn",
    "INVALID_MRN" -> "viewSubmission.error.invalidMrn",
    "INVALID_DISCREPANCIES" -> "viewSubmission.error.invalidDiscrepancies",
    "DIVERSION_REJECTED_INVALID_DECLARATION" -> "viewSubmission.error.diversionRejectedInvalidDeclaration",
    "DIVERSION_REJECTED_UNKNOWN_MRN" -> "viewSubmission.error.diversionRejectedUnknownMrn",
    "DIVERSION_REJECTED_ALREADY_EXITED" -> "viewSubmission.error.diversionRejectedAlreadyExited",
    "DIVERSION_REJECTED_OTHER" -> "viewSubmission.error.diversionRejectedOther"
  )

  def toMessageKeys(errors: Seq[SingleSubmissionError]): Seq[SubmissionErrorMessage] =
    if (errors.exists(error => technicalErrors.contains(error.code))) {
      Seq(SubmissionErrorMessage("viewSubmission.error.technical"))
    } else {
      errors.flatMap {
        case SingleSubmissionError("ELEMENT_TOO_LONG", _, Some(path), _) =>
          for {
            constraint <- ValidationErrorMapper.get(path)
            max <- constraint.maxLength
          } yield SubmissionErrorMessage("viewSubmission.error.elementTooLong", Seq(constraint.fieldLabel, max.toString))

        case SingleSubmissionError("ELEMENT_TOO_SHORT", _, Some(path), _) =>
          for {
            constraint <- ValidationErrorMapper.get(path)
            min <- constraint.minLength
          } yield SubmissionErrorMessage("viewSubmission.error.elementTooShort", Seq(constraint.fieldLabel, min.toString))

        case SingleSubmissionError("MISSING_FIELD", _, Some(path), _) =>
          ValidationErrorMapper.get(path).map { constraint =>
            SubmissionErrorMessage("viewSubmission.error.missingField", Seq(constraint.fieldLabel))
          }

        case SingleSubmissionError("INVALID_CHARACTERS", _, Some(path), _) =>
          ValidationErrorMapper.get(path).map { constraint =>
            SubmissionErrorMessage("viewSubmission.error.invalidCharacters", Seq(constraint.fieldLabel))
          }

        case SingleSubmissionError("INVALID_VALUE_SPECIFIC_TYPE", _, Some(path), _) =>
          ValidationErrorMapper.get(path).map { constraint =>
            SubmissionErrorMessage("viewSubmission.error.invalidValueSpecificType", Seq(constraint.fieldLabel))
          }

        case SingleSubmissionError("INVALID_VALUE_PATTERN", _, Some(path), _) =>
          ValidationErrorMapper.get(path).map { constraint =>
            SubmissionErrorMessage("viewSubmission.error.invalidValuePattern", Seq(constraint.fieldLabel))
          }

        case SingleSubmissionError("LOWER_THAN_MIN", _, Some(path), _) =>
          ValidationErrorMapper.get(path).map { constraint =>
            SubmissionErrorMessage("viewSubmission.error.lowerThanMin", Seq(constraint.fieldLabel))
          }

        case SingleSubmissionError("GREATER_THAN_MAX", _, Some(path), _) =>
          ValidationErrorMapper.get(path).map { constraint =>
            SubmissionErrorMessage("viewSubmission.error.greaterThanMax", Seq(constraint.fieldLabel))
          }

        case SingleSubmissionError("EQUAL_OR_LOWER_THAN_MIN", _, Some(path), _) =>
          ValidationErrorMapper.get(path).map { constraint =>
            SubmissionErrorMessage("viewSubmission.error.equalOrLowerThanMin", Seq(constraint.fieldLabel))
          }

        case SingleSubmissionError("EQUAL_OR_GREATER_THAN_MIN", _, Some(path), _) =>
          ValidationErrorMapper.get(path).map { constraint =>
            SubmissionErrorMessage("viewSubmission.error.equalOrGreaterThanMin", Seq(constraint.fieldLabel))
          }

        case error =>
          errorMappings
            .get(error.code)
            .map(SubmissionErrorMessage(_))
      }.distinct
    }
}

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

import play.api.libs.json.{Format, Json}

case class ViewSubmissionViewModel(
  submissionId: String,
  mrn: String,
  ducr: String,
  officeOfExit: String,
  submittedDate: String,
  status: SubmissionStatus,
  discrepancyConsignment: Option[String],
  locationType: Option[String],
  locationUnlocode: Option[String],
  locationAdditionalIdentifier: Option[String],
  locationAuthorisationReferenceNumber: Option[String],
  partOfConsolidation: Option[Boolean],
  mucr: Option[String],
  anyDiscrepancies: String,
  isSplitExit: String,
  errors: Seq[String] = Seq.empty,
  locationQualifier: Option[String] = None
) {
  def identifiedBy(implicit messages: play.api.i18n.Messages): String = {
    val identification = locationQualifier.map(_.trim.toLowerCase).map {
      case "u" | "unlocode" =>
        (messages("locationId.unlocode"), locationUnlocode)

      case "y" | "authorisationnumber" | "authnumber" =>
        (messages("locationId.authnumber"), locationAuthorisationReferenceNumber)

      case other =>
        (other, Option.empty[String])
    }
    identification.fold(locationAdditionalIdentifier.getOrElse("")) { case (label, reference) =>
      Seq(Some(label), reference.filter(_.nonEmpty), locationAdditionalIdentifier.filter(_.nonEmpty)).flatten.mkString(": ")
    }
  }
}
object ViewSubmissionViewModel {
  implicit val format: Format[ViewSubmissionViewModel] = Json.format[ViewSubmissionViewModel]
}

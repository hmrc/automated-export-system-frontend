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

case class ViewSubmissionsViewModel(summaries: Seq[SubmissionSummary])

case class SubmissionSummary(
  reference: String,
  mrn: String,
  ducr: String,
  officeOfExit: OfficeOfExit,
  submittedDate: String,
  submissionStatus: SubmissionStatus
)

case class SubmissionStatus(key: String, cssClass: String)

object ViewSubmissionViewModel {
  implicit val format: Format[ViewSubmissionsViewModel] = Json.format[ViewSubmissionsViewModel]
}

object SubmissionSummary {
  implicit val format: Format[SubmissionSummary] = Json.format[SubmissionSummary]
}

object SubmissionStatus {
  implicit val format: Format[SubmissionStatus] = Json.format[SubmissionStatus]
}

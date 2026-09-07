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

package uk.gov.hmrc.automatedexportsystemfrontend.views.submission.lookups

import uk.gov.hmrc.automatedexportsystemfrontend.models.{OfficeOfExit, SubmissionStatus}

object SubmissionLookups {
  def mapOfficeOfExit(code: String): OfficeOfExit =
    code match {
      case "GB000051" => OfficeOfExit.Belfast
      case "GB000142" => OfficeOfExit.Larne
      case "GB000244" => OfficeOfExit.Warrenpoint
      case "GB000411" => OfficeOfExit.Foyle
      case _          => throw new IllegalArgumentException(s"Unknown office of exit code: $code")
    }

  def mapStatus(status: Int): SubmissionStatus =
    status match {
      case 1 => SubmissionStatus("viewSubmissions.status.accepted", "govuk-tag--green")

      case 2 => SubmissionStatus("viewSubmissions.status.amended", "govuk-tag--yellow")

      case 3 => SubmissionStatus("viewSubmissions.status.cancelled", "govuk-tag--red")

      case 4 => SubmissionStatus("viewSubmissions.status.awaitingDecision", "govuk-tag--blue")

      case _ => throw new IllegalArgumentException(s"Unknown submission status: $status")
    }
}

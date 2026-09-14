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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.submission

import play.api.i18n.Messages
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Amend.{AmendLocationIdSummary, AmendLocationTypeSummary}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

class SingleSubmissionHelper {

  def authorisationNumberHandler(authorisationNumber: Option[String], submissionId: String)(implicit messages: Messages): Option[SummaryListRow] =
    authorisationNumber match {
      case Some(number) => AmendLocationIdSummary.authNumberRow(number, submissionId, false)
      case _            => None
    }

  def additionalIdHandler(additionalId: Option[String], submissionId: String)(implicit messages: Messages): Option[SummaryListRow] =
    additionalId match {
      case Some(number) => AmendLocationIdSummary.additionalIdRow(number, submissionId, false)
      case _            => None
    }

  def unloHandler(unlo: Option[String], submissionId: String)(implicit messages: Messages): Option[SummaryListRow] =
    unlo match {
      case Some(code) => AmendLocationIdSummary.unloRow(code, submissionId, false)
      case _          => None
    }
}

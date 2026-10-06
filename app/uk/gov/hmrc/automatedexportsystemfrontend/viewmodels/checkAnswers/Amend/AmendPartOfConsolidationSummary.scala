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

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Amend

import uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes as amendRoute
import play.api.i18n.Messages
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, PartOfConsolidationAnswer, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendPartOfConsolidationPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object AmendPartOfConsolidationSummary {
  def row(answers: UserAnswers)(submissionId: String)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(AmendPartOfConsolidationPage(submissionId)).map { answer =>
      build(answer.boolean, answer.mucr, submissionId, withChangeLink = true)
    }

  def row(referenceNumber: Option[String], submissionId: String, withChangeLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(build(referenceNumber.isDefined, referenceNumber, submissionId, withChangeLink))

  private def build(isPartOfConsolidation: Boolean, parentUcr: Option[String], submissionId: String, withChangeLink: Boolean)(
    implicit messages: Messages
  ): SummaryListRow = {

    val value =
      if (isPartOfConsolidation) {
        parentUcr match {
          case Some(mucr) => s"${messages("site.yes")} - ${messages("site.parOfConsolidation")}: $mucr"
          case None       => messages("site.yes")
        }
      } else {
        messages("site.no")
      }

    SummaryListRowViewModel(
      key = "partOfConsolidation.checkYourAnswersLabel",
      value = ValueViewModel(value),
      actions =
        if (withChangeLink)
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendPartOfConsolidationController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("partOfConsolidation.change.hidden"))
          )
        else Seq.empty
    )
  }
}

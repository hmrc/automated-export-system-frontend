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

import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes as amendRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendDiscrepancyTransportDocPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object AmendDiscrepancyTransportDocSummary {

  def rows(answers: UserAnswers)(submissionId: String)(implicit messages: Messages): Seq[Option[SummaryListRow]] = {
    val maybeAnswer = answers.get(AmendDiscrepancyTransportDocPage(submissionId))

    Seq(
      maybeAnswer.flatMap(_.documentType.map { documentType =>
        buildTransportType(documentType, submissionId, withAmendLink = true)
      }),
      maybeAnswer.flatMap(_.referenceNumber.map { referenceNumber =>
        buildDocReference(referenceNumber.toString, submissionId, withAmendLink = true)
      })
    )
  }

  def transportTypeRow(answers: UserAnswers)(submissionId: String)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(AmendDiscrepancyTransportDocPage(submissionId)).flatMap { answer =>
      answer.documentType.map { documentType =>
        buildTransportType(documentType, submissionId, withAmendLink = true)
      }
    }

  def transportTypeRow(answerFromXml: Int, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(buildTransportType(answerFromXml, submissionId, withAmendLink))

  def docReferenceRow(answers: UserAnswers)(submissionId: String)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(AmendDiscrepancyTransportDocPage(submissionId)).flatMap { answer =>
      answer.referenceNumber.map { referenceNumber =>
        buildDocReference(referenceNumber.toString, submissionId, withAmendLink = true)
      }
    }

  def docReferenceRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(buildDocReference(answerFromXml, submissionId, withAmendLink))

  private def buildTransportType(value: Int, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "discrepancyTransportDoc.documentType.checkYourAnswersLabel",
      value = ValueViewModel(HtmlContent(HtmlFormat.escape(value.toString))),
      actions =
        if (withAmendLink)
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyTransportDocController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyTransportDoc.documentType.change.hidden"))
          )
        else Seq.empty
    )

  private def buildDocReference(value: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "discrepancyTransportDoc.referenceNumber.checkYourAnswersLabel",
      value = ValueViewModel(HtmlContent(HtmlFormat.escape(value))),
      actions =
        if (withAmendLink)
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyTransportDocController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyTransportDoc.referenceNumber.change.hidden"))
          )
        else Seq.empty
    )
}

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
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendDiscrepancyTransportMeansPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object AmendDiscrepancyTransportMeansSummary {

  def rows(answers: UserAnswers)(submissionId: String)(implicit messages: Messages): Seq[Option[SummaryListRow]] = {
    val maybeAnswer = answers.get(AmendDiscrepancyTransportMeansPage(submissionId))

    Seq(
      maybeAnswer.flatMap(_.transportType.map { transportType =>
        buildTransportType(transportType, submissionId, withAmendLink = true)
      }),
      maybeAnswer.flatMap(_.transportIdNumber.map { transportIdNumber =>
        buildTransportId(transportIdNumber, submissionId, withAmendLink = true)
      }),
      maybeAnswer.flatMap(_.countryOfRegistration.map { countryOfRegistration =>
        buildCountryOfRegistration(countryOfRegistration, submissionId, withAmendLink = true)
      })
    )
  }

  def transportTypeRow(answers: UserAnswers)(submissionId: String)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(AmendDiscrepancyTransportMeansPage(submissionId)).flatMap { answer =>
      answer.transportType.map { transportType =>
        buildTransportType(transportType, submissionId, withAmendLink = true)
      }
    }

  def transportTypeRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(buildTransportType(answerFromXml, submissionId, withAmendLink))

  def transportIdRow(answers: UserAnswers)(submissionId: String)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(AmendDiscrepancyTransportMeansPage(submissionId)).flatMap { answer =>
      answer.transportIdNumber.map { transportIdNumber =>
        buildTransportId(transportIdNumber, submissionId, withAmendLink = true)
      }
    }

  def transportIdRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(buildTransportId(answerFromXml, submissionId, withAmendLink))

  def countryOfRegistrationRow(answers: UserAnswers)(submissionId: String)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(AmendDiscrepancyTransportMeansPage(submissionId)).flatMap { answer =>
      answer.countryOfRegistration.map { countryOfRegistration =>
        buildCountryOfRegistration(countryOfRegistration, submissionId, withAmendLink = true)
      }
    }

  def countryOfRegistrationRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(
    implicit messages: Messages
  ): Option[SummaryListRow] =
    Some(buildCountryOfRegistration(answerFromXml, submissionId, withAmendLink))

  private def buildTransportType(value: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "discrepancyTransportMeans.transportType.checkYourAnswersLabel",
      value = ValueViewModel(HtmlContent(HtmlFormat.escape(value))),
      actions =
        if (withAmendLink)
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyTransportMeansController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyTransportMeans.transportType.change.hidden"))
          )
        else Seq.empty
    )

  private def buildTransportId(value: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "discrepancyTransportMeans.transportIdNumber.checkYourAnswersLabel",
      value = ValueViewModel(HtmlContent(HtmlFormat.escape(value))),
      actions =
        if (withAmendLink)
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyTransportMeansController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyTransportMeans.transportIdNumber.change.hidden"))
          )
        else Seq.empty
    )

  private def buildCountryOfRegistration(value: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "discrepancyTransportMeans.countryOfRegistration.checkYourAnswersLabel",
      value = ValueViewModel(HtmlContent(HtmlFormat.escape(value))),
      actions =
        if (withAmendLink)
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyTransportMeansController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyTransportMeans.countryOfRegistration.change.hidden"))
          )
        else Seq.empty
    )
}

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

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Create

import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyTransportMeansPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object DiscrepancyTransportMeansSummary {

  def rows(answers: UserAnswers)(implicit messages: Messages): Option[Seq[SummaryListRow]] =
    answers.get(DiscrepancyTransportMeansPage).map { answer =>

      val transportType = HtmlFormat.escape(answer.transportType)
      val transportIdNumber = HtmlFormat.escape(answer.transportIdNumber)
      val countryOfRegistration = HtmlFormat.escape(answer.countryOfRegistration)

      Seq(
        SummaryListRowViewModel(
          key = "discrepancyTransportMeans.transportType.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent(transportType)),
          actions = Seq(
            ActionItemViewModel("site.change", createRoute.DiscrepancyTransportMeansController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("discrepancyTransportMeans.transportType.change.hidden"))
          )
        ),
        SummaryListRowViewModel(
          key = "discrepancyTransportMeans.transportIdNumber.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent(transportIdNumber)),
          actions = Seq(
            ActionItemViewModel("site.change", createRoute.DiscrepancyTransportMeansController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("discrepancyTransportMeans.transportIdNumber.change.hidden"))
          )
        ),
        SummaryListRowViewModel(
          key = "discrepancyTransportMeans.countryOfRegistration.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent(countryOfRegistration)),
          actions = Seq(
            ActionItemViewModel("site.change", createRoute.DiscrepancyTransportMeansController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("discrepancyTransportMeans.countryOfRegistration.change.hidden"))
          )
        )
      )
    }
}

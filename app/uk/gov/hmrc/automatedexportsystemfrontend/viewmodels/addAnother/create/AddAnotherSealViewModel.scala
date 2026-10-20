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

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.addAnother.create

import play.api.i18n.Messages
import uk.gov.hmrc.automatedexportsystemfrontend.models.{Mode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancySeals
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Empty, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, Key, SummaryListRow, Value}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}

case class AddAnotherSealViewModel(
  numberOfSeals: Int,
  remainingSeals: Int,
  allowMore: Boolean,
  addedSingularOrPlural: String,
  remainingSingularOrPlural: String,
  rows: Seq[SummaryListRow]
)

object AddAnotherSealViewModel {
  def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): AddAnotherSealViewModel = {
    val numberOfSeals: Int = DiscrepancySeals.count(userAnswers)
    val remainingSeals: Int = DiscrepancySeals.remaining(userAnswers)

    AddAnotherSealViewModel(
      numberOfSeals = numberOfSeals,
      remainingSeals = remainingSeals,
      allowMore = remainingSeals > 0,
      addedSingularOrPlural = if (numberOfSeals == 1) "singular" else "plural",
      remainingSingularOrPlural = if (remainingSeals == 1) "singular" else "plural",
      rows = rows(userAnswers, mode)
    )
  }

  def rows(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): Seq[SummaryListRow] =
    DiscrepancySeals.getAll(userAnswers).zipWithIndex.map { case (sealIdentifier, i) =>
      val index = i + 1
      SummaryListRow(
        key = Key(content = Text(sealIdentifier), classes = "govuk-!-font-weight-regular hmrc-summary-list__key"),
        value = Value(content = Empty, classes = "govuk-!-display-none"),
        actions = Some(
          Actions(items =
            Seq(
              ActionItem(
                href = createRoute.DiscrepancySealsController.onPageLoad(index, mode).url,
                content = Text(messages("site.change")),
                visuallyHiddenText = Some(messages("addAnotherSeal.change.hidden", index))
              ),
              ActionItem(
                href = createRoute.RemoveSealController.onPageLoad(index, mode).url,
                content = Text(messages("site.remove")),
                visuallyHiddenText = Some(messages("addAnotherSeal.remove.hidden", index))
              )
            )
          )
        )
      )
    }
}

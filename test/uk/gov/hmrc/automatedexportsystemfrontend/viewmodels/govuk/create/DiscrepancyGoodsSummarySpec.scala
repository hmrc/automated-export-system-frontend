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

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.create

import generators.Generators
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, UserAnswers, WhatHasChangedDetails}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyGoodsPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Create.DiscrepancyGoodsSummary
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class DiscrepancyGoodsSummarySpec extends AnyFreeSpec with Matchers with Generators {

  private implicit val messages: Messages = Helpers.stubMessages()

  "rows" - {
    "when answered, return all summary rows" in {
      val whatHasChangedDetails = WhatHasChangedDetails("goodsItemNumber", Some("2GB647298735290-S569"), "20", "10")
      val userAnswers = UserAnswers("id")
        .set(DiscrepancyGoodsPage, whatHasChangedDetails)
        .get

      DiscrepancyGoodsSummary.rows(userAnswers) shouldBe Some(
        Seq(
          SummaryListRowViewModel(
            key = "discrepancyGoods.goodsItemNumber.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("goodsItemNumber")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.DiscrepancyGoodsController.onPageLoad(CheckMode).url
              )
                .withVisuallyHiddenText("discrepancyGoods.goodsItemNumber.change.hidden")
            )
          ),
          SummaryListRowViewModel(
            key = "discrepancyGoods.ducr.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("2GB647298735290-S569")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.DiscrepancyGoodsController.onPageLoad(CheckMode).url
              )
                .withVisuallyHiddenText("discrepancyGoods.ducr.change.hidden")
            )
          ),
          SummaryListRowViewModel(
            key = "discrepancyGoods.newGrossMass.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("20")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.DiscrepancyGoodsController.onPageLoad(CheckMode).url
              )
                .withVisuallyHiddenText("discrepancyGoods.newGrossMass.change.hidden")
            )
          ),
          SummaryListRowViewModel(
            key = "discrepancyGoods.newNetMass.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("10")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.DiscrepancyGoodsController.onPageLoad(CheckMode).url
              )
                .withVisuallyHiddenText("discrepancyGoods.newNetMass.change.hidden")
            )
          )
        )
      )
    }

    "when answered, return summary rows excluding ducr" in {
      val whatHasChangedDetails = WhatHasChangedDetails("goodsItemNumber", None, "20", "10")
      val userAnswers = UserAnswers("id")
        .set(DiscrepancyGoodsPage, whatHasChangedDetails)
        .get

      DiscrepancyGoodsSummary.rows(userAnswers) shouldBe Some(
        Seq(
          SummaryListRowViewModel(
            key = "discrepancyGoods.goodsItemNumber.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("goodsItemNumber")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.DiscrepancyGoodsController.onPageLoad(CheckMode).url
              )
                .withVisuallyHiddenText("discrepancyGoods.goodsItemNumber.change.hidden")
            )
          ),
          SummaryListRowViewModel(
            key = "discrepancyGoods.newGrossMass.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("20")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.DiscrepancyGoodsController.onPageLoad(CheckMode).url
              )
                .withVisuallyHiddenText("discrepancyGoods.newGrossMass.change.hidden")
            )
          ),
          SummaryListRowViewModel(
            key = "discrepancyGoods.newNetMass.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("10")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.DiscrepancyGoodsController.onPageLoad(CheckMode).url
              )
                .withVisuallyHiddenText("discrepancyGoods.newNetMass.change.hidden")
            )
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      DiscrepancyGoodsSummary.rows(userAnswers) shouldBe None
    }
  }
}

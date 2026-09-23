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

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.amend

import generators.Generators
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, UserAnswers, WhatHasChangedDetails}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendDiscrepancyGoodsPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Amend.AmendDiscrepancyGoodsSummary
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class AmendDiscrepancyGoodsSummarySpec extends AnyFreeSpec with Matchers with Generators {

  private implicit val messages: Messages = Helpers.stubMessages()

  "rows" - {
    "when answered, return all summary rows with change links" in {
      val goodsItem = 1
      val ducr = "2GB647298735290-S569"
      val grossMass: BigDecimal = 20
      val netMass: BigDecimal = 10

      AmendDiscrepancyGoodsSummary.goodsItemNumberRow(goodsItem, "submissionId", true) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyGoods.goodsItemNumber.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("1")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendDiscrepancyGoodsController.onPageLoad(CheckMode, "submissionId").url
            )
              .withVisuallyHiddenText("discrepancyGoods.goodsItemNumber.change.hidden")
          )
        )
      )

      AmendDiscrepancyGoodsSummary.goodsItemDucrRow(ducr, "submissionId", true) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyGoods.ducr.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("2GB647298735290-S569")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendDiscrepancyGoodsController.onPageLoad(CheckMode, "submissionId").url
            )
              .withVisuallyHiddenText("discrepancyGoods.ducr.change.hidden")
          )
        )
      )

      AmendDiscrepancyGoodsSummary.grossMassRow(grossMass, "submissionId", true) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyGoods.newGrossMass.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("20")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendDiscrepancyGoodsController.onPageLoad(CheckMode, "submissionId").url
            )
              .withVisuallyHiddenText("discrepancyGoods.newGrossMass.change.hidden")
          )
        )
      )
      AmendDiscrepancyGoodsSummary.netMassRow(netMass, "submissionId", true) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyGoods.newNetMass.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("10")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendDiscrepancyGoodsController.onPageLoad(CheckMode, "submissionId").url
            )
              .withVisuallyHiddenText("discrepancyGoods.newNetMass.change.hidden")
          )
        )
      )
    }

    "when answered, return all summary rows without change links" in {
      val goodsItem = 1
      val ducr = "2GB647298735290-S569"
      val grossMass: BigDecimal = 20
      val netMass: BigDecimal = 10

      AmendDiscrepancyGoodsSummary.goodsItemNumberRow(goodsItem, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyGoods.goodsItemNumber.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("1")),
          actions = Seq.empty
        )
      )

      AmendDiscrepancyGoodsSummary.goodsItemDucrRow(ducr, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyGoods.ducr.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("2GB647298735290-S569")),
          actions = Seq.empty
        )
      )

      AmendDiscrepancyGoodsSummary.grossMassRow(grossMass, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyGoods.newGrossMass.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("20")),
          actions = Seq.empty
        )
      )
      AmendDiscrepancyGoodsSummary.netMassRow(netMass, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyGoods.newNetMass.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("10")),
          actions = Seq.empty
        )
      )
    }
  }
}

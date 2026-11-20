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

package uk.gov.hmrc.automatedexportsystemfrontend.queries

import play.api.libs.json.JsPath
import uk.gov.hmrc.automatedexportsystemfrontend.models.{UserAnswers, WhatHasChangedDetails}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyGoodsPage

import scala.util.Try

case object DiscrepancyGoods extends Gettable[List[WhatHasChangedDetails]] with Settable[List[WhatHasChangedDetails]] {
  override def path: JsPath = JsPath \ "standard" \ "discrepancyGoods"

  val maxDiscrepancyGoods: Int = 999

  def getAll(userAnswers: UserAnswers): List[WhatHasChangedDetails] = userAnswers.get(this).getOrElse(Nil)

  def count(userAnswers: UserAnswers): Int = getAll(userAnswers).size

  def remaining(userAnswers: UserAnswers): Int = math.max(0, maxDiscrepancyGoods - count(userAnswers))

  def exists(userAnswers: UserAnswers, goodsItemIndex: Int): Boolean = userAnswers.get(DiscrepancyGoodsPage(goodsItemIndex)).isDefined

  def nextIndex(userAnswers: UserAnswers): Int = count(userAnswers) + 1

  def removeOne(userAnswers: UserAnswers, goodsItemIndex: Int): Try[UserAnswers] =
    userAnswers.remove(DiscrepancyGoodsPage(goodsItemIndex))
}

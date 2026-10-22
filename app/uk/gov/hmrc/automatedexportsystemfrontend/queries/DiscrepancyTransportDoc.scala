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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{DocumentDetails, PackingDetails, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyTransportDocPage

import scala.util.Try

case object DiscrepancyTransportDoc extends Gettable[List[DocumentDetails]] with Settable[List[DocumentDetails]] {
  override def path: JsPath = JsPath \ "standard" \ "discrepancyPacking"

  val maxTransportDocuments: Int = 99

  def getAll(userAnswers: UserAnswers): List[DocumentDetails] = userAnswers.get(this).getOrElse(Nil)

  def count(userAnswers: UserAnswers): Int = getAll(userAnswers).size

  def remaining(userAnswers: UserAnswers): Int = math.max(0, maxTransportDocuments - count(userAnswers))

  def exists(userAnswers: UserAnswers, docIndex: Int): Boolean = userAnswers.get(DiscrepancyTransportDocPage(docIndex)).isDefined

  def nextIndex(userAnswers: UserAnswers): Int = count(userAnswers) + 1

  def removeOne(userAnswers: UserAnswers, docIndex: Int): Try[UserAnswers] =
    userAnswers.remove(DiscrepancyTransportDocPage(docIndex))
}

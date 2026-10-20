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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{ContainerDetails, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyTransportEquipmentPage

import scala.util.Try

case object DiscrepancyTransportEquipment extends Gettable[List[ContainerDetails]] with Settable[List[ContainerDetails]] {

  override def path: JsPath =
    JsPath \ "standard" \ "discrepancyTransportEquipment"

  val maxTransportEquipment: Int = 9999

  def getAll(userAnswers: UserAnswers): List[ContainerDetails] =
    userAnswers.get(this).getOrElse(Nil)

  def count(userAnswers: UserAnswers): Int =
    getAll(userAnswers).size

  def remaining(userAnswers: UserAnswers): Int =
    math.max(0, maxTransportEquipment - count(userAnswers))

  def exists(userAnswers: UserAnswers, transportEquipmentIndex: Int): Boolean =
    userAnswers
      .get(DiscrepancyTransportEquipmentPage(transportEquipmentIndex))
      .isDefined

  def nextIndex(userAnswers: UserAnswers): Int =
    count(userAnswers) + 1

  def removeOne(userAnswers: UserAnswers, transportEquipmentIndex: Int): Try[UserAnswers] =
    userAnswers.remove(DiscrepancyTransportEquipmentPage(transportEquipmentIndex))
}

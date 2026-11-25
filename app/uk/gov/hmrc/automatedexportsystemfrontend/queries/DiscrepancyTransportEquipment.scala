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

import play.api.libs.json.{JsObject, JsPath}
import uk.gov.hmrc.automatedexportsystemfrontend.models.{ContainerDetails, TransportEquipmentData, UserAnswers}

import scala.util.Try

case object DiscrepancyTransportEquipment extends Gettable[List[TransportEquipmentData]] with Settable[List[TransportEquipmentData]] {

  override def path: JsPath =
    JsPath \ "standard" \ "discrepancyTransportEquipment"

  val maxTransportEquipment: Int = 9999

  def getAll(userAnswers: UserAnswers): List[TransportEquipmentData] =
    userAnswers.get(this).getOrElse(Nil)

  def count(userAnswers: UserAnswers): Int =
    getAll(userAnswers).size

  def remaining(userAnswers: UserAnswers): Int =
    math.max(0, maxTransportEquipment - count(userAnswers))

  def exists(userAnswers: UserAnswers, transportEquipmentIndex: Int): Boolean =
    transportEquipmentIndex > 0 && transportEquipmentIndex <= getAll(userAnswers).size

  def nextIndex(userAnswers: UserAnswers): Int =
    count(userAnswers) + 1

  def removeOne(userAnswers: UserAnswers, transportEquipmentIndex: Int): Try[UserAnswers] = {
    val transportEquipmentToRemove = new Settable[JsObject] {
      override def path: JsPath = DiscrepancyTransportEquipment.path \ (transportEquipmentIndex - 1)
    }
    userAnswers.remove(transportEquipmentToRemove)
  }

  def getContainerDetails(userAnswers: UserAnswers): List[ContainerDetails] =
    getAll(userAnswers).flatMap(_.containerDetails)

  def countContainerDetails(userAnswers: UserAnswers): Int =
    getContainerDetails(userAnswers).size
}

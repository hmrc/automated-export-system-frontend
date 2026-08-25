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

package uk.gov.hmrc.automatedexportsystemfrontend.models

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

sealed trait LocationQualifier

object LocationQualifier extends Enumerable.Implicits {
  // Only UN/LOCODE and authorisation number are allowed for UK IE507
  case object UnLocode extends WithName("unlocode") with LocationQualifier
  case object AuthorisationNumber extends WithName("authnumber") with LocationQualifier

  val values: Seq[LocationQualifier] = Seq(UnLocode, AuthorisationNumber)

  def options(implicit messages: Messages): Seq[SelectItem] = values.zipWithIndex.map { case (value, index) =>
    SelectItem(text = messages(s"locationId.${value.toString}"), value = Some(value.toString))
  }

  implicit val enumerable: Enumerable[LocationQualifier] =
    Enumerable(values.map(v => v.toString -> v): _*)
}

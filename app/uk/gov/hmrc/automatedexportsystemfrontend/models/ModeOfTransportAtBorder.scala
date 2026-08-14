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
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait ModeOfTransportAtBorder

object ModeOfTransportAtBorder extends Enumerable.Implicits {

  case object Sea extends WithName("sea") with ModeOfTransportAtBorder
  case object Rail extends WithName("rail") with ModeOfTransportAtBorder
  case object Road extends WithName("road") with ModeOfTransportAtBorder
  case object Air extends WithName("air") with ModeOfTransportAtBorder

  val values: Seq[ModeOfTransportAtBorder] = Seq(Sea, Rail, Road, Air)

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map { case (value, index) =>
    RadioItem(content = Text(messages(s"discrepancyConsignment.${value.toString}")), value = Some(value.toString), id = Some(s"value_$index"))
  }

  implicit val enumerable: Enumerable[ModeOfTransportAtBorder] =
    Enumerable(values.map(v => v.toString -> v): _*)
}

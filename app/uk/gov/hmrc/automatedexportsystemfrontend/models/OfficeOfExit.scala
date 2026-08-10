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
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

sealed trait OfficeOfExit

object OfficeOfExit extends Enumerable.Implicits {

  // TODO Office codes have been hard coded here to pass scheme validaiton check in the backend. To update with codes when ready.
  case object Belfast extends WithName("GB000051") with OfficeOfExit
  case object Larne extends WithName("GB000142") with OfficeOfExit
  case object Warrenpoint extends WithName("GB000244") with OfficeOfExit
  case object Foyle extends WithName("GB000411") with OfficeOfExit

  val values: Seq[OfficeOfExit] = Seq(Belfast, Larne, Warrenpoint, Foyle)

  def options(implicit messages: Messages): Seq[SelectItem] =
    SelectItem(text = messages(s"officeOfExit.placeholder"), value = None, disabled = true, selected = false)
      +: values.zipWithIndex.map { case (value, index) =>
        SelectItem(text = messages(s"officeOfExit.${value.toString}"), value = Some(value.toString))
      }

  implicit val enumerable: Enumerable[OfficeOfExit] =
    Enumerable(values.map(v => v.toString -> v): _*)
}

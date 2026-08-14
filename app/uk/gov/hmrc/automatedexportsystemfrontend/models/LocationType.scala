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

sealed trait LocationType

object LocationType extends Enumerable.Implicits {

  case object DesignatedLocation extends WithName("designatedLocation") with LocationType
  case object AuthorisedPlace extends WithName("authorisedPlace") with LocationType
  case object ApprovedPlace extends WithName("approvedPlace") with LocationType
  case object Other extends WithName("other") with LocationType

  val values: Seq[LocationType] = Seq(DesignatedLocation, AuthorisedPlace, ApprovedPlace, Other)

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map { case (value, index) =>
    RadioItem(content = Text(messages(s"locationType.${value.toString}")), value = Some(value.toString), id = Some(s"value_$index"))
  }

  implicit val enumerable: Enumerable[LocationType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}

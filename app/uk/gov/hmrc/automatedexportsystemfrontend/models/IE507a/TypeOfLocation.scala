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

package uk.gov.hmrc.automatedexportsystemfrontend.models.IE507a

import play.api.libs.json.*
import uk.gov.hmrc.automatedexportsystemfrontend.models.LocationType
import uk.gov.hmrc.automatedexportsystemfrontend.xml.XmlWrites

import scala.xml.Text

// TODO fetch this mapping from CRDL rather than hardcoding
enum TypeOfLocation(val value: String) extends Enum[TypeOfLocation] {
  case DesignatedLocation extends TypeOfLocation("A")
  case AuthorisedPlace extends TypeOfLocation("B")
  case ApprovedPlace extends TypeOfLocation("C")
  case Other extends TypeOfLocation("D")
}

object TypeOfLocation {
  private val nameMap: Map[String, TypeOfLocation] = values.map(v => v.name() -> v).toMap

  def fromUserAnswers(typ: LocationType): TypeOfLocation =
    typ match {
      case LocationType.DesignatedLocation => TypeOfLocation.DesignatedLocation
      case LocationType.AuthorisedPlace    => TypeOfLocation.AuthorisedPlace
      case LocationType.ApprovedPlace      => TypeOfLocation.ApprovedPlace
      case LocationType.Other              => TypeOfLocation.Other
    }

  given reads: Reads[TypeOfLocation] = Reads.of[String].flatMapResult { value =>
    if (nameMap.contains(value)) JsSuccess(nameMap(value))
    else JsError(s"Invalid TypeOfLocation: $value")
  }

  given writes: Writes[TypeOfLocation] =
    Writes.of[String].contramap(_.value)

  given format: Format[TypeOfLocation] = Format(reads, writes)

  given xmlWrites: XmlWrites[TypeOfLocation] =
    XmlWrites.instance(t => Text(t.value))
}

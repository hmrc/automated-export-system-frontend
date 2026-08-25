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

import play.api.libs.json.{Format, JsError, JsSuccess, Reads, Writes}
import uk.gov.hmrc.automatedexportsystemfrontend.xml.XmlWrites

import scala.xml.Text

// TODO possibly fetch this mapping from CRDL rather than hardcoding
enum QualifierOfTheIdentification(val value: String) extends Enum[QualifierOfTheIdentification] {
  case PostalCode extends QualifierOfTheIdentification("T")
  case UnLocode extends QualifierOfTheIdentification("U")
  case CustomsOfficeIdentifier extends QualifierOfTheIdentification("V")
  case GpsCoordinates extends QualifierOfTheIdentification("W")
  case EoriNumber extends QualifierOfTheIdentification("X")
  case AuthorisationNumber extends QualifierOfTheIdentification("Y")
  case FreeText extends QualifierOfTheIdentification("Z")
}

object QualifierOfTheIdentification {
  private val nameMap: Map[String, QualifierOfTheIdentification] = values.map(v => v.name() -> v).toMap

  given reads: Reads[QualifierOfTheIdentification] = Reads.of[String].flatMapResult { value =>
    if (nameMap.contains(value)) JsSuccess(nameMap(value))
    else JsError(s"Invalid QualifierOfTheIdentification: $value")
  }

  given writes: Writes[QualifierOfTheIdentification] =
    Writes.of[String].contramap(_.value)

  given format: Format[QualifierOfTheIdentification] = Format(reads, writes)

  given xmlWrites: XmlWrites[QualifierOfTheIdentification] =
    XmlWrites.instance(t => Text(t.value))
}

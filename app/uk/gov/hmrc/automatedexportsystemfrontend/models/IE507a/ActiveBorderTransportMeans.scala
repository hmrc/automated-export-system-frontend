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

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.automatedexportsystemfrontend.xml.XmlWrites

case class ActiveBorderTransportMeans(typeOfIdentification: String, identificationNumber: String, nationality: String)

object ActiveBorderTransportMeans {
  given format: Format[ActiveBorderTransportMeans] = Json.format[ActiveBorderTransportMeans]

  given xmlWrites: XmlWrites[ActiveBorderTransportMeans] = XmlWrites.instance { a =>
    XmlWrites.elem(
      "ActiveBorderTransportMeans",
      XmlWrites.textElem("typeOfIdentification", a.typeOfIdentification),
      XmlWrites.textElem("identificationNumber", a.identificationNumber),
      XmlWrites.textElem("nationality", a.nationality)
    )
  }
}

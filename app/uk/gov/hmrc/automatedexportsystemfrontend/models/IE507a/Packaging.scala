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
import scala.xml.NodeSeq

case class Packaging(sequenceNumber: Int, typeOfPackages: Option[String], numberOfPackages: Option[String], shippingMarks: Option[String])

object Packaging {
  given format: Format[Packaging] = Json.format[Packaging]

  given xmlWrites: XmlWrites[Packaging] = XmlWrites.instance { packaging =>
    XmlWrites.elem(
      "Packaging",
      XmlWrites.textElem("sequenceNumber", packaging.sequenceNumber),
      packaging.typeOfPackages
        .map(XmlWrites.textElem("typeOfPackages", _))
        .getOrElse(NodeSeq.Empty),
      packaging.numberOfPackages
        .map(XmlWrites.textElem("numberOfPackages", _))
        .getOrElse(NodeSeq.Empty),
      packaging.shippingMarks
        .map(XmlWrites.textElem("shippingMarks", _))
        .getOrElse(NodeSeq.Empty)
    )
  }
}

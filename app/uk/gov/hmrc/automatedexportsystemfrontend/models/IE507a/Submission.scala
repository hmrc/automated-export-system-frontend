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

import play.api.libs.json.{Json, OFormat, Writes}
import uk.gov.hmrc.automatedexportsystemfrontend.models.IE507a.ExportOperationType
import uk.gov.hmrc.automatedexportsystemfrontend.xml.{XmlOps, XmlWrites}

case class Submission(submissionId: Option[String], exportOperation: ExportOperation, CustomsOfficeOfExitActual: CustomsOfficeOfExitActual)

object Submission {
  implicit val format: OFormat[Submission] = Json.format[Submission]

  implicit val xmlWrites: XmlWrites[Submission] =
    XmlWrites.instance { s =>
      XmlWrites.rootElem("Submission", XmlWrites.optElem("submissionId", s.submissionId), s.exportOperation.toXml, s.CustomsOfficeOfExitActual.toXml)
    }
}

case class ExportOperation(exportOperationType: ExportOperationType, mrn: String, discrepanciesExist: Boolean, splitIndicator: Boolean)

object ExportOperation {
  implicit val format: OFormat[ExportOperation] = Json.format[ExportOperation]

  implicit val xmlWrites: XmlWrites[ExportOperation] =
    XmlWrites.instance { op =>
      XmlWrites.elem(
        "ExportOperation",
        XmlWrites.elem("type", op.exportOperationType.toXml),
        XmlWrites.textElem("MRN", op.mrn),
        XmlWrites.textElem("discrepanciesExist", if (op.discrepanciesExist) "1" else "0"),
        XmlWrites.textElem("splitIndicator", if (op.splitIndicator) "1" else "0")
      )
    }
}

case class CustomsOfficeOfExitActual(referenceNumber: String)

object CustomsOfficeOfExitActual {
  implicit val format: OFormat[CustomsOfficeOfExitActual] = Json.format[CustomsOfficeOfExitActual]

  implicit val xmlWrites: XmlWrites[CustomsOfficeOfExitActual] =
    XmlWrites.instance { c =>
      XmlWrites.elem("CustomsOfficeOfExitActual", XmlWrites.textElem("referenceNumber", c.referenceNumber))
    }
}

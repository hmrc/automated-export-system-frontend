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

package uk.gov.hmrc.automatedexportsystemfrontend.pages.amend

import uk.gov.hmrc.automatedexportsystemfrontend.pages.QuestionPage
import play.api.libs.json.JsPath
import uk.gov.hmrc.automatedexportsystemfrontend.models.UserAnswers

import scala.util.Try

case class AmendAnyDiscrepanciesPage(submissionId: String) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "amend" \ submissionId \ toString

  override def toString: String = "amendAnyDiscrepancies"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(false) => // and split indicator //TODO
        userAnswers
          .remove(AmendDiscrepancyConsignmentPage(submissionId))
          .flatMap(_.remove(AmendDiscrepancyTransportPage(submissionId)))
          .flatMap(_.remove(AmendDiscrepancySealsPage(submissionId)))
          .flatMap(_.remove(AmendDiscrepancyReferencePage(submissionId)))
          .flatMap(_.remove(AmendDiscrepancyTransportMeansPage(submissionId)))
          .flatMap(_.remove(AmendDiscrepancyTransportDocPage(submissionId)))
          .flatMap(_.remove(AmendDiscrepancyGoodsPage(submissionId)))
          .flatMap(_.remove(AmendDiscrepancyPackingPage(submissionId)))
      case _ => super.cleanup(value, userAnswers)
    }
}

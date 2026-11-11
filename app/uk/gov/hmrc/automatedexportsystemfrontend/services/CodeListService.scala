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

package uk.gov.hmrc.automatedexportsystemfrontend.services

import uk.gov.hmrc.automatedexportsystemfrontend.connectors.CodeListConnector
import uk.gov.hmrc.automatedexportsystemfrontend.models.codelists.CodeListEntry
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CodeListService @Inject() (connector: CodeListConnector)(using ec: ExecutionContext) {

  def getTransportModes()(using HeaderCarrier): Future[Seq[CodeListEntry]] =
    connector.getTransportModes().map { entries =>
      require(entries.nonEmpty, "TransportMode codelist is empty")

      require(entries.map(_.key).distinct.size == entries.size, "TransportMode codelist contains duplicate codes")

      require(
        entries.forall(entry => entry.key.trim.nonEmpty && entry.value.trim.nonEmpty),
        "TransportMode codelist contains an empty code or description"
      )

      entries
    }

  def getTransportModeDescription(code: Option[String])(using HeaderCarrier): Future[Option[String]] =
    code match {
      case Some(value) =>
        getTransportModes().map(entries => CodeListService.description(value, entries))
      case None =>
        Future.successful(None)
    }
}

object CodeListService {

  def selectItems(entries: Seq[CodeListEntry], placeholder: String): Seq[SelectItem] =
    SelectItem(text = placeholder, value = Some("")) +: entries.map { entry =>
      SelectItem(text = entry.value, value = Some(entry.key))
    }

  def description(code: String, entries: Seq[CodeListEntry]): Option[String] =
    entries.find(_.key == code).map(_.value)
}

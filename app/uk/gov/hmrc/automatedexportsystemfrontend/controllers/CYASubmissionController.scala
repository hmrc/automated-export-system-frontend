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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers

import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.*
import uk.gov.hmrc.automatedexportsystemfrontend.models.UserAnswers
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.HappyPath.{EnterDucrSummary, EnterMrnSummary}
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.SummaryListViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.CYASubmissionView
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.Future
import viewmodels.checkAnswers.happyPath.{AnyDiscrepanciesSummary, EnterDucrSummary, EnterMrnSummary, IsSplitExitSummary, OfficeOfExitSummary, PartOfConsolidationSummary}
import views.html.CYASubmissionView

class CYASubmissionController @Inject() (
  override val messagesApi: MessagesApi,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: CYASubmissionView
) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (actionBuilder andThen getData).async { implicit request =>

    val userAnswers = request.userAnswers.get

    val rows = rowGenerator(userAnswers)

    val list = SummaryListViewModel(rows = rows.flatten)

    val page: play.twirl.api.HtmlFormat.Appendable = view(list)
    Future.successful(Ok(page))
  }

  private def rowGenerator(answers: UserAnswers)(implicit messages: Messages): Seq[Option[SummaryListRow]] =
    Seq(
      EnterMrnSummary.row(answers),
      EnterDucrSummary.row(answers),
      OfficeOfExitSummary.row(answers),
      PartOfConsolidationSummary.row(answers),
      IsSplitExitSummary.row(answers),
      AnyDiscrepanciesSummary.row(answers)
    )
}

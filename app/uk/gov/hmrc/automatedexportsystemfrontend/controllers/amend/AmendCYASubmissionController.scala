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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend

import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.*
import uk.gov.hmrc.automatedexportsystemfrontend.models.UserAnswers
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Amend.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.SummaryListViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.amend.AmendCYASubmissionView
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.Future

class AmendCYASubmissionController @Inject() (
  override val messagesApi: MessagesApi,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: AmendCYASubmissionView
) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (actionBuilder andThen getData andThen requireData).async { implicit request =>

    val userAnswers = request.userAnswers

    Future.successful(
      Ok(
        view(
          SummaryListViewModel(exportOperationRowsGenerator(userAnswers).flatten),
          SummaryListViewModel(consignmentRowsGenerator(userAnswers).flatten),
          SummaryListViewModel(customsOfficeExitRowGenerator(userAnswers).flatten),
          SummaryListViewModel(extraRowsGenerator(userAnswers).flatten)
        )
      )
    )
  }

  private def exportOperationRowsGenerator(answers: UserAnswers)(implicit messages: Messages): Seq[Option[SummaryListRow]] =
    Seq(AmendEnterMrnSummary.row(answers), AmendIsSplitExitSummary.row(answers))

  private def consignmentRowsGenerator(answers: UserAnswers)(implicit messages: Messages): Seq[Option[SummaryListRow]] =
    Seq(AmendEnterDucrSummary.row(answers), AmendPartOfConsolidationSummary.row(answers))

  private def customsOfficeExitRowGenerator(answers: UserAnswers)(implicit messages: Messages): Seq[Option[SummaryListRow]] =
    Seq(AmendOfficeOfExitSummary.row(answers))

  private def extraRowsGenerator(answers: UserAnswers)(implicit messages: Messages): Seq[Option[SummaryListRow]] =
    Seq(AmendAnyDiscrepanciesSummary.row(answers))

}

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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.submission

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.{AesAuthRequestActionBuilder, AesDataRequiredAction, AesDataRetrievalAction}
import uk.gov.hmrc.automatedexportsystemfrontend.models.OfficeOfExit.{Belfast, Foyle, Larne, Warrenpoint}
import uk.gov.hmrc.automatedexportsystemfrontend.models.{SubmissionStatus, SubmissionSummary, ViewSubmissionsViewModel}
import uk.gov.hmrc.automatedexportsystemfrontend.utils.DateTimeFormats
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.submission.ViewSubmissionsView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import java.time.LocalDate
import javax.inject.Inject

class ViewSubmissionsController @Inject() (
  override val messagesApi: MessagesApi,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  val controllerComponents: MessagesControllerComponents,
  view: ViewSubmissionsView
) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (actionBuilder andThen getData) { implicit request =>

    // TODO, populate this view model with submission retrieval from backend get call when ready, remove placeholder data
    val viewSubmissionsViewModel: Option[ViewSubmissionsViewModel] = Some(
      ViewSubmissionsViewModel(
        Seq(
          SubmissionSummary(
            "IE507A-2026-04817",
            "24GB1234567890AB1",
            "GB123456789000-CONSGT001",
            Belfast,
            LocalDate.of(2026, 3, 1).format(DateTimeFormats.shortDateFormat),
            SubmissionStatus("viewSubmissions.status.accepted", "govuk-tag--green")
          ),
          SubmissionSummary(
            "IE507A-2026-04818",
            "24GB1234567890AB2",
            "GB123456789000-CONSGT002",
            Foyle,
            LocalDate.of(2026, 4, 2).format(DateTimeFormats.shortDateFormat),
            SubmissionStatus("viewSubmissions.status.awaitingDecision", "govuk-tag--blue")
          ),
          SubmissionSummary(
            "IE507A-2026-04819",
            "24GB1234567890AB3",
            "GB123456789000-CONSGT003",
            Larne,
            LocalDate.of(2026, 5, 3).format(DateTimeFormats.shortDateFormat),
            SubmissionStatus("viewSubmissions.status.cancelled", "govuk-tag--red")
          ),
          SubmissionSummary(
            "IE507A-2026-04820",
            "24GB1234567890AB4",
            "GB123456789000-CONSGT004",
            Warrenpoint,
            LocalDate.of(2026, 6, 4).format(DateTimeFormats.shortDateFormat),
            SubmissionStatus("viewSubmissions.status.amended", "govuk-tag--yellow")
          )
        )
      )
    )

    Ok(view(viewSubmissionsViewModel))
  }
}

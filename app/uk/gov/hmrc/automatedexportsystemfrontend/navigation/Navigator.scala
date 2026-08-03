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

package uk.gov.hmrc.automatedexportsystemfrontend.navigation

import play.api.mvc.Call
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.happyPath.routes as happyRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.*
import uk.gov.hmrc.automatedexportsystemfrontend.pages.Page

import javax.inject.{Inject, Singleton}

@Singleton
class Navigator @Inject() () {

  val normalRoutes: Page => UserAnswers => Call =
    _ => _ => problemRoute.JourneyRecoveryController.onPageLoad()

  val checkRoutes: Page => UserAnswers => Call =
    _ => _ => problemRoute.JourneyRecoveryController.onPageLoad()

  private val checkRouteMap: Page => UserAnswers => Call = { case _ =>
    _ => happyRoute.CYASubmissionController.onPageLoad()
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}

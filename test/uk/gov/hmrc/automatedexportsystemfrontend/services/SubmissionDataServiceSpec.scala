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

import org.scalatest.freespec.AnyFreeSpec
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.OfficeOfExit
import uk.gov.hmrc.automatedexportsystemfrontend.pages.happyPath.{AnyDiscrepanciesPage, EnterMrnPage, IsSplitExitPage, OfficeOfExitPage}

import scala.language.postfixOps

class SubmissionDataServiceSpec extends SpecBase {

  "buildStandardSubmission" - {

    val service = new SubmissionDataService
    "must return an String of XML when all required answers present " in {

      val userAnswers = emptyUserAnswers
        .set(EnterMrnPage, "MRN")
        .get
        .set(AnyDiscrepanciesPage, false)
        .get
        .set(IsSplitExitPage, false)
        .get
        .set(OfficeOfExitPage, OfficeOfExit.Belfast)
        .get

      val result = service.buildStandardSubmission(userAnswers)

      result shouldBe an[Option[String]]
      result.value should include("<MRN>MRN</MRN>")
      result.value should include("<type>1</type>")
      result.value should include("<discrepanciesExist>0</discrepanciesExist>")
      result.value should include("<splitIndicator>0</splitIndicator>")
      result.value should include("<referenceNumber>GB000051</referenceNumber>")
    }

    "must return a None when all required answers not present" in {

      val userAnswers = emptyUserAnswers.set(EnterMrnPage, "MRN").get

      service.buildStandardSubmission(userAnswers) shouldBe None
    }
  }
}

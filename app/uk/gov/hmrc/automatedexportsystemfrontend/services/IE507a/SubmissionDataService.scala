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

package uk.gov.hmrc.automatedexportsystemfrontend.services.IE507a

import com.google.inject.Inject
import play.api.Logging
import uk.gov.hmrc.automatedexportsystemfrontend.models.IE507a.ExportOperationType.Standard
import uk.gov.hmrc.automatedexportsystemfrontend.models.IE507a.{CustomsOfficeOfExitActual, ExportOperation, Submission}
import uk.gov.hmrc.automatedexportsystemfrontend.models.UserAnswers
import uk.gov.hmrc.automatedexportsystemfrontend.pages.happyPath.{
  AnyDiscrepanciesPage,
  EnterDucrPage,
  EnterMrnPage,
  IsSplitExitPage,
  OfficeOfExitPage,
  PartOfConsolidationPage
}
import uk.gov.hmrc.automatedexportsystemfrontend.xml.XmlWrites
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.xml.NodeSeq

class SubmissionDataService @Inject() extends Logging {

  def buildStandardSubmission(userAnswers: UserAnswers): Option[NodeSeq] =
    collectUserAnswers(userAnswers).map { submission =>
      <Submission>
        {
        submission.submissionId
          .map(id => <SubmissionId>
          {id}
        </SubmissionId>)
          .getOrElse(NodeSeq.Empty)
      }<ExportOperation>
        <ExportOperationType>
          {submission.exportOperation.exportOperationType}
        </ExportOperationType>
        <MRN>
          {submission.exportOperation.mrn}
        </MRN>
        <DiscrepanciesExist>
          {submission.exportOperation.discrepanciesExist}
        </DiscrepanciesExist>
        <SplitIndicator>
          {submission.exportOperation.splitIndicator}
        </SplitIndicator>
      </ExportOperation>
        <CustomsOfficeOfExitActual>
          <ReferenceNumber>
            {submission.CustomsOfficeOfExitActual.referenceNumber}
          </ReferenceNumber>
        </CustomsOfficeOfExitActual>
      </Submission>
    }

  private def collectUserAnswers(userAnswers: UserAnswers): Option[Submission] =
    for {
      mrn <- userAnswers.get(EnterMrnPage)
      officeOfExit <- userAnswers.get(OfficeOfExitPage)
      discrepanciesExist <- userAnswers.get(AnyDiscrepanciesPage)
      splitIndicator <- userAnswers.get(IsSplitExitPage)
      referenceNumber <- userAnswers.get(OfficeOfExitPage)
    } yield Submission(None, ExportOperation(Standard, mrn, discrepanciesExist, splitIndicator), CustomsOfficeOfExitActual(referenceNumber.toString))

}

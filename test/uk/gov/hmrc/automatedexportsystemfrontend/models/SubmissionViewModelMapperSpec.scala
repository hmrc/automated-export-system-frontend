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

package uk.gov.hmrc.automatedexportsystemfrontend.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.automatedexportsystemfrontend.models.OfficeOfExit
import java.time.LocalDateTime
import java.util.UUID

class SubmissionViewModelMapperSpec extends AnyWordSpec with Matchers {

  "SubmissionViewModelMapper" should {

    "map a submission correctly" in {

      val submissionId =
        UUID.fromString("6fb33641-6dc7-4a4f-adef-06238c13a317")

      val response = SubmissionResponseList(
        Seq(
          SubmissionResponse(
            submissionId = submissionId,
            mrn = "26GB0000X6524786A9",
            ducr = Some("GB123456789012"),
            officeOfExitCode = "GB000051",
            updatedAt = LocalDateTime.parse("2026-08-03T00:00:00"),
            status = 1
          )
        )
      )

      val result = SubmissionViewModelMapper.toViewModel(response)

      result.summaries should have size 1

      val summary = result.summaries.head

      summary.reference shouldBe submissionId.toString
      summary.mrn shouldBe "26GB0000X6524786A9"
      summary.ducr shouldBe "GB123456789012"
      summary.officeOfExit shouldBe OfficeOfExit.Belfast
      summary.submittedDate should not be empty
      summary.submissionStatus shouldBe SubmissionStatus("viewSubmissions.status.accepted", "govuk-tag--green")
    }

    "map all supported office of exit codes correctly" in {

      val submissions = SubmissionResponseList(
        Seq(
          SubmissionResponse(UUID.randomUUID(), "26GB0000X6524786A9", None, "GB000051", LocalDateTime.of(2026, 8, 17, 10, 30), 1),
          SubmissionResponse(UUID.randomUUID(), "26GB0000X6524786A9", None, "GB000142", LocalDateTime.of(2026, 8, 17, 10, 30), 1),
          SubmissionResponse(UUID.randomUUID(), "26GB0000X6524786A9", None, "GB000244", LocalDateTime.of(2026, 8, 17, 10, 30), 1),
          SubmissionResponse(UUID.randomUUID(), "26GB0000X6524786A9", None, "GB000411", LocalDateTime.of(2026, 8, 17, 10, 30), 1)
        )
      )

      val result = SubmissionViewModelMapper.toViewModel(submissions)

      result.summaries.map(_.officeOfExit) shouldBe Seq(OfficeOfExit.Belfast, OfficeOfExit.Larne, OfficeOfExit.Warrenpoint, OfficeOfExit.Foyle)
    }

    "map all supported submission statuses correctly" in {

      val submissions = SubmissionResponseList(
        Seq(
          SubmissionResponse(UUID.randomUUID(), "A1B2C3D4E5F6G7H8I9J0", None, "GB000051", LocalDateTime.of(2026, 8, 17, 10, 30), 1),
          SubmissionResponse(UUID.randomUUID(), "A1B2C3D4E5F6G7H8I9J0", None, "GB000051", LocalDateTime.of(2026, 8, 17, 10, 30), 2),
          SubmissionResponse(UUID.randomUUID(), "A1B2C3D4E5F6G7H8I9J0", None, "GB000051", LocalDateTime.of(2026, 8, 17, 10, 30), 3),
          SubmissionResponse(UUID.randomUUID(), "A1B2C3D4E5F6G7H8I9J0", None, "GB000051", LocalDateTime.of(2026, 8, 17, 10, 30), 4)
        )
      )

      val result = SubmissionViewModelMapper.toViewModel(submissions)

      result.summaries.map(_.submissionStatus) shouldBe Seq(
        SubmissionStatus("viewSubmissions.status.accepted", "govuk-tag--green"),
        SubmissionStatus("viewSubmissions.status.amended", "govuk-tag--yellow"),
        SubmissionStatus("viewSubmissions.status.cancelled", "govuk-tag--red"),
        SubmissionStatus("viewSubmissions.status.awaitingDecision", "govuk-tag--blue")
      )
    }
  }
}

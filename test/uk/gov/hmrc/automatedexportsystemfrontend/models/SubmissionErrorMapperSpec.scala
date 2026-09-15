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

class SubmissionErrorMapperSpec extends AnyWordSpec with Matchers {

  "SubmissionErrorMapper" should {

    "return technical error when a technical error exists" in {

      val errors = Seq(
        SingleSubmissionError(code = "RULE_VIOLATION", description = None, path = None, originalValue = None),
        SingleSubmissionError(code = "INVALID_MRN", description = None, path = None, originalValue = None)
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq("viewSubmission.error.technical")
    }

    "remove duplicate errors" in {

      val errors = Seq(
        SingleSubmissionError(code = "INVALID_MRN", description = None, path = None, originalValue = None),
        SingleSubmissionError(code = "INVALID_MRN", description = None, path = None, originalValue = None)
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq("viewSubmission.error.invalidMrn")
    }

    "return multiple validation errors" in {

      val errors = Seq(
        SingleSubmissionError(code = "INVALID_MRN", description = None, path = None, originalValue = None),
        SingleSubmissionError(code = "UNKNOWN_MRN", description = None, path = None, originalValue = None)
      )

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq("viewSubmission.error.invalidMrn", "viewSubmission.error.unknownMrn")
    }

    "ignore unknown errors" in {

      val errors = Seq(SingleSubmissionError(code = "SOME_NEW_CODE", description = None, path = None, originalValue = None))

      SubmissionErrorMapper.toMessageKeys(errors) shouldBe Seq.empty
    }
  }
}

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

package uk.gov.hmrc.automatedexportsystemfrontend.utils

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.mockito.Mockito.{spy, when}

import java.util.UUID

class IdGeneratorSpec extends AnyFreeSpec with Matchers {

  "generateNoHyphen" - {

    "return the generated UUID without hyphens" in {
      val uuid = UUID.fromString("8f3c2a19-7d2b-4b74-a9f0-123456789012")

      val idGenerator = spy(new IdGeneratorImpl)

      when(idGenerator.generate).thenReturn(uuid)

      idGenerator.generateNoHyphen mustBe "8f3c2a197d2b4b74a9f0123456789012"
    }
    "return a 32-character value" in {
      val idGenerator = new IdGeneratorImpl

      idGenerator.generateNoHyphen.length mustBe 32
    }

    "return a value containing no hyphens" in {
      val idGenerator = new IdGeneratorImpl

      idGenerator.generateNoHyphen must not include "-"
    }
  }
}

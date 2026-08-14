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

package generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import uk.gov.hmrc.automatedexportsystemfrontend.models.{ContainerDetails, ModeOfTransportAtBorder, OfficeOfExit}

trait ModelGenerators {}

implicit lazy val arbitraryContainerDetails: Arbitrary[ContainerDetails] =
  Arbitrary {
    for {
      containerId <- arbitrary[String]
      numberOfSeals <- arbitrary[Int]
    } yield ContainerDetails(containerId, numberOfSeals)
  }

implicit lazy val arbitraryModeAtTheBorder: Arbitrary[ModeOfTransportAtBorder] =
  Arbitrary {
    Gen.oneOf(ModeOfTransportAtBorder.values.toSeq)
  }

implicit lazy val arbitraryOfficeOfExit: Arbitrary[OfficeOfExit] =
  Arbitrary {
    Gen.oneOf(OfficeOfExit.values.toSeq)
  }

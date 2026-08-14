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
import uk.gov.hmrc.automatedexportsystemfrontend.models.*

trait ModelGenerators {}

implicit lazy val arbitraryDiscrepancyPacking: Arbitrary[PackingDetails] =
  Arbitrary {
    for {
      packagingCode <- arbitrary[String]
      numberOfPackages <- arbitrary[String]
      shippingMarks <- arbitrary[String]
    } yield PackingDetails(packagingCode, numberOfPackages, shippingMarks)
  }

implicit lazy val arbitraryDiscrepancyGoods: Arbitrary[WhatHasChangedDetails] =
  Arbitrary {
    for {
      goodsItemNumber <- arbitrary[String]
      declarationUniqueConsignmentReference <- arbitrary[String]
      newGrossMass <- arbitrary[String]
      newNetMass <- arbitrary[String]
    } yield WhatHasChangedDetails(goodsItemNumber, declarationUniqueConsignmentReference, newGrossMass, newNetMass)
  }

implicit lazy val arbitraryDocumentDetails: Arbitrary[DocumentDetails] =
  Arbitrary {
    for {
      documentType <- arbitrary[String]
      referenceNumber <- arbitrary[String]
    } yield DocumentDetails(documentType, referenceNumber)
  }

implicit lazy val arbitraryTransportAcrossBorderDetails: Arbitrary[TransportAcrossBorderDetails] =
  Arbitrary {
    for {
      transportType <- arbitrary[String]
      transportIdNumber <- arbitrary[String]
      countryOfRegistration <- arbitrary[String]
    } yield TransportAcrossBorderDetails(transportType, transportIdNumber, countryOfRegistration)
  }

implicit lazy val arbitraryLocationDetails: Arbitrary[LocationDetails] =
  Arbitrary {
    for {
      locationType <- arbitrary[String]
      unlocode <- arbitrary[String]
      locationAdditionalIdentifier <- arbitrary[String]
      authorisationReferenceNumber <- arbitrary[String]
    } yield LocationDetails(locationType, unlocode, locationAdditionalIdentifier, authorisationReferenceNumber)
  }

implicit lazy val arbitraryLocationType: Arbitrary[LocationType] =
  Arbitrary {
    Gen.oneOf(LocationType.values.toSeq)
  }

implicit lazy val arbitraryContainerDetails: Arbitrary[ContainerDetails] =
  Arbitrary {
    for {
      containerId <- arbitrary[String]
      numberOfSeals <- arbitrary[Int]
    } yield ContainerDetails(containerId, numberOfSeals)
  }

implicit lazy val arbitraryModeOfTransportAtTheBorder: Arbitrary[ModeOfTransportAtBorder] =
  Arbitrary {
    Gen.oneOf(ModeOfTransportAtBorder.values.toSeq)
  }

implicit lazy val arbitraryOfficeOfExit: Arbitrary[OfficeOfExit] =
  Arbitrary {
    Gen.oneOf(OfficeOfExit.values.toSeq)
  }

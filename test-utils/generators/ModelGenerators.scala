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

trait ModelGenerators {
  // Lowercase alpha chars are not allowed in most identifiers
  private val alphaNumChar: Gen[Char] = Gen.oneOf(Gen.alphaUpperChar, Gen.numChar)

  val gbInventoryMucr: Gen[String] =
    for {
      inventoryPartSize <- Gen.choose(3, 4)
      inventoryPart <- Gen.stringOfN(inventoryPartSize, alphaNumChar)
      remainingChars = 31 // Max length 35 minus GB/ and hyphen
      idPartSize <- Gen.choose(5, remainingChars - inventoryPartSize)
      idPart <- Gen.stringOfN(idPartSize, alphaNumChar)
    } yield s"GB/$inventoryPart-$idPart"

  val gbEoriMucr: Gen[String] =
    for {
      eoriPartSize <- Gen.choose(9, 12)
      eoriPart <- Gen.stringOfN(eoriPartSize, alphaNumChar)
      remainingChars = 31 // Max length 35 minus GB/ and hyphen
      idPartSize <- Gen.choose(1, remainingChars - eoriPartSize)
      idPart <- Gen.stringOfN(idPartSize, alphaNumChar)
    } yield s"GB/$eoriPart-$idPart"

  val airMucr: Gen[String] =
    for {
      alphaNumPart <- Gen.stringOfN(3, alphaNumChar)
      numPart <- Gen.stringOfN(8, Gen.numChar)
    } yield s"A:$alphaNumPart$numPart"

  val courierMucr: Gen[String] =
    for {
      alphaPart <- Gen.stringOfN(3, Gen.alphaUpperChar)
      numSize <- Gen.choose(3, 30)
      alphaNumPart <- Gen.stringOfN(numSize, Gen.numChar)
    } yield s"C:$alphaPart$alphaNumPart"

  val mucrGen: Gen[String] =
    Gen.oneOf(gbInventoryMucr, gbEoriMucr, airMucr, courierMucr)

  val standardDucr: Gen[String] =
    for {
      lastYearDigit <- Gen.choose('0', '9')
      countryChars <- Gen.stringOfN(2, Gen.alphaUpperChar)
      eoriChars <- Gen.stringOfN(12, alphaNumChar)
      idPartSize <- Gen.choose(1, 18)
      idPart <- Gen.stringOfN(idPartSize, Gen.oneOf(alphaNumChar, Gen.const('-'), Gen.const('('), Gen.const(')')))
    } yield s"$lastYearDigit$countryChars$eoriChars-$idPart"

  val ducrGen: Gen[String] =
    Gen.oneOf(standardDucr, mucrGen)

  given arbitraryDiscrepancyPacking: Arbitrary[PackingDetails] =
    Arbitrary {
      for {
        packagingCode <- arbitrary[String]
        numberOfPackages <- arbitrary[String]
        shippingMarks <- arbitrary[String]
      } yield PackingDetails(packagingCode, numberOfPackages, shippingMarks)
    }

  given arbitraryDiscrepancyGoods: Arbitrary[WhatHasChangedDetails] =
    Arbitrary {
      for {
        goodsItemNumber <- arbitrary[String]
        declarationUniqueConsignmentReference <- arbitrary[Option[String]]
        newGrossMass <- arbitrary[String]
        newNetMass <- arbitrary[String]
      } yield WhatHasChangedDetails(goodsItemNumber, declarationUniqueConsignmentReference, newGrossMass, newNetMass)
    }

  given arbitraryDocumentDetails: Arbitrary[DocumentDetails] =
    Arbitrary {
      for {
        documentType <- arbitrary[String]
        referenceNumber <- arbitrary[String]
      } yield DocumentDetails(documentType, referenceNumber)
    }

  given arbitraryTransportAcrossBorderDetails: Arbitrary[TransportAcrossBorderDetails] =
    Arbitrary {
      for {
        transportType <- arbitrary[String]
        transportIdNumber <- arbitrary[String]
        countryOfRegistration <- arbitrary[String]
      } yield TransportAcrossBorderDetails(transportType, transportIdNumber, countryOfRegistration)
    }

  given arbitraryLocationDetails: Arbitrary[LocationDetails] =
    Arbitrary {
      for {
        locationType <- arbitrary[LocationQualifier]
        unlocode <- arbitrary[String]
        locationAdditionalIdentifier <- arbitrary[String]
        authorisationReferenceNumber <- arbitrary[String]
      } yield LocationDetails(locationType, unlocode, locationAdditionalIdentifier, authorisationReferenceNumber)
    }

  given arbitraryLocationType: Arbitrary[LocationType] =
    Arbitrary {
      Gen.oneOf(LocationType.values)
    }

  given arbitraryContainerDetails: Arbitrary[ContainerDetails] =
    Arbitrary {
      for {
        containerId <- arbitrary[String]
        numberOfSeals <- arbitrary[Int]
      } yield ContainerDetails(containerId, numberOfSeals)
    }

  given arbitraryModeOfTransportAtTheBorder: Arbitrary[ModeOfTransportAtBorder] =
    Arbitrary {
      Gen.oneOf(ModeOfTransportAtBorder.values)
    }

  given arbitraryOfficeOfExit: Arbitrary[OfficeOfExit] =
    Arbitrary {
      Gen.oneOf(OfficeOfExit.values)
    }

  given arbitraryLocationQualifier: Arbitrary[LocationQualifier] =
    Arbitrary {
      Gen.oneOf(LocationQualifier.values)
    }
}

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

package uk.gov.hmrc.automatedexportsystemfrontend.forms

object Constants {
  // UK_MRNType pattern
  lazy val mrnMaxLength = 18
  lazy val mrnRegex = "^([2][4-9]|[3-9][0-9])[A-Z]{2}[A-Z0-9]{12}[A-E][0-9]$"

  // UK_DUCRType pattern
  lazy val ducrMaxLength = 35
  lazy val ducrRegex =
    "^[0-9][A-Z][A-Z][0-9A-Z\\(\\)\\-/]{6,32}|GB/[0-9A-Z]{3,4}-[0-9A-Z]{5,28}|GB/[0-9A-Z]{9,12}-[0-9A-Z]{1,23}|A:[0-9A-Z]{3}[0-9]{8}|C:[A-Z]{3}[0-9A-Z]{3,30}$"

  // UK_ParentUCRType pattern
  lazy val mucrMaxLength = 35
  lazy val mucrRegex = "^GB/[0-9A-Z]{3,4}-[0-9A-Z]{5,29}|GB/[0-9A-Z]{9,12}-[0-9A-Z]{1,23}|A:[0-9A-Z]{3}[0-9]{8}|C:[A-Z]{3}[0-9A-Z]{3,30}$"

  // UK_ContainerIdentificationNumberType pattern
  lazy val containerIdMaxLength = 17
  lazy val containerIdRegex = "^[!-~][ -~]{1,15}[!-~]|[!-~]{1,2}$"

  // UK_AlphaNumeric_MAX20_NoSpaces pattern
  lazy val sealIdentifierMaxLength = 20
  lazy val sealIdentifierRegex = "^\\P{Z}(.{0,18}\\P{Z})?$"

  lazy val goodsItemNumberMaxValue = 9999
//  lazy val goodsItemNumberRegex = "^[A-Za-z0-9]{1,35}$"

  lazy val shippingMarksMaxLength = 512
  lazy val shippingMarksRegex = "^(?=.{1,512}$)\\P{Z}(.*\\P{Z})?$"

  // UK_AlphaNumeric35NoSpacesType pattern
  lazy val authorisationNumberMaxLength = 35
  lazy val authorisationNumberRegex = "^\\P{Z}(.{0,33}\\P{Z})?$"

  // UK_AlphaNumeric4NoSpacesType pattern
  lazy val additionalIdentifierMaxLength = 4
  lazy val additionalIdentifierRegex = "^\\P{Z}(.{0,2}\\P{Z})?$"

  // UK_NumericWithoutZero_4 pattern
  lazy val transportDocumentTypeRegex = "^[1-9][0-9]{0,3}$"
  lazy val transportDocumentReferenceNumberRegex = "^[1-9][0-9]{0,3}$"

  lazy val unlocodeMaxLength = 17

  lazy val grossMassRegex = "^(0|[1-9]\\d*)(\\.\\d{1,6})?$"
  lazy val netMassRegex = "^(0|[1-9]\\d*)(\\.\\d{1,6})?$"

  lazy val identificationNumberRegex = "^\\P{Z}(.{0,33}\\P{Z})?$"

  lazy val documentTypeRegex = "^[1-9][0-9]{0,3}$"
  lazy val documentTypeMaxLength = 4

  lazy val referenceNumberRegex = "^\\P{Z}(.{0,68}\\P{Z})?$"
  lazy val referenceNumberMaxLength = 70

  lazy val numberOfSealsMaxValue = 9999

  lazy val numberOfPackagesMaxValue = 99999999
}

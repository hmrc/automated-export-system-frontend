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
  lazy val mrnMaxLength = 18
  lazy val mrnRegex = "^([2][4-9]|[3-9][0-9])[A-Z]{2}[A-Z0-9]{12}[A-E][0-9]$"
  lazy val ducrMaxLength = 35
  lazy val ducrRegex = "^[A-Za-z0-9]{1,35}$"
  lazy val mucrMaxLength = 35
  lazy val mucrRegex = "^[A-Za-z0-9]{1,35}$"
  lazy val containerIdMaxLength = 17
  lazy val containerIdRegex = "^[!-~][ -~]{1,15}[!-~]|[!-~]{1,2}$"
  lazy val sealIdentifierMaxLength = 20
  lazy val sealIdentifierRegex = "^\\P{Z}(.{0,18}\\P{Z})?$"
  lazy val goodsItemNumberMaxLength = 35
  lazy val goodsItemNumberRegex = "^[A-Za-z0-9]{1,35}$"
  lazy val shippingMarksMaxLength = 512
  lazy val shippingMarksRegex = "^(?=.{1,512}$)\\P{Z}(.*\\P{Z})?$"
}

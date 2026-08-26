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

package uk.gov.hmrc.automatedexportsystemfrontend.forms.create

import play.api.data.Form
import play.api.data.Forms.*
import uk.gov.hmrc.automatedexportsystemfrontend.forms.mappings.Mappings
import uk.gov.hmrc.automatedexportsystemfrontend.models.LocationDetails

import javax.inject.Inject

class LocationIdFormProvider @Inject() extends Mappings {

  def apply(): Form[LocationDetails] = Form(
    mapping(
      "locationType" -> text("locationId.error.locationType.required")
        .verifying(maxLength(100, "locationId.error.locationType.length")),
      "unlocode" -> text("locationId.error.unlocode.required")
        .verifying(maxLength(100, "locationId.error.unlocode.length")),
      "locationAdditionalIdentifier" -> text("locationId.error.locationAdditionalIdentifier.required")
        .verifying(maxLength(100, "locationId.error.locationAdditionalIdentifier.length")),
      "authorisationReferenceNumber" -> text("locationId.error.authorisationReferenceNumber.required")
        .verifying(maxLength(100, "locationId.error.authorisationReferenceNumber.length"))
    )(LocationDetails.apply)(x => Some((x.locationType, x.unlocode, x.locationAdditionalIdentifier, x.authorisationReferenceNumber)))
  )
}

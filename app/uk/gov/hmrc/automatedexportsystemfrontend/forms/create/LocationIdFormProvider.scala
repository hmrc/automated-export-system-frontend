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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.Constants.*
import uk.gov.hmrc.automatedexportsystemfrontend.forms.mappings.Mappings
import uk.gov.hmrc.automatedexportsystemfrontend.models.{LocationDetails, LocationQualifier}

import javax.inject.Inject

class LocationIdFormProvider @Inject() extends Mappings {

  def apply(): Form[LocationDetails] = Form(
    mapping(
      "locationType" -> enumerable[LocationQualifier]("locationId.error.locationType.required"),
      "unlocode" -> text("locationId.error.unlocode.required")
        .verifying(maxLength(unlocodeMaxLength, "locationId.error.unlocode.length")),
      "locationAdditionalIdentifier" -> text("locationId.error.locationAdditionalIdentifier.required")
        .verifying(
          firstError(
            maxLength(additionalIdentifierMaxLength, "locationId.error.locationAdditionalIdentifier.length"),
            regexp(additionalIdentifierRegex, "locationId.error.locationAdditionalIdentifier.invalid")
          )
        ),
      "authorisationReferenceNumber" -> text("locationId.error.authorisationReferenceNumber.required")
        .verifying(
          firstError(
            maxLength(authorisationNumberMaxLength, "locationId.error.authorisationReferenceNumber.length"),
            regexp(authorisationNumberRegex, "locationId.error.authorisationReferenceNumber.invalid")
          )
        )
    )(LocationDetails.apply)(x => Some((x.locationType, x.unlocode, x.locationAdditionalIdentifier, x.authorisationReferenceNumber)))
  )
}

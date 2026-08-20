package uk.gov.hmrc.automatedexportsystemfrontend.forms

import javax.inject.Inject

import uk.gov.hmrc.automatedexportsystemfrontend.forms.mappings.Mappings
import play.api.data.Form

class $className$FormProvider @Inject() extends Mappings {

  def apply(): Form[Boolean] =
    Form(
      "value" -> boolean("$className;format="decap"$.error.required")
    )
}

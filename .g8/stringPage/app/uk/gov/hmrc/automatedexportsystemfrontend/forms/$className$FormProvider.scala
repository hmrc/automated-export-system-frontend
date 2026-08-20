package uk.gov.hmrc.automatedexportsystemfrontend.forms

import javax.inject.Inject

import uk.gov.hmrc.automatedexportsystemfrontend.forms.mappings.Mappings
import play.api.data.Form

class $className$FormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("$className;format="decap"$.error.required")
        .verifying(maxLength($maxLength$, "$className;format="decap"$.error.length"))
    )
}

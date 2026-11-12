package forms

import forms.behaviours.OptionFieldBehaviours
import uk.gov.hmrc.automatedexportsystemfrontend.models.WhatDoYouWantToDo
import play.api.data.FormError

class WhatDoYouWantToDoFormProviderSpec extends OptionFieldBehaviours {

  val form = new WhatDoYouWantToDoFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "whatDoYouWantToDo.error.required"

    behave like optionsField[WhatDoYouWantToDo](
      form,
      fieldName,
      validValues = WhatDoYouWantToDo.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }
}

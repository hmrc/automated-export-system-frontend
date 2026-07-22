package forms

import forms.behaviours.OptionFieldBehaviours
import forms.happyPath.OfficeOfExitFormProvider
import models.OfficeOfExit
import play.api.data.FormError

class OfficeOfExitFormProviderSpec extends OptionFieldBehaviours {

  val form = new OfficeOfExitFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "officeOfExit.error.required"

    behave like optionsField[OfficeOfExit](
      form,
      fieldName,
      validValues  = OfficeOfExit.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}

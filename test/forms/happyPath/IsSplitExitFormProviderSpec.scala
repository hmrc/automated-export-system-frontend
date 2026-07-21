package forms.happyPath

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class IsSplitExitFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "isSplitExit.error.required"
  val invalidKey = "error.boolean"

  val form = new IsSplitExitFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}

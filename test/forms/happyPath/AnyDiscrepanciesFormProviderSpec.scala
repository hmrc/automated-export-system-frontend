package forms.happyPath

import forms.behaviours.BooleanFieldBehaviours
import forms.happyPath.AnyDiscrepanciesFormProvider
import play.api.data.FormError

class AnyDiscrepanciesFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "anyDiscrepancies.error.required"
  val invalidKey = "error.boolean"

  val form = new AnyDiscrepanciesFormProvider()()

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

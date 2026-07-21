package forms.happyPath

import forms.behaviours.BooleanFieldBehaviours
import forms.happyPath.PartOfConsolidationFormProvider
import play.api.data.FormError

class PartOfConsolidationFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "partOfConsolidation.error.required"
  val invalidKey = "error.boolean"

  val form = new PartOfConsolidationFormProvider()()

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

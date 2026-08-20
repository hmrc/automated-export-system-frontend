package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers

import uk.gov.hmrc.automatedexportsystemfrontend.config.CurrencyFormatter.currencyFormat
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.routes
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.$className$Page
import play.api.i18n.Messages
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object $className$Summary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get($className$Page).map {
      answer =>

        SummaryListRowViewModel(
          key     = "$className;format="decap"$.checkYourAnswersLabel",
          value   = ValueViewModel(currencyFormat(answer)),
          actions = Seq(
            ActionItemViewModel("site.change", routes.$className$Controller.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("$className;format="decap"$.change.hidden"))
          )
        )
    }
}

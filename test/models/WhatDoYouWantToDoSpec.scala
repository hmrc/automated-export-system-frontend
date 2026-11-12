package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class WhatDoYouWantToDoSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "WhatDoYouWantToDo" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(WhatDoYouWantToDo.values.toSeq)

      forAll(gen) { whatDoYouWantToDo =>
        JsString(whatDoYouWantToDo.toString).validate[WhatDoYouWantToDo].asOpt.value shouldBe whatDoYouWantToDo
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!WhatDoYouWantToDo.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[WhatDoYouWantToDo] shouldBe JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(WhatDoYouWantToDo.values.toSeq)

      forAll(gen) { whatDoYouWantToDo =>
        Json.toJson(whatDoYouWantToDo) shouldBe JsString(whatDoYouWantToDo.toString)
      }
    }
  }
}

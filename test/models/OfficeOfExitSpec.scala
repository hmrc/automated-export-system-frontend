package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class OfficeOfExitSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "OfficeOfExit" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(OfficeOfExit.values.toSeq)

      forAll(gen) {
        officeOfExit =>

          JsString(officeOfExit.toString).validate[OfficeOfExit].asOpt.value mustEqual officeOfExit
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!OfficeOfExit.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[OfficeOfExit] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(OfficeOfExit.values.toSeq)

      forAll(gen) {
        officeOfExit =>

          Json.toJson(officeOfExit) mustEqual JsString(officeOfExit.toString)
      }
    }
  }
}

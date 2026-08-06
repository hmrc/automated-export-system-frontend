import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import uk.gov.hmrc.automatedexportsystemfrontend.xml.{XmlOps, XmlWrites}

import scala.xml.*

class XmlOpsSpec extends AnyFreeSpec with Matchers {

  "rootElem" - {

    "must create an element with the aes namespace and prefix" in {
      val result = XmlWrites.rootElem("Submission")

      result.prefix mustBe "aes"
      result.label mustBe "Submission"
      result.scope.getURI("aes") mustBe "http://ecs.dgtaxud.ec"
    }

    "must include child elements" in {
      val result =
        XmlWrites.rootElem("Submission", XmlWrites.textElem("foo", "bar"))

      (result \ "foo").text mustBe "bar"
    }
  }

  "elem" - {

    "must create an element without a namespace prefix" in {
      val result = XmlWrites.elem("ExportOperation")

      result.prefix mustBe null
      result.label mustBe "ExportOperation"
    }

    "must include child elements" in {
      val result =
        XmlWrites.elem("Parent", XmlWrites.textElem("Child", "value"))

      (result \ "Child").text mustBe "value"
    }
  }

  "textElem" - {

    "must create an element containing text" in {
      val result = XmlWrites.textElem("MRN", "12345")

      result.label mustBe "MRN"
      result.text mustBe "12345"
    }

    "must support non string values" in {
      val result = XmlWrites.textElem("type", 123)

      result.text mustBe "123"
    }
  }

  "optElem" - {

    "must create an element when the option contains a value" in {
      val result = XmlWrites.optElem("submissionId", Some("abc"))

      result.text mustBe "abc"
    }

    "must return NodeSeq.Empty when the option is empty" in {
      val result = XmlWrites.optElem("submissionId", None)

      result mustBe NodeSeq.Empty
    }
  }

  "optionWrites" - {

    implicit val stringXmlWrites: XmlWrites[String] =
      XmlWrites.instance(s => XmlWrites.textElem("value", s))

    "must write the value when defined" in {
      val result = implicitly[XmlWrites[Option[String]]]
        .writes(Some("abc"))

      result.text mustBe "abc"
    }

    "must return NodeSeq.Empty when None" in {
      val result = implicitly[XmlWrites[Option[String]]]
        .writes(None)

      result mustBe NodeSeq.Empty
    }
  }

  "XmlOps.toXml" - {

    case class Test(value: String)

    implicit val testXmlWrites: XmlWrites[Test] =
      XmlWrites.instance(t => XmlWrites.textElem("value", t.value))

    "must delegate to the implicit XmlWrites instance" in {
      val result = Test("hello").toXml

      result.text mustBe "hello"
    }
  }
}

/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.addAnother.create

import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.Pagination

class PaginationBuilderSpec extends SpecBase {

  private def build(current: Int, total: Int): Option[Pagination] =
    PaginationBuilder.pagination(current, total, href)

  private def href(n: Int): String = s"/page/$n"

  private implicit val messages: Messages = Helpers.stubMessages()

  private def layout(p: Pagination): Seq[String] =
    p.items.value.map { item =>
      if (item.ellipsis.contains(true)) "…"
      else if (item.current.contains(true)) s"[${item.number.value}]"
      else item.number.value
    }

  "PaginationBuilder" - {

    "totalPages" - {
      Seq(0 -> 1, 1 -> 1, 25 -> 1, 26 -> 2, 50 -> 2, 51 -> 3, 999 -> 40).foreach { case (count, expected) =>
        s"should be $expected for $count items" in {
          PaginationBuilder.totalPages(count) shouldBe expected
        }
      }
    }

    "firstItem" - {
      Seq(1 -> 1, 2 -> 26, 3 -> 51).foreach { case (page, expected) =>
        s"should be $expected on page $page" in {
          PaginationBuilder.firstItem(page) shouldBe expected
        }
      }
    }

    "lastItem" - {
      Seq((1, 60, 25), (2, 60, 50), (3, 60, 60), (1, 10, 10), (2, 26, 26)).foreach { case (page, count, expected) =>
        s"should be $expected on page $page of $count items" in {
          PaginationBuilder.lastItem(page, count) shouldBe expected
        }
      }
    }

    "slice" - {
      val items = (1 to 60).toList

      "should return the first 25 on page 1" in {
        PaginationBuilder.slice(items, 1) shouldBe (1 to 25).toList
      }

      "should return items 26 to 50 on page 2 " in {
        PaginationBuilder.slice(items, 2) shouldBe (26 to 50).toList
      }

      "should return the partial final page" in {
        PaginationBuilder.slice(items, 3) shouldBe (51 to 60).toList
      }

      "should return empty when the page is past the end " in {
        PaginationBuilder.slice(items, 4) shouldBe empty
      }

      "should return empty for page 0 and negative pages" in {
        PaginationBuilder.slice(items, 0) shouldBe empty
        PaginationBuilder.slice(items, -1) shouldBe empty
      }

      "should return empty for an empty list" in {
        PaginationBuilder.slice(List.empty[Int], 1) shouldBe empty
      }

      "should keep absolute indexes when zipped before slicing" in {
        val sliced = PaginationBuilder.slice((1 to 30).toList.zipWithIndex, 2)
        sliced.size shouldBe 5
        sliced.head shouldBe (26 -> 25)
        sliced.last shouldBe (30 -> 29)
      }
    }

    "pagination" - {

      "should be None when there is one page or fewer" in {
        build(1, 1) shouldBe None
        build(1, 0) shouldBe None
      }

      "for 4 pages or fewer (neighbours only)" - {
        Seq(
          (1, 2, Seq("[1]", "2")),
          (2, 2, Seq("1", "[2]")),
          (1, 3, Seq("[1]", "2", "3")),
          (2, 3, Seq("1", "[2]", "3")),
          (3, 3, Seq("1", "2", "[3]")),
          (1, 4, Seq("[1]", "2", "3", "4")),
          (2, 4, Seq("1", "[2]", "3", "4")),
          (3, 4, Seq("1", "2", "[3]", "4")),
          (4, 4, Seq("1", "2", "3", "[4]"))
        ).foreach { case (current, total, expected) =>
          s"should render $expected for page $current of $total" in {
            layout(build(current, total).value) shouldBe expected
          }
        }
      }
    }

    "should collapse with ellipses when there are more than 4 pages" in {
      Seq(
        (1, 5, Seq("[1]", "2", "…", "5")),
        (3, 5, Seq("1", "2", "[3]", "4", "5")),
        (5, 5, Seq("1", "…", "4", "[5]")),
        (4, 8, Seq("1", "2", "3", "[4]", "5", "…", "8")),
        (8, 8, Seq("1", "…", "7", "[8]")),
        (20, 40, Seq("1", "…", "19", "[20]", "21", "…", "40"))
      ).foreach { case (current, total, expected) =>
        withClue(s"page $current of $total: ")(layout(build(current, total).value) shouldBe expected)
      }
    }

    "should be None for a single page" in {
      PaginationBuilder.pagination(1, 1, href) shouldBe None
    }

    "should only include Previous and Next where they apply" in {
      val first = PaginationBuilder.pagination(1, 5, href).value
      first.previous shouldBe None
      first.next.value.href shouldBe "/page/2"
      first.next.value.text.value shouldBe "site.pagination.next"

      val last = PaginationBuilder.pagination(5, 5, href).value
      last.next shouldBe None
      last.previous.value.href shouldBe "/page/4"
      last.previous.value.text.value shouldBe "site.pagination.previous"
    }
  }
}

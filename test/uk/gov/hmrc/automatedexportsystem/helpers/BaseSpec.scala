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

package uk.gov.hmrc.automatedexportsystem.helpers

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import org.scalatest.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.{HeaderNames, Status}
import play.api.i18n.DefaultLangs
import play.api.test.{DefaultAwaitTimeout, FutureAwaits, ResultExtractors}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.language.LanguageUtils

import scala.concurrent.ExecutionContext

trait BaseSpec
    extends AnyWordSpec with OptionValues with Inside with Matchers with EitherValues with BeforeAndAfterEach
    // with FakeGmsAuth
    with DefaultAwaitTimeout with FutureAwaits with Status with HeaderNames with ResultExtractors {}
// with AllMocks
// with ViewInstances

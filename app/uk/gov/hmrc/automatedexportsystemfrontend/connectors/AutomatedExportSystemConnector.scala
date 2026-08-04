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

package uk.gov.hmrc.automatedexportsystemfrontend.connectors

import com.google.inject.*
import org.apache.pekko.Done
import play.api.Logging
import play.api.http.Status.ACCEPTED
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}
import play.api.libs.ws.writeableOf_String
import uk.gov.hmrc.automatedexportsystemfrontend.config.FrontendAppConfig

import scala.concurrent.{ExecutionContext, Future}
import scala.xml.NodeSeq

@Singleton
class AutomatedExportSystemConnector @Inject() (frontendAppConfig: FrontendAppConfig, httpClient: HttpClientV2)(implicit ec: ExecutionContext)
    extends Logging {

  def submitIE507a(submission: NodeSeq)(implicit hc: HeaderCarrier): Future[Done] =
    httpClient
      .post(url"${frontendAppConfig.automatedExportSystemApi}/message")
      .withBody(submission.toString)
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case ACCEPTED =>
            Future.successful(Done)
          case _ =>
            logger.error(s"Failed to submit IE507a to /automated-export-system/message with status : ${response.status}")
            Future.failed(UpstreamErrorResponse("Unexpected response from /automated-export-system/message", response.status))
        }
      }
}

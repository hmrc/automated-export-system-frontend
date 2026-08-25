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

package uk.gov.hmrc.automatedexportsystemfrontend.xml

import scala.xml._

trait XmlWrites[T] {
  def writes(value: T): NodeSeq
}

object XmlWrites {
  def apply[T](implicit w: XmlWrites[T]): XmlWrites[T] = w
  def instance[T](f: T => NodeSeq): XmlWrites[T] = (value: T) => f(value)

  private val aesNamespace =
    new NamespaceBinding("aes", "http://ecs.dgtaxud.ec", TopScope)

  def rootElem(tag: String, children: NodeSeq*): Elem =
    Elem("aes", tag, Null, aesNamespace, minimizeEmpty = true, children.flatten: _*)

  def elem(tag: String, children: NodeSeq*): Elem =
    Elem(null, tag, Null, TopScope, minimizeEmpty = true, children.flatten: _*)

  def textElem[A](tag: String, value: A): Elem =
    elem(tag, Text(value.toString))

  def optElem[A](tag: String, value: Option[A]): NodeSeq =
    value.fold(NodeSeq.Empty: NodeSeq)(v => textElem(tag, v))

  implicit def optionWrites[T](implicit w: XmlWrites[T]): XmlWrites[Option[T]] =
    instance(_.fold(NodeSeq.Empty: NodeSeq)(w.writes))

  implicit def listWrites[T](implicit w: XmlWrites[T]): XmlWrites[List[T]] =
    instance(_.flatMap(w.writes))
}

implicit class XmlOps[T](private val value: T) extends AnyVal {
  def toXml(implicit w: XmlWrites[T]): NodeSeq = w.writes(value)
}

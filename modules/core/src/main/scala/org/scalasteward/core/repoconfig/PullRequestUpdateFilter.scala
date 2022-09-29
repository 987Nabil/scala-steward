/*
 * Copyright 2018-2022 Scala Steward contributors
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

package org.scalasteward.core.repoconfig

import cats.Eq
import cats.syntax.all._
import io.circe._
import io.circe.syntax._
import org.scalasteward.core.data.SemVer

final case class PullRequestUpdateFilter private (
    group: Option[String] = None,
    artifact: Option[String] = None,
    version: Option[SemVer.Change] = None
)

object PullRequestUpdateFilter {
  def apply(
      group: Option[String] = None,
      artifact: Option[String] = None,
      version: Option[SemVer.Change] = None
  ): Either[String, PullRequestUpdateFilter] =
    if (group.isEmpty && artifact.isEmpty && version.isEmpty)
      Left("At least one predicate should be added to the filter")
    else Right(new PullRequestUpdateFilter(group, artifact, version))

  implicit val pullRequestUpdateDecoder: Decoder[PullRequestUpdateFilter] = { cursor =>
    for {
      group <- cursor.get[Option[String]]("group")
      artifact <- cursor.get[Option[String]]("artifact")
      version <- cursor.get[Option[SemVer.Change]]("version")
      filter <- apply(group, artifact, version).leftMap(DecodingFailure(_, Nil))
    } yield filter
  }

  implicit val pullRequestUpdateFilterEq: Eq[PullRequestUpdateFilter] =
    Eq.fromUniversalEquals

  implicit val pullRequestUpdateFilterEncoder: Encoder[PullRequestUpdateFilter] = filter =>
    Json.obj(
      "group" := filter.group,
      "artifact" := filter.artifact,
      "version" := filter.version
    )

}

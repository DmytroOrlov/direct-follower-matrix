package mining

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import izumi.distage.model.definition.ModuleDef
import izumi.distage.testkit.TestConfig
import izumi.distage.testkit.scalatest.DistageBIOEnvSpecScalatest
import mining.models.{DirectFollowerMatrix, Event}
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import zio._

class MiningTest extends DistageBIOEnvSpecScalatest[ZIO] with OptionValues with EitherValues with TypeCheckedTripleEquals with Matchers {
  override def config: TestConfig = super.config.copy(
    moduleOverrides = new ModuleDef {
    }
  )

  "CsvReader" must {
    "readEvents" in {
      for {
        fmt <- ZIO.service[DateTimeFormatter]
        eventStream <- CsvReader.>.readEvents
        _ <- zio.IO.unit
        _ <- IO {
          def parse(d: String) = ZonedDateTime.from(fmt.parse(d))

          eventStream must contain theSameElementsAs Seq(
            Event("trace_0", "Incident logging", parse("2016-01-04T12:09:44+01:00[Europe/Paris]"), parse("2016-01-04T12:09:44+01:00[Europe/Paris]"), ""),
            Event("trace_0", "Resolution and recovery", parse("2016-01-05T03:56:44+01:00[Europe/Paris]"), parse("2016-01-05T04:30:44+01:00[Europe/Paris]"), ""),
            Event("trace_0", "Incident closure", parse("2016-01-05T04:31:44+01:00[Europe/Paris]"), parse("2016-01-05T04:48:44+01:00[Europe/Paris]"), ""),
            Event("trace_1", "Incident logging", parse("2016-01-04T12:26:44+01:00[Europe/Paris]"), parse("2016-01-04T12:35:44+01:00[Europe/Paris]"), ""),
            Event("trace_1", "Incident closure", parse("2016-01-04T17:51:44+01:00[Europe/Paris]"), parse("2016-01-04T17:59:44+01:00[Europe/Paris]"), ""),
          )
        }
      } yield ()
    }
  }
  "DirectFollowerExtraction" must {
    "extract DirectFollowerMatrix" in {
      for {
        fmt <- ZIO.service[DateTimeFormatter]
        DirectFollowerMatrix(header, value) <- DirectFollowerExtraction.>.extract
        _ <- IO {
          def parse(d: String) = ZonedDateTime.from(fmt.parse(d))

          header must contain theSameElementsAs Seq(
            "Incident logging",
            "Resolution and recovery",
            "Incident closure",
          )

          value must contain theSameElementsAs Seq(
            ("Incident logging", "Resolution and recovery") -> Vector((parse("2016-01-04T12:09:44+01:00[Europe/Paris]"), parse("2016-01-05T04:48:44+01:00[Europe/Paris]"))),
            ("Resolution and recovery", "Incident closure") -> Vector((parse("2016-01-04T12:09:44+01:00[Europe/Paris]"), parse("2016-01-05T04:48:44+01:00[Europe/Paris]"))),
            ("Incident logging", "Incident closure") -> Vector((parse("2016-01-04T12:26:44+01:00[Europe/Paris]"), parse("2016-01-04T17:59:44+01:00[Europe/Paris]"))),
          )
        }
      } yield ()
    }
  }
}

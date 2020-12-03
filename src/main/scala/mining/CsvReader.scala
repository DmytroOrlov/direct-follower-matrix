package mining

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId}

import com.github.tototoshi.csv.CSVReader
import distage.Id
import mining.models.Event
import zio.macros.accessible
import zio.{IO, Task, ZIO}

import scala.io.Source.fromResource

@accessible
trait CsvReader {
  def readEvents: Task[Stream[Event]]
}

object CsvReader {
  /* zio.macros.accessible adds:
  object > {
    def readEvents = ZIO.accessM[CsvReader](_.readEvents)
  }
 */
  def make(
      csv: String@Id("csv"),
      fmt: DateTimeFormatter@Id("csv"),
  ) = for {
    zone <- ZIO.service[ZoneId].toManaged_
    // Get the log file from the resources
    reader <- IO(CSVReader.open(fromResource(csv))).toManaged(r => IO(r.close()).ignore)

    /////////////////////////////
    /// YOUR WORK STARTS HERE ///
    /////////////////////////////
    stringsStreamWithHeader = reader.toStream
    res <- IO {
      val stringsStream = stringsStreamWithHeader.tail
      stringsStream.map {
        case id :: act :: start :: end :: clazz :: _ =>
          Event(
            id,
            act,
            LocalDateTime.parse(start, fmt).atZone(zone),
            LocalDateTime.parse(end, fmt).atZone(zone),
            clazz,
          )
      }
    }.toManaged_
  } yield new CsvReader {
    def readEvents = IO.succeed {
      res
    }
  }
}

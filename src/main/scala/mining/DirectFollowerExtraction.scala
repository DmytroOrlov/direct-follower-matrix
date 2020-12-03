package mining

import mining.models.{DirectFollowerMatrix, Trace}
import zio._
import zio.macros.accessible

@accessible
trait DirectFollowerExtraction {
  def extract: Task[DirectFollowerMatrix]
}

object DirectFollowerExtraction {
  /* zio.macros.accessible adds:
  object > {
    def extract = ZIO.accessM[DirectFollowerExtraction](_.extract)
  }
 */
  val make =
    for {
      eventStream <- CsvReader.>.readEvents
      traces <- IO {
        eventStream.groupBy(_.traceId).map {
          case (id, events) =>
            val traceEvents = events.sortBy(_.start).toIndexedSeq
            Trace(
              id,
              traceEvents,
              start = traceEvents.map(_.start).min,
              end = traceEvents.map(_.end).max,
            )
        }
      }
      directFollowerMatrix <- IO {
        val res = traces.flatMap { t =>
          val directFollowers =
            t.events
              .sliding(2)
              .filter(_.length == 2)

          directFollowers.map { es =>
            (es.head.activity, es(1).activity) -> (t.start, t.end)
          }
        }
          .toIndexedSeq
          .groupMap {
            case (activities, _) => activities
          } {
            case (_, range) => range
          }
        val header = res.keySet.flatMap {
          case (s, e) => Set(s, e)
        }
        DirectFollowerMatrix(header.toSeq, res.withDefaultValue(Nil))
      }
    } yield new DirectFollowerExtraction {
      def extract = IO.succeed {
        directFollowerMatrix
      }
    }
}

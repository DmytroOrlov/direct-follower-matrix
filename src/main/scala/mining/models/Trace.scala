package mining.models

import java.time.ZonedDateTime

case class Trace(
    traceId: String,
    events: Seq[Event],
    start: ZonedDateTime,
    end: ZonedDateTime,
)

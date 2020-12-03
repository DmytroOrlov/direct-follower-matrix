package mining.models

import java.time.ZonedDateTime

case class Event(
    traceId: String,
    activity: String,
    start: ZonedDateTime,
    end: ZonedDateTime,
    clazz: String,
)

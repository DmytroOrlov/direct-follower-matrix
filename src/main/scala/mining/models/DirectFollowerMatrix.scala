package mining.models

import java.time.ZonedDateTime

import cats.Show
import mining.models.DirectFollowerMatrix._

/**
 *
 * @param value value represents activities to show, traces to count/filter
 *              (prevActivity -> nextActivity) -> Seq(trace.start -> trace.end)
 */
case class DirectFollowerMatrix(header: Seq[String], value: Map[(PrevActivity, NextActivity), Seq[(TraceStart, TraceEnd)]]) {
  def filter(start: ZonedDateTime, end: ZonedDateTime): DirectFollowerMatrix =
    DirectFollowerMatrix(
      header,
      value
        .view
        .mapValues(_.filter {
          case (traceStart, traceEnd) =>
            !traceStart.isBefore(start) && !traceStart.isAfter(end) &&
              !traceEnd.isBefore(start) && !traceEnd.isAfter(end)
        })
        .toMap
        .withDefaultValue(Nil)
    )
}

object DirectFollowerMatrix {
  type PrevActivity = String
  type NextActivity = String
  type TraceStart = ZonedDateTime
  type TraceEnd = ZonedDateTime

  implicit val `Show[DirectFollowers]`: Show[DirectFollowerMatrix] =
    Show.show {
      case DirectFollowerMatrix(header, value) =>
        val nameColumn = List.fill(28)(" ").mkString
        val topHeader = header.map(s => f"$s%-28s").mkString("", "", "\n")
        val tableContent =
          header.map { row =>
            val rowName = f"$row%-28s"
            val rowValues =
              header.map { col =>
                val n = value(row -> col).length
                f"$n%-28s"
              }.mkString

            rowName + rowValues
          }.mkString("\n")

        nameColumn + topHeader + tableContent
    }
}

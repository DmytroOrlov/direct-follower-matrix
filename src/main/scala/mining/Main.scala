package mining

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import cats.syntax.show._
import distage.config.ConfigModuleDef
import distage.{Tag, _}
import izumi.distage.plugins.PluginConfig
import izumi.distage.plugins.load.PluginLoader
import zio._
import zio.console.putStrLn

/**
 * Extracts the direct follower matrix from a log.
 * Parses each line of the log file as an `Event` and builds the `Trace`s from the events.
 * Then counts all direct follower relations and prints them to the command line.
 */
object Main extends App {
  def run(args: List[String]) = {
    val program = for {
      fmt <- ZIO.service[DateTimeFormatter]
      directFollowers <- DirectFollowerExtraction.>.extract
      _ <-
        if (args.isEmpty) putStrLn(directFollowers.show)
        else IO {
          (ZonedDateTime.from(fmt.parse(args(0))),
            ZonedDateTime.from(fmt.parse(args(1))))
        }.flatMap { case (start, end) =>
          putStrLn(directFollowers.filter(start, end).show)
        }
    } yield ()

    def provideHas[R: HasConstructor, A: Tag](fn: R => A): ProviderMagnet[A] =
      HasConstructor[R].map(fn)

    val definition = new ModuleDef with ConfigModuleDef {
      make[Task[Unit]].from(provideHas(program.provide))
    }

    val pluginConfig = PluginConfig.cached(
      packagesEnabled = Seq(
        "mining",
      )
    )
    val appModules = PluginLoader().load(pluginConfig)

    Injector()
      .produceGetF[Task, Task[Unit]]((appModules :+ definition).merge)
      .useEffect
      .catchAll(e => putStrLn(s"$e").as(ExitCode.failure))
      .as(ExitCode.success)
  }
}

case class AppCfg(
    zone: String,
    csvFormat: String
)

package mining

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME

import com.typesafe.config.ConfigFactory
import distage.config.ConfigModuleDef
import izumi.distage.config.AppConfigModule
import izumi.distage.plugins.PluginDef
import zio.console.Console

object MiningPlugin extends PluginDef with ConfigModuleDef {
  include(AppConfigModule(ConfigFactory.defaultApplication()))

  makeConfig[AppCfg]("app")
  make[String]
    .named("csv")
    .fromValue("IncidentExample.csv")
  make[DateTimeFormatter]
    .named("csv")
    .from { c: AppCfg => DateTimeFormatter.ofPattern(c.csvFormat) }
  make[DateTimeFormatter]
    .fromValue(ISO_ZONED_DATE_TIME)
  make[ZoneId].from { c: AppCfg => ZoneId.of(c.zone) }

  make[CsvReader].fromHas(CsvReader.make _)
  make[DirectFollowerExtraction].fromHas(DirectFollowerExtraction.make)

  make[Console.Service].fromHas(Console.live)
}

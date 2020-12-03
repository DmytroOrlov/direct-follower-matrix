package mining.fixtures

import izumi.distage.effect.modules.ZIODIEffectModule
import izumi.distage.model.definition.StandardAxis.Env
import izumi.distage.plugins.PluginDef

object FixturesPlugin extends PluginDef with ZIODIEffectModule {
  make[String]
    .named("csv")
    .fromValue("IncidentTest.csv")
    .tagged(Env.Test)
}

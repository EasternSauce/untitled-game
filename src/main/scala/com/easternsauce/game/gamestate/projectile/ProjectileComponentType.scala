package com.easternsauce.game.gamestate.projectile

sealed trait ProjectileComponentType

// Example mappings from old AbilityComponentType
case object ArrowComponent extends ProjectileComponentType
case object ReturningArrowComponent extends ProjectileComponentType
case object FireballComponent extends ProjectileComponentType
case object IceShardComponent extends ProjectileComponentType
case object LightningBoltComponent extends ProjectileComponentType

// Add other projectile types as needed, mirroring the old system

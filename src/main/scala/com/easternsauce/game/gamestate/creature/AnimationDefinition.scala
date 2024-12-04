package com.easternsauce.game.gamestate.creature

case class AnimationDefinition(
    frameWidth: Int,
    frameHeight: Int,
    stanceFrames: FramesDefinition,
    walkFrames: FramesDefinition,
    meleeAttackFrames: FramesDefinition,
    deathFrames: FramesDefinition,
    spellcastFrames: Option[FramesDefinition] = None,
    bowShotFrames: Option[FramesDefinition] = None
) {}

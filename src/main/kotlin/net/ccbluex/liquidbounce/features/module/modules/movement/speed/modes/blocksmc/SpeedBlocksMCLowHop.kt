/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015-2024 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speed.modes.blocksmc

import net.ccbluex.liquidbounce.config.Choice
import net.ccbluex.liquidbounce.config.ChoiceConfigurable
import net.ccbluex.liquidbounce.event.events.MovementInputEvent
import net.ccbluex.liquidbounce.event.events.PlayerJumpEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.event.repeatable
import net.ccbluex.liquidbounce.features.module.modules.movement.speed.ModuleSpeed
import net.ccbluex.liquidbounce.features.module.modules.world.scaffold.ModuleScaffold
import net.ccbluex.liquidbounce.utils.client.Timer
import net.ccbluex.liquidbounce.utils.entity.moving
import net.ccbluex.liquidbounce.utils.entity.sqrtSpeed
import net.ccbluex.liquidbounce.utils.entity.strafe
import net.ccbluex.liquidbounce.utils.kotlin.Priority
import net.minecraft.entity.effect.StatusEffects

class SpeedBlocksMCLowHop(override val parent: ChoiceConfigurable<*>) : Choice("BlocksMCLowHop") {

    private var airTicks = 0

    companion object {
        var shouldLag = false
    }

    override fun disable() {
        airTicks = 0
        shouldLag = false
    }

    val repeatable = repeatable {

        player.strafe(speed = player.sqrtSpeed.coerceAtLeast(0.247))

        if (player.isOnGround) {
            airTicks = 0
        } else {
            airTicks++

            if (ModuleScaffold.enabled) {

                if (player.velocity.y > 0) {
                    Timer.requestTimerSpeed(1.2f, Priority.IMPORTANT_FOR_USAGE_1, ModuleSpeed)
                    shouldLag = true
                } else {
                    shouldLag = false
                }

            } else {
                if (airTicks == 4) {
                    player.velocity.y -= 0.11292218600823005
                    if ((player.getStatusEffect(StatusEffects.SPEED)?.amplifier ?: 0) > 0) {
                        player.velocity = player.velocity.multiply(
                            1.02,
                            1.0,
                            1.02
                        )
                    }
                }
            }

            when (airTicks) {
                2 -> player.velocity = player.velocity.multiply(1.01,1.0, 1.01)
            }

        }
    }

    val jumpEvent = handler<PlayerJumpEvent> {
        val atLeast = 0.287 + 0.23 * (player.getStatusEffect(StatusEffects.SPEED)?.amplifier ?: 0)

        player.strafe(speed = player.sqrtSpeed.coerceAtLeast(atLeast))
    }

    val movementInputEvent = handler<MovementInputEvent> {
        if (player.moving) {
            it.jumping = true
        }
    }
}


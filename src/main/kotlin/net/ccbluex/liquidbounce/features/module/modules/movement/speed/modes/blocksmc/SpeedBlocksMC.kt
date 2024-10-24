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
import net.ccbluex.liquidbounce.utils.entity.moving
import net.ccbluex.liquidbounce.utils.entity.sqrtSpeed
import net.ccbluex.liquidbounce.utils.entity.strafe
import net.minecraft.entity.effect.StatusEffects

class SpeedBlocksMC(override val parent: ChoiceConfigurable<*>) : Choice("BlocksMC") {

    private val fullStrafe by boolean("FullStrafe", true)
    private val lowHop by boolean("LowHop", true)
    private val damageBoost by boolean("DamageBoost", true)
    private val damageLowHop by boolean("DamageLowHop", true)

    private var airTicks = 0
    private var canSpeed = false

    override fun enable() {
        canSpeed = false
    }

    override fun disable() {
        airTicks = 0
        player.strafe(speed = 0.0)
    }

    val repeatable = repeatable {

        if (player.isOnGround) {
            airTicks = 0
            canSpeed = true
        } else {
            airTicks++

            if (!canSpeed) {
                return@repeatable
            }
            if (fullStrafe) {
                if (player.moving) {
                    player.strafe(speed = player.sqrtSpeed.coerceAtLeast(0.281))
                }
            } else {
                if (airTicks >= 7) {
                    player.strafe(speed = player.sqrtSpeed.coerceAtLeast(0.281), strength = 0.7)
                } else {
                    player.strafe(strength = 0.0)
                }
            }

            if (lowHop && airTicks == 4) {
                player.velocity.y = -0.09800000190734863
            }

            if (player.hurtTime == 9 && damageBoost) {
                player.strafe(speed = 1.0)
            }

            if (damageLowHop && player.hurtTime >= 1) {
                if (player.velocity.y > 0) {
                    player.velocity.y -= 0.15
                }
            }

        }
    }

    val jumpEvent = handler<PlayerJumpEvent> {
        val atLeast = 0.3 + 0.2 * (player.getStatusEffect(StatusEffects.SPEED)?.amplifier ?: 0)
        if (!canSpeed) {
            return@handler
        }

        player.strafe(speed = player.sqrtSpeed.coerceAtLeast(atLeast))
    }

    val movementInputEvent = handler<MovementInputEvent> {
        if (player.moving) {
            it.jumping = true
        }
    }
}

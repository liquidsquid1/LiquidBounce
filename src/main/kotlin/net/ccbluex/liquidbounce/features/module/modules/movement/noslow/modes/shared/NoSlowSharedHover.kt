/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2025 CCBlueX
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
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslow.modes.shared

import net.ccbluex.liquidbounce.config.types.Choice
import net.ccbluex.liquidbounce.config.types.ChoiceConfigurable
import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.events.PlayerAfterJumpEvent
import net.ccbluex.liquidbounce.event.events.PlayerNetworkMovementTickEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.utils.client.InteractionTracker.untracked
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
import net.minecraft.util.Hand

/**
 * @anticheat hypixel
 * @anticheatVersion 24/1/2025
 */
internal class NoSlowSharedHover(override val parent: ChoiceConfigurable<*>) : Choice("Hover") {

    private var readyToDisable = false
    private var ticksSinceJump = 0

    @Suppress("unused")
    private val onNetworkTick = handler<PlayerNetworkMovementTickEvent> { event ->
        if(event.state != EventState.PRE) return@handler

        if (player.isUsingItem) {
            ticksSinceJump++

            if (!readyToDisable && player.isOnGround) {
                readyToDisable = true
                event.y += 0.01
            }

            event.ground = ticksSinceJump <= 1
            if (player.isOnGround) {
                event.ground = false
            }
        } else {
            readyToDisable = false
        }
    }

    @Suppress("unused")
    private val afterJumpHandler = handler<PlayerAfterJumpEvent> {
        ticksSinceJump = 0
    }

}

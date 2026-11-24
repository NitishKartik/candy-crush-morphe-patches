/*
 * Copyright (C) 2026 hxreborn
 * SPDX-License-Identifier: GPL-3.0-only
 */
package app.morphe.patches.candycrushsaga.misc.lives

import app.morphe.patcher.patch.intOption
import app.morphe.patcher.patch.mutableClassDefBy
import app.morphe.patcher.patch.packageMetadata
import app.morphe.patcher.patch.patch
import app.morphe.patches.candycrushsaga.misc.boosters.candyCrushRewardsPatch
import app.morphe.patches.candycrushsaga.shared.isCandyCrushSodaSaga
import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patches.shared.misc.requireArm64StandardInstall
import app.morphe.patches.shared.misc.smali.replaceStub

private const val BOOSTERS = "Lapp/hxreborn/extension/candycrushsaga/Boosters;"
private const val MINUTES_PER_HOUR = 60
private const val MAX_HOURS = Int.MAX_VALUE / 3600

@Suppress("unused")
val unlimitedLivesPatch = patch(
    name = "Unlimited lives",
    description = "Adds a configurable duration of unlimited lives every time the game starts.",
) {
    compatibleWith(AppCompatibilities.CANDY_CRUSH_SAGA, AppCompatibilities.CANDY_CRUSH_SODA_SAGA)
    availability(requireArm64StandardInstall)
    dependsOn(candyCrushRewardsPatch)

    val hours by intOption(
        key = "unlimitedLivesHours",
        default = 24,
        title = "Unlimited lives duration (hours)",
        description = "Hours of unlimited lives granted on startup. Enter a whole number from 1 to $MAX_HOURS.",
        required = true,
    ) { it != null && it in 1..MAX_HOURS }

    execute {
        val duration = checkNotNull(hours)
        val amount = if (isCandyCrushSodaSaga(packageMetadata.packageName)) duration * MINUTES_PER_HOUR else duration
        mutableClassDefBy(BOOSTERS).replaceStub("livesAmount", 1, """
            const v0, $amount
            return v0
        """)
    }
}

package com.ut3.hiddendoor.game.utils

import android.content.Context
import androidx.core.content.edit

class Preferences(val context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    var currentLevel: String
        get() = sharedPreferences.getString("current_level", "introduction")!!
        set(value) = sharedPreferences.edit { putString("current_level", value) }
}
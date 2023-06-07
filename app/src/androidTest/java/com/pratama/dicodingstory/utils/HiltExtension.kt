package com.pratama.dicodingstory.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.core.util.Preconditions
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.pratama.dicodingstory.R

inline fun <reified T : Activity> launchActInHiltContainer(
    @StyleRes themeResId: Int = com.google.android.material.R.style.AlertDialog_AppCompat,
    crossinline action: T.() -> Unit = {}
) {
    val startActivityIntent = Intent(
            ApplicationProvider.getApplicationContext(),
            T::class.java
    ).apply {
        putExtra(
            "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
            themeResId
        )
    }

    ActivityScenario.launch<T>(startActivityIntent).onActivity { activity ->
        activity.action()
    }
}
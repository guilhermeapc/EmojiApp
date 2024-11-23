// CrashReportingTree.kt
package com.guilhermeapc.emojiapp

import timber.log.Timber

class CrashReportingTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // TODO crash reporting logic here. For example, send logs to Firebase Crashlytics

    }
}
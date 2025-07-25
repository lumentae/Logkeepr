package dev.lumentae.logkeepr

import dev.lumentae.logkeepr.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

object Globals {
    lateinit var DATABASE: AppDatabase
    var DATABASE_SCOPE = CoroutineScope(Job() + Dispatchers.Main)
}

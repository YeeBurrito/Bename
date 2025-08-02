package com.bename.ui.locations

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bename.R
import com.bename.data.local.database.AppDatabase
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("GeofenceReceiver", "Received geofence broadcast: $intent. Extras: ${intent.extras}")
        val event = GeofencingEvent.fromIntent(intent)
        if (event == null) {
            Log.e("GeofenceReceiver", "GeofencingEvent is null. Intent extras: ${intent.extras}")
            return
        }
        val triggeringGeofences = event.triggeringGeofences ?: run {
            Log.e("GeofenceReceiver", "No triggering geofences")
            return
        }
        val locationNames = triggeringGeofences.map { it.requestId }
        Log.d("GeofenceReceiver", "Triggered geofences: $locationNames")
        Log.d("GeofenceReceiver", "Geofence transition type: ${event.geofenceTransition}")

        when (event.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.d("GeofenceReceiver", "Geofence transition is ENTER")
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getInstance(context)
                    val tagDao = db.tagDao()
                    val tags = tagDao.getAllTags()
                    val matchedTags = tags.filter { tag ->
                        tag.locations.any { it in locationNames }
                    }
                    val tagNames = matchedTags.joinToString(", ") { it.name }
                    Log.d("GeofenceReceiver", "Matched tags: $tagNames")
                    if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                        sendNotification(context, tagNames)
                        Log.d("GeofenceReceiver", "Notification sent for tags: $tagNames")
                    } else {
                        Log.w("GeofenceReceiver", "Notifications are disabled")
                    }
                }
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getInstance(context)
                    val tagDao = db.tagDao()
                    val tags = tagDao.getTags().first()
                    val matchedTags = tags.filter { tag ->
                        tag.locations.any { it in locationNames }
                    }
                    val tagNames = matchedTags.joinToString(", ") { it.name }
                    Log.d("GeofenceReceiver", "Matched tags (DWELL): $tagNames")
                    if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                        sendNotification(context, "Inside geofence: $tagNames")
                        Log.d("GeofenceReceiver", "Notification sent for DWELL tags: $tagNames")
                    } else {
                        Log.w("GeofenceReceiver", "Notifications are disabled")
                    }
                }
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getInstance(context)
                    val tagDao = db.tagDao()
                    val tags = tagDao.getTags().first()
                    val matchedTags = tags.filter { tag ->
                        tag.locations.any { it in locationNames }
                    }
                    val tagNames = matchedTags.joinToString(", ") { it.name }
                    Log.d("GeofenceReceiver", "Matched tags (EXIT): $tagNames")
                    if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                        sendNotification(context, "Exited geofence: $tagNames")
                        Log.d("GeofenceReceiver", "Notification sent for EXIT tags: $tagNames")
                    } else {
                        Log.w("GeofenceReceiver", "Notifications are disabled")
                    }
                }
            }
            else -> {
                Log.d(
                    "GeofenceReceiver",
                    "Geofence transition is not ENTER, DWELL, or EXIT: ${event.geofenceTransition}"
                )
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendNotification(context: Context, tagNames: String) {
        val channelId = "geofence_channel"
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Nearby Tag(s)")
            .setContentText("Tags: $tagNames")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(context).notify(1001, notification)
    }
}
// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui.locations

import android.Manifest
import android.app.PendingIntent
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

// BroadcastReceiver to handle geofence transitions (ENTER, DWELL, EXIT)
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
                // User has entered a geofenced area
                LocationStatusManager.setCurrentLocations(locationNames)
                Log.d("GeofenceReceiver", "Geofence transition is ENTER")
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getInstance(context)
                    val tagDao = db.tagDao()
                    val personDao = db.personDao()
                    val tags = tagDao.getAllTags()
                    val people = personDao.getAllPersons() // Get all people from DB

                    for (location in locationNames) {
                        // Find tags associated with this location
                        val matchedTags = tags.filter { tag -> tag.locations.contains(location) }
                        for (tag in matchedTags) {
                            // Find people associated with this tag
                            val taggedPeople = people.filter { person -> tag.uid in person.tagIds }
                            val peopleNames = taggedPeople.joinToString(", ") { "${it.firstName} ${it.lastName.orEmpty()}" }
                            val notificationText = if (peopleNames.isNotBlank()) {
                                "You are near $location\nThese people tagged with ${tag.name} are here\n$peopleNames"
                            } else {
                                "You are near $location\nNo people tagged with ${tag.name} are here"
                            }
                            // Send notification if enabled
                            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                                sendNotification(context, notificationText)
                                Log.d("GeofenceReceiver", "Notification sent: $notificationText")
                            } else {
                                Log.w("GeofenceReceiver", "Notifications are disabled")
                            }
                        }
                    }
                }
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                // User is dwelling in a geofenced area
                LocationStatusManager.setCurrentLocations(locationNames)
                Log.d("GeofenceReceiver", "Geofence transition is DWELL")
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                // User has exited a geofenced area
                Log.d("GeofenceReceiver", "Geofence transition is EXIT")
            }
            else -> {
                Log.d(
                    "GeofenceReceiver",
                    "Geofence transition is not ENTER, DWELL, or EXIT: ${event.geofenceTransition}"
                )
            }
        }
    }

    // Helper function to send a notification about nearby tags/people
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendNotification(context: Context, message: String) {
        val channelId = "geofence_channel"
        val intent = Intent(context, com.bename.ui.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Nearby Tag(s)")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notification)
    }
}
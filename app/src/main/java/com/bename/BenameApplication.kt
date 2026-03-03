// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

// Application class for initializing global app state
@HiltAndroidApp
class BenameApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createGeofenceNotificationChannel() // Set up notification channel for geofence events
    }

    // Creates a notification channel for geofence notifications (required for Android O+)
    private fun createGeofenceNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "geofence_channel", // Channel ID
                "Geofence Notifications", // Channel name
                NotificationManager.IMPORTANCE_HIGH // Importance level
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
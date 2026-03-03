// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui.locations

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.bename.data.local.database.Location
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest

// Manages geofence registration for a list of locations
class GeofenceManager(
    private val context: Context,
    private val geofencingClient: GeofencingClient
) {
    // Registers geofences for the provided locations
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun registerGeofences(locations: List<Location>) {
        if (locations.isEmpty()) {
            Log.e("GeofenceManager", "No locations provided, cannot register geofences")
            return
        }
        Log.d("GeofenceManager", "Registering geofences for locations: ${locations.map { it.name }}")
        val geofences = locations.map { location ->
            Log.d(
                "GeofenceManager",
                "Creating geofence: name=${location.name}, lat=${location.latitude}, lng=${location.longitude}, radius=${location.radius}"
            )
            Geofence.Builder()
                .setRequestId(location.name) // Use location name as geofence ID
                .setCircularRegion(
                    location.latitude,
                    location.longitude,
                    500f // Fixed radius in meters
                )
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER or
                            Geofence.GEOFENCE_TRANSITION_EXIT or
                            Geofence.GEOFENCE_TRANSITION_DWELL
                )
                .setLoiteringDelay(30000) // Required for DWELL transition (30 seconds)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        }
        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()
        geofencingClient.addGeofences(request, getGeofencePendingIntent())
            .addOnSuccessListener {
                Log.d("GeofenceManager", "Geofences successfully registered")
            }
            .addOnFailureListener { e ->
                Log.e("GeofenceManager", "Failed to register geofences", e)
            }
    }

    // Returns a PendingIntent to handle geofence transitions
    private fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
}
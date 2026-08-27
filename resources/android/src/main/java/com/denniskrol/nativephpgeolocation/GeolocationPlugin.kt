package com.denniskrol.nativephpgeolocation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import com.nativephp.mobile.bridge.BridgeFunction
import com.nativephp.mobile.bridge.BridgeResponse
import java.util.concurrent.TimeUnit

object GeolocationPlugin {

    private fun hasForegroundPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun hasBackgroundPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    class RequestPermission(private val activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            return if (hasForegroundPermission(activity)) {
                BridgeResponse.success(
                    mapOf(
                        "granted" to true,
                        "message" to "Permission already granted"
                    )
                )
            } else {
                activity.requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1001
                )

                BridgeResponse.success(
                    mapOf(
                        "granted" to false,
                        "message" to "Permission requested"
                    )
                )
            }
        }
    }

    class RequestBackgroundPermission(private val activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            if (hasBackgroundPermission(activity)) {
                return BridgeResponse.success(
                    mapOf("granted" to true, "message" to "Background location permission already granted")
                )
            }

            if (!hasForegroundPermission(activity)) {
                activity.requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1001
                )

                return BridgeResponse.success(
                    mapOf(
                        "granted" to false,
                        "message" to "Foreground location permission requested"
                    )
                )
            }

            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity.packageName, null)
            )
            activity.startActivity(intent)

            return BridgeResponse.success(
                mapOf(
                    "granted" to false,
                    "message" to "Open Permissions, then Location, and choose Allow all the time"
                )
            )
        }
    }

    class HasBackgroundPermission(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> = BridgeResponse.success(
            mapOf("granted" to hasBackgroundPermission(context))
        )
    }

    class GetCurrentPosition(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            if (!hasForegroundPermission(context)) {
                return BridgeResponse.error(
                    "LOCATION_PERMISSION_DENIED", "Location permission not granted"
                )
            }

            return getCurrentPosition(context, parameters)
        }
    }

    class GetBackgroundPosition(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            if (!hasForegroundPermission(context)) {
                return BridgeResponse.error(
                    "LOCATION_PERMISSION_DENIED", "Foreground location permission not granted"
                )
            }

            if (!hasBackgroundPermission(context)) {
                return BridgeResponse.error(
                    "BACKGROUND_LOCATION_PERMISSION_DENIED", "Background location permission not granted"
                )
            }

            return getCurrentPosition(context, parameters)
        }
    }

    private fun getCurrentPosition(context: Context, parameters: Map<String, Any>): Map<String, Any> {
        val highAccuracy = parameters["highAccuracy"] as? Boolean ?: true

        return try {
            val priority = if (highAccuracy) {
                Priority.PRIORITY_HIGH_ACCURACY
            } else {
                Priority.PRIORITY_BALANCED_POWER_ACCURACY
            }
            val request = CurrentLocationRequest.Builder()
                .setPriority(priority)
                .setDurationMillis(10_000)
                .build()
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val location = try {
                Tasks.await(fusedLocationClient.getCurrentLocation(request, null), 12, TimeUnit.SECONDS)
            } catch (_: java.util.concurrent.TimeoutException) {
                null
            } ?: Tasks.await(fusedLocationClient.lastLocation)

            if (location == null) {
                BridgeResponse.error(
                    "LOCATION_UNAVAILABLE", "Location unavailable"
                )
            } else {
                BridgeResponse.success(
                    mapOf(
                        "success" to true,
                        "latitude" to location.latitude,
                        "longitude" to location.longitude,
                        "accuracy" to location.accuracy.toDouble(),
                        "timestamp" to location.time
                    )
                )
            }
        } catch (e: Exception) {
            BridgeResponse.error(
                "LOCATION_ERROR", e.message ?: "Unknown location error"
            )
        }
    }
}

package com.example.myapplication.sync

import android.Manifest
import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.data.repository.NoteRepository
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncService : Service(), KoinComponent {

    private val repository: NoteRepository by inject()
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private lateinit var connectivityManager: ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            serviceScope.launch {
                Log.i("SyncService", "Network available, syncing notes")
                try{
                    repository.syncNotes()
                }catch (e: Exception) {
                    Log.e("SyncService", "Failed to sync notes", e)
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun onCreate() {
        super.onCreate()
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

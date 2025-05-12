package com.example.gomoku

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotifService : Service() {

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            try{
                val requests = ArrayList<String>()
                val invitations = ArrayList<Map<String,String>>()
                while(auth.currentUser != null) {
                    Thread.sleep(10000)
                    Log.i("NotifService", "Checking for new requests or invitations")

                    val user = auth.currentUser!!.uid
                    Log.i("NotifService", "User : $user")

                    db.collection("users")
                        .document(user)
                        .get().addOnSuccessListener { res ->
                            val newRequests = (res.get("requests") as? List<*>)?.filterIsInstance<String>() ?: emptyList()

                            if (requests != newRequests && newRequests.size > requests.size) {
                                Log.i("NotifService", "New requests : $newRequests")
                                requests.clear()
                                requests.addAll(newRequests)
                                val userRequest = requests.last()
                                showNotification("request", "Nouvelle demande d'ami", "$userRequest souhaite être ami avec vous !")
                            }else{
                                Log.i("NotifService", "No new requests")
                            }

                            val newInvitations = (res.get("invitation") as? List<Map<String, String>>) ?: emptyList()
                            Log.i("NotifService", "Invitations : $invitations")
                            Log.i("NotifService", "New invitations : $newInvitations")

                            if (invitations != newInvitations && newInvitations.size > invitations.size) {
                                Log.i("NotifService", "New invitations : $newInvitations")
                                invitations.clear()
                                invitations.addAll(newInvitations)

                                val lastInvitation = invitations.lastOrNull()
                                Log.i("NotifService", "Last invitation : $lastInvitation")
                                if (lastInvitation != null && lastInvitation.size >= 2) {
                                    val inviter = lastInvitation["inviter"]
                                    val lobbyId = lastInvitation["lobbyId"]
                                    Log.i("NotifService", "Inviter : $inviter")
                                    Log.i("NotifService", "LobbyId : $lobbyId")
                                    showNotification(
                                        type = "invitation",
                                        title = "Invitation à jouer",
                                        message = "$inviter vous invite à jouer.",
                                        inviter = inviter,
                                        lobbyId = lobbyId
                                    )
                                }
                            }else{
                                Log.i("NotifService", "No new invitations")
                            }
                        }
                }
            } catch (e: InterruptedException){
                Thread.currentThread().interrupt()
            }
            stopSelf(msg.arg1)
        }
    }

    override fun onCreate() {
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
            auth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()

        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }

    fun showNotification(
        type: String,
        title: String,
        message: String,
        inviter: String? = null,
        lobbyId: String? = null
    ) {
        val channelId = "default_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Default Channel"
            val descriptionText = "General notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", type)
            if(type == "invitation"){
                putExtra("inviter", inviter)
                putExtra("lobbyId", lobbyId)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(this)) {
                notify(1001, builder.build())
            }
        } else {
            Log.w("Notif", "Permission POST_NOTIFICATIONS non accordée.")
        }
    }
}
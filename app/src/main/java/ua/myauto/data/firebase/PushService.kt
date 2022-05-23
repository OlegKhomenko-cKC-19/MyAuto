package ua.myauto.data.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ua.myauto.R
import ua.myauto.data.db.repository.InsuranceRepository
import ua.myauto.data.db.repository.UserRepository
import ua.myauto.data.local.LocalRepository
import ua.myauto.domain.models.insurance.Insurance
import ua.myauto.ui.activity.InsuranceListActivity
import kotlin.random.Random

class PushService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            GlobalScope.launch {
                val user = UserRepository.newInstance(LocalRepository.username).getCurrentUser()
                val insurances = InsuranceRepository.newInstance(user).getInsurances()

                val needToPayInsurances = insurances.filter { it.isPaymentRequired }
                needToPayInsurances.forEach {
                    sendNotification(getNotificationText(it))
                }
            }
        }
    }

    private fun getNotificationText(insurance: Insurance): String {
        return getString(
            R.string.notification_insurance_text_placeholder,
            insurance.carNumber,
            LocalRepository.daysBeforePush
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, InsuranceListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 15251, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_warning_white)
            .setContentTitle(getString(R.string.notification_insurance_title))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId,
            "Необхідно оплатити",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
package com.cs240.netzero;


        import android.app.NotificationChannel;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Build;

        import androidx.annotation.RequiresApi;
        import androidx.core.app.NotificationCompat;

        import java.util.Objects;

        import com.cs240.netzero.data.Utilities;

        import static android.content.Context.MODE_PRIVATE;
        import static com.cs240.netzero.R.*;

public class NotificationReceiver extends BroadcastReceiver {
    private final String channelId = "BenzTrack Notifications";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = Objects.requireNonNull(context).getSharedPreferences("it.nicolasguarini.benztrack", MODE_PRIVATE);

        if (sharedPreferences != null && sharedPreferences.getFloat("consumptionPrevMonth", -1f) != -1f) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent repeatingIntent = new Intent(context, DashboardActivity.class);
            repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = this.channelId;
                NotificationChannel mChannel = new NotificationChannel(channelId, "General Notifications", NotificationManager.IMPORTANCE_DEFAULT);
                mChannel.setDescription("This is default channel used for all other notifications");
                notificationManager.createNotificationChannel(mChannel);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            float spentThisMonth = sharedPreferences.getFloat("spentThisMonth", -1f);
            float emittedThisMonth = sharedPreferences.getFloat("emittedThisMonth", -1f);
            float consumptionThisMonth = sharedPreferences.getFloat("consumptionThisMonth", -1f);
            float consumptionPrevMonth = sharedPreferences.getFloat("consumptionPrevMonth", -1f);

            String monthYear = Utilities.getThisMonthYear();
            float deltaPercent = (getDeltaPercent(consumptionPrevMonth, consumptionThisMonth) * 10.0f) / 10.0f;
            String contentTitle = context.getString(string.consumptions_update) + monthYear;
            String contentText = context.getString(string.spent_this_month) + spentThisMonth + context.getString(string.emitting) + emittedThisMonth + context.getString(string.with_medium_consumption) + consumptionThisMonth + " " + context.getString(string.km_l) + ", ";
            contentText += deltaPercent < 0f ? "-" + deltaPercent + context.getString(string.doing_great) : "+" + deltaPercent + context.getString(string.can_do_better);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(drawable.ic_logo)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                    .setAutoCancel(true);

            Objects.requireNonNull(notificationManager).notify(100, builder.build());
        }
    }

    private float getDeltaPercent(float consumption1, float consumption2) {
        return consumption1 > consumption2 ? -((consumption1 - consumption2) / consumption1 * 100) : (consumption2 - consumption1) / consumption2 * 100;
    }
}

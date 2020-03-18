package net.veldor.intermittentfasting.receivers;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.veldor.intermittentfasting.App;
import net.veldor.intermittentfasting.MainActivity;
import net.veldor.intermittentfasting.db.DbQueries;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static net.veldor.intermittentfasting.App.FASTING_TIMER;
import static net.veldor.intermittentfasting.utils.MyNotify.PERIOD_FINISHED_NOTIFICATION;

public class MiscActionsReceiver extends BroadcastReceiver {
    public static final String EXTRA_ACTION_TYPE = "action type";
    public static final String ACTION_START_EATING = "start eating";
    public static final String ACTION_START_FASTING = "start fasting";
    public static final String ACTION_SHOW_STAT = "show stat";
    public static final String ACTION_I_EAT = "i eat";
    public static final String ACTION_I_I_DRINK = "i drink";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("surprise", "MiscActionsReceiver onReceive: receive command");
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d("surprise", "MiscActionsReceiver onReceive: receive boot completed command");
            long startTime = App.getPreferences().getLong(FASTING_TIMER, 0);
            if(startTime > 0){
                App.getInstance().startTimerWorker();
            }
            return;
        }
        String action = intent.getStringExtra(EXTRA_ACTION_TYPE);
        if(action != null){
            switch (action){
                case ACTION_START_EATING:
                case ACTION_START_FASTING:
                    DbQueries.saveCurrentPeriod();
                    App.getNotifier().sendPeriodFinishedNotification();
                    // запущу период пищевого окна
                    App.getInstance().isFasting = !App.getInstance().isFasting;
                    App.getInstance().startTimer();
                    // покажу уведомление о завершении прогресса
                    break;
                case ACTION_SHOW_STAT:
                    // Отменю уведомление
                    if (App.getNotifier().mNotificationManager != null) {
                        App.getNotifier().mNotificationManager.cancel(PERIOD_FINISHED_NOTIFICATION);
                    }
                    Intent showStatIntent = new Intent(App.getInstance(), MainActivity.class);
                    showStatIntent.putExtra(MainActivity.START_FRAGMENT, MainActivity.START_STATISTICS);
                    showStatIntent.setFlags(FLAG_ACTIVITY_NEW_TASK|FLAG_ACTIVITY_SINGLE_TOP|FLAG_ACTIVITY_CLEAR_TOP);
                    App.getInstance().startActivity(showStatIntent);
                    break;
                case ACTION_I_EAT:
                    App.iEat();
                    break;
                case ACTION_I_I_DRINK:
                    App.iDrink();
                    break;
            }
        }
    }
}

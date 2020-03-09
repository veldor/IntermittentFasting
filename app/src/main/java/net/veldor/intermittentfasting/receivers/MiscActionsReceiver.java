package net.veldor.intermittentfasting.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.veldor.intermittentfasting.App;

public class MiscActionsReceiver extends BroadcastReceiver {
    public static final String EXTRA_ACTION_TYPE = "action type";
    public static final String ACTION_START_EATING = "cancel download";
    public static final String ACTION_START_FASTING = "pause download";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra(EXTRA_ACTION_TYPE);
        if(action != null){
            switch (action){
                case ACTION_START_EATING:
                    // запущу период пищевого окна
                    App.getInstance().isFasting = false;
                    App.getInstance().startTimer();
                    break;
                case ACTION_START_FASTING:
                        // запущу период голодания
                    App.getInstance().isFasting = true;
                    App.getInstance().startTimer();
                    break;
            }
        }
    }
}

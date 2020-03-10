package net.veldor.intermittentfasting.db;

import net.veldor.intermittentfasting.App;
import net.veldor.intermittentfasting.db.dao.PeriodDao;
import net.veldor.intermittentfasting.db.entity.Period;

import static net.veldor.intermittentfasting.App.FASTING_TIMER;

public class DbQueries {
    private static final String PERIOD_FASTING = "fasting";
    private static final String PERIOD_EATING = "eating";

    public static void saveCurrentPeriod() {
        // если таймер запущен- сохраняю данные о нём
        long startTime = App.getPreferences().getLong(FASTING_TIMER, 0);
        if(startTime > 0){
            PeriodDao dao = App.getDb().periodDao();
            Period period = new Period();
            if(App.getInstance().isFasting){
                period.periodType = PERIOD_FASTING;
            }
            else{
                period.periodType = PERIOD_EATING;
            }
            period.periodStart = startTime;
            period.periodFinish = System.currentTimeMillis();
            dao.insert(period);
        }
    }
}

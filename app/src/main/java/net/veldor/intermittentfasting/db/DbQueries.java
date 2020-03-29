package net.veldor.intermittentfasting.db;

import net.veldor.intermittentfasting.App;
import net.veldor.intermittentfasting.db.dao.PeriodDao;
import net.veldor.intermittentfasting.db.entity.Drink;
import net.veldor.intermittentfasting.db.entity.Eat;
import net.veldor.intermittentfasting.db.entity.Period;

import static net.veldor.intermittentfasting.App.FASTING_TIMER;

public class DbQueries {
    public static final String PERIOD_FASTING = "fasting";
    public static final String PERIOD_EATING = "eating";

    public static void saveCurrentPeriod() {
        // если таймер запущен- сохраняю данные о нём
        long startTime = App.getPreferences().getLong(FASTING_TIMER, 0);
        if(startTime > 0){
            PeriodDao dao = App.getDb().periodDao();
            Period period = new Period();
            if(App.getInstance().isFasting()){
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

    public static void saveEat() {
        Eat newEat = new Eat();
        newEat.eatTime = System.currentTimeMillis();
        App.getDb().eatDao().insert(newEat);
    }
    public static void saveDrink() {
        Drink newDrink = new Drink();
        newDrink.drinkTime = System.currentTimeMillis();
        App.getDb().drinkDao().insert(newDrink);
    }

    public static Eat getLastEat() {
        return App.getDb().eatDao().getLastEat();
    }

    public static Drink getLastDrink() {
        return App.getDb().drinkDao().getLastDrink();
    }
}

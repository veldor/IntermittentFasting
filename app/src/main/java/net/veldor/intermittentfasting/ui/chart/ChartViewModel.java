package net.veldor.intermittentfasting.ui.chart;

import androidx.lifecycle.ViewModel;

import net.veldor.intermittentfasting.App;
import net.veldor.intermittentfasting.db.entity.Period;
import net.veldor.intermittentfasting.workers.TimerWorker;

import java.util.Calendar;
import java.util.List;

import static net.veldor.intermittentfasting.db.DbQueries.PERIOD_EATING;
import static net.veldor.intermittentfasting.db.DbQueries.PERIOD_FASTING;

public class ChartViewModel extends ViewModel {


    private final long mStartOfDay;

    public ChartViewModel() {
        // получу метку времени начала периода
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        mStartOfDay = calendar.getTimeInMillis();
    }


    Long getTotalFastingTime() {
        long totalFastingTime = 0;
        Period currentPeriod;
        // получу все периоды голодания за сегодня
        List<Period> periods = App.getDb().periodDao().getPeriodsFrom(mStartOfDay);
        // получу данные первого периода. Считаю, что до этого был другой период
        if (periods != null && periods.size() > 0) {
            currentPeriod = periods.get(0);
            if (currentPeriod.periodType.equals(PERIOD_EATING)) {
                // считаю, что до начала этого периода вы голодали со вчера
                totalFastingTime += currentPeriod.periodStart - mStartOfDay;
            } else {
                totalFastingTime += currentPeriod.periodFinish - currentPeriod.periodStart;
            }
            int counter = 1;
            while (counter < periods.size()) {
                currentPeriod = periods.get(counter);
                if (currentPeriod.periodType.equals(PERIOD_FASTING)) {
                    totalFastingTime += currentPeriod.periodFinish - currentPeriod.periodStart;
                }
                ++counter;
            }
            // теперь посмотрю, если сейчас идёт период голодания- занчит, прибавлю к времени время, прошедшее с конца последнего периода до текущего времени
            if (currentPeriod.periodType.equals(PERIOD_EATING)) {
                totalFastingTime += System.currentTimeMillis() - currentPeriod.periodFinish;
            }
        }
        return totalFastingTime;
    }

    Long getTotalEatingTime() {
        long totalEatingTime = 0;
        Period currentPeriod;
        // получу все периоды пищевых окон за сегодня
        List<Period> periods = App.getDb().periodDao().getPeriodsFrom(mStartOfDay);
        // получу данные первого периода. Считаю, что до этого был другой период
        if (periods != null && periods.size() > 0) {
            currentPeriod = periods.get(0);
            if (currentPeriod.periodType.equals(PERIOD_FASTING)) {
                // считаю, что до начала этого периода вы ели со вчера
                totalEatingTime += currentPeriod.periodStart - mStartOfDay;
            } else {
                totalEatingTime += currentPeriod.periodFinish - currentPeriod.periodStart;
            }
            int counter = 1;
            while (counter < periods.size()) {
                currentPeriod = periods.get(counter);
                if (currentPeriod.periodType.equals(PERIOD_EATING)) {
                    totalEatingTime += currentPeriod.periodFinish - currentPeriod.periodStart;
                }
                ++counter;
            }
            // теперь посмотрю, если сейчас идёт период еды- занчит, прибавлю к времени время, прошедшее с конца последнего периода до текущего времени
            if (currentPeriod.periodType.equals(PERIOD_FASTING)) {
                totalEatingTime += System.currentTimeMillis() - currentPeriod.periodFinish;
            }
        }
        return totalEatingTime;
    }

    int getEats() {
        return App.getDb().eatDao().getEatsFrom(mStartOfDay);
    }

    int getDrinks() {
        return App.getDb().drinkDao().getDrinksFrom(mStartOfDay);
    }

    String getLastDrinkTime() {
        return TimerWorker.hmsTimeFormatter(System.currentTimeMillis() - App.getDb().drinkDao().getLastDrink().drinkTime);
    }

    String getLastEatTime() {
        return TimerWorker.hmsTimeFormatter(System.currentTimeMillis() - App.getDb().eatDao().getLastEat().eatTime);
    }
}
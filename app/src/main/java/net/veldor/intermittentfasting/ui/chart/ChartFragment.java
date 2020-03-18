package net.veldor.intermittentfasting.ui.chart;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import net.veldor.intermittentfasting.R;
import net.veldor.intermittentfasting.utils.MyFormatter;
import net.veldor.intermittentfasting.workers.TimerWorker;

import java.util.Locale;

public class ChartFragment extends Fragment {

    private ChartViewModel mViewModel;
    private View mRoot;
    private TextView mTotalFastingTimeView, mTotalEatingTimeView, mTotalEatsView, mTotalDrinksView, mLastEatTimeView, mLastDrinkTimeView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ChartViewModel.class);
        mRoot = inflater.inflate(R.layout.fragment_dashboard, container, false);
        setupUI();
        return mRoot;
    }

    private void setupUI() {
        // Добавлю переменные
        mTotalFastingTimeView = mRoot.findViewById(R.id.totalFastingTime);
        mTotalEatingTimeView = mRoot.findViewById(R.id.totalEatingTime);
        mTotalEatsView = mRoot.findViewById(R.id.totalEats);
        mTotalDrinksView = mRoot.findViewById(R.id.totalDrinks);
        mLastEatTimeView = mRoot.findViewById(R.id.lastEatTime);
        mLastDrinkTimeView = mRoot.findViewById(R.id.lastDrinkTime);
        // обновлю данные о периоде
        refreshPeriodData();
    }

    private void refreshPeriodData() {
        Context context = getContext();
        if(context != null){
            long fastingTime = mViewModel.getTotalFastingTime();
            long eatingTime = mViewModel.getTotalEatingTime();
            // посчитаю соотношение периодов в %
            long eatingPercent = MyFormatter.countPercent(eatingTime, fastingTime);
            mTotalFastingTimeView.setText(String.format(Locale.ENGLISH, getContext().getString(R.string.fasting_time_chart), TimerWorker.hmsTimeFormatter(fastingTime), eatingPercent));
            mTotalEatingTimeView.setText(String.format(Locale.ENGLISH, getContext().getString(R.string.eating_time_chart), TimerWorker.hmsTimeFormatter(eatingTime), 100 - eatingPercent));
            // посчитаю количество фактов еды за день
            int eatCount = mViewModel.getEats();
            int drinksCount = mViewModel.getDrinks();
            mTotalEatsView.setText(String.format(Locale.ENGLISH, getString(R.string.total_eats), eatCount));
            mTotalDrinksView.setText(String.format(Locale.ENGLISH, getString(R.string.total_drinks),drinksCount));
            if(drinksCount > 0){
                mLastDrinkTimeView.setVisibility(View.VISIBLE);
                mLastDrinkTimeView.setText(String.format(Locale.ENGLISH, getString(R.string.last_drink_time_message), mViewModel.getLastDrinkTime()));
            }
            else{
                mLastDrinkTimeView.setVisibility(View.GONE);
            }
            if(eatCount > 0){
                mLastEatTimeView.setVisibility(View.VISIBLE);
                mLastEatTimeView.setText(String.format(Locale.ENGLISH, getString(R.string.last_eat_time_message), mViewModel.getLastEatTime()));
            }
            else{
                mLastEatTimeView.setVisibility(View.GONE);
            }
        }
    }
}

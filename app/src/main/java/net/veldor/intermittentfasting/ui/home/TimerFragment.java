package net.veldor.intermittentfasting.ui.home;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;

import net.veldor.intermittentfasting.App;
import net.veldor.intermittentfasting.R;
import net.veldor.intermittentfasting.db.DbQueries;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static net.veldor.intermittentfasting.App.TIME_WORKER;

public class TimerFragment extends Fragment {


    private ProgressBar mProgressBar;
    private Button mStartFastingBtn, mStartEatingBtn;
    private CountDownTimer mCountDownTimer;
    private TextView mTimeView;
    private long mTimeCountInMilliSeconds = 60000;

    private MutableLiveData<String> mSpendTime = new MutableLiveData<>();
    private Timer mTimer;
    private FragmentActivity mActivity;
    private long mTimerStart;
    private TextView mCurrentTime;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timer, container, false);
        mActivity = getActivity();
        setupUI(root);
        setupObservers();
        return root;
    }

    private void setupObservers() {
        // буду отслеживать изменение периода, если он меняется- перезапущу таймер
    }

    private void setupUI(View root) {
        mProgressBar = root.findViewById(R.id.progressBarCircle);
        mStartFastingBtn = root.findViewById(R.id.startFastingTimerBtn);
        mStartEatingBtn = root.findViewById(R.id.StartEatingTimerBtn);
        mTimeView = root.findViewById(R.id.textViewTime);
        mCurrentTime = root.findViewById(R.id.currentTime);

        mStartFastingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbQueries.saveCurrentPeriod();
                // зарегистрирую время начала таймера
                App.getInstance().isFasting = true;
                App.getInstance().startTimer();
                mTimeView.setText(hmsTimeFormatter(0));
                mTimerStart = App.getPreferences().getLong(App.FASTING_TIMER, 0);
                startTimer();
                mStartFastingBtn.setVisibility(View.INVISIBLE);
                mStartEatingBtn.setVisibility(View.VISIBLE);
                mCurrentTime.setText(R.string.fasting_time_message);
                itsFastingTime();
            }
        });

        mStartEatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbQueries.saveCurrentPeriod();
                App.getInstance().isFasting = false;
                App.getInstance().startTimer();
                mTimeView.setText(hmsTimeFormatter(0));
                mTimerStart = App.getPreferences().getLong(App.FASTING_TIMER, 0);
                startTimer();
                mStartEatingBtn.setVisibility(View.INVISIBLE);
                mStartFastingBtn.setVisibility(View.VISIBLE);
                mCurrentTime.setText(R.string.eating_time_message);
                itsEatingTime();
            }
        });

        // проверю статус таймера
        LiveData<List<WorkInfo>> timerWorkerStatus = WorkManager.getInstance(mActivity).getWorkInfosForUniqueWorkLiveData(TIME_WORKER);
        timerWorkerStatus.observe(mActivity, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> status) {
                // проверю, что фрагмент виден
                if(isVisible()){
                    if (status != null && status.size() > 0) {
                        // если таймер запущен, выясню, какой из двух- таймер голодания или еды
                        if (status.get(0).getState().equals(WorkInfo.State.RUNNING) || status.get(0).getState().equals(WorkInfo.State.ENQUEUED)) {
                            startTimer();
                            if (App.getInstance().isFasting) {
                                // если период голодания- покажу таймер включения периода еды
                                mStartEatingBtn.setVisibility(View.VISIBLE);
                                mStartFastingBtn.setVisibility(View.INVISIBLE);
                                mCurrentTime.setText(R.string.fasting_time_message);
                                itsFastingTime();
                            } else {
                                mStartEatingBtn.setVisibility(View.INVISIBLE);
                                mStartFastingBtn.setVisibility(View.VISIBLE);
                                mCurrentTime.setText(R.string.eating_time_message);
                                itsEatingTime();
                            }
                        }
                    }
                }
            }
        });
    }

    private void startTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimerStart = App.getPreferences().getLong(App.FASTING_TIMER, 0);
        mTimer = new Timer();
        mTimer.schedule(new UpdateTimeTask(), 0, 1000); //тикаем каждую секунду без задержки
        // начну отслеживать изменения таймера
        mSpendTime.observe(mActivity, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    mTimeView.setText(s);
                }
            }
        });
    }

    //задача для таймера
    class UpdateTimeTask extends TimerTask {
        public void run() {
            // обновлю данные таймера
            mSpendTime.postValue(hmsTimeFormatter(System.currentTimeMillis() - mTimerStart));
        }
    }


    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {
        mCountDownTimer = new CountDownTimer(mTimeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                mProgressBar.setProgress(0);

                mTimeView.setText(hmsTimeFormatter(millisUntilFinished));

                mProgressBar.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

            }

        }.start();
    }

    /**
     * method to convert millisecond to time format
     */
    private String hmsTimeFormatter(long milliSeconds) {
        return String.format(Locale.ENGLISH, "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
    }

    private void itsFastingTime() {
        mTimeView.setTextColor(getResources().getColor(R.color.danger_color));
        mCurrentTime.setTextColor(getResources().getColor(R.color.danger_color));
        mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.drawable_circle_danger_color));
    }

    private void itsEatingTime() {
        mTimeView.setTextColor(getResources().getColor(R.color.colorAccent));
        mCurrentTime.setTextColor(getResources().getColor(R.color.colorAccent));
        mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.drawable_circle_yellow));
    }
}

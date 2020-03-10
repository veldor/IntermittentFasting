package net.veldor.intermittentfasting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import net.veldor.intermittentfasting.ui.dashboard.ChartFragment;
import net.veldor.intermittentfasting.ui.home.TimerFragment;

public class MainActivity extends AppCompatActivity {

    public static final String START_FRAGMENT = "start fragment";
    public static final int START_STATISTICS = 1;
    private BottomNavigationView mNnavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNnavView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(mNnavView, navController);

        // проверю, если в интенте указано открыть целевой фрагмент- открою его
        Intent intent = getIntent();
        int startFragment = intent.getIntExtra(START_FRAGMENT, -1);
        if(startFragment >= 0){
            switch (startFragment){
                case START_STATISTICS:
                    switchToStatistics();
                    break;
            }
        }
    }

    public void switchToStatistics() {
        Log.d("surprise", "MainActivity switchToStatistics: switch stat");
        mNnavView.setSelectedItemId(R.id.navigation_dashboard);
    }
}

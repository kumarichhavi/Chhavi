package nerd.tuxmobil.fahrplan.congress.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import kotlin.Unit;
import nerd.tuxmobil.fahrplan.congress.R;
import nerd.tuxmobil.fahrplan.congress.autoupdate.UpdateService;
import nerd.tuxmobil.fahrplan.congress.net.ConnectivityObserver;
import nerd.tuxmobil.fahrplan.congress.utils.ActivityHelper;

public abstract class BaseActivity extends AppCompatActivity {

    private ConnectivityObserver connectivityObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectivityObserver = new ConnectivityObserver(this, () -> {
            Log.d(getClass().getSimpleName(), "Network is available.");
            startUpdateService();
            return Unit.INSTANCE;
        });
        connectivityObserver.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectivityObserver.stop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                return ActivityHelper.navigateUp(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void startUpdateService() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean defaultValue = getResources().getBoolean(R.bool.preferences_auto_update_enabled_default_value);
        boolean doAutoUpdates = preferences.getBoolean("auto_update", defaultValue);
        if (doAutoUpdates) {
            UpdateService.start(this);
        }
    }

    protected void addFragment(@IdRes int containerViewId,
                               @NonNull Fragment fragment,
                               @NonNull String fragmentTag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(containerViewId, fragment, fragmentTag)
                .commit();
    }

    protected void replaceFragment(@IdRes int containerViewId,
                                   @NonNull Fragment fragment,
                                   @NonNull String fragmentTag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .commit();
    }

    protected void replaceFragment(@IdRes int containerViewId,
                                   @NonNull Fragment fragment,
                                   @NonNull String fragmentTag,
                                   @NonNull String backStackStateName) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .addToBackStack(backStackStateName)
                .commit();
    }

    protected void removeFragment(@NonNull String fragmentTag) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            supportFragmentManager
                    .beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    protected
    @Nullable
    Fragment findFragment(@NonNull String fragmentTag) {
        return getSupportFragmentManager().findFragmentByTag(fragmentTag);
    }

}

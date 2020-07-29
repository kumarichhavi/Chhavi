package nerd.tuxmobil.fahrplan.congress.changes;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import nerd.tuxmobil.fahrplan.congress.MyApp;
import nerd.tuxmobil.fahrplan.congress.R;
import nerd.tuxmobil.fahrplan.congress.base.AbstractListFragment;
import nerd.tuxmobil.fahrplan.congress.base.BaseActivity;
import nerd.tuxmobil.fahrplan.congress.contract.BundleKeys;
import nerd.tuxmobil.fahrplan.congress.details.SessionDetailsActivity;
import nerd.tuxmobil.fahrplan.congress.models.Session;

public class ChangeListActivity extends BaseActivity implements
        AbstractListFragment.OnSessionListClick {

    private static final String LOG_TAG = "ChangeListActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int actionBarColor = ContextCompat.getColor(this, R.color.colorActionBar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(actionBarColor));

        boolean requiresScheduleReload = false;
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                requiresScheduleReload = extras.getBoolean(BundleKeys.REQUIRES_SCHEDULE_RELOAD, false);
            }
        }

        if (savedInstanceState == null) {
            ChangeListFragment fragment = ChangeListFragment.newInstance(false, requiresScheduleReload);
            addFragment(R.id.container, fragment, ChangeListFragment.FRAGMENT_TAG);
            MyApp.LogDebug(LOG_TAG, "onCreate fragment created");
        }
    }

    @Override
    public void onSessionListClick(Session session, boolean requiresScheduleReload) {
        if (session != null) {
            SessionDetailsActivity.startForResult(this, session, session.day, requiresScheduleReload);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MyApp.SESSION_VIEW && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
        }
    }
}

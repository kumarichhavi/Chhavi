package nerd.tuxmobil.fahrplan.congress.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.ligi.tracedroid.logging.Log;

import java.util.Locale;

import info.metadude.android.eventfahrplan.commons.temporal.DateFormatter;
import nerd.tuxmobil.fahrplan.congress.BuildConfig;
import nerd.tuxmobil.fahrplan.congress.MyApp;
import nerd.tuxmobil.fahrplan.congress.R;
import nerd.tuxmobil.fahrplan.congress.alarms.AlarmTimePickerFragment;
import nerd.tuxmobil.fahrplan.congress.calendar.CalendarSharing;
import nerd.tuxmobil.fahrplan.congress.contract.BundleKeys;
import nerd.tuxmobil.fahrplan.congress.models.Session;
import nerd.tuxmobil.fahrplan.congress.navigation.RoomForC3NavConverter;
import nerd.tuxmobil.fahrplan.congress.repositories.AppRepository;
import nerd.tuxmobil.fahrplan.congress.sharing.JsonSessionFormat;
import nerd.tuxmobil.fahrplan.congress.sharing.SessionSharer;
import nerd.tuxmobil.fahrplan.congress.sharing.SimpleSessionFormat;
import nerd.tuxmobil.fahrplan.congress.sidepane.OnSidePaneCloseListener;
import nerd.tuxmobil.fahrplan.congress.utils.FahrplanMisc;
import nerd.tuxmobil.fahrplan.congress.utils.FeedbackUrlComposer;
import nerd.tuxmobil.fahrplan.congress.utils.SessionUrlComposer;
import nerd.tuxmobil.fahrplan.congress.utils.StringUtils;
import nerd.tuxmobil.fahrplan.congress.wiki.WikiSessionUtils;


public class SessionDetailsFragment extends Fragment {

    private static final String LOG_TAG = "Detail";

    public static final String FRAGMENT_TAG = "detail";

    public static final int SESSION_DETAILS_FRAGMENT_REQUEST_CODE = 546;

    private static final String SCHEDULE_FEEDBACK_URL = BuildConfig.SCHEDULE_FEEDBACK_URL;

    private static final boolean SHOW_FEEDBACK_MENU_ITEM = !TextUtils.isEmpty(SCHEDULE_FEEDBACK_URL);

    private AppRepository appRepository;

    private String sessionId;

    private String title;

    private Locale locale;

    private Typeface boldCondensed;

    private Typeface black;

    private Typeface light;

    private Typeface regular;

    private Typeface bold;

    private Session session;

    private int day;

    private String subtitle;

    private String spkr;

    private String abstractt;

    private String descr;

    private String links;

    private String room;

    private boolean sidePane = false;

    private boolean requiresScheduleReload = false;

    private boolean hasArguments = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appRepository = AppRepository.INSTANCE;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        MyApp.LogDebug(LOG_TAG, "onCreate");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (sidePane) {
            return inflater.inflate(R.layout.detail_narrow, container, false);
        } else {
            return inflater.inflate(R.layout.detail, container, false);
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        day = args.getInt(BundleKeys.SESSION_DAY, 0);
        sessionId = args.getString(BundleKeys.SESSION_ID);
        title = args.getString(BundleKeys.SESSION_TITLE);
        subtitle = args.getString(BundleKeys.SESSION_SUBTITLE);
        spkr = args.getString(BundleKeys.SESSION_SPEAKERS);
        abstractt = args.getString(BundleKeys.SESSION_ABSTRACT);
        descr = args.getString(BundleKeys.SESSION_DESCRIPTION);
        links = args.getString(BundleKeys.SESSION_LINKS);
        room = args.getString(BundleKeys.SESSION_ROOM);
        sidePane = args.getBoolean(BundleKeys.SIDEPANE, false);
        requiresScheduleReload = args.getBoolean(BundleKeys.REQUIRES_SCHEDULE_RELOAD, false);
        hasArguments = true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Activity activity = requireActivity();
        if (hasArguments) {
            AssetManager assetManager = activity.getAssets();
            boldCondensed = Typeface.createFromAsset(assetManager, "Roboto-BoldCondensed.ttf");
            black = Typeface.createFromAsset(assetManager, "Roboto-Black.ttf");
            light = Typeface.createFromAsset(assetManager, "Roboto-Light.ttf");
            regular = Typeface.createFromAsset(assetManager, "Roboto-Regular.ttf");
            bold = Typeface.createFromAsset(assetManager, "Roboto-Bold.ttf");

            locale = getResources().getConfiguration().locale;

            session = sessionOf(sessionId);

            // Detailbar

            TextView t;
            t = view.findViewById(R.id.session_detailbar_date_time_view);
            if (session != null && session.dateUTC > 0) {
                t.setText(DateFormatter.newInstance().getFormattedDateTimeShort(session.dateUTC));
            } else {
                t.setText("");
            }

            t = view.findViewById(R.id.session_detailbar_location_view);
            if (TextUtils.isEmpty(room)) {
                t.setText("");
            } else {
                t.setText(room);
            }

            t = view.findViewById(R.id.session_detailbar_session_id_view);
            if (TextUtils.isEmpty(sessionId)) {
                t.setText("");
            } else {
                t.setText("ID: " + sessionId);
            }

            // Title

            t = view.findViewById(R.id.session_details_content_title_view);
            setUpTextView(t, boldCondensed, title);

            // Subtitle

            t = view.findViewById(R.id.session_details_content_subtitle_view);
            if (TextUtils.isEmpty(subtitle)) {
                t.setVisibility(View.GONE);
            } else {
                setUpTextView(t, light, subtitle);
            }

            // Speakers

            t = view.findViewById(R.id.session_details_content_speakers_view);
            if (TextUtils.isEmpty(spkr)) {
                t.setVisibility(View.GONE);
            } else {
                setUpTextView(t, black, spkr);
            }

            // Abstract

            t = view.findViewById(R.id.session_details_content_abstract_view);
            if (TextUtils.isEmpty(abstractt)) {
                t.setVisibility(View.GONE);
            } else {
                abstractt = StringUtils.getHtmlLinkFromMarkdown(abstractt);
                setUpHtmlTextView(t, bold, abstractt);
            }

            // Description

            t = view.findViewById(R.id.session_details_content_description_view);
            if (TextUtils.isEmpty(descr)) {
                t.setVisibility(View.GONE);
            } else {
                descr = StringUtils.getHtmlLinkFromMarkdown(descr);
                setUpHtmlTextView(t, regular, descr);
            }

            // Links

            TextView linksView = view.findViewById(R.id.session_details_content_links_section_view);
            t = view.findViewById(R.id.session_details_content_links_view);
            if (TextUtils.isEmpty(links)) {
                linksView.setVisibility(View.GONE);
                t.setVisibility(View.GONE);
            } else {
                linksView.setTypeface(bold);
                MyApp.LogDebug(LOG_TAG, "show links");
                linksView.setVisibility(View.VISIBLE);
                links = links.replaceAll("\\),", ")<br>");
                links = StringUtils.getHtmlLinkFromMarkdown(links);
                setUpHtmlTextView(t, regular, links);
            }

            // Session online

            final TextView sessionOnlineSectionView = view.findViewById(R.id.session_details_content_session_online_section_view);
            sessionOnlineSectionView.setTypeface(bold);
            final TextView sessionOnlineLinkView = view.findViewById(R.id.session_details_content_session_online_view);
            if (WikiSessionUtils.containsWikiLink(links)) {
                sessionOnlineSectionView.setVisibility(View.GONE);
                sessionOnlineLinkView.setVisibility(View.GONE);
            } else {
                String sessionUrl = new SessionUrlComposer(session).getSessionUrl();
                if (sessionUrl.isEmpty()) {
                    sessionOnlineSectionView.setVisibility(View.GONE);
                    sessionOnlineLinkView.setVisibility(View.GONE);
                } else {
                    sessionOnlineSectionView.setVisibility(View.VISIBLE);
                    sessionOnlineLinkView.setVisibility(View.VISIBLE);
                    String sessionLink = "<a href=\"" + sessionUrl + "\">" + sessionUrl + "</a>";
                    setUpHtmlTextView(sessionOnlineLinkView, regular, sessionLink);
                }
            }

            activity.invalidateOptionsMenu();
        }
        activity.setResult(Activity.RESULT_CANCELED);
    }

    private void setUpTextView(@NonNull TextView textView,
                               @NonNull Typeface typeface,
                               @NonNull String text) {
        textView.setTypeface(typeface);
        textView.setText(text);
        textView.setVisibility(View.VISIBLE);
    }


    private void setUpHtmlTextView(@NonNull TextView textView,
                                   @NonNull Typeface typeface,
                                   @NonNull String text) {
        textView.setTypeface(typeface);
        textView.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        textView.setLinkTextColor(ContextCompat.getColor(textView.getContext(), R.color.text_link_color));
        textView.setMovementMethod(new LinkMovementMethod());
        textView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detailmenu, menu);
        MenuItem item;
        if (session != null) {
            if (session.highlight) {
                item = menu.findItem(R.id.menu_item_flag_as_favorite);
                if (item != null) {
                    item.setVisible(false);
                }
                item = menu.findItem(R.id.menu_item_unflag_as_favorite);
                if (item != null) {
                    item.setVisible(true);
                }
            }
            if (session.hasAlarm) {
                item = menu.findItem(R.id.menu_item_set_alarm);
                if (item != null) {
                    item.setVisible(false);
                }
                item = menu.findItem(R.id.menu_item_delete_alarm);
                if (item != null) {
                    item.setVisible(true);
                }
            }
        }
        item = menu.findItem(R.id.menu_item_feedback);
        String feedbackUrl = new FeedbackUrlComposer(session, SCHEDULE_FEEDBACK_URL).getFeedbackUrl();
        if (SHOW_FEEDBACK_MENU_ITEM && !TextUtils.isEmpty(feedbackUrl)) {
            if (item != null) {
                item.setVisible(true);
            }
        } else {
            if (item != null) {
                item.setVisible(false);
            }
        }
        if (sidePane) {
            item = menu.findItem(R.id.menu_item_close_session_details);
            if (item != null) {
                item.setVisible(true);
            }
        }
        item = menu.findItem(R.id.menu_item_navigate);
        if (item != null) {
            boolean isVisible = !getRoomConvertedForC3Nav().isEmpty();
            item.setVisible(isVisible);
        }
        if (BuildConfig.ENABLE_CHAOSFLIX_EXPORT) {
            item = menu.findItem(R.id.menu_item_share_session_menu);
        } else {
            item = menu.findItem(R.id.menu_item_share_session);
        }
        if (item != null) {
            item.setVisible(true);
        }
    }

    @NonNull
    private String getRoomConvertedForC3Nav() {
        String currentRoom = requireActivity().getIntent().getStringExtra(BundleKeys.SESSION_ROOM);
        if (currentRoom == null) {
            currentRoom = room;
        }
        return RoomForC3NavConverter.convert(currentRoom);
    }

    @NonNull
    private Session sessionOf(String sessionId) {
        return appRepository.readSessionBySessionId(sessionId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SESSION_DETAILS_FRAGMENT_REQUEST_CODE &&
                resultCode == AlarmTimePickerFragment.ALERT_TIME_PICKED_RESULT_CODE) {
            int alarmTimesIndex = data.getIntExtra(
                    AlarmTimePickerFragment.ALARM_PICKED_INTENT_KEY, 0);
            onAlarmTimesIndexPicked(alarmTimesIndex);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showAlarmTimePicker() {
        AlarmTimePickerFragment.show(this, SESSION_DETAILS_FRAGMENT_REQUEST_CODE);
    }

    private void onAlarmTimesIndexPicked(int alarmTimesIndex) {
        Activity activity = requireActivity();
        if (session != null) {
            FahrplanMisc.addAlarm(activity, appRepository, session, alarmTimesIndex);
        } else {
            Log.e(getClass().getSimpleName(), "onAlarmTimesIndexPicked: session: null. alarmTimesIndex: " + alarmTimesIndex);
        }
        refreshUI(activity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Session session;
        Activity activity = requireActivity();
        switch (item.getItemId()) {
            case R.id.menu_item_feedback: {
                session = sessionOf(sessionId);
                String feedbackUrl = new FeedbackUrlComposer(session, SCHEDULE_FEEDBACK_URL).getFeedbackUrl();
                Uri uri = Uri.parse(feedbackUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }
            case R.id.menu_item_share_session:
            case R.id.menu_item_share_session_text:
                session = sessionOf(sessionId);
                String formattedSession = SimpleSessionFormat.format(session);
                if (!SessionSharer.shareSimple(activity, formattedSession)) {
                    Toast.makeText(activity, R.string.share_error_activity_not_found, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.menu_item_share_session_json:
                session = sessionOf(sessionId);
                String jsonSession = JsonSessionFormat.format(session);
                if (!SessionSharer.shareJson(activity, jsonSession)) {
                    Toast.makeText(activity, R.string.share_error_activity_not_found, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.menu_item_add_to_calendar:
                session = sessionOf(sessionId);
                CalendarSharing.addToCalendar(session, activity);
                return true;
            case R.id.menu_item_flag_as_favorite:
                if (this.session != null) {
                    this.session.highlight = true;
                    appRepository.updateHighlight(this.session);
                    appRepository.notifyHighlightsChanged();
                }
                refreshUI(activity);
                return true;
            case R.id.menu_item_unflag_as_favorite:
                if (this.session != null) {
                    this.session.highlight = false;
                    appRepository.updateHighlight(this.session);
                    appRepository.notifyHighlightsChanged();
                }
                refreshUI(activity);
                return true;
            case R.id.menu_item_set_alarm:
                showAlarmTimePicker();
                return true;
            case R.id.menu_item_delete_alarm:
                if (this.session != null) {
                    FahrplanMisc.deleteAlarm(activity, appRepository, this.session);
                }
                refreshUI(activity);
                return true;
            case R.id.menu_item_close_session_details:
                closeFragment(activity, FRAGMENT_TAG);
                return true;
            case R.id.menu_item_navigate:
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(BuildConfig.C3NAV_URL + getRoomConvertedForC3Nav()));
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshUI(@NonNull Activity activity) {
        activity.invalidateOptionsMenu();
        activity.setResult(Activity.RESULT_OK);
    }

    private void closeFragment(@NonNull Activity activity, @NonNull String fragmentTag) {
        if (activity instanceof OnSidePaneCloseListener) {
            ((OnSidePaneCloseListener) activity).onSidePaneClose(fragmentTag);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApp.LogDebug(LOG_TAG, "onDestroy");
    }
}

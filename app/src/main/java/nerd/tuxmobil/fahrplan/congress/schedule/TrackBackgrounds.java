package nerd.tuxmobil.fahrplan.congress.schedule;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import nerd.tuxmobil.fahrplan.congress.R;

public class TrackBackgrounds {

    public static Map<String, String> getHashMapResource(Context c, int hashMapResId) {
        Map<String, String> map = null;
        XmlResourceParser parser = c.getResources().getXml(hashMapResId);

        String key = null;
        String value = null;

        try {
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                } else if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("map")) {
                        boolean isLinked = parser.getAttributeBooleanValue(null, "linked", false);

                        map = isLinked ? new LinkedHashMap<>() : new HashMap<>();
                    } else if (parser.getName().equals("entry")) {
                        key = parser.getAttributeValue(null, "key");

                        if (null == key) {
                            parser.close();
                            return null;
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (parser.getName().equals("entry")) {
                        map.put(key, value);
                        key = null;
                        value = null;
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    if (null != key) {
                        value = parser.getText();
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    public static Map<String, Integer> buildTrackBackgroundHashMap(Map<String,
            String> trackNamesMap, String prefix, String resourceType, Context context) {
        HashMap<String, Integer> trackNameResourceIdPairs = new HashMap<>();
        Resources res = context.getResources();
        String packageName = context.getPackageName();

        for (Map.Entry<String, String> entry : trackNamesMap.entrySet()) {
            String key = entry.getKey();
            String value = prefix;
            // Handle empty track names
            // key can have the value: ""
            // See track_resource_names.xml
            if (!TextUtils.isEmpty(key)) {
                value += "_" + entry.getValue();
            }
            int resourceId = res.getIdentifier(value, resourceType, packageName);
            trackNameResourceIdPairs.put(key, resourceId);
        }
        return trackNameResourceIdPairs;
    }

    public static Map<String, Integer> getTrackNameBackgroundColorDefaultPairs(Context context) {
        Map<String, String> trackNames = getHashMapResource(context, R.xml.track_resource_names);
        return buildTrackBackgroundHashMap(trackNames, "track_background_default", "color", context);
    }

    public static Map<String, Integer> getTrackNameBackgroundColorHighlightPairs(Context context) {
        Map<String, String> trackNames = getHashMapResource(context, R.xml.track_resource_names);
        return buildTrackBackgroundHashMap(trackNames, "track_background_highlight", "color", context);
    }

}

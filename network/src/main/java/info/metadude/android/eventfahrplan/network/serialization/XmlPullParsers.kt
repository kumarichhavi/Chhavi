@file:JvmName("XmlPullParsers")

package info.metadude.android.eventfahrplan.network.serialization

import androidx.annotation.NonNull
import org.xmlpull.v1.XmlPullParser

@NonNull
fun XmlPullParser.getSanitizedText(): String = text?.trim() ?: ""

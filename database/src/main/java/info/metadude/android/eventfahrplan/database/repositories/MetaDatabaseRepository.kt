package info.metadude.android.eventfahrplan.database.repositories

import android.content.ContentValues
import android.database.sqlite.SQLiteException
import android.util.Log
import info.metadude.android.eventfahrplan.database.contract.FahrplanContract.MetasTable
import info.metadude.android.eventfahrplan.database.contract.FahrplanContract.MetasTable.Columns.*
import info.metadude.android.eventfahrplan.database.extensions.delete
import info.metadude.android.eventfahrplan.database.extensions.getInt
import info.metadude.android.eventfahrplan.database.extensions.getString
import info.metadude.android.eventfahrplan.database.extensions.insert
import info.metadude.android.eventfahrplan.database.extensions.read
import info.metadude.android.eventfahrplan.database.extensions.upsert
import info.metadude.android.eventfahrplan.database.models.Meta
import info.metadude.android.eventfahrplan.database.sqliteopenhelper.MetaDBOpenHelper

class MetaDatabaseRepository(

        private val sqLiteOpenHelper: MetaDBOpenHelper

) {

    fun insert(values: ContentValues) = with(sqLiteOpenHelper) {
        writableDatabase.upsert({
            delete(MetasTable.NAME)
        }, {
            insert(MetasTable.NAME, values)
        })
    }

    fun query(): Meta {
        val database = sqLiteOpenHelper.readableDatabase

        val cursor = try {
            database.read(MetasTable.NAME)
        } catch (e: SQLiteException) {
            e.printStackTrace()
            return Meta()
        }

        val meta = cursor.use {
            if (cursor.moveToFirst()) {
                Meta(
                        numDays = cursor.getInt(NUM_DAYS),
                        version = cursor.getString(VERSION),
                        title = cursor.getString(TITLE),
                        subtitle = cursor.getString(SUBTITLE),
                        eTag = cursor.getString(ETAG)
                )
            } else {
                Meta()
            }
        }

        Log.d(javaClass.simpleName, "query(): $meta")

        return meta
    }

}

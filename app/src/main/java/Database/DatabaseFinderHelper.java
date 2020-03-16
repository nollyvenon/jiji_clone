package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import data.FindPoster;
import others.Constants;

public class DatabaseFinderHelper extends SQLiteOpenHelper {
    Context context;
    public DatabaseFinderHelper(@Nullable Context context) {
        super(context, Constants.DBNAME_FIND, null, Constants.VERSION_FIND);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createdTable = "CREATE TABLE IF NOT EXISTS " + Constants.DBTABLE_FIND + "(" + Constants.DBID_FIND + " INTEGER PRIMARY KEY, " +
                Constants.DBDATE_FIND + " LONG, " + Constants.DBUSERNAME_FIND + " TEXT, " + Constants.DBPHONE_FIND + " TEXT );";
        db.execSQL(createdTable);
    }

    public void saveFindPoster(FindPoster findPoster) {
        SQLiteDatabase db = this.getWritableDatabase();

        String username = findPoster.getUsername();
        String phoneNumber = findPoster.getPhoneNumber();

        ContentValues values = new ContentValues();
        values.put(Constants.DBUSERNAME_FIND, username);
        values.put(Constants.DBPHONE_FIND, phoneNumber);
        values.put(Constants.DBDATE_FIND, System.currentTimeMillis());

        long isInserted = db.insert(Constants.DBTABLE_FIND, null, values);
        if(isInserted != -1) {
            Toast.makeText(context, "Profile Update Successful", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Profile Update Failed", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void updateFindPoster(FindPoster findPoster) {
        SQLiteDatabase db = this.getWritableDatabase();

        String username = findPoster.getUsername();
        String phoneNumber = findPoster.getPhoneNumber();

        ContentValues values = new ContentValues();
        values.put(Constants.DBUSERNAME_FIND, username);
        values.put(Constants.DBPHONE_FIND, phoneNumber);
        values.put(Constants.DBDATE_FIND, System.currentTimeMillis());

        db.update(Constants.DBTABLE_FIND, values, Constants.DBID_FIND + " = ?", new String[]{"0"});
        db.close();
    }

    public ArrayList<FindPoster> fetchFindPoster() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<FindPoster> findPosters = new ArrayList<>();

        Cursor cursor = db.query(Constants.DBTABLE_FIND, new String[]{
                Constants.DBID, Constants.DBDATE_FIND, Constants.DBUSERNAME_FIND, Constants.DBPHONE_FIND},
                null, null, null,
                null, Constants.DBDATE_FIND + " DESC ");

        if (cursor.moveToFirst()) {
            do {

                FindPoster findPoster = new FindPoster();
                findPoster.setId(cursor.getInt(cursor.getColumnIndex(Constants.DBID)));
                findPoster.setUsername(cursor.getString(cursor.getColumnIndex(Constants.DBUSERNAME)));
                findPoster.setPhoneNumber(cursor.getString(cursor.getColumnIndex(Constants.DBPHONE)));

                DateFormat dateFormat = DateFormat.getDateInstance();
                String createdAt = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.DBDATE))).getTime());
                findPoster.setCreatedAt(createdAt);

                findPosters.add(findPoster);

            } while (cursor.moveToNext());
        }

        db.close();
        return findPosters;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.DBTABLE_FIND);
        onCreate(db);
    }
}

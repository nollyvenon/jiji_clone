package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import others.Constants;
import data.AdPoster;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    Context context;

    public DatabaseOpenHelper(Context context) {
        super(context, Constants.DBNAME, null, Constants.VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createdTable = "CREATE TABLE IF NOT EXISTS " + Constants.DBTABLE + "(" +
                Constants.DBID + " INTEGER PRIMARY KEY, " + Constants.DBADS + " TEXT, " + Constants.DBFINDS + " TEXT, " +
                Constants.DBPHONE + " TEXT, " + Constants.DBAUTH + " TEXT, " + Constants.DBUSERTYPE + " TEXT );";
        db.execSQL(createdTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.DBTABLE);
        onCreate(db);
    }

    public void saveAdPoster(AdPoster adPosters) {
        SQLiteDatabase db = this.getWritableDatabase();

        String userType = adPosters.getUserType();
        String phoneNumber = adPosters.getVerifiedPhoneNumber();
        String auth = adPosters.getAuth();

        ContentValues values = new ContentValues();
        values.put(Constants.DBPHONE, phoneNumber);
        values.put(Constants.DBAUTH, auth);
        values.put(Constants.DBUSERTYPE, userType);

        if (userType != null){
            if (userType.equals(Constants.ADS)) {
                values.put(Constants.DBADS, userType);
                values.put(Constants.DBFINDS, "");
            }

            if (userType.equals(Constants.FINDS)) {
                values.put(Constants.DBFINDS, userType);
                values.put(Constants.DBADS, "");
            }
        }

        long isInserted = db.insert(Constants.DBTABLE, null, values);
        if (isInserted != -1) {
            Log.d("b40", "success");
        } else {
            Log.d("b40", "failed");
        }
        db.close();
    }

    public void updateAdPoster(AdPoster adPoster) {
        SQLiteDatabase db = this.getWritableDatabase();

        String auth = adPoster.getAuth();
        String phoneNumber = adPoster.getVerifiedPhoneNumber();
        String userType = adPoster.getUserType();

        ContentValues values = new ContentValues();

        if(phoneNumber != null) values.put(Constants.DBPHONE, phoneNumber);
        if(auth != null) values.put(Constants.DBAUTH, auth);

        if (userType != null) {
            values.put(Constants.DBUSERTYPE, userType);
            if (userType.equals(Constants.ADS)) {
                values.put(Constants.DBADS, userType);
                values.put(Constants.DBFINDS, "");
            }

            if (userType.equals(Constants.FINDS)) {
                values.put(Constants.DBFINDS, userType);
                values.put(Constants.DBADS, "");
            }
        }

        db.update(Constants.DBTABLE, values, Constants.DBID + "= 1", null);
        db.close();
    }

    public AdPoster getAdPoster() {
        AdPoster finder = new AdPoster();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.DBTABLE, new String[]{Constants.DBID, Constants.DBPHONE,
                        Constants.DBAUTH, Constants.DBUSERTYPE, Constants.DBADS, Constants.DBFINDS},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            finder.setID(cursor.getInt(cursor.getColumnIndex(Constants.DBID)));
            finder.setVerifiedPhoneNumber(cursor.getString(cursor.getColumnIndex(Constants.DBPHONE)));
            finder.setAuth(cursor.getString(cursor.getColumnIndex(Constants.DBAUTH)));
            finder.setUserType(cursor.getString(cursor.getColumnIndex(Constants.DBUSERTYPE)));
            finder.setAds(cursor.getString(cursor.getColumnIndex(Constants.DBADS)));
            finder.setFinds(cursor.getString(cursor.getColumnIndex(Constants.DBFINDS)));
        }

        db.close();
        return finder;
    }

    public void removeAdPoster(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.DBTABLE, Constants.DBID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}

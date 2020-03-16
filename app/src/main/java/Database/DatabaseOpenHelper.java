package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import others.Constants;
import data.AdPoster;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    Context context;
    ArrayList<AdPoster> adPosters = new ArrayList<>();

    public DatabaseOpenHelper(Context context) {
        super(context, Constants.DBNAME, null, Constants.VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createdTable = "CREATE TABLE IF NOT EXISTS " + Constants.DBTABLE + "(" +
                Constants.DBID + " INTEGER PRIMARY KEY, " + Constants.DBDATE + " LONG, " + Constants.DBLOGO + " INTEGER, " +
                Constants.DBLOCATION + " TEXT, " + Constants.DBMARKETAREA + " TEXT, " + Constants.DBBUSINESEXP + " TEXT, " +
                Constants.DBUSERNAME + " TEXT, " + Constants.DBBUSINESSNAME + " TEXT," + Constants.DBBUSINESSDES + " TEXT, " +
                Constants.DBPHONE + " TEXT, " + Constants.DBSERVICEDES + " TEXT, " + Constants.DBACCOUNTNUM + " TEXT );";
        db.execSQL(createdTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.DBTABLE);
        onCreate(db);
    }

    public void saveAdPoster(AdPoster adPosters) {
        SQLiteDatabase db = this.getWritableDatabase();

        String location = adPosters.getLocation();
        String marketArea = adPosters.getMarketArea();
        String businessYear = adPosters.getBusinessYear();
        String username = adPosters.getUsername();
        String businessName = adPosters.getBusinessName();
        String businessDescription = adPosters.getBusinessDescription();
        String serviceDescription = adPosters.getServiceDescription();
        String phoneNumber = adPosters.getPhoneNumber();
        String accountNumber = adPosters.getAccountNumber();

        ContentValues values = new ContentValues();
        values.put(Constants.DBLOCATION, location);
        values.put(Constants.DBMARKETAREA, marketArea);
        values.put(Constants.DBBUSINESEXP, businessYear);
        values.put(Constants.DBUSERNAME, username);
        values.put(Constants.DBBUSINESSNAME, businessName);
        values.put(Constants.DBBUSINESSDES, businessDescription);
        values.put(Constants.DBSERVICEDES, serviceDescription);
        values.put(Constants.DBPHONE, phoneNumber);
        values.put(Constants.DBACCOUNTNUM, accountNumber);
        values.put(Constants.DBDATE, System.currentTimeMillis());

        long isInserted = db.insert(Constants.DBTABLE, null, values);
        if (isInserted != -1) {
            Toast.makeText(context, "Profile Update Successful", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Profile Update Failed", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void updateAdPoster(AdPoster adPoster) {
        SQLiteDatabase db = this.getWritableDatabase();

        String location = adPoster.getLocation();
        String marketArea = adPoster.getMarketArea();
        String businessYear = adPoster.getBusinessYear();
        String username = adPoster.getUsername();
        String businessName = adPoster.getBusinessName();
        String businessDescription = adPoster.getBusinessDescription();
        String serviceDescription = adPoster.getServiceDescription();
        String phoneNumber = adPoster.getPhoneNumber();
        String accountNumber = adPoster.getAccountNumber();

        ContentValues values = new ContentValues();
        values.put(Constants.DBLOCATION, location);
        values.put(Constants.DBMARKETAREA, marketArea);
        values.put(Constants.DBBUSINESEXP, businessYear);
        values.put(Constants.DBUSERNAME, username);
        values.put(Constants.DBBUSINESSNAME, businessName);
        values.put(Constants.DBBUSINESSDES, businessDescription);
        values.put(Constants.DBSERVICEDES, serviceDescription);
        values.put(Constants.DBPHONE, phoneNumber);
        values.put(Constants.DBACCOUNTNUM, accountNumber);
        values.put(Constants.DBDATE, System.currentTimeMillis());

        db.update(Constants.DBTABLE, values, Constants.DBID + " = ?", new String[]{"0"});
        db.close();
    }

    public ArrayList<AdPoster> getAdPoster() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.DBTABLE, new String[]{Constants.DBID, Constants.DBDATE, Constants.DBBUSINESSNAME,
                        Constants.DBLOCATION, Constants.DBMARKETAREA, Constants.DBBUSINESEXP, Constants.DBUSERNAME,
                        Constants.DBBUSINESSDES, Constants.DBSERVICEDES, Constants.DBPHONE, Constants.DBACCOUNTNUM},
                null, null, null,
                null, Constants.DBDATE + " DESC ");

        if (cursor.moveToFirst()) {
            AdPoster finder = new AdPoster();
            finder.setID(cursor.getInt(cursor.getColumnIndex(Constants.DBID)));
            finder.setLocation(cursor.getString(cursor.getColumnIndex(Constants.DBLOCATION)));
            finder.setMarketArea(cursor.getString(cursor.getColumnIndex(Constants.DBMARKETAREA)));
            finder.setBusinessYear(cursor.getString(cursor.getColumnIndex(Constants.DBBUSINESEXP)));
            finder.setUsername(cursor.getString(cursor.getColumnIndex(Constants.DBUSERNAME)));
            finder.setBusinessDescription(cursor.getString(cursor.getColumnIndex(Constants.DBBUSINESSDES)));
            finder.setServiceDescription(cursor.getString(cursor.getColumnIndex(Constants.DBSERVICEDES)));
            finder.setPhoneNumber(cursor.getString(cursor.getColumnIndex(Constants.DBPHONE)));
            finder.setAccountNumber(cursor.getString(cursor.getColumnIndex(Constants.DBACCOUNTNUM)));
            finder.setBusinessName(cursor.getString(cursor.getColumnIndex(Constants.DBBUSINESSNAME)));

            DateFormat dateFormat = DateFormat.getDateInstance();
            String createdAt = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.DBDATE))).getTime());
            finder.setCreatedAt(createdAt);

            adPosters.add(finder);
        }

        db.close();
        return adPosters;
    }

    public void removeAdPoster(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.DBTABLE, Constants.DBID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}

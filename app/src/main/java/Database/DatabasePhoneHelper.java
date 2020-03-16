package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import data.VerifiedPhoneNumber;
import others.Constants;

public class DatabasePhoneHelper extends SQLiteOpenHelper {

    ArrayList<VerifiedPhoneNumber> verifiedPhoneNumbers = new ArrayList<>();

    public DatabasePhoneHelper(@Nullable Context context) {
        super(context, Constants.DBNAME_PHONE, null, Constants.DBVERSION_PHONE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createdTable = "CREATE TABLE " + Constants.DBTABLE_PHONE + "(" + Constants.DBUSERTYPE_PHONE + " TEXT," +
                Constants.DBID_PHONE + " INTEGER PRIMARY KEY," + Constants.DBDATE_PHONE + " LONG," + Constants.DBPHONE_PHONE + " TEXT);";
        db.execSQL(createdTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.DBTABLE_PHONE);
        onCreate(db);
    }

    public void saveVerifiedPhone(VerifiedPhoneNumber verifiedPhoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        String phoneNumber = verifiedPhoneNumber.getPhoneNumber();
        String userType = verifiedPhoneNumber.getUserType();

        ContentValues values = new ContentValues();
        values.put(Constants.DBPHONE_PHONE, phoneNumber);
        values.put(Constants.DBUSERTYPE_PHONE, userType);
        values.put(Constants.DBDATE_PHONE, System.currentTimeMillis());

        db.insert(Constants.DBTABLE_PHONE, null, values);
        db.close();
    }

    public void updateVerifiedPhone(VerifiedPhoneNumber verifiedPhoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        String phoneNumber = verifiedPhoneNumber.getPhoneNumber();
        String userType = verifiedPhoneNumber.getUserType();

        ContentValues values = new ContentValues();
        values.put(Constants.DBPHONE_PHONE, phoneNumber);
        values.put(Constants.DBUSERTYPE_PHONE, userType);
        values.put(Constants.DBDATE_PHONE, System.currentTimeMillis());

        db.update(Constants.DBTABLE_PHONE, values, Constants.DBID_PHONE + " = ?", new String[]{"0"});
        db.close();
    }

    public ArrayList<VerifiedPhoneNumber> getVerifiedPhone() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.DBTABLE_PHONE, new String[]{Constants.DBID_PHONE, Constants.DBDATE_PHONE,
                        Constants.DBUSERTYPE_PHONE, Constants.DBPHONE_PHONE},
                null, null, null, null, Constants.DBDATE_PHONE + " DESC", "1");

        if (cursor.moveToFirst()) {
            VerifiedPhoneNumber verifiedPhoneNumber = new VerifiedPhoneNumber();
            verifiedPhoneNumber.setId(cursor.getInt(cursor.getColumnIndex(Constants.DBID_PHONE)));
            verifiedPhoneNumber.setUserType(cursor.getString(cursor.getColumnIndex(Constants.DBUSERTYPE_PHONE)));
            verifiedPhoneNumber.setPhoneNumber(cursor.getString(cursor.getColumnIndex(Constants.DBPHONE_PHONE)));

            DateFormat dateFormat = DateFormat.getDateInstance();
            String createdAt = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.DBDATE_PHONE))).getTime());
            verifiedPhoneNumber.setCreatedAt(createdAt);

            verifiedPhoneNumbers.add(verifiedPhoneNumber);
        }

        db.close();
        return verifiedPhoneNumbers;
    }

    public void removeVerifiedPhoneNumbers(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.DBTABLE_PHONE, Constants.DBID_PHONE + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}

package fadel.mfadel.app1test;

/**
 * Created by Angel on 2017-05-22.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SettingsDB.db";
    public static final String SETTINGS_TABLE_NAME = "tblSettings";
    public static final String SETTINGS_COLUMN_ID = "id";
    public static final String SETTINGS_COLUMN_FTPIP = "ftpip";
    public static final String SETTINGS_COLUMN_FTPUN = "ftpun";
    public static final String SETTINGS_COLUMN_FTPPASS = "ftppass";
    public static final String SETTINGS_COLUMN_SQLIP = "sqlip";
    public static final String SETTINGS_COLUMN_SQLUN = "sqlun";
    public static final String SETTINGS_COLUMN_SQLPASS = "sqlpass";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table tblSettings " +
                        "(id integer primary key, ftpip text,ftpun text,ftppass text, sqlip text,sqlun text,sqlpass text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS tblSettings");
        onCreate(db);
    }

    public boolean insertSettings (String ftp_ip, String ftp_un, String ftp_pass, String sql_ip,String sql_un, String sql_pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ftpip", ftp_ip);
        contentValues.put("ftpun", ftp_un);
        contentValues.put("ftppass", ftp_pass);
        contentValues.put("sqlip", sql_ip);
        contentValues.put("sqlun", sql_un);
        contentValues.put("sqlpass", sql_pass);
        db.insert("tblSettings", null, contentValues);
        return true;
    }

    public Cursor getDatabyID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from tblSettings where id="+id+"", null );
        return res;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from tblSettings ", null );
        return res;
    }
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SETTINGS_TABLE_NAME);
        return numRows;
    }

    public boolean updateSettings (Integer id, String ftp_ip, String ftp_un, String ftp_pass, String sql_ip,String sql_un, String sql_pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ftpip", ftp_ip);
        contentValues.put("ftpun", ftp_un);
        contentValues.put("ftppass", ftp_pass);
        contentValues.put("sqlip", sql_ip);
        contentValues.put("sqlun", sql_un);
        contentValues.put("sqlpass", sql_pass);
        db.update("tblSettings", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deletesettings (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("tblSettings",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllSettings() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from tblSettings", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(SETTINGS_COLUMN_FTPIP)));
            res.moveToNext();
        }
        return array_list;
    }
}
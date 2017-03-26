package example.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DELL on 4/2/2016.
 */
public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "fav.db";
    static final String COLOMN_NAME = "movieName";
    static final String TABLE_NAME = "fav";
    static final String ID = "ID";
    Context context;

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " +TABLE_NAME+ "(" + ID + " INTEGER PRIMARY KEY," +COLOMN_NAME +
                " TEXT NOT NULL "+" );";

        db.execSQL(SQL_CREATE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);

    }
    public long add(String name ){
        ContentValues newValues = new ContentValues();
        newValues.put(COLOMN_NAME, name);
        SQLiteDatabase db = getWritableDatabase();
        long test = db.insert(TABLE_NAME, null, newValues);
        return  test;

    }


    public long delete( String name ){
        //COLOMN_NAME+" like ?"
        String where = COLOMN_NAME+ "='" + name+"'";
        String whereArgs[] = null;
        SQLiteDatabase db = getWritableDatabase();
        long test = db.delete(TABLE_NAME,where, whereArgs);
        return test;

    }
    public long clear(){
        //COLOMN_NAME+" like ?"
        String where = null;
        String whereArgs[] = null;
        SQLiteDatabase db = getWritableDatabase();
        long test = db.delete(TABLE_NAME,where, whereArgs);
        return test;

    }
//    public long update(String nameOld, String numberNew){
//        ContentValues updatedValues = new ContentValues();
//        updatedValues.put(COLOMN_NUMBER, numberNew);
//        String where = COLOMN_NAME + "=" + nameOld;
//        String whereArgs[] = null;
//        SQLiteDatabase db = getWritableDatabase();
//        long test =db.update(TABLE_NAME,updatedValues,"name like ?",new String[]{nameOld});
//        return test;
//
//
//
//    }
}

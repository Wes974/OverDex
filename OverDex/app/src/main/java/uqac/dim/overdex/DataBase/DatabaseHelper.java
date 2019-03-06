package uqac.dim.overdex.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase myDataBase;
    private final Context myContext;
    private static String DATABASE_NAME = "overdex.db";
    private static String DATABASE_PATH ="";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        this.myContext = context;
    }

    public void createDatabase() throws IOException {

        boolean dbExist = checkDataBase();
        if (dbExist) {
            Log.v("DIM", "DB exists");
        } else {
            Log.v("DIM", "DB not exists");
            try {
                this.getReadableDatabase();
                this.close();
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");}
        }
    }

    private boolean checkDataBase(){
        boolean checkDB = false;
        try{
            File dbfile = new File(DATABASE_PATH + DATABASE_NAME);
            checkDB = dbfile.exists();
        }
        catch(SQLiteException e){
            Log.v("DIM", e.getMessage());
        }
        return checkDB;
    }

    private void copyDataBase() throws IOException{
        OutputStream myOutput = new FileOutputStream(DATABASE_PATH + DATABASE_NAME);
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

    public void db_delete(){
        File file = new File(DATABASE_PATH + DATABASE_NAME);
        if(file.exists()){
            file.delete();
            System.out.println("delete database file.");
        }
    }

    public void openDatabase() throws SQLException {
        String myPath = DATABASE_PATH + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDataBase() throws SQLException {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db_delete();
        }
    }
}
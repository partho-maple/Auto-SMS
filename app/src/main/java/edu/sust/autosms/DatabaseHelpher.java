package edu.sust.autosms;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelpher extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "contactsManager";
    private static final String TABLE_DATA = "data";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_TAGS = "tags";
    private static final String KEY_ANSWER = "answer";
    ArrayList <String> DBtrueIDsForRececler = new ArrayList<String>();;

    Context context;

    public DatabaseHelpher(Context context) {
        // superclass constructor
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Log.d("QQQ", "--- onCreate database ---");
        // create table with fields
        String CREATE_DATA_TABLE = "CREATE TABLE " + TABLE_DATA + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_NUMBER + " TEXT," + KEY_TAGS + " TEXT," + KEY_ANSWER + " TEXT" + ")";
        db.execSQL(CREATE_DATA_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);

        onCreate(db);
    }

    /* Insert into database*/
    public void insertIntoDB(String name,String number,String tags,String answer){
        Log.d("insert", "before insert");

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_NUMBER, number);
        values.put(KEY_TAGS, tags);
        values.put(KEY_ANSWER, answer);

        // 3. insert
        long id = db.insert(TABLE_DATA, null, values);

        showDB();

        // 4. close
        db.close();
        Toast.makeText(context, "insert value", Toast.LENGTH_LONG);
        Log.i("insert into DB", "After insert");
    }
    /* Update into database*/
    public void updateIntoDB(String name,String number,String tags,String answer, String position){
        Log.d("update", "update db");

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_NUMBER, number);
        values.put(KEY_TAGS, tags);
        values.put(KEY_ANSWER, answer);

        // 5. get true position update's datas
        add_to_list(); //updating array list data
        String listID = DBtrueIDsForRececler.get(Integer.parseInt(position));

        // 4. update
        long id = db.update(TABLE_DATA, values, "id = ?",
                new String[] { listID });

        showDB();

        // 5. close
        db.close();
        Toast.makeText(context, "insert value", Toast.LENGTH_LONG);
        Log.i("insert into DB", "After insert");
    }
    /* Retrive  data from database */
    public List<DatabaseSettingGetting> getDataFromDB(){
        List<DatabaseSettingGetting> modelList = new ArrayList<DatabaseSettingGetting>();
        String query = "select * from "+TABLE_DATA;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        int nameColIndex = cursor.getColumnIndex(KEY_NAME);
        int numberColIndex = cursor.getColumnIndex(KEY_NUMBER);
        int tagsColIndex = cursor.getColumnIndex(KEY_TAGS);
        int answerColIndex = cursor.getColumnIndex(KEY_ANSWER);

        if (cursor.moveToFirst()){
            do {
                DatabaseSettingGetting DBdata = new DatabaseSettingGetting();
                DBdata.setName(cursor.getString(nameColIndex));
                DBdata.setNumber(cursor.getString(numberColIndex));
                DBdata.setTags(cursor.getString(tagsColIndex));
                DBdata.setAnswer(cursor.getString(answerColIndex));

                modelList.add(DBdata);
            }while (cursor.moveToNext());
        }

        Log.d("student data", modelList.toString());

        return modelList;
    }


    /*delete a row from database*/

    public void deleteARow(String id){ //delete items from the database
        SQLiteDatabase db= this.getWritableDatabase();

        add_to_list(); //updating array list data

        String listID = DBtrueIDsForRececler.get(Integer.parseInt(id));

        db.delete(TABLE_DATA, "id = " + listID, null);
        DBtrueIDsForRececler.remove(Integer.parseInt(id));
        db.close();
    }

    public void showDB(){ //Output of database elements to console

        SQLiteDatabase db = this.getWritableDatabase();
        //make a request for all data from the table mytable, we get the Cursor
        Cursor c = db.query(TABLE_DATA, null, null, null, null, null, null);
        // put the cursor position on the first row of the sample
        // if there are no rows in the selection, false will return
        if (c.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex(KEY_ID);
            int nameColIndex = c.getColumnIndex(KEY_NAME);
            int numberColIndex = c.getColumnIndex(KEY_NUMBER);
            int tagsColIndex = c.getColumnIndex(KEY_TAGS);
            int answerColIndex = c.getColumnIndex(KEY_ANSWER);

            do {
                // we get the values ​​by column numbers and write everything to the log
                Log.d("QQQ",
                        "ID = " + c.getInt(idColIndex) +
                                ", name = " + c.getString(nameColIndex) +
                                ", number = " + c.getString(numberColIndex)+
                                ", tags = " + c.getString(tagsColIndex)+
                                ", answer = " + c.getString(answerColIndex));
                // go to next line
                // and if there is no next (current - last), then false - exit the loop

            } while (c.moveToNext());
        } else
            Log.d("QQQ", "0 rows");
        c.close();

    }

    public void add_to_list(){  //Adding DB ID data to the list, for correct deletion of Recycler elements and DB
        SQLiteDatabase db = this.getWritableDatabase();
        // make a request for all data from the table mytable, we get the Cursor
        Cursor c = db.query(TABLE_DATA, null, null, null, null, null, null);

        // put the cursor position on the first row of the sample
        //if there are no rows in the selection, false will return
        if (c.moveToFirst()) {
            // determine column numbers by name in the selection
            int idColIndex = c.getColumnIndex(KEY_ID);

            do {
                //List the ID values ​​that are in the database.
                DBtrueIDsForRececler.add(c.getString(idColIndex));

            } while (c.moveToNext());

        } else
            Log.d("QQQ", "0 rows");
        c.close();

        for (int i = 0; i < DBtrueIDsForRececler.size(); i++) {
            Log.d("ListID: "+i,DBtrueIDsForRececler.get(i));
        }
    }


}

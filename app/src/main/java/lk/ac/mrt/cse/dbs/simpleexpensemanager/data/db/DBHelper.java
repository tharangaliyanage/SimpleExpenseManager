package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.database.Cursor;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "200100K.db";
    private static final String TABLE_ACCOUNT = "account";
    private static final String TABLE_TRANSACTION = "transactions";
    private static final int DEFAULT_LIMIT = 0;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 2);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_ACCOUNT+
                " (accountno TEXT PRIMARY KEY ,"+
                "bankname TEXT  ,"+
                "accountHolderName TEXT, "+
                "balance REAL"
                +")");

        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_TRANSACTION+
                " (transaction_no INTEGER  PRIMARY KEY AUTOINCREMENT,"+
                "accountno TEXT  ,"+
                "date TEXT, "+
                "expenseType TEXT ,"+
                "amount REAL"
                +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_ACCOUNT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_TRANSACTION);
        onCreate(sqLiteDatabase);
    }


    public boolean insertData(String table_name,ContentValues content) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long result;
        try {
            result = sqLiteDatabase.insertOrThrow(table_name, null,content);
        }
        catch (Exception e) {
            result = -1;
            System.out.println("Insert error");
        }

        if (result == -1) {
            return false;
        }else {
            return true;
        }
    }



    public Cursor getDataWithLimit(String table_name, String [] columns, String [][] conditions, int limit) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String column = "";
        if (columns.length != 0) {
            for (int i = 0; i < columns.length; i++) {
                column += columns[i]+" , ";
            }
            column = column.substring(0,column.length() - 2);
        }
        String condition = "";
        String[] args = null;
        if (conditions.length != 0) {
            args = new String[conditions.length];
            condition += " WHERE ";
            for (int i = 0; i < conditions.length; i++) {
                if (conditions[i].length == 3) {
                    String[] temp = conditions[i];
                    condition += temp[0] + " "+temp[1]+" ? AND ";
                    args[i] = temp[2];
                }

            }
            condition = condition.substring(0,condition.length() - 4);
        }
        else
        {
            condition = "";
        }
        String lim = "";
        if (limit != 0) {
            lim = " LIMIT "+String.valueOf(limit);
        }

        String sql = "select "+column+" from " + table_name + condition + lim;
        Cursor result = sqLiteDatabase.rawQuery(sql, args);
        return result;
    }

    public Cursor getData(String table_name, String [] columns, String [][] conditions) {
        return getDataWithLimit(table_name, columns, conditions, DEFAULT_LIMIT);
    }

    public boolean updateData(String table_name, ContentValues content, String[ ] condition) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String cond = condition[0]+" "+condition[1]+" ? ";
        String[] args = {condition[2]};

        long result;
        try {
            result = sqLiteDatabase.update(table_name, content, cond, args);
        }catch (Exception e){

            result = -1;
        }

        if (result == -1) {
            return false;
        }else{
            return true;
        }
    }

    public Integer deleteData(String table_name, String column, String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(table_name, column+" = ?", new String[] {id});
    }

    public void deleteTableContent(String table_name) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from "+ table_name);
    }





}
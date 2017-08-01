package com.example.kanduri.projectriktam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Kanduri on 01-07-2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    ArrayList<String> str;

    long gid;

    public static final String DATABASE_NAME="expensemanager.db";

    public DatabaseHelper(Context context)
    {
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("CREATE TABLE group_table ( ID INTEGER PRIMARY KEY AUTOINCREMENT,GNAME TEXT,CTIME CURRENT_TIMESTAMP )");
        sqLiteDatabase.execSQL("CREATE TABLE member_table ( ID INTEGER PRIMARY KEY AUTOINCREMENT,GID INTEGER,NAME TEXT )");
        sqLiteDatabase.execSQL("CREATE TABLE spent_table ( ID INTEGER PRIMARY KEY AUTOINCREMENT,GID INTEGER,NAME TEXT,PAYEE INTEGER,AMT REAL,DATE TEXT )");
        sqLiteDatabase.execSQL("CREATE TABLE expense_table ( ID INTEGER PRIMARY KEY AUTOINCREMENT,GID INTEGER,AID INTEGER, MID INTEGER,SHARE REAL )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS group_table");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS member_table");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS spent_table");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS expense_table");
        onCreate(sqLiteDatabase);
    }

    public long insertGroupData(String gname)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("GNAME",gname);
        contentValues.put("CTIME",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        long id=db.insert("group_table",null,contentValues);
        return id;
    }

    public long insertMemberData(long gid,String name)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("GID",gid);
        contentValues.put("NAME",name);
        long id=db.insert("member_table",null,contentValues);
        return id;
    }

    public long insertActivityData(long gid,String aname,long payee,double amt,String date)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("GID",gid);
        contentValues.put("NAME",aname);
        contentValues.put("PAYEE",payee);
        contentValues.put("AMT",amt);
        contentValues.put("DATE",date);
        long id=db.insert("spent_table",null,contentValues);
        return id;
    }

    public long insertExpense(long gid,long aid,long mid,double share)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("GID",gid);
        contentValues.put("AID",aid);
        contentValues.put("MID",mid);
        contentValues.put("SHARE",share);
        long id=db.insert("expense_table",null,contentValues);
        return id;
    }

    public void updateActivityData(long gid,long aid,long mid,String aname,double spent,String date)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("UPDATE spent_table SET NAME=+\""+aname+"\",PAYEE="+mid+",AMT="+spent+",DATE=\""+date+"\" WHERE GID="+gid+" AND ID="+aid);
    }

    public ArrayList<String> storeMemberData(long group_id)
    {
        gid = group_id;
        SQLiteDatabase db=this.getWritableDatabase();
        str=new ArrayList<>();
        Cursor res=db.rawQuery("SELECT NAME FROM member_table WHERE GID="+gid+" ORDER BY ID DESC",null);
        while(res.moveToNext())
        {
            str.add(res.getString(res.getColumnIndex("NAME")));
        }
        return str;
    }

    public ArrayList<String> storeGroupData()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        str=new ArrayList<>();
        Cursor res=db.rawQuery("SELECT * FROM group_table ORDER BY ID DESC",null);
        while(res.moveToNext())
        {
            str.add(res.getString(res.getColumnIndex("GNAME")));
        }
        return str;
    }

    public long getMemberId(String mname)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT (SELECT ID FROM member_table WHERE GID="+gid+" AND NAME=\""+mname+"\") AS ID",null);
        if(res!=null && res.moveToNext())
        {
            long ref=res.getLong(res.getColumnIndex("ID"));
            return ref;
        }
        return 0;
    }

    public long getGroupId(String name)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT ID FROM group_table WHERE GNAME=\""+name+"\"",null);
        if(res!=null && res.moveToNext())
        {
            long ref=res.getLong(0);
            return ref;
        }
        return 0;
    }

    public void addShares(long gid,long aid)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("UPDATE expense_table SET SHARE = SHARE + " +
                "(SELECT AMT/" +
                "(SELECT COUNT(*) FROM expense_table WHERE GID="+gid+" AND AID="+aid +
                ") FROM spent_table WHERE GID="+gid+" AND ID="+aid+
                ") WHERE GID="+gid+" AND AID="+aid);
    }

    public Cursor storeReport(long gid)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT NAME,IFNULL(SPENT,0) AS SPENT,IFNULL(SHARE,0) AS SHARE FROM member_table " +
                "LEFT JOIN (SELECT PAYEE,SUM(AMT) AS SPENT FROM spent_table GROUP BY PAYEE) x " +
                "ON member_table.ID=x.PAYEE " +
                "LEFT JOIN (SELECT MID,SUM(SHARE) AS SHARE FROM expense_table GROUP BY MID) y " +
                "ON member_table.ID=y.MID "+
                "WHERE member_table.GID="+gid+
                " GROUP BY member_table.ID",null);
        return res;

    }

    public Cursor storeSpends(long groupid)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT member_table.NAME as NAME,spent_table.NAME as \"SPENT FOR\",spent_table.AMT as AMOUNT," +
                "spent_table.DATE as DATE "+
                "FROM member_table,spent_table "+
                "WHERE member_table.ID=spent_table.PAYEE and spent_table.GID="+groupid+" ORDER BY spent_table.ID DESC",null);
        return res;
    }

    public void deleteGroup(long groupid)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE FROM expense_table where GID="+groupid);
        db.execSQL("DELETE FROM spent_table where GID="+groupid);
        db.execSQL("DELETE FROM member_table where GID="+groupid);
        db.execSQL("DELETE FROM group_table where ID="+groupid);

    }
}

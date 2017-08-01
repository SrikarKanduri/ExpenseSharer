package com.example.kanduri.projectriktam;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

public class SpendsScreen extends AppCompatActivity {
    private long groupid;
    private ListView list_layout;
    private DatabaseHelper myDb;
    private String gname;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spends_screen);
        groupid=getIntent().getLongExtra("groupid",1L);
        gname=getIntent().getStringExtra("groupname");
        getSupportActionBar().setTitle(gname+" Spends Report");
        myDb=new DatabaseHelper(this);
        list_layout=(ListView) findViewById(R.id.listView_spends);
        buildList();
    }
    private void buildList()
    {
        Cursor c = myDb.storeSpends(groupid);
        ArrayList<String> arrayList=new ArrayList<>();
        int rows = c.getCount();
        c.moveToFirst();
        for(int i=0;i<rows;i++) {
            arrayList.add(c.getString(3)+"\n"+String.format(c.getString(0))+" spent INR "+c.getLong(2)+" for "+c.getString(1));
            c.moveToNext();
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.select_dialog_item,arrayList);
        list_layout.setAdapter(adapter);
    }
}

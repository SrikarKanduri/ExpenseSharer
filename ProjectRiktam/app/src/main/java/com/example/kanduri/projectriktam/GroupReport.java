package com.example.kanduri.projectriktam;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class GroupReport extends AppCompatActivity {

    private ArrayList<String> arrayList;
    private String gname;
    private long gid;
    private Intent intent;
    private ListView list_layout;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_report);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        gname=getIntent().getStringExtra("groupname");
        getSupportActionBar().setTitle(gname);
        myDb=new DatabaseHelper(this);
        gid = myDb.getGroupId(gname);

        list_layout=(ListView) findViewById(R.id.listView_members);

        buildList();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(GroupReport.this,AddScreen.class);
                intent.putExtra("groupid",gid);
                intent.putExtra("groupname",gname);
                startActivity(intent);
            }
        });
    }

    private void buildList()
    {
        Cursor c = myDb.storeReport(gid);
        arrayList=new ArrayList<>();
        int rows = c.getCount();
        c.moveToFirst();
        for(int i=0;i<rows;i++) {
         arrayList.add(c.getString(0)+"'s balance : "+Math.round(c.getDouble(2)-c.getDouble(1))+"\n"+
                 "Spent : "+Math.round(c.getDouble(1))+String.format("%15s","Share : ")+Math.round(c.getDouble(2)) );
        c.moveToNext();
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.select_dialog_item,arrayList);
        list_layout.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.spends_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.spends_id:
                intent= new Intent(GroupReport.this,SpendsScreen.class);
                intent.putExtra("groupid",gid);
                intent.putExtra("groupname",gname);
                startActivity(intent);
                break;
            case R.id.delete_grp:
                myDb.deleteGroup(gid);
                intent= new Intent(GroupReport.this,HomeScreen.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

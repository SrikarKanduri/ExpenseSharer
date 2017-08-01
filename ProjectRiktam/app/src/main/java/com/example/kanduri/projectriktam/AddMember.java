package com.example.kanduri.projectriktam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class AddMember extends AppCompatActivity {
    private DatabaseHelper myDb;
    private Button save,addButton;
    private EditText addMember;
    private ListView memberView;
    private ArrayList<String> memberList;
    private long groupid;
    private boolean zeroCheck;
    private String groupname;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        zeroCheck=true;
        myDb=new DatabaseHelper(this);
        groupid=getIntent().getLongExtra("groupid",1L);
        groupname=getIntent().getStringExtra("groupname");
        getSupportActionBar().setTitle(groupname+" Add Member");
        memberView=(ListView)findViewById(R.id.listView_members);
        memberList=new ArrayList<>();

        addMember=(EditText)findViewById(R.id.editText_add_member);
        addButton=(Button)findViewById(R.id.button_add_member);
        save=(Button)findViewById(R.id.button_save);
        onClickAddMember();
    }
    private void onClickAddMember()
    {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addMember.getText().toString().equals(""))
                    Toast.makeText(AddMember.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                else
                {
                    zeroCheck=false;
                    memberList.add(0,addMember.getText().toString());
                    Toast.makeText(AddMember.this, "Member added successfully", Toast.LENGTH_SHORT).show();
                    addMember.setText("");
                    ArrayAdapter<String> instantAdapter=new ArrayAdapter<>(AddMember.this,android.R.layout.select_dialog_item, memberList);
                    memberView.setAdapter(instantAdapter);
                }

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!zeroCheck)
                {
                    for(int i=0;i<memberList.size();i++)
                        myDb.insertMemberData(groupid,memberList.get(i));
                    Intent intent = new Intent(AddMember.this, GroupReport.class);
                    intent.putExtra("groupname", groupname);
                    startActivity(intent);
                }
                else
                    Toast.makeText(AddMember.this, "Group members cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        myDb.deleteGroup(groupid);
        Intent intent = new Intent(AddMember.this, HomeScreen.class);
        startActivity(intent);
    }
}

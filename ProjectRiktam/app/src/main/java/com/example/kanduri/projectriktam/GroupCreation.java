package com.example.kanduri.projectriktam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GroupCreation extends AppCompatActivity {

    private Button btn;
    private EditText gname;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);
        myDb = new DatabaseHelper(this);
        btn=(Button)findViewById(R.id.button_create_group);
        gname=(EditText)findViewById(R.id.editText_gname);
        onGroupCreate();
    }

    private void onGroupCreate()
    {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gname.getText().toString().equals("")) {
                    Toast.makeText(GroupCreation.this,"Please enter the group name",Toast.LENGTH_SHORT).show();
                } else {
                    long isInserted = myDb.insertGroupData(gname.getText().toString());
                    if (isInserted != -1) {
                        Toast.makeText(GroupCreation.this, "Group creation successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), AddMember.class);
                        intent.putExtra("groupid", isInserted);
                        intent.putExtra("groupname",gname.getText().toString());
                        startActivity(intent);
                    } else
                        Toast.makeText(GroupCreation.this, "Group creation unsuccessful", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}

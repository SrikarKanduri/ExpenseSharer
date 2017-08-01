package com.example.kanduri.projectriktam;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class AddScreen extends AppCompatActivity {
    private DatabaseHelper myDb;
    private ArrayList<String> memberList,tempList;
    private ListView memberView;
    private EditText addAmount,addSpent,addDate;
    private Spinner addPayee;
    private long groupid,mid,pid,aid;
    private int DIALOG_ID,year,month,day;
    private String groupname;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_screen);
        Calendar calendar=Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        day=calendar.get(Calendar.DAY_OF_MONTH);
        myDb=new DatabaseHelper(this);
        groupid=getIntent().getLongExtra("groupid",1L);
        groupname=getIntent().getStringExtra("groupname");
        getSupportActionBar().setTitle(groupname+" Add Expense");
        memberList = myDb.storeMemberData(groupid);
        addPayee=(Spinner)findViewById(R.id.spinner_payee);
        addAmount=(EditText)findViewById(R.id.editText_amount);
        addSpent=(EditText)findViewById(R.id.editText_spent);
        addDate=(EditText)findViewById(R.id.editText_date);
        memberView=(ListView)findViewById(R.id.listView_members);
        aid=myDb.insertActivityData(groupid,"",0,0,"");

        adapter=new ArrayAdapter<>(AddScreen.this,android.R.layout.simple_spinner_item,memberList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addPayee.setAdapter(adapter);
        tempList=new ArrayList<>(memberList);

        updateAdapter();
        onClickButtons();
    }

    private void updateAdapter()
    {
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_multiple_choice,tempList);
        memberView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        memberView.setAdapter(adapter);
    }
    protected Dialog createDialog(int id)
    {
        if(id==DIALOG_ID)
            return  new DatePickerDialog(this,dPicker, year,month,day);
        return null;

    }

    private DatePickerDialog.OnDateSetListener dPicker=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            year=i;
            month=i1+1;
            day=i2;
            addDate.setText(day+"/"+month+"/"+year);
        }
    };

    private void onClickButtons()
    {
        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog(DIALOG_ID).show();
            }
        });

        addPayee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pid=myDb.getMemberId((String)adapterView.getItemAtPosition(i));
                tempList=new ArrayList<>(memberList);
                tempList.remove(adapterView.getItemAtPosition(i));
                updateAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.add_expense_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.expense_id:
                if(addSpent.getText().toString().equals(""))
                    Toast.makeText(AddScreen.this,"Please enter 'Spent for'",Toast.LENGTH_SHORT).show();
                else if(addAmount.getText().toString().equals(""))
                    Toast.makeText(AddScreen.this,"Please enter amount spent",Toast.LENGTH_SHORT).show();
                else
                {
                    int cntChoice = memberView.getCount();
                    SparseBooleanArray sparseBooleanArray = memberView.getCheckedItemPositions();
                    for(int i = 0; i < cntChoice; i++) {
                        if (sparseBooleanArray.get(i)) {
                            mid = myDb.getMemberId(memberView.getItemAtPosition(i).toString());
                            myDb.insertExpense(groupid, aid, mid, 0);
                        }
                    }

                    myDb.updateActivityData(groupid,aid,pid,
                            addSpent.getText().toString(),Double.parseDouble(addAmount.getText().toString()),
                            day+"/"+month+"/"+year);
                    myDb.insertExpense(groupid, aid, pid, 0);
                    myDb.addShares(groupid,aid);
                    Toast.makeText(AddScreen.this, "Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddScreen.this, GroupReport.class);
                    intent.putExtra("groupname", groupname);
                    startActivity(intent);
                }
        }
        return super.onOptionsItemSelected(item);
    }
}

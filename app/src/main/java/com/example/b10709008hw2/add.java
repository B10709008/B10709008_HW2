package com.example.b10709008hw2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.database.sqlite.SQLiteDatabase;
import com.example.b10709008hw2.data.WaitlistContract;
import com.example.b10709008hw2.data.WaitlistDbHelper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class add extends AppCompatActivity {
    String s1;
    private GuestListAdapter guestListAdapter;
    private EditText name;
    private EditText number;
    private SQLiteDatabase sqLiteDatabase;
    private EditText mNewNameEditText;
    private EditText mNewNumberEditText;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);
//        RecyclerView waitlistRecyclerView;
//        waitlistRecyclerView = (RecyclerView) this.findViewById(R.id.RecyclerView);
        name=(EditText) findViewById(R.id.name_text_view);
        number=(EditText) findViewById(R.id.number_text_view);
        mNewNameEditText = (EditText) this.findViewById(R.id.name_text_view);
        mNewNumberEditText = (EditText) this.findViewById(R.id.number_text_view);
//        final Menu menu1=(Menu) findViewById(R.id.add);
        final Button bt1=(Button) findViewById(R.id.button1);
        final Button bt2=(Button) findViewById(R.id.button2);
        final Intent intent=getIntent();
        int s=intent.getIntExtra("s",0);
        s1=intent.getStringExtra("result");
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent();
                intent1.setClass(add.this,MainActivity.class);
                addToWaitlist(view);
                add.this.setResult(RESULT_OK,intent1);
                add.this.finish();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add.this.finish();
            }
        });
        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        Cursor cursor = getAllGuests();
        guestListAdapter = new GuestListAdapter(this, cursor);


    }
    public void addToWaitlist(View view) {
        if (mNewNameEditText.getText().length() == 0 ||
                mNewNumberEditText.getText().length() == 0) {
            return;
        }
        int partySize = 1;
        try {
            partySize = Integer.parseInt(mNewNumberEditText.getText().toString());
        } catch (NumberFormatException ex) {
            Log.d("error","error");
        }
        addNewGuest(mNewNameEditText.getText().toString(), partySize);
        guestListAdapter.swapCursor(getAllGuests());
        mNewNumberEditText.clearFocus();
        mNewNameEditText.getText().clear();
        mNewNumberEditText.getText().clear();
    }
    private Cursor getAllGuests() {
        return sqLiteDatabase.query(
                WaitlistContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP
        );
    }
    private long addNewGuest(String name, int partySize) {
        ContentValues cv = new ContentValues();
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name);
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, partySize);
        return sqLiteDatabase.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, cv);
    }
}

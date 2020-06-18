package com.example.b10709008hw2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.b10709008hw2.data.WaitlistContract;
import com.example.b10709008hw2.data.WaitlistDbHelper;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private RecyclerView recyclerView;
    private Menu menuadd;
    private Menu menusetting;
    private GuestListAdapter guestListAdapter;
    private RecyclerView.LayoutManager layoutmanager;
    private SQLiteDatabase sqLiteDatabase;
    private EditText mNewNameEditText;
    private EditText mNewNumberEditText;
    private  final static String LOG_TAG=MainActivity.class.getSimpleName();
    private RecyclerView RecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupSharedPreferences();
        recyclerView=(RecyclerView)findViewById(R.id.RecyclerView);
        menuadd=findViewById (R.id.add);
        menusetting=findViewById(R.id.setting);
        recyclerView.setHasFixedSize(true);
        layoutmanager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutmanager);
        WaitlistDbHelper dbHelper= new WaitlistDbHelper(this);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        Cursor cursor = getAllGuests();
        guestListAdapter = new GuestListAdapter(this, cursor);
        recyclerView.setAdapter(guestListAdapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, @NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, @NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, int direction) {
                final RecyclerView.ViewHolder viewHolder1= viewHolder;
                final AlertDialog alertDialog= new AlertDialog.Builder(MainActivity.this).setCancelable(true).create();
                alertDialog.setTitle("警告視窗");
                alertDialog.setMessage("是否確定刪除?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        long id =(long) viewHolder1.itemView.getTag();
                        removeGuest(id);
                        guestListAdapter.swapCursor(getAllGuests());
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        guestListAdapter.swapCursor(getAllGuests());
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        }).attachToRecyclerView(recyclerView);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.add) {
            Context context = MainActivity.this;
            Intent intent=new Intent();
            intent.setClass(MainActivity.this,add.class);
            intent.putExtra("s","1");
            startActivityForResult(intent,1);
            String textToShow = "Search clicked";
            Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
            return true;
        }
        if(itemThatWasClickedId==R.id.setting){
            Context context=MainActivity.this;
            Intent intent=new Intent();
            intent.setClass(MainActivity.this,Setting.class);
            intent.putExtra("s","1");
            startActivity(intent);
            String textToShow="Search clicked";
            Toast.makeText(context,textToShow,Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        guestListAdapter.swapCursor(getAllGuests());
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
    private  boolean removeGuest(long id ){
        return sqLiteDatabase.delete(WaitlistContract.WaitlistEntry.TABLE_NAME,WaitlistContract.WaitlistEntry._ID+"="+id,null)>0;
    }
    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
    private void loadColorFromPreferences(SharedPreferences sharedPreferences) {
        String a=(sharedPreferences.getString(getString(R.string.pref_color_key),getString(R.string.pref_color_red_value)));
        SharedPreferences pref=getSharedPreferences("ChangeColor",MODE_PRIVATE);
        pref.edit().putString("Color",a).commit();
        guestListAdapter.updateColor();
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(getString(R.string.pref_color_key))) {
            loadColorFromPreferences(sharedPreferences);
        }
    }
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}

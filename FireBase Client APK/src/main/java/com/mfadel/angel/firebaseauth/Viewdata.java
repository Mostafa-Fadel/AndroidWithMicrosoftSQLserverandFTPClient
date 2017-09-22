package com.mfadel.angel.firebaseauth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Viewdata extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;

    ListView mlistview;

    String Title;

    private ArrayList<String> mfiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewdata);

        mlistview=(ListView) findViewById(R.id.lstviewdata);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("MediaFiles");

        final ArrayAdapter<String> arrayadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mfiles);



        mlistview.setAdapter(arrayadapter);

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

              //  String value = dataSnapshot.getValue(String.class);
                String key  = dataSnapshot.getKey();

                String title = dataSnapshot.child("Title").getValue(String.class);
                String Description = dataSnapshot.child("Description").getValue(String.class);
                String Uploaded_By = dataSnapshot.child("Uploaded_By").getValue(String.class);
                String Uploaded_Date = dataSnapshot.child("Uploaded_Date").getValue(String.class);

                mfiles.add(title + "\n" + Description  + "\n" + Uploaded_By  + "\n" + Uploaded_Date + "\n" + key );

                arrayadapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {



            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView)view).getText().toString();

                Bundle bndl= new Bundle();
                bndl.putString("itemkey", item);

                Intent intent = new Intent(getApplicationContext(),viewitem.class);
                intent.putExtras(bndl);
                startActivity(intent);

            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        switch(item.getItemId()) {
            case R.id.uploadnewitem:
                Bundle dataBundle = new Bundle();
               // dataBundle.putInt("id", Integer.parseInt(id));
                Intent intent = new Intent(getApplicationContext(),UserActivity.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

package com.mfadel.angel.firebaseauth;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CustomViewData extends AppCompatActivity {


    List<ListItem> itemsList;

    //the listview
    ListView listView;


    FirebaseDatabase database;
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "New Item", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                Intent intent = new Intent(getApplicationContext(),UserActivity.class);
               startActivity(intent);

            }
        });

        itemsList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("MediaFiles");




        //creating the adapter
        MyListAdapter adapter = new MyListAdapter(this, R.layout.customlistview, itemsList);

        //attaching adapter to the listview
        listView.setAdapter(adapter);


        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //  String value = dataSnapshot.getValue(String.class);
                String key  = dataSnapshot.getKey();

                String title = dataSnapshot.child("Title").getValue(String.class);
                String thumbnailurl = dataSnapshot.child("ThumbnailUrl").getValue(String.class);
                String Uploaded_By = dataSnapshot.child("Uploaded_By").getValue(String.class);
                String Uploaded_Date = dataSnapshot.child("Uploaded_Date").getValue(String.class);


                itemsList.add(new ListItem(thumbnailurl, title,key));



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
               // Bundle dataBundle = new Bundle();
                // dataBundle.putInt("id", Integer.parseInt(id));
                Intent intent = new Intent(getApplicationContext(),UserActivity.class);
               // intent.putExtras(dataBundle);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

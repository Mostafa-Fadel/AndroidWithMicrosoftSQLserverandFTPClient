package com.mfadel.angel.firebaseauth;

/**
 * Created by Angel on 2017-09-25.
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Belal on 9/14/2017.
 */

//we need to extend the ArrayAdapter class as we are building an adapter
public class MyListAdapter extends ArrayAdapter<ListItem> {

    //the list values in the List of type hero
    //List<Hero> heroList;
    List<ListItem> itemslist;
    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;

    //constructor initializing the values
    public MyListAdapter(Context context, int resource, List<ListItem> itemsList) {
        super(context, resource, itemsList);
        this.context = context;
        this.resource = resource;
        this.itemslist = itemsList;
    }

    //this will return the ListView Item as a View
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //we need to get the view of the xml for our list item
        //And for this we need a layoutinflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //getting the view
        View view = layoutInflater.inflate(resource, null, false);

        //getting the view elements of the list from the view
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView textViewtitle = (TextView) view.findViewById(R.id.txttitle);
        final TextView textViewkey = (TextView) view.findViewById(R.id.txtkey);
        Button buttonView = (Button) view.findViewById(R.id.btnview);

        //getting the hero of the specified position
        ListItem item = itemslist.get(position);

        //adding values to the list item
        //imageView.setImageDrawable(context.getResources().getDrawable(item.getImage()));
        try{
            Picasso.with(context).load(item.getImage()).fit().centerCrop().into(imageView);
        }catch (Exception ex){

            //Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        textViewtitle.setText(item.gettitle());
        textViewkey.setText(item.getitemkey());

        //adding a click listener to the button to remove item from the list
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //we will call this method to remove the selected value from the list
                //we are passing the position which is to be removed in the method


                String key =  textViewkey.getText().toString();

                Bundle bndl= new Bundle();
                bndl.putString("itemkey", key);

                Intent intent = new Intent(context,viewitem.class);
                intent.putExtras(bndl);
                context.startActivity(intent);
            }
        });

        //finally returning the view
        return view;
    }

    //this method will remove the item from the list
    private void removeHero(final int position) {
        //Creating an alert dialog to confirm the deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to delete this?");

        //if the response is positive in the alert
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //removing the item
                itemslist.remove(position);

                //reloading the list
                notifyDataSetChanged();
            }
        });

        //if response is negative nothing is being done
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        //creating and displaying the alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

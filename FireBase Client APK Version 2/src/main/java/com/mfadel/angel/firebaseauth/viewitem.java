package com.mfadel.angel.firebaseauth;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class viewitem extends AppCompatActivity {

    String itemkey;
    EditText edttitle, edtdescription, edtnote;
    Button btndownload, btnpreview;
    ImageView previewimage;
    TextView txtitem;

    String filename, fileurl, thumbnailurl;

    FirebaseDatabase database;
    DatabaseReference myRef, ItemRef, filenameref, thumbnailref;


    private StorageReference mStorageRef ;

    Uri storageFileUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewitem);


        Bundle extras = getIntent().getExtras();

        String[] tmp;

        itemkey = extras.getString("itemkey");
        tmp = itemkey.split("\n");


        edttitle = (EditText) findViewById(R.id.title);

        txtitem = (TextView) findViewById(R.id.txtitem);

       // edtdescription = (EditText) findViewById(R.id.description);
      //  edtnote = (EditText) findViewById(R.id.note);

        previewimage   = (ImageView ) findViewById(R.id.imagepreview);

        btndownload = (Button) findViewById(R.id.btn_download);
        btnpreview = (Button) findViewById(R.id.btn_view);

        database = FirebaseDatabase.getInstance();


       // Toast.makeText(viewitem.this, "MediaFiles/" + itemkey, Toast.LENGTH_SHORT).show();
       // Toast.makeText(viewitem.this, tmp[tmp.length - 1], Toast.LENGTH_SHORT).show();
        myRef = database.getReference("MediaFiles/" + tmp[tmp.length - 1]);

        filenameref =  myRef.child("FileName");

        ItemRef =  myRef.child("DownloadUrl");

        thumbnailref = myRef.child("ThumbnailUrl");


       // mStorageRef = FirebaseStorage.getInstance().getReference();

       filenameref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filename = dataSnapshot.getValue(String.class);
                //do what you want with the email
                // Toast.makeText(viewitem.this, filename, Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        thumbnailref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thumbnailurl = dataSnapshot.getValue(String.class);

                  // Toast.makeText(viewitem.this, thumbnailurl, Toast.LENGTH_SHORT).show();
                try{
                    Picasso.with(viewitem.this).load(thumbnailurl).fit().centerCrop().into(previewimage);
                }catch (Exception ex){

                 //   Toast.makeText(viewitem.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        ItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
              fileurl = dataSnapshot.getValue(String.class);

           //    Toast.makeText(viewitem.this, filename, Toast.LENGTH_SHORT).show();
                  try{
                   Picasso.with(viewitem.this).load(fileurl).fit().centerCrop().into(previewimage);
                  }catch (Exception ex){

                        Toast.makeText(viewitem.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                  }
           }
            @Override
           public void onCancelled(DatabaseError databaseError) {
           }
       });


      myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //String title  = dataSnapshot.getValue();
                String key = dataSnapshot.getKey();
                String value = dataSnapshot.getValue(String.class);

                txtitem.setText(txtitem.getText() + key + ": " + value + "\n") ;
               // edtdescription.setText(edtdescription.getText() + "-" + value  +";")



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



        btnpreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              try{
                  Intent intent = new Intent();
                  intent.setAction(Intent.ACTION_VIEW);
                  intent.addCategory(Intent.CATEGORY_BROWSABLE);
                  intent.setData(Uri.parse(fileurl));   //"http://casidiablo.net"
                  startActivity(intent);
              }catch(Exception ex){
                  Toast.makeText(viewitem.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }



            }
        });
        btndownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileurl.toString()!=""){

                   // Uri fileuri= Uri.parse(mStorageRef.child(filename).getDownloadUrl().toString());

                    Toast.makeText(viewitem.this, "Start Downloading...", Toast.LENGTH_SHORT).show();

                    final ProgressDialog progressdialog = new ProgressDialog(viewitem.this);


                    progressdialog.setTitle("Downloading...");
                    progressdialog.setCancelable(false);
                    progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressdialog.show();

                    // Intent intent = new Intent();
                  // intent.setAction(Intent.ACTION_VIEW);
                 //  intent.addCategory(Intent.CATEGORY_BROWSABLE);
                 //  intent.setData(Uri.parse(filename));   //"http://casidiablo.net"
                 //  startActivity(intent);


                   // String pathtoimage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbs‌​olutePath()+ "/Camera/IMG_20150521_144859.jpg";

                File rootPath = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/FireBaseDownloads/");

                    Toast.makeText(viewitem.this,rootPath.getPath(), Toast.LENGTH_SHORT).show();
                if(!rootPath.exists()) {
                    rootPath.mkdirs();
                }

                final File localFile = new File(rootPath, filename);

                mStorageRef = FirebaseStorage.getInstance().getReference(filename);

                mStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                       // Log.e("firebase ",";local tem file created  created " +localFile.toString());
                        //  updateDb(timestamp,localFile.toString(),position);
                        progressdialog.dismiss();
                        Toast.makeText(viewitem.this, "File Downloaded", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                      //  Log.e("firebase ",";local tem file not created  created " +exception.toString());
                        progressdialog.dismiss();
                        Toast.makeText(viewitem.this, "Download Failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            //Some math to get the Percentage of the Download :)
                          //  long progress = (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                          //  progressdialog.setProgress((int) taskSnapshot.getBytesTransferred());
                          //  progressdialog.setMessage(((int) progress)  + "% Downloaded ...");
                            double progressPercentage = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            double size = (taskSnapshot.getTotalByteCount()) / (1000000);
                            progressdialog.setMessage("File Size: " + (size) + " - " + ((int) progressPercentage) + "%");

                        }
                    });
            }else{
                Toast.makeText(viewitem.this, "No file is selected", Toast.LENGTH_SHORT).show();
            }


        }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        switch(item.getItemId()) {
            case R.id.deleteitem:
                try{


                Bundle dataBundle = new Bundle();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Delete Item");
                builder.setMessage("Are you sure you want to delele this Item?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        myRef.setValue(null);

                        mStorageRef = FirebaseStorage.getInstance().getReference(filename);
                        mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                            }
                        });

                       dialog.dismiss();
                        Toast.makeText(viewitem.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                }catch(Exception ex){
                    Toast.makeText(viewitem.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //Get Path from URI
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


}

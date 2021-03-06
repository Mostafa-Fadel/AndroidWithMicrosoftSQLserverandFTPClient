package com.mfadel.angel.firebaseauth;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.StringDef;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import android.os.AsyncTask;

import android.view.View;
import android.view.WindowManager;



import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import android.provider.MediaStore.Video.Thumbnails;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    EditText edttitle, edtdescription, edtnote;
    Button btnSelectItem, btnSubmit;
    ImageView previewimage;

    FirebaseDatabase database;
    DatabaseReference myRef;
    private StorageReference mStorageRef;


    private static final int REQUEST_TAKE_GALLERY_VIDEO = 1;

    String selectedItempath;
    String filetype ="";
    File selectedfile;
    Uri fileuri;
    Uri downloadUrl;

    Uri thumbnailpath;


    String fn, thumbnail_fn;

    String childname;
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd HHmmss");

    Date date;
    float fileSize;


   // ProgressDialog progressdialog;

    private FirebaseAuth mAuth;

    FirebaseUser currentUser ;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        edttitle = (EditText) findViewById(R.id.title);
        edtdescription = (EditText) findViewById(R.id.description);
        edtnote = (EditText) findViewById(R.id.note);
        previewimage   = (ImageView ) findViewById(R.id.imagepreview);

        btnSubmit = (Button) findViewById(R.id.btn_submit);

         btnSelectItem = (Button) findViewById(R.id.btn_selectitem);

         database = FirebaseDatabase.getInstance();

          myRef = database.getReference("MediaFiles");
          mStorageRef = FirebaseStorage.getInstance().getReference();

          mAuth = FirebaseAuth.getInstance();
          currentUser =  mAuth.getCurrentUser();

          previewimage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (fileuri.toString()!=""){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(fileuri);   //"http://casidiablo.net"
                    startActivity(intent);
                }else{
                    Toast.makeText(UserActivity.this, "No file is selected", Toast.LENGTH_SHORT).show();
                }

               }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String filenameArray[] = selectedItempath.split("\\.");
                 String extension = filenameArray[filenameArray.length-1];
                //fn = "android" + dateFormat.format(date) + "." + extension;
                childname= "file" + dateFormat.format(date);
                fn =  childname +   "." + extension;;

                thumbnail_fn = "th"+ childname + ".png";

                File rootPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/MyThumbnails/");
                if(!rootPath.exists()) {
                    rootPath.mkdirs();
                }
                final File localFile = new File(rootPath, thumbnail_fn);

                BitmapDrawable bitmapDrawable = ((BitmapDrawable) previewimage.getDrawable());
                Bitmap bitmap;
                if(bitmapDrawable==null){
                    previewimage.buildDrawingCache();
                    bitmap = previewimage.getDrawingCache();
                    previewimage.buildDrawingCache(false);
                }else
                {
                    bitmap = bitmapDrawable .getBitmap();
                }

                Bitmap resizedbitmap = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.25), (int)(bitmap.getHeight()*0.25), true);

                thumbnailpath=  Uri.fromFile(localFile);
                 saveBitmap(resizedbitmap, localFile.getPath());

               UploadFile();

               // myRef.child(t).child("DownloadUrl").setValue(downloadUrl.toString());

            }
        });

        btnSelectItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItempath ="";
                try{
                    pickItem();

                  //  if (selectedItem != ""){
                  //     Bitmap bMap = BitmapFactory.decodeFile(selectedItem);
                 //      previewimage.setImageBitmap(bMap);
                 //  }

                }catch (Exception ex){
                    Toast.makeText(UserActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private  void UploadFile(){
      // Uri file = Uri.fromFile(new File(selectedItempath));
       try{
           final StorageReference stRef = mStorageRef.child(fn);
           final StorageReference thRef = mStorageRef.child(thumbnail_fn);

           final  ProgressDialog progressdialog = new ProgressDialog(this);
           progressdialog.setTitle("Uploading...");
           progressdialog.setCancelable(false);
           progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

           progressdialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {


                   progressdialog.dismiss();
               }
           });


           progressdialog.show();


            stRef.putFile(fileuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content

                           // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            progressdialog.hide();

                            myRef.child(childname).child("FileName").setValue(fn);
                            myRef.child(childname).child("Title").setValue(edttitle.getText().toString());
                            myRef.child(childname).child("Description").setValue(edtdescription.getText().toString());
                            myRef.child(childname).child("Note").setValue(edtnote.getText().toString());
                            myRef.child(childname).child("Uploaded_Date").setValue(dateFormat2.format(date));
                            myRef.child(childname).child("Uploaded_By").setValue(currentUser.getEmail());
                            myRef.child(childname).child("FileType").setValue(filetype);
                            myRef.child(childname).child("DownloadUrl").setValue("");
                            downloadUrl = taskSnapshot.getDownloadUrl();
                            myRef.child(childname).child("DownloadUrl").setValue(downloadUrl.toString());



                            thRef.putFile(thumbnailpath)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    downloadUrl = taskSnapshot.getDownloadUrl();
                                    myRef.child(childname).child("ThumbnailUrl").setValue(downloadUrl.toString());



                                }
                             });

                            Toast.makeText(UserActivity.this,"File Uploaded Successfuly.", Toast.LENGTH_SHORT).show();
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressdialog.dismiss();
                            Toast.makeText(UserActivity.this,"File Failed : "  + exception.getMessage() , Toast.LENGTH_SHORT).show();
                            // Handle unsuccessful uploads
                            // ...
                        }
                    })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                   long progress = (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressdialog.setProgress((int) taskSnapshot.getBytesTransferred());
                   progressdialog.setMessage(((int) progress)  + "% Uploaded ...");

                   // double progressPercentage = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                   //   double size = (taskSnapshot.getTotalByteCount()) / (1000000);
                   // progressdialog.setMessage("File Size: " + (size) + " - " + ((int) progressPercentage) + "% - Click away to cancel");

                }
            })
            ;
    }catch (Exception ex){
           AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setTitle("Upload File");
           builder.setMessage("This is my message. " + ex.getMessage());

           // add a button
           builder.setPositiveButton("OK", null);

           // create and show the alert dialog
           AlertDialog dialog = builder.create();
           dialog.show();
    }


    }

    public void pickItem() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*, video/*");
        startActivityForResult(intent, 1);
    }


    //Get Path from URI
    public String getRealPathFromURI(Uri contentURI) {
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_GALLERY_VIDEO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                Toast.makeText(UserActivity.this, "No File Selected", Toast.LENGTH_SHORT).show();
                return ;
            }

            fileuri = data.getData();

            date = new Date();

            selectedItempath = getRealPathFromURI(fileuri);
            selectedfile = new File(selectedItempath);
            fileSize =selectedfile.length();
           // edttitle.setText(fileuri.toString());
            try {
                //Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Selected_Image_Uri);
               // String path = data.getData().getPath();
               if (selectedItempath.contains(".mp4") ||selectedItempath.contains(".mpg") ||selectedItempath.contains(".mpeg")
                       || selectedItempath.contains(".3gp") ||selectedItempath.contains(".avi") ||selectedItempath.contains(".mxf")
                       || selectedItempath.contains(".gif") ||selectedItempath.contains(".wmv") ||selectedItempath.contains(".flv")
                       || selectedItempath.contains(".mov") ||selectedItempath.contains(".swf")
                       ) {
                   filetype = "Video";

                   previewimage.setImageBitmap(GlobalFunctions.retriveVideoFrameFromVideo(selectedItempath));


                } else  {
                   filetype = "Image";
                  // Log.d(this.getClass().getName(), "Image");
                 // previewimage.setImageBitmap(decodeFile(selectedfile));
                   Picasso.with(UserActivity.this).load(fileuri).fit().centerCrop().into(previewimage);

               }


                // previewimage.setImageURI(fileuri);
            } catch (Exception ex) {
                ex.printStackTrace();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    public void saveBitmap(Bitmap bitmap, String path){
        if(bitmap!=null){
            try {
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(path); //here is set your file path where you want to save or also here you can set file object directly

                    bitmap.compress(Bitmap.CompressFormat.PNG, 25, outputStream); // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

 /*  @IgnoreExtraProperties
    public static class MediaFiles {

        public String Owner;
        public String filename;
        public String title;
        public String description;
        public String note;
        public String uploaddate;



        public MediaFiles() {
            // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        }

        public  Mediafiles(String Owner, String filename, String title, String description, String note, String uploaddate) {

           this.Owner = Owner;
            this.filename = filename;
            this.title=title;
            this.description=description;
            this.note =note;
            this.uploaddate=uploaddate;
        }

    }
*/
}



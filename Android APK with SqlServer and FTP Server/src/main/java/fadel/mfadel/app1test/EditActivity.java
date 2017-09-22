package fadel.mfadel.app1test;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;

import android.view.View;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.Toast;



import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import org.apache.commons.net.ftp.FTPReply;


import java.io.BufferedInputStream;
import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import android.view.View.OnClickListener;

import com.squareup.picasso.Picasso;

import static android.R.attr.data;



public class EditActivity extends AppCompatActivity {


    ConnectionClass CON_CLS;
    EditText edtowner, edtdescription, edttitle,  edtnote;

    Button btnbrowse,  btnUpload;
    //ProgressBar prog_bar;
    ImageView previewimage;



    int port = 21;

    boolean success = false;
    String msg;
    boolean finishupload = false;
    String fn = "";

   // String selectedItem;
    String selectedItempath;
    String filetype ="";
    File selectedfile;
    Uri fileuri;


    private static  final int REQUEST_PICK_FILE = 1;

    private static final int REQUEST_TAKE_GALLERY_VIDEO = 1;

    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();


    private long fileSize = 0;
    private long CurrentSize = 0;
private int uploadpercentage =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        edtowner = (EditText) findViewById(R.id.edtowner);
        edtdescription = (EditText) findViewById(R.id.edtdescription);

        edttitle = (EditText) findViewById(R.id.edttitle);
        edtnote = (EditText) findViewById(R.id.edtnote);
        btnbrowse = (Button) findViewById(R.id.btnBrowse);
        //btnbrowsevideo = (Button) findViewById(R.id.btnBrowsevideo);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        previewimage   = (ImageView ) findViewById(R.id.imagepreview);
       //  prog_bar = (ProgressBar) findViewById(R.id.prog_bar);

      //  prog_bar.setVisibility(View.GONE);


        String z = "";

        ArrayList<String> mylist = new ArrayList<String>();
        CON_CLS = new ConnectionClass();

        CON_CLS.ip = GlobalVariables.getSrvip();
        CON_CLS.un = GlobalVariables.getDBun();
        CON_CLS.password = GlobalVariables.getDBpass();

        Connection con = CON_CLS.CONN();

        btnbrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    pickItem();
            }
        });


        /*btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditActivity.DoUpload  doupload = new EditActivity.DoUpload();
                doupload.execute("");

            }
        });*/

        finishupload=false;

        previewimage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (fileuri.toString()!=""){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(fileuri);   //"http://casidiablo.net"
                    startActivity(intent);
                }else{
                    Toast.makeText(EditActivity.this, "No file is selected", Toast.LENGTH_SHORT).show();
                }

            }
        });

         addListenerOnButton();

    }


    public void addListenerOnButton() {


        btnUpload.setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        CurrentSize = 0;
                        EditActivity.DoUpload  doupload = new EditActivity.DoUpload();
                        doupload.execute("");
                        // prepare for a progress bar dialog
                        progressBar = new ProgressDialog(v.getContext());
                       progressBar.setCancelable(false);
                       progressBar.setMessage("File Uploading ...");
                       progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                       progressBar.setProgress(0);

                       progressBar.setMax((int) fileSize);

                       // prog_bar.setProgress(0);
                      //  prog_bar.setMax((int) fileSize);
                        progressBar.show();

                        //reset progress bar status
                        progressBarStatus = 0;



                        new Thread(new Runnable() {
                            public void run() {
                                while (progressBarStatus < (int) fileSize) {


                                    // process some tasks
                                    //progressBarStatus = doSomeTasks();

                                    progressBarStatus = (int) CurrentSize;

                                    uploadpercentage =  (( progressBarStatus / (int) fileSize) *100);

                                    // your computer is too fast, sleep 1 second
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    // Update the progress bar
                                    progressBarHandler.post(new Runnable() {
                                        public void run() {
                                            progressBar.setProgress(progressBarStatus);
                                              //  prog_bar.setProgress(progressBarStatus);
                                           // btnUpload.setText( "Uploading ...  " + uploadpercentage + " %");

                                        }
                                    });
                                }

                                if (finishupload=true) {
                                   progressBar.dismiss();
                                   // prog_bar.setVisibility(View.GONE);

                                }

                                // ok, file is downloaded,
                                if (progressBarStatus >= (int) fileSize) {

                                    // sleep 2 seconds, so that you can see the 100%
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    // close the progress bar dialog
                                    progressBar.dismiss();
                                }
                            }
                        }).start();

                    }

                });

    }

    // file download simulator... a really simple



    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                //System.out.println("SERVER: " + aReply);
            }
        }
    }

    public void pickItem() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*, video/*");
        startActivityForResult(intent, 1);
    }
    public void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, 1);
    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_GALLERY_VIDEO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                Toast.makeText(EditActivity.this, "No File Selected", Toast.LENGTH_SHORT).show();
                return ;
            }
            Uri Selected_Image_Uri = data.getData();
            selectedItem = getRealPathFromURI(Selected_Image_Uri);
            File file0 = new File(selectedItem);
            fileSize =file0.length();
                 }
    }


    */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == REQUEST_TAKE_GALLERY_VIDEO && resultCode == Activity.RESULT_OK) {
          if (data == null) {
              //Display an error
              Toast.makeText(EditActivity.this, "No File Selected", Toast.LENGTH_SHORT).show();
              return ;
          }

          fileuri = data.getData();



          //  previewimage.setImageURI(Selected_Image_Uri);

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
                  // Log.d(this.getClass().getName(), "Video");
                  previewimage.setImageBitmap(retriveVideoFrameFromVideo(selectedItempath));
              } else  {
                  filetype = "Image";
                  // Log.d(this.getClass().getName(), "Image");
                  // previewimage.setImageBitmap(decodeFile(selectedfile));
                  Picasso.with(EditActivity.this).load(fileuri).fit().centerCrop().into(previewimage);

              }


              // previewimage.setImageURI(fileuri);
          } catch (Exception ex) {
              ex.printStackTrace();
          } catch (Throwable throwable) {
              throwable.printStackTrace();
          }
      }
  }
    public static Bitmap retriveVideoFrameFromVideo(String p_videoPath)
            throws Throwable
    {
        Bitmap m_bitmap = null;
        MediaMetadataRetriever m_mediaMetadataRetriever = null;
        try
        {
            m_mediaMetadataRetriever = new MediaMetadataRetriever();
            m_mediaMetadataRetriever.setDataSource(p_videoPath);
            m_bitmap = m_mediaMetadataRetriever.getFrameAtTime();
        }
        catch (Exception m_e)
        {
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String p_videoPath)"
                            + m_e.getMessage());
        }
        finally
        {
            if (m_mediaMetadataRetriever != null)
            {
                m_mediaMetadataRetriever.release();
            }
        }
        return m_bitmap;
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

    public class DoUpload extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;

        String sfile = selectedItempath;
        String title = edttitle.getText().toString();
        String owner = edtowner.getText().toString();
        String description = edtdescription.getText().toString();
        String note = edtnote.getText().toString();


        @Override
        protected void onPreExecute() {

            finishupload=false;
            Toast.makeText(EditActivity.this, "Start Uploading...", Toast.LENGTH_SHORT).show();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

           // prog_bar.setVisibility(View.VISIBLE);

            //For Progress BAR

        }

        @Override
        protected void onPostExecute(String r) {


            Toast.makeText(EditActivity.this, msg, Toast.LENGTH_SHORT).show();
            if (isSuccess) {

                edtowner.setText("");
                edtdescription.setText("");
                edttitle.setText("");
                edtnote.setText("");
                selectedItempath ="";
                // Intent i = new Intent(MainActivity.this, MainActivity.class);
                //  startActivity(i);
               // finish();

               // prog_bar.setVisibility(View.GONE);
            }
            else {
                progressBar.dismiss();

            }
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            btnUpload.setText("Upload");
        }

        @Override
        protected String doInBackground(String... params) {


            String server = GlobalVariables.getFtpserver();

            String user = GlobalVariables.getFtpun();
            String pass = GlobalVariables.getFtppass();


            FTPClient ftpClient = new FTPClient();
            try {
                ftpClient.connect(server, port);
                showServerReply(ftpClient);
                int replyCode = ftpClient.getReplyCode();
                if (!FTPReply.isPositiveCompletion(replyCode)) {
                    msg = ("Operation failed. Server reply code: " + replyCode);

                }
                 success = ftpClient.login(user, pass);
                //showServerReply(ftpClient);
                if (!success) {
                    msg = ("Could not login to the server");

                } else {

                    ftpClient.enterLocalPassiveMode(); // important!
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date date = new Date();

                    String data = sfile;

                    String filenameArray[] = data.split("\\.");
                    String extension = filenameArray[filenameArray.length-1];
                    fn = "android" + dateFormat.format(date) + "." + extension;


                    File file = new File(sfile);


                    //Approch #1 Input Stream
                   /* BufferedInputStream buffIn = new BufferedInputStream(
                            new FileInputStream(file));

                    ProgressInputStream progressInput = new ProgressInputStream(
                            buffIn);


                    boolean result = ftpClient.storeFile(fn ,
                            progressInput);

                    if (result) Log.v("upload result", "succeeded");
                    buffIn.close();
                    ftpClient.logout();
                    ftpClient.disconnect();
*/
                    InputStream inputStream = new FileInputStream(file);
                    OutputStream outputStream = ftpClient.storeFileStream(fn);
                    byte[] bytesIn = new byte[4096];
                    int read = 0;

                    CurrentSize = 0;

                    while ((read = inputStream.read(bytesIn)) != -1) {
                        outputStream.write(bytesIn, 0, read);

                        CurrentSize =  CurrentSize + read;
                    }
                    inputStream.close();
                    outputStream.close();



                    Connection con = CON_CLS.CONN();

                    if (con == null) {
                        msg = "Error in connection with SQL server";
                    } else {

                        DateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                        Date date2 = new Date();
                        //return dateFormat.format(date);
                        String query = "INSERT INTO [MNESdb].[dbo].[tbl_files] ([filename], [title], " +
                                " [finishrecord], [mainstorage], [serverid], [owner], [recorddate], [description], [reporter], [note]) " +
                                "VALUES     ('" + fn + "', '" + title +
                                "', 1, 1, 0, '" + owner + "', '" + dateFormat2.format(date2) +
                                "', '" + description + "', '" + owner + "', '" + note + "')";

                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        preparedStatement.executeUpdate();
                        msg = "Uploaded Successfully";
                        isSuccess = true;

                    }


                }
            } catch (IOException ex) {
                msg = ("Oops! Something wrong happened -- " + ex.getMessage());
                finishupload=true;
                ex.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            finishupload=true;
            return msg;
        }
    }


}



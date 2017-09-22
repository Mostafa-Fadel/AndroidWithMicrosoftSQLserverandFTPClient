package fadel.mfadel.app1test;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import java.sql.Connection;

import java.sql.ResultSet;

import java.sql.Statement;


public class MainActivity extends AppCompatActivity {


   // ConnectionClass connectionClass;
    EditText edtuserid, edtpass;
    Button btnlogin;
    ProgressBar pbbar;

    DBHelper mydb;

    ConnectionClass CON_CLS;

    String  id,ftp_IP, ftp_UN, ftp_PASS, sql_IP, sql_UN, sql_PASS;
    int cnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/// this code is to reset rhe status of application after reopen if you press home button
        // it makes the application single instance

        if (!isTaskRoot())
        {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        //connectionClass = new ConnectionClass();

        edtuserid = (EditText) findViewById(R.id.edtuserid);
        edtpass = (EditText) findViewById(R.id.edtpass);
        btnlogin = (Button) findViewById(R.id.btnlogin);

        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        pbbar.setVisibility(View.GONE);

try{


        mydb = new DBHelper(this);

        Cursor rs = mydb.getAllData();
        cnt = rs.getCount();

        Bundle dataBundle = new Bundle();


        if (cnt > 0) {
            rs.moveToFirst();

            id = rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_ID));

            ftp_IP = rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_FTPIP));
            ftp_UN = rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_FTPUN));
            ftp_PASS = rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_FTPPASS));
            sql_IP = rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_SQLIP));
            sql_UN = rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_SQLUN));
            sql_PASS = rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_SQLPASS));

            GlobalVariables.setSrvip(sql_IP);
            GlobalVariables.setDBun(sql_UN);
            GlobalVariables.setDBpass(sql_PASS);
            GlobalVariables.setFtpserver(ftp_IP);
            GlobalVariables.setFtpun(ftp_UN);
            GlobalVariables.setFtppass(ftp_PASS);


            if (!rs.isClosed()) {
                rs.close();
            }


        } else {

            dataBundle.putInt("id", 0);

            Intent intent = new Intent(getApplicationContext(), Settings.class);
            intent.putExtras(dataBundle);
            startActivity(intent);

        }

} catch (Exception e){
    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
}





       // GlobalVariables.srvip = "192.168.0.101";
        //GlobalVariables.DBun = "saa";
       // GlobalVariables.DBpass = "123456";

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CON_CLS = new ConnectionClass();

                CON_CLS.ip = GlobalVariables.getSrvip();
                CON_CLS.un = GlobalVariables.getDBun();
                CON_CLS.password =  GlobalVariables.getDBpass();

                DoLogin doLogin = new DoLogin();
                doLogin.execute("");
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
            case R.id.item_settings:
                Bundle dataBundle = new Bundle();


                dataBundle.putInt("id", Integer.parseInt(id));

                Intent intent = new Intent(getApplicationContext(),Settings.class);
                intent.putExtras(dataBundle);


                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onRestart() {
        super.onRestart();
          }



    public class DoLogin extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;

        String userid = edtuserid.getText().toString();
        String password = edtpass.getText().toString();

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, r, Toast.LENGTH_SHORT).show();
            if (isSuccess) {

              Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.setFlags (Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
              startActivity(intent);

               // Intent i = new Intent(MainActivity.this, MainActivity.class);
              //  startActivity(i);
                finish();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            if (userid.trim().equals("") || password.trim().equals(""))
                z = "Please enter User Id and Password";
            else {
                try {


                    Connection con = CON_CLS.CONN();

                    //Connection con = CONN();

                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {
                        String query = "select * from tbl_users where username ='" + userid + "' and password='" + password + "'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        if (rs.next()) {

                            z = "Login successfull";
                            isSuccess = true;




                        } else {
                            z = "Invalid Credentials";
                            isSuccess = false;
                        }

                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = "Exceptions";
                }
            }


            return z;
        }
    }
}

package fadel.mfadel.app1test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    EditText edtIP, edtun, edtpass;
    EditText edtftpIP, edtftpun, edtftppass;

    Button btnapply;

    DBHelper mydb;
    int cnt;
     int id;
 //   String id, ftp_IP, ftp_UN, ftp_PASS, sql_IP, sql_UN, sql_PASS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        edtIP = (EditText) findViewById(R.id.edtserverip);
        edtun = (EditText) findViewById(R.id.edtusername);
        edtpass = (EditText) findViewById(R.id.edtpass);

        edtftpIP = (EditText) findViewById(R.id.edtftpip);
        edtftpun = (EditText) findViewById(R.id.edtftpusername);
        edtftppass = (EditText) findViewById(R.id.edtftppass);

        btnapply = (Button) findViewById(R.id.btnapply);

        mydb = new DBHelper(this);

      /*  Cursor rs = mydb.getAllData();
        cnt = rs.getCount();
        if (cnt > 0) {
            rs.moveToFirst();

             id = rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_ID));



             ftp_IP = rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_FTPIP));
             ftp_UN = rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_FTPUN));
             ftp_PASS = rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_FTPPASS));
             sql_IP = rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_SQLUN));
             sql_UN = rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_SQLUN));
             sql_PASS = rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_SQLPASS));


            edtIP.setText(sql_IP);
            edtun.setText(sql_UN);
            edtpass.setText(sql_PASS);

            edtftpIP.setText(ftp_IP);
            edtftpun.setText(ftp_UN);
            edtftppass.setText(ftp_PASS);


            if (!rs.isClosed()) {
                rs.close();
            }
        }else{
            id = "0";
        }

*/
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {


            id = extras.getInt("id");

            if(id>0){
                edtIP.setText(GlobalVariables.getSrvip());
                edtun.setText(GlobalVariables.getDBun());
                edtpass.setText(GlobalVariables.getDBpass());

                edtftpIP.setText(GlobalVariables.getFtpserver());
                edtftpun.setText(GlobalVariables.getFtpun());
                edtftppass.setText(GlobalVariables.getFtppass());
            }

    }

        btnapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

           try{

                if(id > 0) {


                    if (mydb.updateSettings(id, edtftpIP.getText().toString(),
                            edtftpun.getText().toString(), edtftppass.getText().toString(),
                            edtIP.getText().toString(), edtun.getText().toString(), edtpass.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(getApplicationContext(), "not Updated", Toast.LENGTH_SHORT).show();
                    }
                }
                 else{
                    if(mydb.insertSettings( edtftpIP.getText().toString(),
                            edtftpun.getText().toString(), edtftppass.getText().toString(),
                            edtIP.getText().toString(), edtun.getText().toString(), edtpass.getText().toString())){
                        Toast.makeText(getApplicationContext(), "Settings Saved",
                                Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(getApplicationContext(), "not Saved",
                                Toast.LENGTH_SHORT).show();
                    }
                 }

                    GlobalVariables.setSrvip(edtIP.getText().toString());
                    GlobalVariables.setDBun(edtun.getText().toString());
                    GlobalVariables.setDBpass(edtpass.getText().toString());
                    GlobalVariables.setFtpserver( edtftpIP.getText().toString());
                    GlobalVariables.setFtpun( edtftpun.getText().toString());
                    GlobalVariables.setFtppass(edtftppass.getText().toString());

                    finish();
                }catch (Exception e){
                       // e.getMessage();
                        Toast.makeText(getApplicationContext(), e.getMessage(),
                                Toast.LENGTH_LONG).show();
               }




            }
        });
    }
}

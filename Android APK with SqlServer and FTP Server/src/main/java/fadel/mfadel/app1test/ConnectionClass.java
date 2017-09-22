/**
 * Created by Angel on 2017-03-17.
 */

package fadel.mfadel.app1test;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;


import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;


public class ConnectionClass {

    String ip =  "";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "MNESdb";
    String un = "";
    String password = "";

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @SuppressLint("NewApi")
    public Connection CONN() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}

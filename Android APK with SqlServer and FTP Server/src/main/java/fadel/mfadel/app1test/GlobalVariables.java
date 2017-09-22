package fadel.mfadel.app1test;

import android.app.Application;

/**
 * Created by Angel on 2017-03-18.
 */

public class GlobalVariables  extends Application {

        static String srvip = "192.168.0.105";
        static String DBun = "sa";
        static String DBpass = "123456";
        static String ftpserver = "192.168.0.105";
        static String ftpun = "a";
        static String ftppass = "a";



        public static String getSrvip() {
                return srvip;
        }

        public static void setSrvip(String srvip) {
                GlobalVariables.srvip = srvip;
        }

        public static String getDBun() {
                return DBun;
        }

        public static void setDBun(String DBun) {
                GlobalVariables.DBun = DBun;
        }

        public static String getDBpass() {
                return DBpass;
        }

        public static void setDBpass(String DBpass) {
                GlobalVariables.DBpass = DBpass;
        }

        public static String getFtpserver() {
                return ftpserver;
        }

        public static void setFtpserver(String ftpserver) {
                GlobalVariables.ftpserver = ftpserver;
        }

        public static String getFtpun() {
                return ftpun;
        }

        public static void setFtpun(String ftpun) {
                GlobalVariables.ftpun = ftpun;
        }

        public static String getFtppass() {
                return ftppass;
        }

        public static void setFtppass(String ftppass) {
                GlobalVariables.ftppass = ftppass;
        }
}

package biz.trustpay.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class Log {

    public static final int ERROR = 1;
    public static final int INFO = 2;
    static String version = "V1.1";

    public static void Log(int type, String title, String message) {


        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        String logdate = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
        String strtype = "";
        if (type == ERROR) {
            strtype = "ERROR";
        }
        if (type == INFO) {
            strtype = "INFO";
        }
        message = version + ";" + logdate + ";" + title + ";" + strtype + ";" + message;
        System.out.println("Logging " + message);
        append(message);
        System.out.println("Logged! ");


    }

    private static void append(String logmessage) {
        System.out.println("in append");
        String logfile = "/var/log/tomcat7/log_trustpay_cim_webclient.log";
        System.out.println("logfile " + logfile);
        File log = new File(logfile);
        if (!log.exists()) {
            try {
                log.createNewFile();
            } catch (IOException ex) {
                System.out.println("error " + ex.getMessage());
            }
        }
        BufferedWriter writer = null;
        try {

            writer = new BufferedWriter(new FileWriter(log, true));
            writer.write(logmessage);
            writer.newLine();

        } catch (IOException ex) {
            System.out.println("error " + ex.getMessage());
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                System.out.println("error " + ex.getMessage());
            }
        }

    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.networking;


import biz.trustpay.servlets.Transact;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author williedejongh
 */
public class HttpCommunication  {

String url = "";

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResponse() {
       
        String ret = "";
        try {
            URL Url = new URL(url);
            System.out.println("To: " + url);
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
            conn.setRequestMethod("GET");

            
            int r = conn.getResponseCode();
            if (r == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                while ((line = in.readLine()) != null) {
                    ret += line;
                }
                in.close();
                in=null;
                
            } 
        } catch (Exception ex) {
            ex.printStackTrace();
            ret="XX";
        } 
        return ret;

    }
}

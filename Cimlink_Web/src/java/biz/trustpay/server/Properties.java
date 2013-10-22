/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.server;

import java.util.UUID;


public class Properties {
  
     public Properties() {
       
     }
    public String getNewGuid() {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        randomUUIDString = randomUUIDString.replaceAll("-", "");
        if (randomUUIDString.length() > 25) {
            randomUUIDString = randomUUIDString.substring(0, 25);
        }
        return randomUUIDString;
    }
}

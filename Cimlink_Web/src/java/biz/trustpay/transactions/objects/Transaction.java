/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.transactions.objects;

import java.sql.Timestamp;

/**
tp_bill_id,tp_application_id,api_version,tp_currencies_id,amount,tp_transaction_id,app_transaction_id,app_user_id,app_text_description,created_at,updated_at,endpoint_id,payment_method_id,notes,tp_payment_provider_id,transaction_status

tp_payon_bill_id,tp_payon_id,tp_payon_currencies_id,tp_bill_id,request_xml,response_xml,status_code,status_message,result,reason_code,reason_message,created_at,updated_at,notes,return_code,return_message,errdescription 

tp_sms_bill_id,tp_sms_channel_id,status,raw_message,sms_ref,created_at,updated_at,notes,tp_bill_id

tp_ukash_bill_id,tp_ukash_currencies_id,voucher_number,ticket_value,txcode,txdescription,ukash_transaction_id,txerrorcode,txerrordescription,change_voucher_encrypted,change_voucher_currency_id,change_voucher_amount,change_voucher_expiry_date,created_at,updated_at,notes,tp_bill_id,tp_transaction_trail_id 
 */
public class Transaction {
   public String transaction_id="";
   public String application_id="";
   public String api_version="1.2.3";
   public String currency="";
   public String territory_id="";
   public double amount=0.0;
   public String app_transaction_id="";
   public String app_user_id="";
   public String app_tx_description="";
   public String payment_method="";
   public String payment_provider="";
   public String status="";
   public String msisdn="";
   public String request_persist="";
   public String response_persist="";
   public Timestamp request_datetime;
   public Timestamp response_datetime;
   public boolean istest=false;
   public String toString() {
       String ret = "[\"application_id\":\""+application_id+"\",";
       ret = ret + "\"transaction_id\":\""+transaction_id+"\",";
       ret = ret + "\"currency\":\""+currency+"\",";
       ret = ret + "\"amount\":\""+amount+"\",";
       ret = ret + "\"payment_method\":\""+payment_method+"\",";
       ret = ret + "\"payment_provider\":\""+payment_provider+"\",";
       ret = ret + "\"status\":\""+status+"\",";
       ret = ret + "\"istest\":\""+istest+"\",";
       ret = ret + "\"app_transaction_id\":\""+app_transaction_id+"\",";
       ret = ret + "\"app_user_id\":\""+app_user_id+"\",";
       ret = ret + "\"app_tx_description\":\""+app_tx_description+"\",";
       ret = ret + "\"msisdn\":\""+msisdn+"\",";
       ret = ret + "\"territory_id\":\""+territory_id+"\"";
       ret = ret +"]";
       return ret;
   }
}

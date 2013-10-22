/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.server;

import biz.trustpay.log.Log;
import biz.trustpay.transactions.objects.PaymentProvider;
import biz.trustpay.transactions.objects.Transaction;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author williedejongh
 */
public class TransactionSession {

    Hashtable hash = new Hashtable();
    Hashtable times = new Hashtable();
    Hashtable methods = new Hashtable();
    Hashtable success = new Hashtable();
    Hashtable fail = new Hashtable();
    private static TransactionSession instance = null;

    private TransactionSession() {
    }

    public static TransactionSession getInstance() {
        if (instance == null) {
            instance = new TransactionSession();
        }
        return instance;

    }

    public void addTransaction(Transaction trans,PaymentProvider[] pps,String successurl,String failurl) {
        clean();
        hash.put(trans.transaction_id, trans);
        Date now = new Date();
        times.put(trans.transaction_id, now);
        methods.put(trans.transaction_id, pps);
        success.put(trans.transaction_id, successurl);
        fail.put(trans.transaction_id, failurl);
        Log.Log(Log.INFO, "Added transaction", trans.transaction_id);
    }

    public Transaction getTransaction(String transactionId) {
        clean();
        Transaction ret = null;
        Object obj = hash.get(transactionId);
        if (obj instanceof Transaction) {
            ret = (Transaction) obj;
        }
        Log.Log(Log.INFO, "get transaction", ret.transaction_id);
        return ret;
    }
    
    public String getSuccess(String transactionId) {
     
        String ret = null;
        Object obj = success.get(transactionId);
        if (obj instanceof String) {
            ret = (String) obj;
        }
        return ret;
    }
    public String getFail(String transactionId) {
        
        String ret = null;
        Object obj = fail.get(transactionId);
        if (obj instanceof String) {
            ret = (String) obj;
        }
        return ret;
    }
    
    public PaymentProvider[] getPaymentProviders(String transactionId) {
        PaymentProvider[] ret = null;
        Object obj = methods.get(transactionId);
        if (obj instanceof PaymentProvider[]) {
            ret = (PaymentProvider[]) obj;
        }
        return ret;
    }

    private void clean() {
        Date now = new Date();
        Enumeration e = times.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            Date date = (Date) times.get(key);
            if (now.getTime() - date.getTime() > 1800000) {
                Log.Log(Log.INFO, "remove transaction", key);
                times.remove(key);
                hash.remove(key);
                methods.remove(key);
                success.remove(key);
                fail.remove(key);
            }
        }

    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.transactions.objects;

/**
 *
 * @author williedejongh
 */
public class TransactionMethod {
	public String buttontext = "";
	public String name = "";
	public String type="";
	public boolean captcha=false;
        public String transactionURL="";
	public String notifier="";
	public TransactionProperty[] properties = new TransactionProperty[0];
	public void addProperty(TransactionProperty prop) {
		TransactionProperty[] tmpproperties = properties;
		properties = new TransactionProperty[tmpproperties.length + 1];
        for (int i = 0; i < tmpproperties.length; i++) {
        	properties[i] = tmpproperties[i];
        }
        properties[tmpproperties.length] = prop;
    }

}


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.transactions.objects;

/**
 *
 * @author williedejongh
 */
public class TransactionProperty {

    public static final int REQUEST = 0;
    public static final int RESPONSE = 1;
    public static final int NONE = 2;
    public static final int TEXTBOX = 3;
    public static final int LABEL = 4;
    public static final int NUMERIC = 5;
    public static final int LIST = 6;
    public static final int CALENDAR = 7;
    public static final int SIGNATURE = 8;
    public static final int TP_AMOUNT = 9;
    public static final int TP_APP_ID = 10;
    public static final int TP_OPERATOR = 11;
    public static final int TP_CURRENCY = 12;
    public static final int TP_TYPE = 13;
    public static final int TP_IP = 14;
    public static final int CAPTCHA = 15;
    public static final int TP_TRANSACTION_ID = 16;
    public static final int APP_REFERENCE =17;
    public static final int APP_USER =18;
    public static final int TX_DESCRIPTION =19;
    public static final int TP_API_VERSION =20;
    public static final int PIN =21;
    public static final int PASSWORD =22;
    public static final int VOUCHER =23;
    public String name = "";
    public boolean input = false;
    public int type = 4;
    public int length = 0;
    public String label = "";
    public int direction = NONE;
    public boolean persist = false;
    public PropertyItem[] items = new PropertyItem[0];
   
    public void addItem(PropertyItem item){
        PropertyItem[] tmpitems = items;
            items = new PropertyItem[tmpitems.length + 1];
            for (int i = 0; i < tmpitems.length; i++) {
                items[i] = tmpitems[i];
            }
            items[tmpitems.length] = item;
    }
}

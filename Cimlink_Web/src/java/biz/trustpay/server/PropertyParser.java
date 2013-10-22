/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.server;

import biz.trustpay.helpers.DocHandler;
import biz.trustpay.helpers.QDParser;
import biz.trustpay.transactions.objects.PropertyItem;
import biz.trustpay.transactions.objects.TransactionMethod;
import biz.trustpay.transactions.objects.TransactionProperty;
import java.io.DataInputStream;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author williedejongh
 */
public class PropertyParser implements DocHandler {
	private TransactionMethod[] methods;
	private TransactionMethod method;
	private TransactionProperty[] properties;
	TransactionProperty property;
	PropertyItem item;
	boolean initem = false;
	boolean inproperty = false;
	boolean inmethod = false;
	static PropertyParser reporter = new PropertyParser();

	public void startDocument() {
		
	}

	public void endDocument() {
		
	}

	public void startElement(String elem, Hashtable h) {

		if (elem.equals("method")) {
			method = new TransactionMethod();
			inmethod = true;
		}
		if (elem.equals("property")) {
			property = new TransactionProperty();
			inproperty = true;
		}
		if (elem.equals("item")) {
			item = new PropertyItem();
			initem = true;
		}
		Enumeration e = h.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String val = (String) h.get(key);
			if (inmethod) {
				if(!inproperty){
				if (key.equals("captcha")) {
					if (val.equals("true")) {
						method.captcha=true;
						TransactionProperty captcha = new TransactionProperty();
						captcha.type = TransactionProperty.CAPTCHA;
						method.addProperty(captcha);
						
					}
				}
				if (key.equals("name")) {
					method.name = val;
				}
                                
				if (key.equals("type")) {
					method.type = val;
				}
				if (key.equals("button")) {
					method.buttontext = val;
				}
                                if (key.equals("transacturl")) {
					method.transactionURL = val;
				}
				}
			}
			if (initem) {
				if (key.equals("text")) {
					item.text = val;
				}
				if (key.equals("value")) {
					item.value = val;
				}

			} else if (inproperty) {
				if (key.equals("dir")) {
					int d = TransactionProperty.NONE;
					if (val.equals("req")) {
						d = TransactionProperty.REQUEST;
					}
					if (val.equals("resp")) {
						d = TransactionProperty.RESPONSE;
					}
					property.direction = (d);
				}
				if (key.equals("input")) {
					boolean b = false;
					if (val.equals("true")) {
						b = true;
					}
					property.input = b;
				}
				if (key.equals("type")) {
					int t = TransactionProperty.LABEL;
					if (val.equals("text")) {
						t = TransactionProperty.TEXTBOX;
					}
					if (val.equals("list")) {
						t = TransactionProperty.LIST;
					}
					if (val.equals("calendar")) {
						t = TransactionProperty.CALENDAR;
					}
					if (val.equals("numeric")) {
						t = TransactionProperty.NUMERIC;
					}
					if (val.equals("signature")) {
						t = TransactionProperty.SIGNATURE;
					}
					if (val.equals("tp_amount")) {
						t = TransactionProperty.TP_AMOUNT;
					}
					if (val.equals("tp_application")) {
						t = TransactionProperty.TP_APP_ID;
					}
					if (val.equals("tp_operator")) {
						t = TransactionProperty.TP_OPERATOR;
					}
					if (val.equals("tp_currency")) {
						t = TransactionProperty.TP_CURRENCY;
					}
					if (val.equals("tp_type")) {
						t = TransactionProperty.TP_TYPE;
					}
					if (val.equals("tp_ip")) {
						t = TransactionProperty.TP_IP;
					}
					if (val.equals("tp_transaction_id")) {
						t = TransactionProperty.TP_TRANSACTION_ID;
					}
					if (val.equals("app_reference")) {
						t = TransactionProperty.APP_REFERENCE;
					}
					if (val.equals("app_user")) {
						t = TransactionProperty.APP_USER;
					}
					if (val.equals("tx_description")) {
						t = TransactionProperty.TX_DESCRIPTION;
					}
					if (val.equals("tp_api_version")) {
						t = TransactionProperty.TP_API_VERSION;
					}
                                        if (val.equals("pin")) {
						t = TransactionProperty.PIN;
					}
					if (val.equals("password")) {
						t = TransactionProperty.PASSWORD;
					}
					if (val.equals("voucher")) {
						t = TransactionProperty.VOUCHER;
					}
					property.type = t;
				}
				if (key.equals("label")) {
					property.label = val;
				}
				if (key.equals("persist")) {
					if (val.equals("true")) {
						property.persist = true;
					}
				}
				if (key.equals("length")) {
					int i = Integer.valueOf(val).intValue();
					property.length = i;
				}
				if (key.equals("name")) {
					property.name = val;
				}

			}

		}
	}

	public void endElement(String elem) {
		if (elem.equals("method")) {
			if (inmethod) {
				TransactionMethod[] tmpmethods = methods;
				methods = new TransactionMethod[tmpmethods.length + 1];
				for (int i = 0; i < tmpmethods.length; i++) {
					methods[i] = tmpmethods[i];
				}
				methods[tmpmethods.length] = method;
				inmethod = false;
			}
		}
		if (elem.equals("property")) {
			method.addProperty(property);
			property = null;
			inproperty = false;
		}
		if (elem.equals("item")) {
			property.addItem(item);
			item = null;
			initem = false;
		}
		
	}

	public void text(String text) {

	}

	public TransactionMethod[] reportOnXml(DataInputStream ds) throws Exception {
		reporter.methods = new TransactionMethod[0];

		QDParser.parse(reporter, ds);
		
		return reporter.methods;
	}
}

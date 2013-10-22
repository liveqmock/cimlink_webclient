/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.transactions.objects;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

public class PaymentProvider {

	String name = "";
	String anchor = "";
	BillingMethod[] bm = new BillingMethod[0];

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAnchor() {
		return anchor;
	}

	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}

	public BillingMethod[] getBm() {
		return bm;
	}

	public void setBm(BillingMethod[] bm) {
		this.bm = bm;
	}

	public void addBm(BillingMethod _bm) {
		BillingMethod[] tmpbm = bm;
		bm = new BillingMethod[tmpbm.length + 1];
		for (int i = 0; i < tmpbm.length; i++) {
			bm[i] = tmpbm[i];
		}
		bm[tmpbm.length] = _bm;
	}

	public String toJSON() {
		JSONObject resp = new JSONObject();
		try {

			JSONArray jsonmethodarr = new JSONArray();
			for (int j = 0; j < bm.length; j++) {

				JSONObject method = new JSONObject();
				method.put("Provider", name);
				method.put("Type", bm[j].type);
				method.put("Country", bm[j].country);
				// method.put("Operator", appinst.getOperator());
				method.put("AppName", bm[j].applicationname);
				method.put("Selected", bm[j].selected);
				method.put("Unselected", bm[j].unselected);
				method.put("Spec", bm[j].spec);

				Currency[] currencies = bm[j].getCurrencies();
				if (currencies != null) {
					JSONArray jsoncurrarr = new JSONArray();
					for (int i = 0; i < currencies.length; i++) {
						JSONObject jsoncurr = new JSONObject();
						jsoncurr.put("code", currencies[i].getCurrencycode());
						jsoncurr.put("name", currencies[i].getNicename());
						jsoncurr.put("symbol", currencies[i].getSymbol());
						jsoncurr.put("isprefix", currencies[i].isIsprefix());
						jsoncurr.put("lower", currencies[i].getLower());
						jsoncurr.put("upper", currencies[i].getUpper());
						jsoncurr.put("step", currencies[i].getStep());
						if (currencies[i].getSn() != null) {
							jsoncurr.put("keyword",
									currencies[i].getSn().fKeyword);
							jsoncurr.put("number",
									currencies[i].getSn().fMsisdn);
							jsoncurr.put("operator",
									currencies[i].getSn().fOperatorCode);
							jsoncurr.put("value", currencies[i].getSn().fValue);
							jsoncurr.put("value_nett",
									currencies[i].getSn().fValueNett);
						}
						jsoncurrarr.put(jsoncurr);
					}
					method.put("Currencies", jsoncurrarr);
				}

				jsonmethodarr.put(method);
			}
			resp.put("method", jsonmethodarr);

		} catch (JSONException ex) {
			System.out.println("Error " + ex.getMessage());
		}
		return resp.toString();
	}
}

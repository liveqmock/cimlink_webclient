/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.server;

import biz.trustpay.transactions.objects.BillingMethod;
import biz.trustpay.transactions.objects.Currency;
import biz.trustpay.transactions.objects.PaymentProvider;
import biz.trustpay.transactions.objects.ServiceNumber;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

public class PaymentProviderParser {

	private  String anchor = "";

	public class ParseException extends Exception {

		public ParseException(String detailMessage) {
			new Exception(detailMessage);
		}
	}

	private  Currency[] parseCurrencyJson(JSONArray jsonarr) throws JSONException {
		
		Currency[] ret = new Currency[0];

		for (int j = 0; j < jsonarr.length(); j++) {
			JSONObject json = jsonarr.getJSONObject(j);
			Currency curr = new Currency();
			// JSONObject json = jsonarr.getJSONObject(0);

			curr.setCurrencycode(json.getString("code"));
			curr.setNicename(json.getString("name"));
			curr.setSymbol(json.getString("symbol"));
			curr.setLower(json.getString("lower"));
			curr.setUpper(json.getString("upper"));
			curr.setStep(json.getString("step"));
			String strisprefix = json.getString("isprefix");
			if (strisprefix.equalsIgnoreCase("true")) {
				curr.setIsprefix(true);
			} else {
				curr.setIsprefix(false);
			}
			if (json.has("number")) {
				try {
					ServiceNumber sn = new ServiceNumber();
					sn.fCurrency = curr.getCurrencycode();
					sn.fValue = json.getString("value");
					sn.fValueNett = json.getString("value_nett");
					sn.fOperatorCode = json.getString("operator");
					sn.fMsisdn = json.getString("number");
					sn.fKeyword = json.getString("keyword");
					if (sn.fMsisdn != null && !sn.fMsisdn.equals("")) {
						curr.setSn(sn);
					}
				} catch (JSONException ex) {
					//Do nothing if the sn format wrong
				}
			}
                       
			Currency[] tmpret = ret;
			ret = new Currency[tmpret.length + 1];
			for (int z = 0; z < tmpret.length; z++) {
				ret[z] = tmpret[z];
			}
			ret[tmpret.length] = curr;
		}
		return ret;
	}

	private BillingMethod parseBillingMethodJson(JSONObject jsonobj)
			throws JSONException {
		BillingMethod ret = new BillingMethod();

		JSONArray nameArray = jsonobj.names();

		JSONArray valArray = jsonobj.toJSONArray(nameArray);

		for (int i = 0; i < nameArray.length(); i++) {
			
			if (nameArray.getString(i).equals("Provider")) {
				ret.setProvider(valArray.getString(i));
			}
			if (nameArray.getString(i).equals("Country")) {
				ret.setCountry(valArray.getString(i));
			}

			if (nameArray.getString(i).equals("Operator")) {
				ret.setOperator(valArray.getString(i));
			}
			if (nameArray.getString(i).equals("AppName")) {
				ret.setApplicationname(valArray.getString(i));
			}
			if (nameArray.getString(i).equals("Type")) {
				ret.setType(valArray.getString(i));
			}
			if (nameArray.getString(i).equals("Anchor")) {
				anchor = valArray.getString(i);
			}
			if (nameArray.getString(i).equals("Spec")) {
				ret.setSpec(valArray.getString(i));
			}
			if (nameArray.getString(i).equals("Unselected")) {
				ret.setUnselected(valArray.getString(i));
			}
			if (nameArray.getString(i).equals("Selected")) {
				ret.setSelected(valArray.getString(i));
			}
			if (nameArray.getString(i).equals("Currencies")) {
				JSONArray jarr = valArray.getJSONArray(i);
				Currency[] cur = parseCurrencyJson(jarr);
				ret.currencies=cur;
			}
		}

		return ret;
	}

	private PaymentProvider[] addBm(PaymentProvider[] pp,
			BillingMethod bm, String anchor) {
		boolean added = false;
		for (int j = 0; j < pp.length; j++) {
			if (pp[j].getName().equals(bm.provider)) {
				added = true;
				pp[j].addBm(bm);
				pp[j].setAnchor(anchor);
			}
		}
		if (!added) {
			PaymentProvider ppbm = new PaymentProvider();
			ppbm.setName(bm.getProvider());

			ppbm.addBm(bm);
			ppbm.setAnchor(anchor);
			PaymentProvider[] tmppp = pp;
			pp = new PaymentProvider[tmppp.length + 1];
			for (int i = 0; i < tmppp.length; i++) {
				pp[i] = tmppp[i];
			}
			pp[tmppp.length] = ppbm;
		}
		return pp;
	}

	public PaymentProvider[] parseProvidersJson(String content)
			throws ParseException {
		PaymentProvider[] pp = new PaymentProvider[0];

		try {
			JSONObject providers = new JSONObject(content);
			JSONArray myalljson = providers.getJSONArray("methods");
			int p = myalljson.length();
			for (int x = 0; x < p; x++) {
				JSONObject methodjson = myalljson.getJSONObject(x);
				BillingMethod bm = parseBillingMethodJson(methodjson);
				pp = addBm(pp, bm, anchor);

			}

		} catch (JSONException e) {
			throw new ParseException("Problem parsing API response"
					+ e.getMessage());
		}

		return pp;
	}

	public PaymentProvider parseMethodJson(String content)
			throws ParseException {
		PaymentProvider pp = new PaymentProvider();

		try {

			JSONObject provider = new JSONObject(content);
			JSONArray nameArray = provider.names();
			JSONArray valArray = provider.toJSONArray(nameArray);
			int na = nameArray.length();
			for (int i = 0; i < na; i++) {
				if (nameArray.getString(i).equals("method")) {
					
					JSONArray jarr = valArray.getJSONArray(i);
					
					int jr = jarr.length();
					if (jr > 0) {
						JSONObject json = jarr.getJSONObject(0);
						BillingMethod bm = parseBillingMethodJson(json);
						pp.addBm(bm);
					}
				}

			}

		} catch (JSONException e) {
			throw new ParseException("Problem parsing API response"
					+ e.getMessage());
		}

		return pp;
	}

}

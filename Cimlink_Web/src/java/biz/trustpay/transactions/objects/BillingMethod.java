/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.transactions.objects;



/**
 *
 * @author williedejongh
 */
public class BillingMethod {

    public String provider = "";
    public String applicationname = "";
    public String country = "";
    public String operator = "";
    public String type = "";
    public String selected = null;
    public String unselected = null;
    public boolean valid = false;
  
    public Currency[] currencies = new Currency[0];
    public String spec = "";

    public void addCurrency(Currency c) {
        Currency[] tmpcurrencies = currencies;
        currencies = new Currency[tmpcurrencies.length + 1];
        for (int i = 0; i < tmpcurrencies.length; i++) {
            currencies[i] = tmpcurrencies[i];
        }
        currencies[tmpcurrencies.length] = c;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Currency[] getCurrencies() {
        return currencies;
    }

    public String getApplicationname() {
        return applicationname;
    }

    public void setApplicationname(String applicationname) {
        this.applicationname = applicationname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String _provider) {
        this.provider = _provider;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getUnselected() {
        return unselected;
    }

    public void setUnselected(String unselected) {
        this.unselected = unselected;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer("BillingMethod=[");

        ret.append(provider + "\n");
        ret.append(applicationname + "\n");
        ret.append(country + "\n");
        ret.append(operator + "\n");
        ret.append(type + "\n");

        Currency[] currs = getCurrencies();
        ret.append("Currencies=[");
        for (int z = 0; z < currs.length; z++) {
            ret.append(currs[z].toString());
        }
        ret.append("]");

        ret.append("]");
        return ret.toString();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.transactions.objects;

/**
 *
 * @author williedejongh
 */
public class Currency {

    String currencycode = "";
    String nicename = "";
    String symbol = "";
    String lower="";
    String upper ="";
    String step ="";
    public ServiceNumber sn = null;
    boolean isprefix = true;
    public ServiceNumber getSn() {
		return sn;
	}

	public void setSn(ServiceNumber sns) {
		this.sn = sns;
	}

   public String toString() {
		StringBuffer ret = new StringBuffer("[");
		if (sn != null) {
			ret.append("SNStore=[");
			ret.append(sn.fCurrency + "," + sn.fValueNett + "," + sn.fValue);
			ret.append("],");
		}
		ret.append("CurrencyCode=" + currencycode + ",NiceName=" + nicename
				+ ",Symbol=" + symbol + ",IsPrefix=" + isprefix + ",Lower="
				+ lower + ",Upper=" + upper + ",Step=" + step + "]");
		return ret.toString();
	}

    public String getLower() {
        return lower;
    }

    public void setLower(String lower) {
        this.lower = lower;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getUpper() {
        return upper;
    }

    public void setUpper(String upper) {
        this.upper = upper;
    }

    
    
    public String getCurrencycode() {
        return currencycode;
    }

    public void setCurrencycode(String currencycode) {
        this.currencycode = currencycode;
    }

    public boolean isIsprefix() {
        return isprefix;
    }

    public void setIsprefix(boolean isprefix) {
        this.isprefix = isprefix;
    }

    public String getNicename() {
        return nicename;
    }

    public void setNicename(String nicename) {
        this.nicename = nicename;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}

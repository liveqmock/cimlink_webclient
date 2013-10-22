/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.transactions.objects;

public class ServiceNumber {
public String fCurrency;
public String fValue;
public String fValueNett;
public String fOperatorCode;
public String fMsisdn;
public String fKeyword = "kw";


//-----------------------------------------------------------------

public ServiceNumber() {
    // Empty null constructor
}

//-----------------------------------------------------------------

public ServiceNumber(String currency, String value, String valueNett, String operatorCode,
        String msisdn, String keyword) {
    fCurrency     = currency;
    fValue        = value;
    fValueNett    = valueNett;
    fOperatorCode = operatorCode;
    fMsisdn       = msisdn;
    fKeyword      = keyword;
}

//-----------------------------------------------------------------

public String toString() {
    return "fCurrency="+fCurrency+",fValue="+fValue+",fValueNett="+fValueNett+",fOperatorCode="
            +fOperatorCode+",fMsisdn="+fMsisdn+",fKeyword="+fKeyword;
}

//-----------------------------------------------------------------
}

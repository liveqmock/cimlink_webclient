/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.server;

import biz.trustpay.networking.HttpCommunication;
import biz.trustpay.transactions.objects.BillingMethod;
import biz.trustpay.transactions.objects.PaymentProvider;
import biz.trustpay.transactions.objects.Transaction;
import biz.trustpay.transactions.objects.TransactionMethod;
import biz.trustpay.transactions.objects.TransactionProperty;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 *
 * @author williedejongh
 */
public class PaymentMethods {

    public Transaction transaction = null;
    private String appname = "";
    PaymentProvider[] pps = null;
    private String transaction_id = "";
    private StringBuilder popups = new StringBuilder();
    private StringBuilder methods = new StringBuilder();
    private StringBuilder style = new StringBuilder();
    private StringBuilder submit = new StringBuilder();
    private StringBuilder mobile = new StringBuilder();
    private StringBuilder reload = new StringBuilder();
    private StringBuilder validation = new StringBuilder();
    private StringBuilder getcbcode = new StringBuilder();
    private String cbcode = "";
    String country = "";
    String msisdn = "";
    String currency = "";
    String success = "";
    double amount = 0;
    private String keyword = "";

    public PaymentMethods(PaymentProvider[] _pps, String _country, String _transaction_id, double _amount, String _currency, String _cbcode, String _success, String _msisdn) {
        msisdn = _msisdn;
        pps = _pps;
        success = _success;
        cbcode = _cbcode;
        country = _country;
        currency = _currency;
        transaction_id = _transaction_id;
        amount = _amount;
        submit.append("<script type=\"text/javascript\">");
        submit.append("$(document).ready(function() {");

        submit.append("Validate();");
        submit.append("});");
        validation.append("function Validate(){");
        buildMethods();

        validation.append("}");
        submit.append(validation.toString());
        submit.append("</script>");

    }

    public String getHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\"");
        sb.append("\"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        sb.append("<link rel=\"stylesheet\" href=\"css/test.min.css\" />");
        sb.append("<link rel=\"stylesheet\" href=\"https://code.jquery.com/mobile/1.3.2/jquery.mobile.structure-1.3.2.min.css\" />");
        sb.append("<script src=\"https://code.jquery.com/jquery-1.9.1.min.js\"></script>");
        sb.append("<script src=\"https://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.js\"></script>");
        sb.append("<style>");
        sb.append("body {");
        sb.append("display: block;");
        sb.append("margin: 0px;");
        sb.append("padding: 0px;");
        sb.append("text-align: center;");
        sb.append("}");
        sb.append(".container {");
        sb.append("display: block;");
        sb.append("margin: auto;");
        sb.append("padding: 0px 0px 20px 0px;");
        sb.append("max-width: 600px;");
        sb.append("}");
        sb.append("#logo {");
        sb.append("padding: 20px 0px 10px 0px;");
        sb.append("}");
        sb.append("#transmessage {");
        sb.append("}");
        sb.append("#footer p {");
        sb.append("margin: 0px;");
        sb.append("padding: 5px 0px 5px 0px;");
        sb.append("}");
        sb.append(style.toString());
        sb.append("#expiry_date .ui-select {");
        sb.append("display: inline-block;");
        sb.append("}");
        sb.append("</style>");
        sb.append("<title>Pay by CIMLINK</title>");
        sb.append("</head>");
        sb.append("<body style=\"text-align: center;\">");
        sb.append(submit.toString());
        sb.append("<div class=\"container\">");
        sb.append("<img src=\"images/logo.png\" id=\"logo\" alt=\"Logo\" />");
        sb.append("<ul data-role=\"listview\" data-inset=\"true\" data-theme=\"a\">");
        sb.append("<li data-role=\"list-divider\">" + appname + "</li>");
        sb.append("<li><h5>" + transaction.app_tx_description + "</h5><h5>" + "Purchase at " + transaction.currency + " " + transaction.amount + "</h5></li>");
        sb.append("</ul></br>");
        sb.append("<ul data-role=\"listview\" data-inset=\"true\" data-theme=\"a\">");
        sb.append("<li data-role=\"list-divider\">Select a Payment Method</li>");
        sb.append(methods.toString());
        sb.append("</ul></br>");
        
        sb.append("<ul data-role=\"listview\" data-inset=\"true\" data-theme=\"a\">");
        sb.append("<li data-role=\"list-divider\">How to use CIMLINK</li>");
        sb.append("<li><a href=\"http://www.cimlink.mobi\"  ><img src=\"images/cims.png\" alt=\"\" /><h2>Use CIMLINK to pay using your FNB eWallet.</h2><h2>For more information go to <font color=\"#006699\">www.cimlink.mobi</font></h2><a href=\"http://www.cimlink.mobi\"></a></a></li>");
                                
        sb.append("</ul></br>");
        
        sb.append("<a href=\"" + TransactionSession.getInstance().getFail(transaction_id) + "\" data-role=\"button\" data-icon=\"delete\">Cancel</a>");
        sb.append("</div>");
        sb.append(popups.toString());
        sb.append(mobile.toString());
        sb.append(reload.toString());
        sb.append(getcbcode.toString());
        sb.append("<div id=\"footer\" data-role=\"footer\" class=\"ui-bar\" data-position=\"fixed\">");
        sb.append("<p><a href=\"http://www.cimsolutions.co.za\" rel=\"external\">www.cimsolutions.co.za</a></p>");
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    public void buildMethods() {
        boolean carrier = false;
        int count = 0;
        for (int i = 0; i < pps.length; i++) {
            BillingMethod[] bm = pps[i].getBm();
            for (int j = 0; j < bm.length; j++) {
                for (int x = 0; x < bm[j].currencies.length; x++) {
                    Double upper = Double.valueOf(bm[j].currencies[x].getUpper());
                    Double lower = Double.valueOf(bm[j].currencies[x].getLower());

                    if (lower <= amount && amount <= upper && bm[j].currencies[x].getCurrencycode().equals(currency)) {
                        if (bm[j].type.equals("CARRIER BILLING")) {
                            
                        } else {
                            appname = bm[j].applicationname;
                            String methodname = bm[j].type;
                            if (methodname.equals("CIMS")) {
                                methods.append("<li><a href=\"#" + methodname + "\"  data-rel=\"popup\" data-position-to=\"window\" data-transition=\"pop\"><img src=\"images/cims.png\" alt=\"\" /><h2>Pay Via CIMLINK</h2></a></li>");
                                //This is a HACK for now
                                bm[j].selected = "images/cims.png";
                                count++;
                                popups.append(getPopup(bm[j]));
                                break;
                            } 

                        }
                    }
                }
            }
        }
        
        if (count == 0) {
            methods.append("<li><h5>No payment methods for this transaction.</h5></li>");
        }


    }

    public String getPopup(BillingMethod bm) {
        StringBuilder sb = new StringBuilder();

        try {

            PropertyParser parser = new PropertyParser();
            byte[] bs = bm.spec.getBytes("UTF8");
            ByteArrayInputStream bais = new ByteArrayInputStream(bs);
            DataInputStream in = new DataInputStream(bais);
            TransactionMethod[] methods = parser.reportOnXml(in);
            String urlcall = "";
            style.append("#" + bm.type.replaceAll(" ", "_") + "_form {");
            style.append("padding: 20px;");
            style.append("max-width: 550px;");
            style.append("}");
            sb.append("<div data-role=\"popup\" id=\"" + bm.type.replaceAll(" ", "_") + "\" data-theme=\"a\" class=\"ui-corner-all\" data-overlay-theme=\"a\">");
            sb.append("<form id=\"" + bm.type.replaceAll(" ", "_") + "_form\">");
            sb.append("<label id = \"message" + bm.type.replaceAll(" ", "_") + "\"class=\"lbl\">  </label>");
            if (bm.type.equals("CREDIT CARD")) {
                sb.append("<h3><img src=\"" + bm.selected + "\" id=\"brandimg\" name=\"brandimg\" alt=\"\" /></h3>");
            } else {
                sb.append("<h3><img src=\"" + bm.selected + "\" alt=\"\" /></h3>");
            }
            sb.append("<input type=\"hidden\"  name=\"type\" value=\"" + bm.type + "\" >");

            sb.append("<input type=\"hidden\"  name=\"txid\" value=\"" + transaction_id + "\" >");






            for (int j = 0; j < methods.length; j++) {
                urlcall = "type=" + bm.type;
                urlcall = urlcall + "&txid=" + transaction_id;
                submit.append("function Pay" + methods[0].type.replaceAll(" ", "_") + j + "() {");

                submit.append("var ok = true;");
                

                for (int i = 0; i < methods[j].properties.length; i++) {

                    TransactionProperty prop = methods[j].properties[i];
                    if (prop.input) {
                        if (prop.name.equals("expmonth")) {
                            sb.append("<label  class=\"lbl\">Expiry MM/YY</label>");
                        } else if (prop.name.equals("expyear")) {
                            //do nothing
                        } else if (prop.name.equals("brand")) {
                            //do nothing
                        } else {
                            if (prop.type != TransactionProperty.LABEL) {

                                if (prop.type == TransactionProperty.CALENDAR || prop.type == TransactionProperty.LIST) {
                                    sb.append("<label  class=\"lbl\">" + prop.label + "</label>");
                                }

                            }
                        }
                        switch (prop.type) {
                            case TransactionProperty.CALENDAR:
                                sb.append("<input  type=\"datetime\" id=\"" + prop.name + "\" name=\"" + prop.name + "\" class=\"userinput\">");
                                submit.append(" var " + prop.name + " = $(\"input#" + prop.name + "\").val(); ");
                                urlcall = urlcall + "&" + prop.name + "=' + " + prop.name + "+'";

                                break;
                            case TransactionProperty.LABEL:

                                //sb.append("<label class=\"lbl\">" + prop.label + "</label>");
                                //sb.append("</br>");

                                break;
                            case TransactionProperty.LIST:
                                String special = "";
                                if (prop.name.equals("brand")) {
                                    sb.append("<input  type=\"hidden\" id=\"" + prop.name + "\" name=\"" + prop.name + "\" value = \"VISA\"class=\"userinput\">");
                                    //sb.append("<br/><br/><img src=\"images/visa.png\" alt=\"\" id=\"brandimg\" name=\"brandimg\" border = \"2\"/><br/><br/>");
                                    submit.append(" var " + prop.name + " = $(\"input#" + prop.name + "\").val(); ");
                                    urlcall = urlcall + "&" + prop.name + "=' + " + prop.name + "+'";

                                } else {
                                    if (prop.name.equals("expmonth")) {
                                        sb.append("<div id=\"expiry_date\"><p>");
                                        special = "  data-mini=\"true\" data-inline=\"true\" ";
                                    }
                                    if (prop.name.equals("expyear")) {
                                        special = "  data-mini=\"true\" data-inline=\"true\" ";
                                    }
                                    sb.append("<select  id=\"" + prop.name + "\"  name=\"" + prop.name + "\" class=\"userinput" + special + "\">");
                                    for (int x = 0; x < prop.items.length; x++) {
                                        String selected = "";
                                        if (prop.name.equals("country") && prop.items[x].value.equals(country)) {
                                            selected = "selected=\"selected\"";
                                        }
                                        sb.append("<option value=\"" + prop.items[x].value + "\" " + selected + ">" + prop.items[x].text + "</option>");
                                    }
                                    sb.append("</select>");
                                    if (prop.name.equals("expmonth")) {
                                        sb.append("/");
                                    } else if (prop.name.equals("expyear")) {
                                        sb.append("</div>");
                                    }
                                    submit.append(" var " + prop.name + " = $(\"select#" + prop.name + "\").val(); ");
                                    urlcall = urlcall + "&" + prop.name + "=' + " + prop.name + "+'";
                                }
                                break;
                            case TransactionProperty.NUMERIC:
                                if (prop.name.equals("msisdn") && bm.type.equals("WALLET")) {
                                    sb.append("<label class=\"lbl\">Pay from Wallet</label>");
                                }
                                if (prop.name.equals("msisdn")) {
                                    if (msisdn != null && !msisdn.equals("")) {
                                        sb.append("<input  type=\"text\" id=\"" + prop.name + "\" name=\"" + prop.name + "\" placeholder=\"" + prop.label + "\" value =\"" + msisdn + "\" class=\"userinput\">");
                                    } else {
                                        sb.append("<input  type=\"text\" id=\"" + prop.name + "\" name=\"" + prop.name + "\" placeholder=\"" + prop.label + "\" class=\"userinput\">");
                                    }
                                } else {
                                    sb.append("<input  type=\"text\" id=\"" + prop.name + "\" name=\"" + prop.name + "\" placeholder=\"" + prop.label + "\" class=\"userinput\">");
                                }
                                
                                submit.append(" var " + prop.name + " = $(\"input#" + prop.name + "\").val(); ");
                                if (prop.name.equals("number")) {
                                    submit.append("if((" + prop.name + ".length<15||" + prop.name + ".length>16)){");
                                    submit.append("ok=false;");
                                    submit.append(" $('#message" + bm.type.replaceAll(" ", "_") + "').text(\"Card number incorrect length.\");");
                                    submit.append(" $('#message" + bm.type.replaceAll(" ", "_") + "').css(\"color\", 'red');");
                                    submit.append("}");
                                }
                                if (prop.name.equals("verifi")) {
                                    submit.append("if((" + prop.name + ".length<1)){");
                                    submit.append("ok=false;");
                                    submit.append(" $('#message" + bm.type.replaceAll(" ", "_") + "').text(\"CVV cannot be blank.\");");
                                    submit.append(" $('#message" + bm.type.replaceAll(" ", "_") + "').css(\"color\", 'red');");
                                    submit.append("}");
                                }
                                if (prop.name.equals("voucher") && bm.type.equals("VOUCHER")) {
                                    submit.append("if((" + prop.name + ".length!=19)){");
                                    submit.append("ok=false;");
                                    submit.append(" $('#message" + bm.type.replaceAll(" ", "_") + "').text(\"Voucher has to be 19 digits.\");");
                                    submit.append(" $('#message" + bm.type.replaceAll(" ", "_") + "').css(\"color\", 'red');");
                                    submit.append("}");
                                }
                                if (prop.name.equals("msisdn") && bm.type.equals("WALLET")) {
                                    submit.append("if((" + prop.name + ".length<10||" + prop.name + ".length>11)&&(" + prop.name + ".length>0)){");
                                    submit.append("ok=false;");
                                    submit.append(" $('#message" + bm.type.replaceAll(" ", "_") + "').text(\"Invalid cellnumber.\");");
                                    submit.append(" $('#message" + bm.type.replaceAll(" ", "_") + "').css(\"color\", 'red');");
                                    submit.append("}");
                                }
                                urlcall = urlcall + "&" + prop.name + "=' + " + prop.name + "+'";
                                if (prop.name.equals("number")) {
                                    validation.append("$(\"input#number\").on(\"input\", null, null, function(){");
                                    validation.append("var cc = $(\"input#number\").val();");
                                    validation.append("if (cc.substring(0,1)==(\"4\")) {");
                                    validation.append("$(\"input#brand\").val(\"VISA\");");
                                    validation.append("$(\"img#brandimg\").attr(\"src\",\"images/visa.png\");");
                                    validation.append("} else ");
                                    validation.append("if (cc.substring(0,2)==(\"51\") || cc.substring(0,2)==(\"52\") || cc.substring(0,2)==(\"53\") || cc.substring(0,2)==(\"54\") || cc.substring(0,2)==(\"55\")) {");
                                    validation.append("$(\"input#brand\").val(\"MASTER\");");
                                    validation.append("$(\"img#brandimg\").attr(\"src\",\"images/mastercard.png\");");
                                    validation.append("} else {");
                                    validation.append("$(\"input#brand\").val(\"NONE\");");
                                    validation.append("$(\"img#brandimg\").attr(\"src\",\"images/creditcard.png\");");
                                    validation.append("}");
                                    validation.append("})");
                                }
                                break;
                            case TransactionProperty.TEXTBOX:
                                if (prop.name.equals("msisdn")) {
                                    if (msisdn != null && !msisdn.equals("")) {
                                        sb.append("<input   type=\"text\" id=\"" + prop.name + "\" placeholder=\"" + prop.label + "\" name=\"" + prop.name + "\" value =\"" + msisdn + "\" class=\"userinput\">");
                                    } else {
                                        sb.append("<input   type=\"text\" id=\"" + prop.name + "\" placeholder=\"" + prop.label + "\" name=\"" + prop.name + "\" class=\"userinput\">");
                                    }
                                } else {
                                    sb.append("<input   type=\"text\" id=\"" + prop.name + "\" placeholder=\"" + prop.label + "\" name=\"" + prop.name + "\" class=\"userinput\">");
                                }
                                submit.append(" var " + prop.name + " = $(\"input#" + prop.name + "\").val(); ");
                                urlcall = urlcall + "&" + prop.name + "=' + " + prop.name + "+'";
                                if (prop.name.equals("holder")) {
                                    submit.append("if((" + prop.name + ".length<1)){");
                                    submit.append("ok=false;");
                                    submit.append(" $('#message" + bm.type.replaceAll(" ", "_") + "').text(\"Card Holder cannot be blank.\");");
                                    submit.append(" $('#message" + bm.type.replaceAll(" ", "_") + "').css(\"color\", 'red');");
                                    submit.append("}");
                                }


                                break;
                            case TransactionProperty.VOUCHER:
                                if (prop.name.equals("voucher") && bm.type.equals("WALLET")) {
                                    sb.append("<label class=\"lbl\">Pay with Voucher</label>");
                                }
                                sb.append("<input    type=\"text\" id=\"wallet" + prop.name + "\" placeholder=\"" + prop.label + "\" name=\"wallet" + prop.name + "\" class=\"userinput\">");
                                submit.append(" var wallet" + prop.name + " = $(\"input#wallet" + prop.name + "\").val(); ");
                                urlcall = urlcall + "&" + prop.name + "=' + wallet" + prop.name + "+'";
                                if (prop.name.equals("voucher") && bm.type.equals("WALLET")) {
                                    submit.append("if((wallet" + prop.name + ".length!=18)&&(wallet" + prop.name + ".length>0)){");
                                    submit.append("ok=false;");
                                    submit.append(" $('#message" + bm.type.replaceAll(" ", "_") + "').text(\"Voucher has to be 18 digits.\");");
                                    submit.append(" $('#message" + bm.type.replaceAll(" ", "_") + "').css(\"color\", 'red');");
                                    submit.append("}");
                                }
                                break;
                            case TransactionProperty.PIN:
                                sb.append("<input   type=\"password\" id=\"" + prop.name + j + "\" placeholder=\"" + prop.label + "\" name=\"" + prop.name + j + "\" class=\"userinput\">");
                                submit.append(" var " + prop.name + j + " = $(\"input#" + prop.name + j + "\").val(); ");
                                urlcall = urlcall + "&" + prop.name + "=' + " + prop.name + j + "+'";

                                break;
                            case TransactionProperty.PASSWORD:
                                sb.append("<input   type=\"password\" id=\"" + prop.name + "\" placeholder=\"" + prop.label + "\" name=\"" + prop.name + "\" class=\"userinput\">");
                                submit.append(" var " + prop.name + " = $(\"input#" + prop.name + "\").val(); ");
                                urlcall = urlcall + "&" + prop.name + "=' + " + prop.name + "+'";

                                break;


                            default:
                        };



                    }
                }


                sb.append("<a href=\"#\" data-rel=\"back\" data-role=\"button\" data-theme=\"a\" data-icon=\"delete\" data-iconpos=\"left\" data-mini=\"true\" class=\"ui-btn-right\">Cancel</a>");
                sb.append("<input type=\"button\" data-theme=\"a\" data-icon=\"check\" data-mini=\"true\" data-inline=\"true\" value=\"Pay\" name=\"submit\" onclick='JavaScript:Pay" + methods[j].type.replaceAll(" ", "_") + j + "()'/>");
                if (methods.length > 1 && j < methods.length - 1) {
                    sb.append("</br></br><label class=\"lbl\">-OR-</label></br></br>");
                }

                submit.append("var dataString" + methods[0].type.replaceAll(" ", "_") + j + " ='" + urlcall.substring(0, urlcall.length() - 2) + ";");
                submit.append("if(ok){");
                submit.append("$.mobile.showPageLoadingMsg();");
                submit.append("$.ajax({");
                submit.append("type: \"POST\",");
                submit.append("url: \"http://facethemusicpay.m4me.mobi/pay/Pay?\"+dataString" + methods[0].type.replaceAll(" ", "_") + j + "+\"\",");
                submit.append("}).done(function( msg ) {");
                submit.append("$.mobile.hidePageLoadingMsg();");
                submit.append("if(msg.substring(0,4)==(\"http\")){");
                if(methods[0].type.replaceAll(" ", "_").equals("CIMS")){
                    submit.append("alert(\"Please dial *120*1017# to complete your purchase.\");");
                }
                submit.append("window.location=msg;");
                
                submit.append("} else {");
                submit.append(" $('#message" + bm.type.replaceAll(" ", "_") + "').text(msg);");
                submit.append(" $('#message" + bm.type.replaceAll(" ", "_") + "').css(\"color\", 'red');");
                submit.append("}");
                submit.append("});");
                submit.append("}");
                submit.append("}");
            }

            sb.append("</form>");

            sb.append("</div>");


        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return sb.toString();
    }
}

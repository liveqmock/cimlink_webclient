/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.servlets;

import biz.trustpay.log.Log;
import biz.trustpay.networking.HttpCommunication;
import biz.trustpay.server.PropertyParser;
import biz.trustpay.server.TransactionSession;
import biz.trustpay.transactions.objects.BillingMethod;
import biz.trustpay.transactions.objects.PaymentProvider;
import biz.trustpay.transactions.objects.Transaction;
import biz.trustpay.transactions.objects.TransactionMethod;
import biz.trustpay.transactions.objects.TransactionProperty;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.me.JSONObject;

/**
 *
 * @author williedejongh
 */
public class Pay extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String transactionid = request.getParameter("txid");
        String type = request.getParameter("type");

        String captcha = request.getParameter("captcha");
        if (captcha != null) {
            captcha = URLDecoder.decode(captcha, "UTF-8");
        }

        String challenge = request.getParameter("challenge");
        if (challenge != null) {
            challenge = URLDecoder.decode(challenge, "UTF-8");
        }

        String ip = request.getRemoteAddr();
        BillingMethod bm = null;
        PrintWriter out = response.getWriter();
        if (transactionid != null) {
            System.out.println("Pay TransactionId is :" + transactionid);
            System.out.println("Type is :" + type);
            Transaction trans = TransactionSession.getInstance().getTransaction(transactionid);
            if (trans != null) {
                String successurl = TransactionSession.getInstance().getSuccess(transactionid);
                String failurl = TransactionSession.getInstance().getFail(transactionid);
                System.out.println("Payment for: " + trans.toString());
                PaymentProvider[] pps = TransactionSession.getInstance().getPaymentProviders(transactionid);
                if (pps != null) {
                    for (int i = 0; i < pps.length; i++) {
                        BillingMethod[] bms = pps[i].getBm();
                        if (bms != null) {
                            for (int j = 0; j < bms.length; j++) {
                                if (bms[j].type.equals(type)) {
                                    System.out.println("Pay using " + bms[j].toString());
                                    bm = bms[j];
                                    break;
                                }
                            }
                        }
                    }
                }
                if (bm == null) {
                    response.sendError(400, "Invalid payment type!");
                } else {
                    String transacturl = "https://my.trustpay.biz/TrustPayGateway/WebGateway?type=" + URLEncoder.encode(type, "UTF-8") + "&";
                    String msg = "";
                    try {
                        PropertyParser parser = new PropertyParser();
                        byte[] bs = bm.spec.getBytes("UTF8");
                        ByteArrayInputStream bais = new ByteArrayInputStream(bs);
                        DataInputStream in = new DataInputStream(bais);
                        TransactionMethod[] methods = parser.reportOnXml(in);

                        for (int i = 0; i < methods.length; i++) {

                            for (int j = 0; j < methods[i].properties.length; j++) {
                                if (methods[i].properties[j].direction == TransactionProperty.REQUEST) {

                                    String pname = methods[i].properties[j].name;
                                    if (methods[i].properties[j].input) {
                                        String val = request.getParameter(pname);
                                        System.out.println("parameter: " + pname + " value: " + val);
                                        if (val != null) {
                                            transacturl = transacturl + pname + "=" + URLEncoder.encode(val, "UTF-8") + "&";
                                        }
                                    } else {
                                        String value = "";
                                        switch (methods[i].properties[j].type) {
                                            case TransactionProperty.TP_AMOUNT:
                                                value = String.valueOf(trans.amount);
                                                break;
                                            case TransactionProperty.TP_APP_ID:
                                                value = trans.application_id;
                                                break;
                                            case TransactionProperty.TP_CURRENCY:
                                                value = trans.currency;
                                                break;
                                            case TransactionProperty.TP_IP:
                                                value = ip;
                                                break;
                                            case TransactionProperty.TP_OPERATOR:
                                                value = bm.provider;
                                                break;
                                            case TransactionProperty.TP_TYPE:
                                                value = bm.type;
                                                break;
                                            case TransactionProperty.TP_TRANSACTION_ID:
                                                value = trans.transaction_id;
                                                break;
                                            case TransactionProperty.APP_REFERENCE:
                                                value = trans.app_transaction_id;
                                                break;
                                            case TransactionProperty.APP_USER:
                                                value = trans.app_user_id;
                                                break;
                                            case TransactionProperty.TX_DESCRIPTION:
                                                value = trans.app_tx_description;
                                                break;
                                            case TransactionProperty.TP_API_VERSION:
                                                value = trans.api_version;
                                                break;
                                            default:
                                                break;
                                        }
                                        System.out.println("parameter: " + pname + " value: " + value);
                                        if (value != null) {
                                            transacturl = transacturl + pname + "=" + URLEncoder.encode(value, "UTF-8") + "&";
                                        }
                                    }

                                }

                            }
                        }
                        if (captcha != null && challenge != null) {
                            transacturl = transacturl + "captcha=" + URLEncoder.encode(captcha, "UTF-8") + "&";
                            transacturl = transacturl + "challenge=" + URLEncoder.encode(challenge, "UTF-8") + "&";
                        }
                        if (trans.msisdn != null) {
                            transacturl = transacturl + "webmsisdn=" + trans.msisdn + "&";
                        }
                        transacturl = transacturl + "istest=" + trans.istest + "&";
                        transacturl = transacturl.substring(0, transacturl.length() - 1);
                        HttpCommunication comms = new HttpCommunication();
                        comms.setUrl(transacturl);

                        String result = comms.getResponse();
                        System.out.println(result);
                        JSONObject resultobj = new JSONObject(result);

                        if (resultobj.getString("result").equals("SUCCESS")) {
                            System.out.println("Redirecting to " + successurl);
                            out.println(successurl);
                            msg = "SUCCESS" + ";" + successurl;
                        } else {
                            out.println(resultobj.getString("errDescription"));
                            msg = "ERROR" + ";" + resultobj.getString("errDescription");
                        }


                    } catch (Exception ex) {

                        out.println("Transaction Failed.");
                        Log.Log(Log.ERROR, Pay.class.getName() + "186", ex.getMessage());
                    } finally {
                        Log.Log(Log.INFO, Pay.class.getName() + "171", transacturl + ";" + msg);
                        out.close();
                    }
                }
            } else {
                try {
                    out.println("Transaction has expired.");
                } catch (Exception ex) {
                    Log.Log(Log.ERROR, Pay.class.getName() + "195", ex.getMessage());
                } finally {
                    out.close();
                }
            }
        } else {
            response.sendError(400, "Transaction id cannot be null!");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}

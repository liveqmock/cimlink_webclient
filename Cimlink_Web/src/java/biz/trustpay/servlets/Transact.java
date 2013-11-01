/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.servlets;

import biz.trustpay.networking.HttpCommunication;
import biz.trustpay.server.PaymentMethods;
import biz.trustpay.server.PaymentProviderParser;
import biz.trustpay.server.PaymentProviderParser.ParseException;
import biz.trustpay.server.Properties;
import biz.trustpay.server.TransactionSession;
import biz.trustpay.transactions.objects.PaymentProvider;
import biz.trustpay.transactions.objects.Transaction;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author williedejongh
 */
public class Transact extends HttpServlet {

    PrintWriter out = null;
    Transaction trans = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sessionid = request.getSession(true).getId();
        System.out.println("TransactServlet SessionId is :" + sessionid);
        response.setContentType("text/html;charset=UTF-8");
        out = response.getWriter();
        Properties props = new Properties();
        trans = new Transaction();
        String success = request.getParameter("success");
        String fail = request.getParameter("fail");
        String istest = request.getParameter("istest");
        if (istest != null && istest.equals("true")) {
            trans.istest = true;
        } else {
            trans.istest = false;
        }
        trans.msisdn = request.getParameter("msisdn");
        trans.app_transaction_id = request.getParameter("txid");
        trans.currency = request.getParameter("currency");
        trans.app_user_id = request.getParameter("appuser");
        trans.application_id = "ap.9f73283f-5cf7-428d-ab94-4a1e55ef4467";
        trans.app_tx_description = request.getParameter("message");
        if (trans.app_tx_description != null) {
            trans.app_tx_description = URLDecoder.decode(trans.app_tx_description, "UTF-8");
        }
        String country = request.getParameter("countrycode");
        trans.territory_id = country;
        trans.transaction_id = "www." + props.getNewGuid();
        String stramount = request.getParameter("amount");
        if (stramount != null) {
            if (isDouble(stramount)) {
                trans.amount = Double.valueOf(stramount);
            } else {
                trans.amount = 0;
            }
        }
        if (success == null || success.equals("") || !success.toLowerCase().startsWith("http")) {
            response.sendError(400, "Invalid value for success URL!");
        } else if (fail == null || fail.equals("") || !fail.toLowerCase().startsWith("http")) {
            response.sendError(400, "Invalid value for fail URL!");
        } else if (trans.app_transaction_id == null || trans.app_transaction_id.equals("")) {
            response.sendError(400, "Invalid value for txid - transaction ID parameter!");
        } else if (trans.currency == null || trans.currency.equals("")) {
            response.sendError(400, "Invalid value for currency parameter!");
        } else if (trans.application_id == null || trans.application_id.equals("")) {
            response.sendError(400, "Invalid value for appid parameter!");
        } else if (stramount == null || !isDouble(stramount)) {
            response.sendError(400, "Invalid value for amount parameter!");
        } else {

            String url = "";
            HttpCommunication comms = null;
            if (country == null) {
                String ip = request.getRemoteAddr();
                url = "http://api.hostip.info/country.php?ip=" + ip;
                comms = new HttpCommunication();
                comms.setUrl(url);
                country = comms.getResponse();
                if (country.equals("XX")) {
                    country = "ZA";
                }
            }
            trans.territory_id = country;
            url = "https://my.trustpay.biz/PricePointProcessor/GetPricepoints?application=" + trans.application_id + "&country=" + country + "&graphics=HIGH";
            comms = new HttpCommunication();
            comms.setUrl(url);
            String methods = comms.getResponse();

            try {
                boolean carrier = false;
                PaymentProviderParser parser = new PaymentProviderParser();
                PaymentProvider[] pps = parser.parseProvidersJson(methods);
                for (int z = 0; z < pps.length; z++) {
                    for (int t = 0; t < pps[z].getBm().length; t++) {
                        if (pps[z].getBm()[t].type.equals("CARRIER BILLING")) {
                            carrier = true;
                        }
                    }
                }
                String cbcode = "";
                if (carrier) {
                    if (trans.istest) {
                        cbcode = "test";
                    } else {
                        cbcode = "XXXXX";
                        //cbcode = getCarrierCode(trans);
                    }
                }
                PaymentMethods pm = new PaymentMethods(pps, country, trans.transaction_id, trans.amount, trans.currency, cbcode, success,trans.msisdn);
                pm.transaction = trans;

                TransactionSession.getInstance().addTransaction(trans, pps, success, fail);
                out.println(pm.getHtml());
            } catch (ParseException p) {
                p.printStackTrace();
            } finally {
                out.close();
            }
        }
    }

    private boolean IsValidDoubleChar(char c) {
        return "0123456789.".indexOf(c) >= 0;
    }

    public boolean isDouble(String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (IsValidDoubleChar(c)) {
                continue;
            }
            return false;
        }
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
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

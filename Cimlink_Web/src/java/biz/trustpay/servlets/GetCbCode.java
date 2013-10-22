/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.trustpay.servlets;

import biz.trustpay.networking.HttpCommunication;
import biz.trustpay.server.TransactionSession;
import biz.trustpay.transactions.objects.Transaction;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author williedejongh
 */
public class GetCbCode extends HttpServlet {

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
        PrintWriter out = response.getWriter();
        String txid = request.getParameter("txid");
        String action = request.getParameter("action");
        System.out.println("got GetCbCode request for  " + txid);
        try {
            Transaction trans = TransactionSession.getInstance().getTransaction(txid);
            if (action!=null&&action.equals("request")) {
                String code = getCarrierCode(trans);
                out.println(code);
                System.out.println("GetCbCode sent code " + code);
            } else {
                cancelCode(trans);
            }
        } finally {
            out.close();
        }
    }

    private void cancelCode(Transaction trans) {
        String url = "https://my.trustpay.biz/motraffic/WebClientRequest?action=cancel&txid=" + trans.transaction_id;
        HttpCommunication comms = new HttpCommunication();
        comms.setUrl(url);
        comms.getResponse();
    }

    private String getCarrierCode(Transaction trans) {
        String ret = "";
        String url = "https://my.trustpay.biz/motraffic/WebClientRequest?action=request&txid=" + trans.transaction_id;
        url = url + "&msisdn=" + trans.msisdn;
        url = url + "&apptxid=" + trans.app_transaction_id;
        url = url + "&currency=" + trans.currency;
        url = url + "&appuser=" + trans.app_user_id;
        url = url + "&appid=" + trans.application_id;
        try {
            url = url + "&message=" + URLEncoder.encode(trans.app_tx_description, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Transact.class.getName()).log(Level.SEVERE, null, ex);
        }
        url = url + "&amount=" + trans.amount;
        url = url + "&countrycode=" + trans.territory_id;
        url = url + "&istest=" + trans.istest;
        HttpCommunication comms = new HttpCommunication();
        comms.setUrl(url);
        ret = comms.getResponse();

        return ret;
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

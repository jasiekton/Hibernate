package Homework;

import Trwalosc.HatQuery;
import Trwalosc.Hats;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "Wypisywanie", urlPatterns = {"/Wypisywanie"})
public class Servlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String action = request.getParameter("action");

        if (action != null) {
            switch (action) {
                case "Wypisz kapelusze":
                    displayHats(request, response);
                    break;
                case "Dodaj kapelusz":
                    addHat(request, response);
                    break;
                case "Usun kapelusz":
                    deleteHat(request, response);
                    break;
                case "Modyfikuj kapelusz":
                    modifyHat(request, response);
                    break;
            }
        }
    }

     private void displayHats(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        boolean sortByBrand = false;
        boolean includeID = false;
        boolean includeBrand = false;
        boolean includeMaterial = false;
        boolean includeType = false;
        boolean includeStyleColor = false;
        boolean includeHasBrim = false;
        boolean includeHasRim = false;

        String sortParam = request.getParameter("sort");
        if (sortParam != null && sortParam.equals("on")) {
            sortByBrand = true;
        }

        if (request.getParameter("ID") != null) {
            includeID = true;
        }
        if (request.getParameter("brand") != null) {
            includeBrand = true;
        }
        if (request.getParameter("material") != null) {
            includeMaterial = true;
        }
        if (request.getParameter("type") != null) {
            includeType = true;
        }
        if (request.getParameter("styleColor") != null) {
            includeStyleColor = true;
        }
        if (request.getParameter("hasBrim") != null) {
            includeHasBrim = true;
        }
        if (request.getParameter("hasRim") != null) {
            includeHasRim = true;
        }

        List<Hats> hatsList = new HatQuery().getHatsList(sortByBrand);
        String table = new HatQuery().getHatListHTML(hatsList, includeID, includeBrand, includeMaterial, includeType, includeStyleColor, includeHasBrim, includeHasRim);

        // Dodaj poniższy fragment kodu do aktualizacji historii transakcji
        List<String> transactionHistory = HatQuery.getTransactionHistory(request);
        request.setAttribute("transactionHistory", transactionHistory);

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head><meta><link rel='stylesheet' href='Style/css/components.css'>");
            out.println("<link rel='stylesheet' href='Style/css/icons.css'>");
            out.println("<link rel='stylesheet' href='Style/css/responsee.css'>");
            out.println("<body>" + "Lista kapeluszy:<br />");
            out.println(table);
            out.println("<a class=\'button rounded-full-btn reload-btn s-2 margin-bottom\' href=");
            out.println(request.getHeader("referer"));
            out.println("><i class='icon-sli-arrow-left'>Powrót</i></a>");

            // Dodaj poniższy fragment kodu do wstawienia historii transakcji na stronę
            List<String> updatedTransactionHistory = (List<String>) request.getAttribute("transactionHistory");
            if (updatedTransactionHistory != null) {
                out.println("<div><h2>Transaction History:</h2>");
                out.println("<ul id='transactionHistory'>");
                for (String transaction : updatedTransactionHistory) {
                    out.println("<li>" + transaction + "</li>");
                }
                out.println("</ul></div>");
            }

            out.println("</body></html>");
        }

        addToTransactionHistory(request, "Wypisz kapelusze - " + new Date());
    }

    private void addHat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String newID = request.getParameter("newID");
        String newBrand = request.getParameter("newBrand");
        String newMaterial = request.getParameter("newMaterial");
        String newType = request.getParameter("newType");
        String newStyleColor = request.getParameter("newStyleColor");
        boolean newHasBrim = request.getParameter("newHasBrim") != null;
        boolean newHasRim = request.getParameter("newHasRim") != null;

        HatQuery hatQuery = new HatQuery();
        hatQuery.addHat(newID, newBrand, newMaterial, newType, newStyleColor, newHasBrim, newHasRim);
        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head><meta><link rel='stylesheet' href='Style/css/components.css'>");
            out.println("<link rel='stylesheet' href='Style/css/icons.css'>");
            out.println("<link rel='stylesheet' href='Style/css/responsee.css'>");
            out.println("Pomyslnie dodano kapelusz o ID=" + newID);
            out.println("<br/>");
            out.println("<a class=\'button rounded-full-btn reload-btn s-2 margin-bottom\' href=");
            out.println(request.getHeader("referer"));
            out.println("><i class='icon-sli-arrow-left'>Powrót</i></a>");
            out.println("</body></html>");
        }

        addToTransactionHistory(request, "Dodaj kapelusz - " + new Date());
    }

    private void deleteHat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String deleteID = request.getParameter("deleteID");

        HatQuery hatQuery = new HatQuery();
        hatQuery.removeHat(deleteID);
        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head><meta><link rel='stylesheet' href='Style/css/components.css'>");
            out.println("<link rel='stylesheet' href='Style/css/icons.css'>");
            out.println("<link rel='stylesheet' href='Style/css/responsee.css'>");
            out.println("Pomyslnie usunieto kapelusz o ID=" + deleteID);
            out.println("<br/>");
            out.println("<a class=\'button rounded-full-btn reload-btn s-2 margin-bottom\' href=");
            out.println(request.getHeader("referer"));
            out.println("><i class='icon-sli-arrow-left'>Powrót</i></a>");
            out.println("</body></html>");
        }

        addToTransactionHistory(request, "Usun kapelusz - " + new Date());
    }

    private void modifyHat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String modifyID = request.getParameter("modifyID");
        String modifyBrand = request.getParameter("modifyBrand");
        String modifyMaterial = request.getParameter("modifyMaterial");
        String modifyType = request.getParameter("modifyType");
        String modifyStyleColor = request.getParameter("modifyStyleColor");
        boolean modifyHasBrim = request.getParameter("modifyHasBrim") != null;
        boolean modifyHasRim = request.getParameter("modifyHasRim") != null;

        HatQuery hatQuery = new HatQuery();
        hatQuery.modifyHat(modifyID, modifyBrand, modifyMaterial, modifyType, modifyStyleColor, modifyHasBrim, modifyHasRim);
        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head><meta><link rel='stylesheet' href='Style/css/components.css'>");
            out.println("<link rel='stylesheet' href='Style/css/icons.css'>");
            out.println("<link rel='stylesheet' href='Style/css/responsee.css'>");
            out.println("Pomyslnie zmofyfikowano kapelusz o ID=" + modifyID);
            out.println("<br/>");
            out.println("<a class=\'button rounded-full-btn reload-btn s-2 margin-bottom\' href=");
            out.println(request.getHeader("referer"));
            out.println("><i class='icon-sli-arrow-left'>Powrót</i></a>");
            out.println("</body></html>");
        }

        addToTransactionHistory(request, "Zmodyfikuj kapelusz - " + new Date());
    }

    private void addToTransactionHistory(HttpServletRequest request, String transaction) {
    HttpSession session = request.getSession(true);
    List<String> transactionHistory = (List<String>) session.getAttribute("transactionHistory");

    if (transactionHistory == null) {
        transactionHistory = new ArrayList<>();
    }

    transactionHistory.add(transaction + "\n");  // Dodaj znak nowej linii po każdym wpisie
    session.setAttribute("transactionHistory", transactionHistory);
}


     @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("showHistory".equals(action)) {
            showTransactionHistory(request, response);
        } else {
            processRequest(request, response);
        }
    }
     private void showTransactionHistory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<String> transactionHistory = HatQuery.getTransactionHistory(request);

        // Przygotuj tekst historii transakcji do wyświetlenia
        StringBuilder transactionHistoryText = new StringBuilder();  
        if (transactionHistory != null) 
        for (String transaction : transactionHistory) {
            transactionHistoryText.append(transaction).append("\n");
        }

        // Wyślij tekst historii transakcji w odpowiedzi
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(transactionHistoryText.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}

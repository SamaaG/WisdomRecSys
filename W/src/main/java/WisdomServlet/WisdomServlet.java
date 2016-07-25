package WisdomServlet;

import Algorithm.Restaurant;
import Main_Engine.Wisdom;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by dwk89 on 07/24/2016.
 */
@javax.servlet.annotation.WebServlet(name = "WisdomServlet", urlPatterns = {"/results"})
public class WisdomServlet extends javax.servlet.http.HttpServlet {
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("text/html");
        //PrintWriter out = response.getWriter();
        String searchText = request.getParameter("searchText");
        String cost = request.getParameter("cost");
        String wifi = request.getParameter("wifi");
        String healthy = request.getParameter("calory");

        cost = cost == null ? "FALSE" : "TRUE";
        wifi = wifi == null ? "FALSE" : "TRUE";
        healthy = healthy == null ? "FALSE" : "TRUE";

        // Write the response to Json.
        FileWriter writer = new FileWriter("C:/Users/dwk89/IdeaProjects/W/data/Other_Data/webResponse.json");
        BufferedWriter bWriter = new BufferedWriter(writer);
        bWriter.write("{\"Text\":\"" + searchText + "\",\"Price\":\"" + cost + "\",\"Wifi\":\"" + wifi + "\",\"Healthy\":\"" + healthy + "\"}");
        bWriter.close();

        String[] results = new Wisdom(8).Recommend();

        Restaurant R = null;

        String script = new WebWriter().GetWebScript();

        PrintWriter out = response.getWriter();

        int index = script.indexOf('<');
        script = script.substring(index);

        out.println(script);
        out.println("<p><br></p>");

        for(int j = results.length - 1; j >= 0; j--) {
            R = new Restaurant(results[j]);
            out.print("<p align = \"center\">\n" +
                    "<style>\n" +
                    "#results");
            out.print(results.length - j);
            out.print("{\n" +
                    "font-family: \"Trebuchet MS\", sans-serif;\n" +
                    "color:darkblue;\n" +
                    "}\n" +
                    "</style>\n" +
                    "<table id = \"results");
            out.print(results.length - j);
            out.println("\", width = \"600px\">\n" +
                    "<tr><td>Restaurant Name: <b>");
            out.println(R.name);
            out.print("</b></td></tr>\n" +
                    "<tr><td>Address: ");
            out.println(R.address + ", " + R.csp);
            out.print("</td></tr>\n" +
                    "<tr><td>Phone: ");
            out.println(R.phone);
            out.print("</td></tr>\n" +
                    "<tr><td>Website: ");
            out.println(R.url);
            out.print("</td></tr>\n" +
                    "<tr><td>Food: <b>");
            out.print(Math.round(R.food * 100.0) / 100.0);
            out.println("</b>");
            out.print("</td></tr>\n" +
                    "<tr><td>Ambiance: <b>");
            out.println(Math.round(R.ambiance * 100.0) / 100.0);
            out.println("</b>");
            out.print("</td></tr>\n" +
                    "<tr><td>Service: <b>");
            out.println(Math.round(R.service * 100.0) / 100.0);
            out.println("</b>");
            out.print("</td></tr>\n" +
                    "<tr><td>Cost Performance: <b>");
            out.println(Math.round(R.cost * 100.0) / 100.0);
            out.println("</b>");
            out.println("</td></tr></table></p>");
        }

        out.println("<style>\n" +
                ".button {\n" +
                "display: inline-block;\n" +
                "font-family: \"Trebuchet MS\", sans-serif;\n" +
                "font-size: 16px;\n" +
                "background-color: #f44336;\n" +
                "border-radius: 12px;\n" +
                "border: none;\n" +
                "text-align: center;\n" +
                "width: 200px;\n" +
                "padding: 9px 10px;\n" +
                "cursor: pointer;\n" +
                "transition: all 0.5s;\n" +
                "}\n" +
                ".button span {\n" +
                "cursor: pointer;\n" +
                "display: inline-block;\n" +
                "position: relative;\n" +
                "transition: 0.5s;\n" +
                "}\n" +
                ".button span:after {\n" +
                "content: '>>';\n" +
                "position: absolute;\n" +
                "opacity: 0;\n" +
                "top: 0;\n" +
                "right: -20px;\n" +
                "transition: 0.5s;\n" +
                "}\n" +
                ".button:hover span {\n" +
                "padding-right: 25px;\n" +
                "}\n" +
                ".button:hover span:after {\n" +
                "opacity: 1;\n" +
                "right: 0;\n" +
                "}\n" +
                ".button2 {\n" +
                "display: inline-block;\n" +
                "font-family: \"Trebuchet MS\", sans-serif;\n" +
                "font-size: 16px;\n" +
                "background-color: #e7e7e7;\n" +
                "border-radius: 12px;\n" +
                "border: none;\n" +
                "text-align: center;\n" +
                "width: 200px;\n" +
                "padding: 9px 10px;\n" +
                "cursor: pointer;\n" +
                "transition: all 0.5s;\n" +
                "}\n" +
                ".button2 span {\n" +
                "cursor: pointer;\n" +
                "display: inline-block;\n" +
                "position: relative;\n" +
                "transition: 0.5s;\n" +
                "}\n" +
                ".button2 span:after {\n" +
                "content: '>>';\n" +
                "position: absolute;\n" +
                "opacity: 0;\n" +
                "top: 0;\n" +
                "right: -20px;\n" +
                "transition: 0.5s;\n" +
                "}\n" +
                ".button2:hover span {\n" +
                "padding-right: 25px;\n" +
                "}\n" +
                ".button2:hover span:after {\n" +
                "opacity: 1;\n" +
                "right: 0;\n" +
                "}\n" +
                "</style>");

        out.println("<p align = \"center\"><br><button class = \"button\", onclick = \"JavaScript:window.history.back()\">\n" +
                "<span>Start Another Search</span></button>\n" +
                "<button class = \"button button2\", style = \"margin: 0px 0px 0px 16px;\", onclick = \"JavaScript:window.close()\">\n" +
                "<span>Close Window</span></button></p>");

        out.println("</body>\n</html>");
    }
}

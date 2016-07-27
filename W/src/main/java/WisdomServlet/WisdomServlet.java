package WisdomServlet;

import Algorithm.Restaurant;
import Main_Engine.Wisdom;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Dayu Wang on 07/24/2016.
 */
@javax.servlet.annotation.WebServlet(name = "WisdomServlet", urlPatterns = {"/results"})
public class WisdomServlet extends javax.servlet.http.HttpServlet {

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        response.setContentType("text/html");
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
            out.print("</td></tr>\n<tr><td><br>");

            // Print out the pentagonal shape.
            out.print("<svg width = \"380px\" height = \"290px\">\n" +
                    "            \n" +
                    "            <!-- Basic Framework -->\n" +
                    "            <polygon points = \"100,0 4.89,69.1 41.22,180.9 158.78,180.9 195.11,69.1\" style = \"fill:none; stroke:darkblue; stroke-width: 2\" transform = \"translate(40,50)\"></polygon>\n" +
                    "            <style>\n" +
                    "                .corner {\n" +
                    "                    font-family: \"Trebuchet MS\", sans-serif;\n" +
                    "                    font-size: 12pt;\n" +
                    "                }\n" +
                    "                .dash {\n" +
                    "                    color:darkblue;\n" +
                    "                }\n" +
                    "            </style>\n" +
                    "            <text fill = \"darkblue\" class = \"corner\" x = \"70\" y = \"23\" transform = \"translate(40)\">Average</text>\n" +
                    "            <text fill = \"darkblue\" class = \"corner\" x = \"0\" y = \"120\">Food</text>\n" +
                    "            <text fill = \"darkblue\" class = \"corner\" x = \"0\" y = \"240\">Ambiance</text>\n" +
                    "            <text fill = \"darkblue\" class = \"corner\" x = \"210\" y = \"240\">Service</text>\n" +
                    "            <text fill = \"darkblue\" class = \"corner\" x = \"245\" y = \"120\">Cost Performance</text>\n" +
                    "            <path class = \"dash\" d = \"M100 100 L100 0\" stroke = \"darkblue\" stroke-width = \"2\" stroke-dasharray = \"5,5\" transform = \"translate(40,50)\"></path>\n" +
                    "            <path class = \"dash\" d = \"M100 100 L4.89 69.1\" stroke = \"darkblue\" stroke-width = \"2\" stroke-dasharray = \"5,5\" transform = \"translate(40,50)\"></path>\n" +
                    "            <path class = \"dash\" d = \"M100 100 L41.22 180.9\" stroke = \"darkblue\" stroke-width = \"2\" stroke-dasharray = \"5,5\" transform = \"translate(40,50)\"></path>\n" +
                    "            <path class = \"dash\" d = \"M100 100 L158.78 180.9\" stroke = \"darkblue\" stroke-width = \"2\" stroke-dasharray = \"5,5\" transform = \"translate(40,50)\"></path>\n" +
                    "            <path class = \"dash\" d = \"M100 100 L195.11 69.1\" stroke = \"darkblue\" stroke-width = \"2\" stroke-dasharray = \"5,5\" transform = \"translate(40,50)\"></path>\n");

            out.print("<polygon points = \"" + R.FivePoints() + "\" style = \"fill:firebrick; stroke:none;\" fill-opacity = \"0.7\"; transform = \"translate(40,50)\"></polygon>\n");

            out.print(R.SVGText());

            out.print("</svg>");

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
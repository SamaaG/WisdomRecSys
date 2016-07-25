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

        String script = new WebWriter().GetWebScript();

        PrintWriter out = response.getWriter();

        int index = script.indexOf('<');
        script = script.substring(index);

        out.println(script);

        for(int i = 0; i < results.length; i++) {
            out.println("<p><br>" + results[i] + "</p>");
        }

        //out.println("<p align=\"center\"><style>#results{font-family:\"TrebuchetMS\",sans-serif;color:darkblue;}</style><table id=\"results\",width=\"600px\"><tr><td>RestaurantName: <b>");

        /*Restaurant R;

        for (int k = 0; k <= 0; k++) {
            R = new Restaurant(results[k]);

            out.println(R.name);
            out.println("</b></td></tr><tr><td>Address: ");
            out.println(R.address);
            out.println("</td></tr><tr><td>Phone: ");
            out.println(R.phone);
            out.println("</td></tr><tr><td>Website: ");
            out.println(R.url == null ? "N/A" : R.url);
            out.println("</td></tr><tr><td>Food: ");
            out.println(Math.round(R.food * 100.0) / 100.0);
            out.println("</td></tr><tr><td>Ambiance: ");
            out.println(Math.round(R.ambiance * 100.0) / 100.0);
            out.println("</td></tr><tr><td>Service: ");
            out.println(Math.round(R.service * 100.0) / 100.0);
            out.println("</td></tr><tr><td>Cost Performance: ");
            out.println(Math.round(R.cost * 100.0) / 100.0);
            out.println("</td></tr><tr><td><br><br></td></tr>");
        }


        out.println("</table></p>");*/

        out.println("</body>\n</html>");
    }
}

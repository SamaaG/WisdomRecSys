package User_Input;

import Algorithm.Restaurant;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by Dayu Wang on 07/24/2016.
 */
public class UserInput {

    private static final String locationFileName = "C:/Users/dwk89/IdeaProjects/W/data/Trained_Data/Cities.txt";
    private String text;
    private boolean price;
    private boolean wifi;
    private boolean healthy;

    public UserInput() throws IOException {

        String jsonLine = new WebJson().GetJson();
        jsonLine = jsonLine.substring(jsonLine.indexOf('{'), jsonLine.lastIndexOf('}') + 1);
        JsonElement jElement = new JsonParser().parse(jsonLine);
        JsonObject jObject = jElement.getAsJsonObject();

        jElement = jObject.get("Text");
        text = jElement.toString();

        jElement = jObject.get("Price");
        price = jElement.toString().contains("TRUE") ? true : false;

        jElement = jObject.get("Wifi");
        wifi = jElement.toString().contains("TRUE") ? true : false;

        jElement = jObject.get("Healthy");
        healthy = jElement.toString().contains("TRUE") ? true : false;
    }

    public double satisfyDegree (Restaurant r) throws IOException {

        FileReader reader = new FileReader(locationFileName);
        BufferedReader bReader = new BufferedReader(reader);

        String temp = null;
        double satisfy = 0;

        while ((temp = bReader.readLine()) != null) {

            if (text.toLowerCase().contains(temp.toLowerCase()) && r.csp.toLowerCase().contains(temp.toLowerCase())) {
                return 1.0;
            }
        }

        bReader.close();
        String currentToken = null;
        StringTokenizer st = new StringTokenizer(text.toLowerCase());

        while (st.hasMoreTokens()) {

            currentToken = st.nextToken();
            if (r.zipCode.contains(currentToken)) {
                return 1.0;
            }

            if (r.name.toLowerCase().contains(currentToken) || r.type.toLowerCase().contains(currentToken)) {
                satisfy += 0.4;
                break;
            }
        }

        if (this.price && r.cost >= 4) {
            satisfy += 0.2;
        }

        if (this.wifi && r.wifi) {
            satisfy += 0.2;
        }

        if (this.healthy && r.healthy) {
            satisfy += 0.2;
        }

        return satisfy;
    }
}
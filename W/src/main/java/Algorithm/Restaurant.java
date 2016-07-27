package Algorithm;

import Learning_Model.Correlation;
import User_Input.UserInput;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by dwk89 on 07/23/2016.
 */
public class Restaurant {
    public String name;
    public String address;
    public String type;
    public String csp; //City-State-Postal
    public String phone;
    public String url;
    public String zipCode;

    // Pentagonal elements
    public double food;
    public double service;
    public double ambiance;
    public double cost;
    public double combination; // A combination of ratings (mean value).

    public boolean wifi;
    public boolean healthy;

    private boolean eliminate; // Used to pre-filter some data.

    public Restaurant(String jsonLine) throws IOException {
        jsonLine = jsonLine.substring(jsonLine.indexOf('{'), jsonLine.lastIndexOf('}') + 1);
        JsonElement jElement = new JsonParser().parse(jsonLine);
        JsonObject jObject = jElement.getAsJsonObject();
        JsonElement jElement2 = jObject.get("Name");
        name = jElement2.toString().substring(1, jElement2.toString().length() - 1);
        jElement2 = jObject.get("Type");
        type = jElement2.toString();
        jElement2 = jObject.get("Address");
        address = jElement2.toString().substring(1, jElement2.toString().length() - 1);
        jElement2 = jObject.get("City_State_Postal");
        csp = jElement2.toString().substring(1, jElement2.toString().length() - 1);
        jElement2 = jObject.get("Phone");
        phone = jElement2.toString().substring(1, jElement2.toString().length() - 1);
        jElement2 = jObject.get("Website");
        url = jElement2.toString();
        if(url.contains("www")) {
            url = url.substring(1, jElement2.toString().length() - 1);
        }
        else {
            url = "N/A";
        }
        jElement2 = jObject.get("Zip");
        zipCode = jElement2.toString();
        jElement2 = jObject.get("Food");
        food = Double.parseDouble(jElement2.toString().substring(1,jElement2.toString().length() - 1));
        jElement2 = jObject.get("Service");
        service = Double.parseDouble(jElement2.toString().substring(1,jElement2.toString().length() - 1));
        jElement2 = jObject.get("Ambiance");
        ambiance = Double.parseDouble(jElement2.toString().substring(1,jElement2.toString().length() - 1));
        jElement2 = jObject.get("Cost");
        cost = Double.parseDouble(jElement2.toString().substring(1,jElement2.toString().length() - 1));
        combination = (food + service + ambiance + cost) / 4;
        jElement2 = jObject.get("Wifi");
        wifi = jElement2.toString().contains("TRUE") ? true : false;
        jElement2 = jObject.get("Healthy");
        healthy = jElement2.toString().contains("TRUE") ? true: false;

        Correlation c = new Correlation();
        c.selfAdjust(this);

        eliminate = Eliminate();
    }

    private boolean Eliminate() {
        if(food < 3 || service < 3 || ambiance < 3 || cost < 3)
            return true;
        return false;
    }

    public boolean is_better_than(Restaurant r) throws IOException {
        UserInput input = new UserInput();

        if (input.satisfyDegree(this) > input.satisfyDegree(r)) {
            return true;
        }
        if (input.satisfyDegree(this) < input.satisfyDegree(r)) {
            return false;
        }

        if (this.combination - r.combination >= 1) {
            return true;
        }
        if (r.combination - this.combination >= 1) {
            return false;
        }

        if (this.eliminate == false && r.eliminate == true) {
            return true;
        }
        if (this.eliminate == true && r.eliminate == false) {
            return false;
        }

        // Calculate the standard deviation.
        double mean = (this.food + this.ambiance + this.service + this.cost) / 4;
        double mean_r = (r.food + r.ambiance + r.service + r. cost) / 4;

        double food_1 = (this.food - mean) * (this.food - mean);
        double ambiance_1 = (this.ambiance - mean) * (this.ambiance - mean);
        double service_1 = (this.service - mean) * (this.service - mean);
        double cost_1 = (this.cost - mean) * (this.cost - mean);

        double food_2 = (r.food - mean_r) * (r.food - mean_r);
        double ambiance_2 = (r.ambiance - mean_r) * (r.ambiance - mean_r);
        double service_2 = (r.service - mean_r) * (r.service - mean_r);
        double cost_2 = (r.cost - mean_r) * (r.cost - mean_r);

        double variance_this = (food_1 + ambiance_1 + service_1 + cost_1) / 4;
        double variance_r = (food_2 + ambiance_2 + service_2 + cost_2) / 4;

        double sd_this = Math.sqrt(variance_this);
        double sd_r = Math.sqrt(variance_r);

        if (sd_this - sd_r >= 0.5) {
            return false;
        }
        if (sd_r - sd_this >= 0.5) {
            return true;
        }

        if (this.food > r.food) {
            return true;
        }
        else {
            return false;
        }
    }

    /*
    <!-- x_Food = 100 - (100 - 4.89) * (food * (20/100)) -->
    <!-- y_Food = 100 - (100 - 69.1) * (food * (20/100)) -->
    <!-- x_Ambiance = 100 - (100 - 41.22) * (ambiance * (20/100)) -->
    <!-- y_Ambiance = 100 + (180.9 - 100) * (ambiance * (20/100)) -->
    <!-- x_Service = 100 + (158.78 - 100) * (service * (20/100)) -->
    <!-- y_Service = 100 + (180.9 - 100) * (service * (20/100)) -->
    <!-- x_Cost = 100 + (195.11 - 100) * (cost * (20/100)) -->
    <!-- y_Cost = 100 - (100 - 69.1) * (cost * (20/100)) -->
    <!-- x_Average = 100 -->
    <!-- y_Average = 100 - 100 * (average * (20/100)) -->
    */
    public String FivePoints() {
        double x_Food = 100 - (100 - 4.89) * (food * 0.2);
        double y_Food = 100 - (100 - 69.1) * (food * 0.2);
        double x_Ambiance = 100 - (100 - 41.22) * (ambiance * 0.2);
        double y_Ambiance = 100 + (180.9 - 100) * (ambiance * 0.2);
        double x_Service = 100 + (158.78 - 100) * (service * 0.2);
        double y_Service = 100 + (180.9 - 100) * (service * 0.2);
        double x_Cost = 100 + (195.11 - 100) * (cost * 0.2);
        double y_Cost = 100 - (100 - 69.1) * (cost * 0.2);
        double x_Average = 100;
        double y_Average = 100 - 100 * (combination * 0.2);

        DecimalFormat df = new DecimalFormat("0.0");
        String result = "";

        result += (String)df.format(x_Food) + ',' + (String)df.format(y_Food) + ' ';
        result += (String)df.format(x_Ambiance) + ',' + (String)df.format(y_Ambiance) + ' ';
        result += (String)df.format(x_Service) + ',' + (String)df.format(y_Service) + ' ';
        result += (String)df.format(x_Cost) + ',' + (String)df.format(y_Cost) + ' ';
        result += (String)df.format(x_Average) + ',' + (String)df.format(y_Average);

        return result;
    }

    public String SVGText() {
        DecimalFormat df = new DecimalFormat("0.0");

        String result = "<style>\n" +
                ".rating{\n" +
                "font-family: \"Trebuchet MS\", sans-serif;\n" +
                "font-size: 12pt;\n" +
                "font-weight: bold;\n" +
                "}\n" +
                "</style>\n" +
                "<text fill = \"firebrick\" class = \"rating\" x = \"8\" y = \"136\">";

        result += (String)df.format(food);
        result += "</text>\n" +
                "            <text fill = \"firebrick\" class = \"rating\" x = \"25\" y = \"256\">";
        result += (String)df.format(ambiance);
        result += "</text>\n" +
                "            <text fill = \"firebrick\" class = \"rating\" x = \"227\" y = \"256\">";
        result += (String)df.format(service);
        result += "</text>\n" +
                "            <text fill = \"firebrick\" class = \"rating\" x = \"295\" y = \"136\">";
        result += (String)df.format(cost);
        result += "</text>\n" +
                "            <text fill = \"firebrick\" class = \"rating\" x = \"130\" y = \"42\">";
        result += (String)df.format(combination);
        result += "</text>\n";

        return result;
    }
}
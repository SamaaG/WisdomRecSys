package Algorithm;

import Learning_Model.Correlation;
import User_Input.UserInput;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

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
        if(jsonLine.charAt(0) == '[') {
            jsonLine = jsonLine.substring(1);
        }
        jsonLine = jsonLine.substring(0,jsonLine.length() - 1);
        JsonElement jElement = new JsonParser().parse(jsonLine);
        JsonObject jObject = jElement.getAsJsonObject();
        JsonElement jElement2 = jObject.get("Name");
        name = jElement2.toString();
        jElement2 = jObject.get("Type");
        type = jElement2.toString();
        jElement2 = jObject.get("Address");
        address = jElement2.toString();
        jElement2 = jObject.get("City_State_Postal");
        csp = jElement2.toString();
        jElement2 = jObject.get("Phone");
        phone = jElement2.toString();
        jElement2 = jObject.get("Website");
        url = jElement2.toString();
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
}
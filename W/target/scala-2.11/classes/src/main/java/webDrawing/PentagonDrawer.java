package webDrawing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.util.Pair;

import java.io.IOException;

/**
 * Created by dwk89 on 07/24/2016.
 */
public class PentagonDrawer {
    public static final String framePentagon = "<svg height = \"200\", width = \"200\"><polygon points = \"100,2 195.1,71.1 158.8,182.9 41.2,182.9 4.9,71.1\", style = \"fill: none; stroke: purple; stroke-width: 2\"></polygon></svg><svg><circle cx = \"102\" cy = \"102\" r = \"4\" stroke: none fill = \"red\"></circle></svg>";

    private double food;
    private double ambiance;
    private double service;
    private double cost;

    public PentagonDrawer(String restaurantJson) throws IOException {
        restaurantJson = restaurantJson.substring(restaurantJson.indexOf('{'), restaurantJson.lastIndexOf('}') + 1);
        JsonElement jElement = new JsonParser().parse(restaurantJson);
        JsonObject jObject = jElement.getAsJsonObject();

        JsonElement jElement2 = jObject.get("Food");
        food = Double.parseDouble(jElement2.toString().substring(1,jElement2.toString().length() - 1));
        jElement2 = jObject.get("Service");
        service = Double.parseDouble(jElement2.toString().substring(1,jElement2.toString().length() - 1));
        jElement2 = jObject.get("Ambiance");
        ambiance = Double.parseDouble(jElement2.toString().substring(1,jElement2.toString().length() - 1));
        jElement2 = jObject.get("Cost");
        cost = Double.parseDouble(jElement2.toString().substring(1,jElement2.toString().length() - 1));
    }
}

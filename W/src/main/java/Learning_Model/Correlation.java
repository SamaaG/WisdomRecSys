package Learning_Model;

import Algorithm.Restaurant;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.util.Pair;

import java.io.*;

/**
 * Created by dwk89 on 07/24/2016.
 */
public class Correlation {
    private static final double increment = 0.05; // Each learning step is not huge.
    private static final double food = 1.0; // Food is already normalized to unit value.

    private double serviceBottom;
    private double serviceUp;
    private double ambianceBottom;
    private double ambianceUp;
    private double costBottom;
    private double costUp;

    private Pair<String, String> stringSplit(String s) {
        int index = s.indexOf('-');
        String bottom = s.substring(0, index);
        String up = s.substring(index + 1, s.length());
        Pair<String, String> returnValue = new Pair<String, String>(bottom, up);
        return returnValue;
    }

    public Correlation() throws IOException {
        String filePath = "C:/Users/dwk89/IdeaProjects/W/data/Learning_Model/";
        String fileName = "Correlation.json";
        FileReader r = new FileReader(filePath + fileName);
        BufferedReader br = new BufferedReader(r);
        String correlationFile = br.readLine();
        br.close();

        correlationFile = correlationFile.substring(1, correlationFile.length() - 1);
        JsonElement jElement = new JsonParser().parse(correlationFile);
        JsonObject jObject = jElement.getAsJsonObject();
        String temp = null;

        JsonElement jsonElement2 = jObject.get("Service");
        temp = jsonElement2.toString().substring(1, jsonElement2.toString().length() - 1);
        serviceBottom = Double.parseDouble(stringSplit(temp).getKey());
        serviceUp = Double.parseDouble(stringSplit(temp).getValue());

        jsonElement2 = jObject.get("Ambiance");
        temp = jsonElement2.toString().substring(1, jsonElement2.toString().length() - 1);
        ambianceBottom = Double.parseDouble(stringSplit(temp).getKey());
        ambianceUp = Double.parseDouble(stringSplit(temp).getValue());

        jsonElement2 = jObject.get("Cost");
        temp = jsonElement2.toString().substring(1, jsonElement2.toString().length() - 1);
        costBottom = Double.parseDouble(stringSplit(temp).getKey());
        costUp = Double.parseDouble(stringSplit(temp).getValue());
    }

    private double absoluteValue(double d) {
        if(d >= 0) {
            return d;
        }
        else {
            return (-1) * d;
        }
    }

    private void writeToFile(String fileName) throws IOException {
        String filePath = "C:/Users/dwk89/IdeaProjects/W/data/Learning_Model/";
        FileWriter w = new FileWriter(filePath + fileName, false);
        BufferedWriter bw = new BufferedWriter(w);
        String content1 = "[{\"Food\":\"1\",\"Service\":\"";
        String content2 = "\",\"Ambiance\":\"";
        String content3 = "\",\"Cost\":\"";
        String content4 = "\"}]";

        bw.write(content1 + Double.toString(serviceBottom) + '-' + Double.toString(serviceUp)
                + content2 + Double.toString(ambianceBottom) + '-' + Double.toString(ambianceUp)
                + content3 + Double.toString(costBottom) + '-' + Double.toString(costUp)
                + content4
        );

        bw.close();
    }

    public void selfAdjust(Restaurant r) throws IOException {
        double r_food = r.food;
        double r_ambiance = r.ambiance / r_food;
        double r_service = r.service / r_food;
        double r_cost = r.cost / r_food;

        if (r_ambiance < ambianceBottom) {
            r.ambiance += absoluteValue(ambianceBottom * r.food - r.ambiance) * 0.5;
            ambianceBottom -= increment;
            ambianceUp -= increment * 0.99;
        }
        if (r_ambiance > ambianceUp) {
            r.ambiance -= absoluteValue(ambianceUp * r.food - r.ambiance) * 0.5;
            ambianceUp += increment;
            ambianceBottom += increment * 0.99;
        }
        if (r_service < serviceBottom) {
            r.service += absoluteValue(serviceBottom * r.food - r.service) * 0.5;
            serviceBottom -= increment;
            serviceUp -= increment * 0.99;
        }
        if (r_service > serviceUp) {
            r.service -= absoluteValue(serviceUp * r.food - r.service) * 0.5;
            serviceUp += increment;
            serviceBottom += increment * 0.99;
        }
        if (r_cost < costBottom) {
            r.cost += absoluteValue(costBottom * r.food - r.cost) * 0.5;
            costBottom -= increment;
            costUp -= increment * 0.99;
        }
        if (r_cost > costUp) {
            r.cost -= absoluteValue(costUp * r.food - r.cost) * 0.5;
            costUp += increment;
            costBottom += increment * 0.99;
        }

        r.combination = (r.food + r.ambiance + r.service + r.cost) / 4;

        if (absoluteValue(ambianceUp - ambianceBottom) > 2) {
            ambianceUp = (ambianceUp + ambianceBottom) / 2 + 0.5;
            ambianceBottom = ambianceUp - 1;
        }
        if (absoluteValue(serviceUp - serviceBottom) > 2) {
            serviceUp = (serviceUp + serviceBottom) / 2 + 0.5;
            serviceBottom = serviceUp - 1;
        }
        if (absoluteValue(costUp - costBottom) > 2) {
            costUp = (costUp + costBottom) / 2 + 0.5;
            costBottom = costUp - 1;
        }

        writeToFile("Correlation.json");
    }
}
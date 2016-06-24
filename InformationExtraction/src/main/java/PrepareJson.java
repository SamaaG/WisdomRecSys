import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by Samaa on 6/23/2016.
 */

//This class extracts the text part of reviews from the json file in preparation for Info. Extraction
public class PrepareJson {

    //This function reads a text file of json formatting and returns a JSONArray where each line in the text file is saved a JSONobject in the Array
    static JSONArray readFileAsJsonArray(String path)
    {
        JSONParser parser = new JSONParser();
        JSONArray jObs = new JSONArray();

        try {
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String jobStr = scanner.nextLine();
                jObs.add((JSONObject) parser.parse(jobStr));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jObs;
    }


    public static void main(String[] args) throws IOException, ParseException {

        //each object in jLines is a single JSON object representing a user review
        JSONArray jLines = readFileAsJsonArray("SampleDataset.txt");

        PrintWriter out = new PrintWriter("Reviews.txt");

        //extract the text from the reviews and create a new text file
        for( int j = 0; j < jLines.size(); j++ ){
            JSONObject job = (JSONObject) jLines.get(j);
            out.println(job.get("text").toString().replace("\n",""));
        }

    }
}

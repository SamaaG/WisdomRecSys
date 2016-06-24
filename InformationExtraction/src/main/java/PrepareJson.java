import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sun.misc.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samaa on 6/23/2016.
 */
public class PrepareJson {
    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void main(String[] args) throws IOException, ParseException {



        JSONParser parser = new JSONParser();

        /*FileReader fileReader = new FileReader("SampleDataset.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);*/

        String content = readFile("SampleDataset.txt", StandardCharsets.UTF_8);

        Object obj_ = parser.parse(content);
        JSONObject obj = (JSONObject) obj_;

        List<String> results = new ArrayList<String>();

        /*String line = bufferedReader.readLine();
        while (line != null) {
            results.add(line);
            line = bufferedReader.readLine();
        }

        for (int i = 0; i < results.size(); i++){
            review = (String) results.get(i);
            System.out.println(review);
        }

        Object obj = parser.parse(review);
        JSONObject array = (JSONObject) obj;
*/
    }
}

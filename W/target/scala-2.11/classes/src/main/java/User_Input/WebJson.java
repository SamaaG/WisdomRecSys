package User_Input;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by dwk89 on 07/24/2016.
 */
public class WebJson {
    private static final String jsonFile = "C:/Users/dwk89/IdeaProjects/W/data/Other_Data/webResponse.json";

    public String GetJson() throws IOException {
        FileReader reader = new FileReader(jsonFile);
        BufferedReader bReader = new BufferedReader(reader);
        String result = null, temp = null;
        while ((temp = bReader.readLine()) != null) {
            result += temp;
        }
        bReader.close();
        return result;
    }
}
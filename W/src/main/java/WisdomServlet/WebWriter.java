package WisdomServlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Dayu Wang on 07/24/2016.
 */
public class WebWriter {

    private static final String fileName = "C:/Users/dwk89/IdeaProjects/W/data/Other_Data/WebScript.txt";

    public String GetWebScript () throws IOException {

        FileReader reader = new FileReader(fileName);
        BufferedReader bReader = new BufferedReader(reader);

        String result = null;
        String temp = null;

        while((temp = bReader.readLine()) != null) {
            result += temp;
        }

        bReader.close();
        return result;
    }
}

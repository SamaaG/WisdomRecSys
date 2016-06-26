import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.*;

import static java.lang.System.in;

/**
 * Created by poojashekhar on 6/24/16.
 */
public class SentimentAnalysis {

    public TweetWithSentiment findSentiment(String line) {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        int mainSentiment = 0;
        if (line != null && line.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(line);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);

                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }

            }
        }
        if (mainSentiment == 2 || mainSentiment > 4 || mainSentiment < 0) {
            return null;
        }

        TweetWithSentiment tweetWithSentiment = new TweetWithSentiment(line, toCss(mainSentiment));
        return tweetWithSentiment;

    }
    public int findSentiment(String line,int i) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        int mainSentiment = 0;
        if (line != null && line.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(line);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);

                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }

            }
        }
        if (mainSentiment == 2 || mainSentiment > 4 || mainSentiment < 0) {
            return -1;
        }

        return mainSentiment;


    }

    private String toCss(int sentiment) {
        switch (sentiment) {
            case 0:
                return "sentiment : very negative";
            case 1:
                return "sentiment : negative";
            case 2:
                return "sentiment : neutral";
            case 3:
                return "sentiment : positive";
            case 4:
                return "sentiment : very positive";
            default:
                return "";
        }
    }

    public static void main(String[] args) {




        JSONParser parser = new JSONParser();
        JSONObject json=new JSONObject();
        JSONArray jsonArray = new JSONArray();

        HashMap<String, String> id = new HashMap();
        HashMap<String, String> rest_bus = new HashMap();
        HashMap<String, String> final_rest_bus = new HashMap();
        try {

            //reading reviews.json and business.json from yelp dataset into JSONArray
            JSONArray reviewsdata = (JSONArray) parser.parse(new FileReader("/home/poojashekhar/Downloads/yelp_dataset_challenge_academic_dataset/sampledatasetreviews.json"));
            JSONArray businessdata = (JSONArray) parser.parse(new FileReader("/home/poojashekhar/Downloads/yelp_dataset_challenge_academic_dataset/samplebusinessdataset.json"));

            //For writing all console outputs in a file called "output.txt"
            PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
            System.setOut(out);

            //Reading city of choice from the user
            Scanner reader = new Scanner(System.in);  // Reading from System.in
            System.out.print("Enter a city: ");
            String city = reader.nextLine();
            System.out.println(city);


            Iterator itr1 = businessdata.iterator();
            Iterator itr2 = reviewsdata.iterator();

            //Adding (key,value)=(business_id,restaurant_name) in  hashmap if business_category is "Restaurants"
            while (itr1.hasNext()) {

                JSONObject business = (JSONObject) itr1.next();
                String yelpcity = (String) business.get("city");
                String bus_id = (String) business.get("business_id");
                String restaurant_name = (String) business.get("name");
                JSONArray category = (JSONArray) business.get("categories");
                if (category.contains("Restaurants")) {
                    if (yelpcity.equals(city)) {


                        //System.out.println("testing");
                        id.put(bus_id, restaurant_name);

                    }
                }


            }

            //System.out.println(id);

            /*Set busid_rest_set = id.entrySet();
            Iterator itr=busid_rest_set.iterator();
            Map.Entry busid_rest_set_mentry = (Map.Entry)itr.next();

            while (itr.hasNext()) {
                System.out.println(busid_rest_set_mentry.getKey()+" "+busid_rest_set_mentry.getValue());
            }*/

            //Iterate through each business_id in the hashmap created above and extract and concatenate review against each business idea and
            // put in Hashmap named rest_bus with (key,value)=(Restaurant_name,Agrregated Reviews for the restaurant))
            for (Map.Entry<String, String> entry : id.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                for (Object o : reviewsdata) {
                    JSONObject review = (JSONObject) o;
                    String bus_id = (String) review.get("business_id");
                    String revrconcat = null;

                    String review_text = (String) review.get("text");
                    //String busid_bus=(String)busid_rest_set_mentry.getKey();

                    if (key.equals(bus_id)) {
                        //System.out.println("testing");
                        if (rest_bus.containsKey(value)) {
                            review_text=review_text.trim();
                            revrconcat = rest_bus.get(value).concat(review_text);
                            rest_bus.put(value, revrconcat);
                        } else {
                            review_text=review_text.trim();
                            rest_bus.put(value, review_text);
                        }


                    }
                }


            }
            //System.out.println(rest_bus);
            for(Map.Entry<String, String> entry : rest_bus.entrySet())
            {
                String key=entry.getKey();
                String value=entry.getValue();
                //System.out.println(entry);

                value = value.replaceAll("\\n", "");
                final_rest_bus.put(key,value);

            }
            /*for(Map.Entry<String, String> entry : final_rest_bus.entrySet())
            {
                String key=entry.getKey();
                String value=entry.getValue();

                System.out.println(entry);
            }*/



            //System.out.println(rest_bus);


            /*for (Map.Entry<String, String> entry1 : rest_bus.entrySet()) {

                String key = entry1.getKey();
                String value = entry1.getValue();
                System.out.println("testing!!!!!");
                System.out.println(key + ":" + value);

            }*/

            //Passing each aggregated review text from above as string to SentimentAnalyzer above
            //and print Restaurant name ,its sentiment and the aggregated review text on which it performed sentiment analysis
            Iterator it = final_rest_bus.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String rev_tex = (String) pair.getValue();
                String rev_name = (String) pair.getKey();

                //System.out.print(rev_tex);

                SentimentAnalysis sentimentAnalyzer = new SentimentAnalysis();
                TweetWithSentiment tweetWithSentiment = sentimentAnalyzer
                        .findSentiment(rev_tex);
                System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                System.out.println("Restaurant name " + " " +rev_name+ " " + " " +tweetWithSentiment.getCssClass());
                System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                System.out.println("Review Text :");
                System.out.println(rev_tex);
                System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            }
        }

            catch (Exception e) {
            e.printStackTrace();
        }
    }
}

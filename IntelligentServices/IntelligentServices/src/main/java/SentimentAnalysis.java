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
import java.io.FileReader;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;

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


        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Enter a city: ");
        String city= reader.nextLine();

        JSONParser parser = new JSONParser();
        JSONObject json=new JSONObject();
        JSONArray jsonArray = new JSONArray();
        int c=0;

        try {
            JSONArray reviewsdata = (JSONArray) parser.parse(new FileReader("/home/poojashekhar/Downloads/yelp_dataset_challenge_academic_dataset/sampledatasetreviews.json"));
            JSONArray obj = (JSONArray) parser.parse(new FileReader("/home/poojashekhar/Downloads/yelp_dataset_challenge_academic_dataset/samplebusinessdataset.json"));
            for (Object o : obj) {
                JSONObject business = (JSONObject) o;

                String name = (String) business.get("name");
                System.out.println(name);

                String yelpcity = (String) business.get("city");
                System.out.println(yelpcity);

                String category = (String) business.get("category");

                if (yelpcity == city && category=="Restaurant") {
                    System.out.println("testing!!!!!");
                    String business_id = (String) business.get("business_id");
                    for (Object r : reviewsdata) {
                        JSONObject reviews = (JSONObject) r;
                        String business_id1_rev = (String) business.get("business_id");
                        if(business_id==business_id1_rev)
                        {
                            String reviewText = (String) business.get("text");
                            SentimentAnalysis sentimentAnalyzer = new SentimentAnalysis();
                            TweetWithSentiment tweetWithSentiment = sentimentAnalyzer
                                    .findSentiment(reviewText);
                            System.out.println(tweetWithSentiment);

                        }

                    }
                }

            }
        }
            catch (Exception e) {
            e.printStackTrace();
        }
    }
}

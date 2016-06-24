package pipeline

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import pipeline.PrepareJson.prepareJson
import pipeline.WordCount.PerformWordCount
import pipeline.NLPanalysis.{nlpAnalysis, returnLemma}
import pipeline.TFIDF.performTFIDF
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.feature.{NGram, StopWordsRemover, Tokenizer}
import org.apache.spark.rdd.RDD


/**
  * Created by Samaa on 6/24/2016.
  */
object InfoExtract {
  def main(args: Array[String]) {

    // Configuration
    val sparkConf = new SparkConf().setAppName("SparkInfoExtract").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)

    val spark = SparkSession
      .builder
      .appName("SparkInfoExtract")
      .master("local[*]")
      .getOrCreate()

    // Turn off Info Logger for Console
    Logger.getLogger("org").setLevel(Level.OFF);
    Logger.getLogger("akka").setLevel(Level.OFF);


    val dataset = "SampleDataset.txt"
    val property = "text"
    val readyFile = "Reviews.txt"

    //Generate proper text file to be used as input
    prepareJson(dataset, property, readyFile)

    //Count word frequency in file
    PerformWordCount(readyFile, sc)

    //perform NLP analysis on the file
    nlpAnalysis(readyFile)

    //perform TF-IDF analysis of the file
    //performTFIDF(readyFile, sc)

    // Read the file into RDD[String](all rdd manipulations has to be serilizable
    val input = sc.textFile(readyFile).map(line => {
      //Getting Lemmatized Form of the word using CoreNLP
      val lemma = returnLemma(line)
      (0, lemma)
    })

    performTFIDF(readyFile, sc, spark, input)


  }
}

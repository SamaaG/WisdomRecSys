package pipeline

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import pipeline.PrepareJson.prepareJson

import org.apache.log4j.{Level, Logger}


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


    //Generate proper text file to be used as input
    prepareJson("SampleDataset.txt", "text");






  }
}

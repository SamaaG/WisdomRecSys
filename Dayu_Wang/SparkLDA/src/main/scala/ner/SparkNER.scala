package ner

import org.apache.spark.{SparkConf, SparkContext}
import java.io.PrintStream

/**
  * Created by Mayanka on 29-Jun-16.
  */
object SparkNER {
  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "C:\\winutils")
    val conf = new SparkConf().setAppName(s"NERTrain").setMaster("local[*]").set("spark.driver.memory", "4g").set("spark.executor.memory", "4g")
    val sc = new SparkContext(conf)


    val Domain_Based_Words = sc.broadcast(sc.textFile("data/ner/Environ").map(CoreNLP.returnLemma(_)).collect())

    val input = sc.textFile("data/ner/reviews").flatMap(f => {
      val s=f.replace("[^a-zA-Z\\s]","")
      s.split(" ")}).map(ff => {
      val f = CoreNLP.returnLemma(ff)
      if (Domain_Based_Words.value.contains(f))
        (f, "Environment")
      else
        (f, CoreNLP.returnNER(f))
    }

    )
    val topic_output=new PrintStream("data/NER_Results.txt")



    println(input.collect().mkString("\n"))
    topic_output.println(input.collect().mkString("\n"))
  }

}

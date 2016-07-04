package pipeline

import java.io.PrintWriter

import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.feature.{NGram, StopWordsRemover, Tokenizer}
import org.apache.spark.mllib.feature.{HashingTF, IDF}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.immutable.HashMap

/**
  * Created by Samaa on 6/24/2016.
  */
object TFIDF {

    def performTFIDF(sc: SparkContext, spark: SparkSession, input: RDD[(Int, String)]) {

      // Turn off Info Logger for Console
      Logger.getLogger("org").setLevel(Level.OFF);
      Logger.getLogger("akka").setLevel(Level.OFF);

      System.setProperty("hadoop.home.dir", "C:\\winutils");

      //Creating DataFrame from RDD
      val sentenceData = spark.createDataFrame(input).toDF("labels", "sentence")

      //Tokenizer
      val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
      val wordsData = tokenizer.transform(sentenceData)

      //Stop Word Remover
      val remover = new StopWordsRemover()
        .setInputCol("words")
        .setOutputCol("filteredWords")
      val processedWordData = remover.transform(wordsData)

      //NGram (only taking 3 lines to print)
      val ngram = new NGram().setInputCol("filteredWords").setOutputCol("ngrams")
      val ngramDataFrame = ngram.transform(processedWordData)
      ngramDataFrame.take(3).foreach(println)
      println(ngramDataFrame.printSchema())

      //TFIDF TopWords
      val topWords = getTopTFIDFWords(sc, ngramDataFrame.select("filteredWords").rdd)
      println("TOP WORDS: \n\n"+ topWords.mkString("\n"))









/*
      val documents = sc.textFile(filePath)
      val documentseq = documents.map(_.split(" ").toSeq)

      val strData = sc.broadcast(documentseq.collect())
      val hashingTF = new HashingTF()
      val tf = hashingTF.transform(documentseq)
      tf.cache()

      val idf = new IDF().fit(tf)
      val tfidf = idf.transform(tf)

      val tfidfvalues = tfidf.flatMap(f => {
        val ff: Array[String] = f.toString.replace(",[", ";").split(";")
        val values = ff(2).replace("]", "").replace(")","").split(",")
        values
      })
      val tfidfindex = tfidf.flatMap(f => {
        val ff: Array[String] = f.toString.replace(",[", ";").split(";")
        val indices = ff(1).replace("]", "").replace(")","").split(",")
        indices
      })
      tfidf.foreach(f => println(f))

      val tfidfData = tfidfindex.zip(tfidfvalues)
      var hm = new HashMap[String, Double]
      tfidfData.collect().foreach(f => {
        hm += f._1 -> f._2.toDouble
      })
      val mapp = sc.broadcast(hm)

      val documentData = documentseq.flatMap(_.toList)
      val dd = documentData.map(f => {
        val i = hashingTF.indexOf(f)
        val h = mapp.value
        (f, h(i.toString))
      })

      val dd1=dd.distinct().sortBy(_._2,false)
      dd1.take(20).foreach(f=>{
        println(f)
      })*/
    }

  def getTopTFIDFWords(sc: SparkContext, input:RDD[Row]): Array[(String, Double)] = {

    val documentseq = input.map(_.getList(0).toString.replace("[","").replace("]","").replace(" ","").split(",").toSeq)

    val strData = sc.broadcast(documentseq.collect())
    val hashingTF = new HashingTF()
    val tf = hashingTF.transform(documentseq)
    tf.cache()

    val idf = new IDF().fit(tf)
    val tfidf = idf.transform(tf)

    val tfidfvalues = tfidf.flatMap(f => {
      val ff: Array[String] = f.toString.replace(",[", ";").split(";")
      val values = ff(2).replace("]", "").replace(")","").split(",")
      values
    })
    val tfidfindex = tfidf.flatMap(f => {
      val ff: Array[String] = f.toString.replace(",[", ";").split(";")
      val indices = ff(1).replace("]", "").replace(")","").split(",")
      indices
    })
    tfidf.foreach(f => println(f))

    val tfidfData = tfidfindex.zip(tfidfvalues)
    var hm = new HashMap[String, Double]
    tfidfData.collect().foreach(f => {
      hm += f._1 -> f._2.toDouble
    })
    val mapp = sc.broadcast(hm)

    val documentData = documentseq.flatMap(_.toList)
    val dd = documentData.map(f => {
      val i = hashingTF.indexOf(f)
      val h = mapp.value
      (f, h(i.toString))
    })

    val dd1=dd.distinct().sortBy(_._2,false)
    dd1.take(20).foreach(f=>{
      println(f)
    })
    return dd1.take(10)
  }

}

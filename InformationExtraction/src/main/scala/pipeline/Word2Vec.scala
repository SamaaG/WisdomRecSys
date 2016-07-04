package pipeline

import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.feature.{NGram, StopWordsRemover, Tokenizer, Word2Vec}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.SparkContext

/**
  * Created by Samaa on 7/4/2016.
  */
object Word2Vec {
  def word2vec(sc: SparkContext,  input: RDD[(Int, String)]){

    val spark = SparkSession
      .builder
      .appName("SparkW2VML")
      .master("local[*]")
      .config("spark.driver.memory","8g")
      .config("spark.executor.memory","8g")
      .getOrCreate()


    // Turn off Info Logger for Console
    Logger.getLogger("org").setLevel(Level.OFF);
    Logger.getLogger("akka").setLevel(Level.OFF);


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

    //NGram
    val ngram = new NGram().setInputCol("filteredWords").setOutputCol("ngrams")
    val ngramDataFrame = ngram.transform(processedWordData)
    //ngramDataFrame.take(3).foreach(println)
    //println(ngramDataFrame.printSchema())

    //TFIDF TopWords
    val topWords = TFIDF.getTopTFIDFWords(sc, ngramDataFrame.select("ngrams").rdd)
    //println("\n\nTOP WORDS: \n" + topWords.mkString("\n"))

    val newDataFrame = ngramDataFrame.na.drop()

    //println("\n\nSchema: \n")
    //println(newDataFrame.printSchema())

    //Word2Vec Model Generation


    val word2Vec = new Word2Vec()
      .setInputCol("ngrams")
      .setOutputCol("result")
      .setVectorSize(3)
      .setMinCount(0)

    val struct = StructType(StructField("labels", IntegerType, false) ::
      StructField("sentence", StringType, true) ::
      StructField("words", ArrayType(StringType,true), true)::
      StructField("filteredWords",ArrayType(StringType,true), true)::
      StructField("ngrams", ArrayType(StringType,true), true) :: Nil
    )
    val ss=spark.createDataFrame(sc.parallelize(ngramDataFrame.collect()),struct)

    val model = word2Vec.fit(ss)


    //Finding synonyms for TOP TFIDF Words using Word2Vec Model
    topWords.foreach(f => {
      println(f._1 + "  : ")
      val result = model.findSynonyms(f._1, 3)
      result.take(3).foreach(println)
      println
    })


    spark.stop()

  }

}

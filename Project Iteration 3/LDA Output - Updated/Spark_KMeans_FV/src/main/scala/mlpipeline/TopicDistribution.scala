package mlpipeline

/**
  * Created by poojashekhar on 7/17/16.
  */
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd._
import org.apache.spark.mllib.clustering.{DistributedLDAModel, LDA, LocalLDAModel}
import org.apache.spark.mllib.linalg.{Vector, Vectors}

import scala.collection.mutable

object TopicDistribution {

  def main(args: Array[String]) {


    val conf = new SparkConf().setAppName(s"LDAExample").setMaster("local[*]").set("spark.driver.memory", "4g").set("spark.executor.memory", "4g")
    val sc = new SparkContext(conf)
    Logger.getRootLogger.setLevel(Level.WARN)


    //create training document set
    val input = Seq("this is a document", "this could be another document", "these are training, not tests", "here is the final file (document)")
    val corpus: RDD[Array[String]] = sc.parallelize(input.map {
      doc => doc.split("\\s")
    })

    val termCounts: Array[(String, Long)] = corpus.flatMap(_.map(_ -> 1L)).reduceByKey(_ + _).collect().sortBy(-_._2)

    val vocabArray: Array[String] = termCounts.takeRight(termCounts.size).map(_._1)
    val vocab: Map[String, Int] = vocabArray.zipWithIndex.toMap

    // Convert training documents into term count vectors
    val documents: RDD[(Long, Vector)] =
      corpus.zipWithIndex.map { case (tokens, id) =>
        val counts = new mutable.HashMap[Int, Double]()
        tokens.foreach { term =>
          if (vocab.contains(term)) {
            val idx = vocab(term)
            counts(idx) = counts.getOrElse(idx, 0.0) + 1.0
          }
        }
        (id, Vectors.sparse(vocab.size, counts.toSeq))
      }
    // Set LDA parameters and create model
    val numTopics = 10
    val ldaModel: DistributedLDAModel = new LDA().setK(numTopics).setMaxIterations(20).run(documents).asInstanceOf[DistributedLDAModel]
    val localLDAModel: LocalLDAModel = ldaModel.toLocal

    //create test input, convert to term count, and get its topic distribution
    val test_input = Seq("this is my test document")
    val test_document: RDD[(Long, Vector)] = sc.parallelize(test_input.map(doc => doc.split("\\s"))).zipWithIndex.map { case (tokens, id) =>
      val counts = new mutable.HashMap[Int, Double]()
      tokens.foreach { term =>
        if (vocab.contains(term)) {
          val idx = vocab(term)
          counts(idx) = counts.getOrElse(idx, 0.0) + 1.0
        }
      }
      (id, Vectors.sparse(vocab.size, counts.toSeq))
    }

    val topicDistributions = localLDAModel.topicDistributions(test_document)
    println("first topic distribution:" + topicDistributions.first._2.toArray.mkString(", "))
  }
}

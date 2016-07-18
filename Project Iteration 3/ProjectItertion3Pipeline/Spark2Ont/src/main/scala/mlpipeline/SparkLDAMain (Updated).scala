package mlpipeline

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import java.io.PrintStream

import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature._
import org.apache.spark.mllib.clustering._
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}
import scopt.OptionParser
import org.apache.spark.mllib.clustering.{LDA, DistributedLDAModel, LocalLDAModel}




/**
  * An example Latent Dirichlet Allocation (LDA) app. Run with
  * {{{
  * ./bin/run-example mllib.LDAExample [options] <input>
  * }}}
  * If you use it as a template to create your own app, please use `spark-submit` to submit your app.
  */
object SparkLDAMain {

  private case class Params(
                             input: Seq[String] = Seq.empty,
                             k: Int = 10,
                             maxIterations: Int = 100,
                             docConcentration: Double =25,
                             topicConcentration: Double = 75,
                             algorithm: String = "em")

  def main(args: Array[String]) {
    val defaultParams = Params()

    val parser = new OptionParser[Params]("LDAExample") {
      head("LDAExample: an example LDA app for plain text data.")
      opt[Int]("k")
        .text(s"number of topics. default: ${defaultParams.k}")
        .action((x, c) => c.copy(k = x))
      opt[Int]("maxIterations")
        .text(s"number of iterations of learning. default: ${defaultParams.maxIterations}")
        .action((x, c) => c.copy(maxIterations = x))
      opt[Double]("docConcentration")
        .text(s"amount of topic smoothing to use (> 1.0) (-1=auto)." +
          s"  default: ${defaultParams.docConcentration}")
        .action((x, c) => c.copy(docConcentration = x))
      opt[Double]("topicConcentration")
        .text(s"amount of term (word) smoothing to use (> 1.0) (-1=auto)." +
          s"  default: ${defaultParams.topicConcentration}")
        .action((x, c) => c.copy(topicConcentration = x))
      opt[String]("algorithm")
        .text(s"inference algorithm to use. em and online are supported." +
          s" default: ${defaultParams.algorithm}")
        .action((x, c) => c.copy(algorithm = x))
      arg[String]("<input>...")
        .text("input paths (directories) to plain text corpora." +
          "  Each text file line should hold 1 document.")
        .unbounded()
        .required()
        .action((x, c) => c.copy(input = c.input :+ x))
    }

    parser.parse(args, defaultParams).map { params =>
      run(params)
    }.getOrElse {
      parser.showUsageAsError
      sys.exit(1)
    }
  }

  private def run(params: Params) {
    System.setProperty("hadoop.home.dir", "F:\\winutils")
    val conf = new SparkConf().setAppName(s"LDAExample with $params").setMaster("local[*]").set("spark.driver.memory", "4g").set("spark.executor.memory", "4g")
    val sc = new SparkContext(conf)

    Logger.getRootLogger.setLevel(Level.WARN)

    val topic_output = new PrintStream("/home/poojashekhar/Downloads/CS5560 - Tutorial 9 Source Code/Spark_KMeans_FV/data/LDA_Results.txt")

    // Load documents, and prepare them for LDA.
    val preprocessStart = System.nanoTime()
    val (corpus, vocabArray, actualNumTokens) =
      preprocess(sc, params.input)



    corpus.cache()
    val actualCorpusSize = corpus.count()
    val actualVocabSize = vocabArray.length
    val preprocessElapsed = (System.nanoTime() - preprocessStart) / 1e9

    println()
    println(s"Corpus summary:")
    println(s"\t Training set size: $actualCorpusSize documents")
    println(s"\t Vocabulary size: $actualVocabSize terms")
    println(s"\t Training set size: $actualNumTokens tokens")
    println(s"\t Preprocessing time: $preprocessElapsed sec")



    topic_output.println()
    topic_output.println(s"Corpus summary:")
    topic_output.println(s"\t Training set size: $actualCorpusSize documents")
    topic_output.println(s"\t Vocabulary size: $actualVocabSize terms")
    topic_output.println(s"\t Training set size: $actualNumTokens tokens")
    topic_output.println(s"\t Preprocessing time: $preprocessElapsed sec")
    topic_output.println()

    // Run LDA.
    val lda = new LDA()

    val optimizer = params.algorithm.toLowerCase match {
      case "em" => new EMLDAOptimizer
      // add (1.0 / actualCorpusSize) to MiniBatchFraction be more robust on tiny datasets.
      case "online" => new OnlineLDAOptimizer().setMiniBatchFraction(0.05 + 1.0 / actualCorpusSize)
      case _ => throw new IllegalArgumentException(
        s"Only em, online are supported but got ${params.algorithm}.")
    }

    lda.setOptimizer(optimizer)
      .setK(params.k)
      .setMaxIterations(params.maxIterations)
      .setDocConcentration(params.docConcentration)
      .setTopicConcentration(params.topicConcentration)

    val startTime = System.nanoTime()
    val ldaModel = lda.run(corpus)
    val elapsed = (System.nanoTime() - startTime) / 1e9

    println(s"Finished training LDA model.  Summary:")
    println(s"\t Training time: $elapsed sec")


    topic_output.println(s"Finished training LDA model.  Summary:")
    topic_output.println(s"\t Training time: $elapsed sec")

    if (ldaModel.isInstanceOf[DistributedLDAModel]) {
      val distLDAModel = ldaModel.asInstanceOf[DistributedLDAModel]
      val avgLogLikelihood = distLDAModel.logLikelihood / actualCorpusSize.toDouble
      println(s"\t Training data average log likelihood: $avgLogLikelihood")
      println()
      topic_output.println(s"\t Training data average log likelihood: $avgLogLikelihood")
      topic_output.println()
    }

    val vocab: Map[String, Int] = vocabArray.zipWithIndex.toMap

    // Print the topics, showing the top-weighted terms for each topic.
    val topicIndices = ldaModel.describeTopics(maxTermsPerTopic = 100)
    val topics = topicIndices.map { case (terms, termWeights) =>
      terms.zip(termWeights).map { case (term, weight) => (vocabArray(term.toInt), weight) }
    }

    /*val test_input = Seq("this is my test document");
    val distLDAModel = ldaModel.asInstanceOf[DistributedLDAModel]
    val localLDAModel: LocalLDAModel = distLDAModel.toLocal
*/



    println(s"${params.k} topics:")
    topic_output.println(s"${params.k} topics:")
    topics.zipWithIndex.foreach { case (topic, i) =>
      topic_output.println(s"TOPIC $i")
      // topic_output.println(s"TOPIC $i")
      topic.foreach { case (term, weight) =>
        println(s"$term\t$weight")
        //topic_output.println(s"TOPIC_$i")
        topic_output.println(s"$term,$weight")
      }
      println()
      topic_output.println()
    }

    /*val test_document:RDD[(Long,Vector)] = sc.parallelize(test_input.map(doc=>doc.split("\\s"))).zipWithIndex.map{ case (tokens, id) =>
      val counts = new scala.collection.mutable.HashMap[Int, Double]()
      tokens.foreach { term =>
        if (vocab.contains(term)) {
          val idx = vocab(term)
          counts(idx) = counts.getOrElse(idx, 0.0) + 1.0
        }
      }
      (id, Vectors.sparse(vocab.size, counts.toSeq))

    }
    val topicDistributions = localLDAModel.topicDistributions(test_document)
    topic_output.println("first topic distribution:"+topicDistributions.first._2.toArray.mkString(", "))*/
    topic_output.close()
    sc.stop()
  }

  /**
    * Load documents, tokenize them, create vocabulary, and prepare documents as term count vectors.
    *
    * @return (corpus, vocabulary as array, total token count in corpus)
    */
  private def preprocess(
                          sc: SparkContext,
                          paths: Seq[String]): (RDD[(Long, Vector)], Array[String], Long) = {

    val sqlContext = SQLContext.getOrCreate(sc)
    import sqlContext.implicits._

    // Get dataset of document texts
    // One document per line in each text file. If the input consists of many small files,
    // this can result in a large number of small partitions, which can degrade performance.
    // In this case, consider using coalesce() to create fewer, larger partitions.
    val df = sc.textFile(paths.mkString(",")).map(f => CoreNLP.returnLemma(f)).filter(_.length > 3).toDF("docs")


    val tokenizer = new RegexTokenizer()
      .setInputCol("docs")
      .setOutputCol("rawTokens")


    val stopWordsRemover = new StopWordsRemover()
      .setInputCol("rawTokens")
      .setOutputCol("tokens")

    stopWordsRemover.setStopWords(stopWordsRemover.getStopWords);

    val hashingTF = new HashingTF()
      .setNumFeatures(1000)
      .setInputCol(tokenizer.getOutputCol)
      .setOutputCol("features0")

    val idf = new IDF()
      .setInputCol(hashingTF.getOutputCol)
      .setOutputCol("features1")

    val countVectorizer = new CountVectorizer()
      .setInputCol("tokens")
      .setOutputCol("features")

    val pipeline = new Pipeline()
      .setStages(Array(tokenizer, stopWordsRemover,hashingTF,idf, countVectorizer))

    val model = pipeline.fit(df)

    val documents = model.transform(df)
      .select("features1")
      .rdd
      .map { case Row(features: Vector) => features }
      .zipWithIndex()
      .map(_.swap)




    (documents,
      model.stages(4).asInstanceOf[CountVectorizerModel].vocabulary, // vocabulary
      documents.map(_._2.numActives).sum().toLong) // total token count
  }
}

// scalastyle:on println

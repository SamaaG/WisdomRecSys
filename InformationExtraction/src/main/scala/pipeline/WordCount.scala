package pipeline


import java.io.File

import org.apache.spark.sql.Row
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Samaa on 6/24/2016.
  */


object WordCount {

  def PerformWordCount(filePath: String,sc: SparkContext) {

    System.setProperty("hadoop.home.dir","C:\\winutils")


    val input=sc.textFile(filePath)

    val wc=input.flatMap(line=>{line.split(" ")}).map(word=>(word,1)).cache()

    val output=wc.reduceByKey(_+_)

    output.saveAsTextFile("output")

    val o=output.collect()

    var s:String="Words:Count \n"
    o.foreach{case(word,count)=>

      s+=word+" : "+count+"\n"

    }

  }

}


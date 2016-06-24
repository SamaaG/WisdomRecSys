package pipeline


import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by Samaa on 6/24/2016.
  */


object WordCount {

  def PerformWordCount(filePath: String) {

    System.setProperty("hadoop.home.dir","C:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")

    val sc=new SparkContext(sparkConf)

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


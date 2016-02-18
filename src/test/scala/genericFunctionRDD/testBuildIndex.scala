package genericFunctionRDD

import org.apache.spark.{SparkContext, SparkConf}
import org.scalatest.{Matchers, FunSpec}

/**
 * Created by merlin on 2/17/16.
 */
class testBuildIndex extends FunSpec with Matchers {

  describe("test build the tree dimensional index over rdd") {

    val conf = new SparkConf().setAppName("Test for Spark SpatialRDD").setMaster("local[2]")

    val spark = new SparkContext(conf)

    val datapoints=Array{
      (0.1f,0.2f,0.3f);(0.3f,0.5f,0.6f);
      (1.1f,0.2f,0.3f);(0.3f,4.5f,55.6f);
      (3.1f,4.2f,1.3f);(110.3f,0.5f,2.6f);
      (4.1f,5.2f,2.3f);(2.3f,3.5f,44.6f)
    }

    val inputRDD=spark.parallelize(datapoints,2).map{
      case pt=>(pt,1)
    }

    val genericRDD=genericFunctionRDD(inputRDD)

    println(genericRDD.count())

  }

}

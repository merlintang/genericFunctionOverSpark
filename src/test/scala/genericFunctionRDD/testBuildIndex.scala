package genericFunctionRDD

import org.apache.spark.{SparkContext, SparkConf}
import org.scalatest.{FunSuite}
import scala.util.Random._


/**
 * Created by merlin on 2/17/16.
 */
class testBuildIndex extends FunSuite {

  test("test build high dimensional r-tree index over rdd") {

    val conf = new SparkConf().setAppName("Test for Spark GenericRDD").setMaster("local[2]")

    val spark = new SparkContext(conf)

    def uniformPoint():Array[Float]=
      Array(nextFloat, nextFloat,nextFloat)

    val numofpoints=10000

    val dataset = (1 to numofpoints).map(n => uniformPoint())

    val inputRDD=spark.parallelize(dataset,4).map{
      case pt=>(pt,1)
    }

    val min=inputRDD.map
    {
      case f=> val arr=f._1
        arr.foldLeft(1f)((m, n) => m*n)
    }.min()

    println("naive min value "+min)

    val genericRDD=genericFunctionRDD(inputRDD)

    //println("total size "+genericRDD.count())
    //assert(genericRDD.count()==datapoints.length)

    val udf="x1*x2*x3"
    val defudf=Array("x2*x3", "x1*x3","x1*x2")

    println("min "+genericRDD.getSmallest(udf,defudf))

  }

}

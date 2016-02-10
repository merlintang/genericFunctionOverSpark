package genericFunctionRDD.impl

import org.apache.spark.Partitioner
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

/**
 * Created by merlin on 2/9/16.
 */
class RTreePartitioner [K: ClassTag,V:ClassTag](partitions:Int, fraction:Float,
                                                  @transient rdd: RDD[_ <: Product2[K, V]]) extends Partitioner{

  // We allow partitions = 0, which happens when sorting an empty RDD under the default settings.
  require(partitions >= 0, s"Number of partitions cannot be negative but found $partitions.")

  var realnumPartitions=0

  val rtreeForPartition={
    //sample data from big data set
    var sampledata=rdd.map(_._1).sample(false,fraction).collect()

  }

  def numPartitions: Int = {
    partitions
  }

  /**
   * get the related partition for each input data point
   * @param key
   * @return
   */
  def getPartition(key: Any): Int ={
    1
  }

  override def hashCode: Int = realnumPartitions
}

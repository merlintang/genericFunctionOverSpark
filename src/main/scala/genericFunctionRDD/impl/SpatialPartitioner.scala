package genericFunctionRDD.impl

import com.newbrightidea.util.RTree
import org.apache.spark.Partitioner
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

/**
 * Created by merlin on 2/9/16.
 */
class RTreePartitioner [K: ClassTag,V:ClassTag](partitions:Int, fraction:Float,
                                                rdd: RDD[_ <: Product2[K, V]]) extends Partitioner{

  // We allow partitions = 0, which happens when sorting an empty RDD under the default settings.
  require(partitions >= 0, s"Number of partitions cannot be negative but found $partitions.")

  val rtreeForPartition={
    //sample data from big data set
    val sampledata=rdd.map(_._1).sample(false,fraction).collect()

    val numDimensions: Int = Util.input_data_dimensions
    val minNum: Int = Util.rtree_minimum_number
    val maxNum: Int = sampledata.length/partitions

    val rt: RTree[Int] = new RTree[Int](minNum, maxNum, numDimensions, RTree.SeedPicker.QUADRATIC)

    sampledata.foreach
    {
      case coords: Array[Float]  =>
        rt.insert(coords,1)
    }
    rt
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
    key match
    {
      case k:Array[Float]=>
        (this.rtreeForPartition.getPartitionID(k))%this.numPartitions
      case _=>
        1
        //Array(k)
    }
  }

  override def hashCode: Int = rtreeForPartition.size()
}

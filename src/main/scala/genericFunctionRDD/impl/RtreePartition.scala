package genericFunctionRDD.impl


import genericFunctionRDD.SpatialRDDPartition
import org.apache.spark.Logging
import com.newbrightidea.util.RTree

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

/**
 * Created by merlin on 2/9/16.
 */
class RtreePartition [K, V]
(protected val tree: RTree[V])
(
  override implicit val kTag: ClassTag[K],
  override implicit val vTag: ClassTag[V]
  )
  extends SpatialRDDPartition[K,V] with Logging{

  override def size: Long = tree.size()

  override def isDefined(k: K): Boolean = tree==null

  /**
   * range search and find points inside the box, and each element meet the condition, and return a iterator,
   * and this iterator can be used for other RDD
   */
  override def topMin(k: Int,f: (K, V) => Double): Iterator[(K, V)] = ???

  /**
   * range search and find points inside the box, and each element meet the condition, and return a iterator,
   * and this iterator can be used for other RDD
   */
  override def rangefilter(begin: Double, end: Double,f: (K, V) => Double): Iterator[(K, V)] = ???


  override def iterator: Iterator[(K, V)] =
  {
    val nodes=this.tree.iterators()
    val buffer=new ArrayBuffer[(K,V)]()

    for(i <- 0 to nodes.size())
    {
      val n=nodes.get(i)
      buffer.+=((n.getCorrd.asInstanceOf[K],n.getEntry))
    }

    buffer.toIterator
  }

  /**
   * range search and find points inside the box, and each element meet the condition, and return a iterator,
   * and this iterator can be used for other RDD
   */
  override def topMax(k: Int,f: (K, V) => Double): Iterator[(K, V)] = ???


}

private[genericFunctionRDD] object RtreePartition {

  def apply[K: ClassTag, V: ClassTag]
  (iter: Iterator[(K, V)]) =
    apply[K, V, V](iter, (id, a) => a, (id, a, b) => b)

  def apply[K: ClassTag, U: ClassTag, V: ClassTag]
  (iter: Iterator[(K, V)], z: (K, U) => V, f: (K, V, U) => V)
  : SpatialRDDPartition[K, V] =
  {
    val numDimensions: Int = 3
    val minNum: Int = 32
    val maxNum: Int = 64
    val rt: RTree[V] = new RTree[V](minNum, maxNum, numDimensions, RTree.SeedPicker.QUADRATIC)

    iter.foreach
    {
      case (k,v)=>
        k match
        {
          case coords: Array[Float]=>
            rt.insert(coords,v)
        }
    }
    new RtreePartition(rt)
  }

}

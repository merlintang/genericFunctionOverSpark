package genericFunctionRDD.impl


import genericFunctionRDD.SpatialRDDPartition
import org.apache.spark.Logging
import spatialindex.Rtree

import scala.reflect.ClassTag

/**
 * Created by merlin on 2/9/16.
 */
class RtreePartition [K, V]
(protected val tree: Rtree[V])
(
  override implicit val kTag: ClassTag[K],
  override implicit val vTag: ClassTag[V]
  )
  extends SpatialRDDPartition[K,V] with Logging{


  override def size: Long = ???

  override def isDefined(k: K): Boolean = ???

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

  /**
   * Gets the values corresponding to the specified keys, if any. those keys can be the two dimensional object
   */
  override def multiget(ks: Iterator[K]): Iterator[(K, V)] = ???

  /** Return the value for the given key. */
  override def apply(k: K): Option[V] = ???


  override def iterator: Iterator[(K, V)] = ???

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

    val tree = new Rtree(iter)
    new RtreePartition(tree)
  }

}

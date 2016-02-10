package genericFunctionRDD

/**
 * Created by merlin on 2/9/16.
 */

import scala.reflect.ClassTag

abstract class SpatialRDDPartition [K, V] extends Serializable {

  protected implicit def kTag: ClassTag[K]
  protected implicit def vTag: ClassTag[V]

  def size: Long

  /** Return the value for the given key. */
  def apply(k: K): Option[V]

  def isDefined(k: K): Boolean

  def iterator: Iterator[(K, V)]

  /**
   * Gets the values corresponding to the specified keys, if any. those keys can be the two dimensional object
   */
  def multiget(ks: Iterator[K]): Iterator[(K, V)]

  /**
   *range search and find points inside the function related value, and each element meet the condition, and return a iterator,
   * and this iterator can be used for other RDD
   */
  def rangefilter(begin:Double,end:Double,f: (K, V) => Double):Iterator[(K,V)]

  /**
   *top k search for the predifined function
   */
  def topMin(k:Int, f: (K, V) => Double):Iterator[(K,V)]

  /**
   *range search and find points inside the box, and each element meet the condition, and return a iterator,
   * and this iterator can be used for other RDD
   */
  def topMax(k:Int,f: (K, V) => Double):Iterator[(K,V)]


}


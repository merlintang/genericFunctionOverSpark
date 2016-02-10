package genericFunctionRDD

import org.apache.spark.storage.StorageLevel
import org.apache.spark.{TaskContext, Partition, OneToOneDependency}
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

/**
 * Created by merlin on 2/9/16.
 */
class genericFunctionRDD[K: ClassTag, V: ClassTag]
(
val partitionsRDD: RDD[SpatialRDDPartition[K, V]]
)
extends RDD[(K, V)](partitionsRDD.context, List(new OneToOneDependency(partitionsRDD)))
{

  require(partitionsRDD.partitioner.isDefined)

  override val partitioner = partitionsRDD.partitioner

  override protected def getPartitions: Array[Partition] = partitionsRDD.partitions

  override protected def getPreferredLocations(s: Partition): Seq[String] =
    partitionsRDD.preferredLocations(s)

  override def persist(newLevel: StorageLevel): this.type = {
    partitionsRDD.persist(newLevel)
    this
  }

  override def unpersist(blocking: Boolean = true): this.type = {
    partitionsRDD.unpersist(blocking)
    this
  }

  override def setName(_name: String): this.type = {
    partitionsRDD.setName(_name)
    this
  }

  override def count(): Long = {
    partitionsRDD.map(_.size).reduce(_ + _)
  }

  /** Provides the `RDD[(K, V)]` equivalent output. */
  override def compute(part: Partition, context: TaskContext): Iterator[(K, V)] = {
    firstParent[SpatialRDDPartition[K, V]].iterator(part, context).next.iterator
  }

  /**
   *
   * @param k and related function
   */
  def topMax(k:Int,f: (K, V) => Double): Unit =
  {

  }

  /**
   *
   * @param k and related functions
   */
  def topMin(k:Int,f: (K, V) => Double): Unit =
  {

  }

  /**
   *
   * @param begin
   * @param end
   * @param f: (K, V)
   */
  def range(begin:Double, end:Double,f: (K, V) => Double): Unit =
  {

  }

}

object genericFunctionRDD {
  /**
   * Constructs an updatable IndexedRDD from an RDD of pairs, merging duplicate keys arbitrarily.
   */
  def apply[K: ClassTag, V: ClassTag]
  (elems: RDD[(K, V)]): genericFunctionRDD[K, V] = updatable(elems)

  /**
   * Constructs an updatable IndexedRDD from an RDD of pairs, merging duplicate keys arbitrarily.
   */
  def updatable[K: ClassTag , V: ClassTag]
  (elems: RDD[(K, V)])
  : genericFunctionRDD[K, V] = updatable[K, V, V](elems, (id, a) => a, (id, a, b) => b)

  /** Constructs an indexed rdd from an RDD of pairs.
    the default partitioner is the rtree based partioner
    * */
  def updatable[K: ClassTag , U: ClassTag, V: ClassTag]
  (elems: RDD[(K, V)], z: (K, U) => V, f: (K, V, U) => V)
  : genericFunctionRDD[K, V] = {

    val elemsPartitioned = elems.partitionBy(new RTreePartitioner(500,0.01f,elems))

    val partitions = elemsPartitioned.mapPartitions[SpatialRDDPartition[K, V]](
      iter => Iterator(RtreePartition(iter, z, f)),
      preservesPartitioning = true
    )

    new genericFunctionRDD(partitions)
  }

}

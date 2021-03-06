package pl.appsilon.marek.sparkdatalog

import org.apache.spark.rdd.RDD
import pl.appsilon.marek.sparkdatalog.eval.SparkDatalog

case class Database(relations: Map[String, Relation]) {
  def include(relation: Relation) = copy(relations + ((relation.name, relation)))

  def rename(from: String, to: String) = {
    exclude(from).include(relations(from).copy(name=to))
  }

  def exclude(relationName: String) = {
    copy(relations - relationName)
  }


  def datalog(datalogQuery: String): Database = {
    SparkDatalog.datalog(this, datalogQuery)
  }

  def apply(relationName: String): RDD[Fact] = relations(relationName).data


  def materialize(): this.type = {
    relations.foreach(_._2.data.count())
    this
  }

  def collect(): Map[String, Set[Fact]] = {
    relations.mapValues(relation => relation.data.collect().toSet)
  }

}

object Database {
  def empty: Database = new Database(Map())

  def apply(relations: Relation*) =
    new Database(Map() ++ relations.map(relation => (relation.name, relation)))
}

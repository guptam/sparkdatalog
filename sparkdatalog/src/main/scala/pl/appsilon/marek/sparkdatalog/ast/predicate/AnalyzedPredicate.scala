package pl.appsilon.marek.sparkdatalog.ast.predicate

import pl.appsilon.marek.sparkdatalog.{Fact, Valuation}
import pl.appsilon.marek.sparkdatalog.ast.value.ValueLiteral
import pl.appsilon.marek.sparkdatalog.eval.RelationInstance

case class AnalyzedPredicate(tableName: String, args: Seq[Either[ValueLiteral, Int]], emptyValuation: Valuation) {

  val variables: Set[Int] = args.flatMap(_.right.toOption).toSet

  // TODO: co z R(x, x, .... ) ?
  // Moze najpierw obliczac valuation dla tego fact, a potem tylko porownywac
  def matchArgsGeneric(fact: Fact, initialValuation: Valuation): Option[Valuation] = {
    val valuation = initialValuation.clone()

    for((actualValue, arg) <- fact.zip(args)) {
      arg match {
        case Left(literal) =>
          if(literal.value.asInstanceOf[Int] != actualValue){
            return None
          }
        case Right(varId) =>
          valuation(varId) match {
            case Some(`actualValue`) => // OK
            case Some(_) => return None
            case None => valuation.update(varId, Some(actualValue))
          }
      }
    }

    Some(valuation)
  }

  def matchArgs(fact: Fact): Option[Valuation] = matchArgsGeneric(fact, emptyValuation)

  def evaluateLocally(relation: RelationInstance): Seq[Valuation] = relation.facts.flatMap(matchArgs)

  def fetchMatchingInstances(valuation: Valuation, relation: RelationInstance): Seq[Valuation] =
    relation.facts.flatMap(matchArgsGeneric(_, valuation))


}
abstract class Expression{

   def isList : Boolean
   def isEmpty : Boolean
   def isAtom: Boolean
   def length: Int

}

case class SExp(sexp : String) extends Expression{

   val str_exp = sexp.replace("("," ( ").replace(")"," ) ").trim.split(" +").mkString(" ");
   val exp = if (str_exp.length > 0) str_exp.dropRight(1).reverse.dropRight(1).reverse.trim else str_exp;

   override def isList: Boolean={

      if (str_exp.length > 0 && str_exp(0) == '('){

         return true;

      }

      return false;

   }

   override def length(): Int={

      return exp.length;

   }

   override def isEmpty(): Boolean={

      if (exp.toString == "( )"){

         return true;

      }

      return false;

   }

   override def isAtom(): Boolean={

      if (new SExp(exp).isList){

         return false;

      }

      return true;

   }

   override def toString(): String={

      return str_exp;

   }

   def toAtom(): SExp={

      return new SExp(exp);

   }

   def toInfix(): InfixExp={

      return (
         if (first.toString != "not")
            (new InfixExp("(" +
               (if (second.isList)
                  (if (second.first.toString == first.toString)
                  (if (second.second.isList)
                     second.second.toAtom.toInfix else
                     second.second.toString) + " " + first.toString + " " +
                  (if (second.third.isList)
                     second.third.toAtom.toInfix else
                     second.third.toString) else
                  second.toAtom.toInfix) else
               second.toAtom.toString) + " " +

               first.toString + " " +

               (if (third.toAtom.isList)
                  ((if (third.first.toString == first.toString)
                     (if (third.second.isList)
                        third.second.toAtom.toInfix else
                        third.second.toString) + " " + first.toString + " " +
                     (if (third.third.isList)
                        third.third.toAtom.toInfix else
                        third.third.toString) else
                     third.toAtom.toInfix)) else
                  third.toString) + ")"))
         else (new InfixExp(exp))
      )
   }

   private def getRest(start: Int): String={

      var depth = 0;
      var result = "";

      if (exp(start) != '('){

         println("getRest must start with a '('");
         return ""

      }

      for( i <- start until length()){

         result += exp(i);

         if (exp(i+1) == '('){

            depth += 1;

         } else if (exp(i+1) == ')'){

            if (depth == 0){

               return result + ")";

            } else {

               depth -= 1;

            }

         }

      }

      return "invalid list";

   }

   private def indexOf(index: Int): SExp={

      var c = new SExp(exp.split(" +")(index));
      if (c.toString.length > 0 && c.toString()(0) == '('){

         return new SExp(getRest(index));

      }

      return c;

   }

   def car(): SExp={

      return indexOf(0);

   }

   def first(): SExp={

      return car;

   }

   def second(): SExp={

      return cdr.car;

   }

   def third(): SExp={

      return cdr.cdr.car;

   }

   def cdr(): SExp={

      return new SExp("( " + exp.replaceFirst(car.toString,"").replace("()","") + " )");

   }

}

class InfixExp(sexp: String) extends SExp(sexp: String){

   override def toString(): String={

      return str_exp;

   }

   def realExp(): String={

      return exp;

   }

}

object BoolExp{

   def main(args: Array[String]){

      // (  x and (  (  w or y ) and (  w or z ) ) )
      var p1 = SExp("(and y (not z))");
      val p2 = SExp("(or (or nil y) (or (or x x) (or d nil)))");
      val p3 = new InfixExp("(and x (not (or p q)))");
      val p4 = new InfixExp("(and ())");

      println("final evaluation: " + evalCNF(p1));
      //runTests

   }


   def substituteExp(exp: SExp, bindings: SExp): SExp={

      if (bindings.toString() == "( )"){

         return exp;

      } else {

         return substituteExp(new SExp(exp.toString().replace(bindings.first.first.toString,bindings.first.second.toString)),bindings.cdr);

      }

   }

   /**
    * Simplify an expression
    * @type {expression}
    */
   private def simplify(exp: SExp): SExp={

      if (exp.isEmpty){

         println("empty! " + exp.toString)
         return new SExp("hello");

      } else if (exp.first.toString.toLowerCase == "or"){

         return (orEval(new SExp("(" + exp.first.toString + " " +
            (if(exp.second.isList) simplify(exp.second).toString else exp.second.toString) + " " +
            (if(exp.third.isList) simplify(exp.third).toString else exp.third.toString) + ")")));

      } else if (exp.first.toString.toLowerCase == "and"){

         return (andEval(new SExp("(" + exp.first.toString + " " +
            (if(exp.second.isList) simplify(exp.second).toString else exp.second.toString) + " " +
            (if(exp.third.isList) simplify(exp.third).toString else exp.third.toString) + ")")));

      } else if (exp.first.toString.toLowerCase == "not"){

         return notEval(exp);

      } else {

         return exp;

      }

   }

   /**
    * Evaluate an or expression
    * @type {expression}
    */
   private def orEval(exp: SExp): SExp={

      if (exp.second == exp.third){

         return exp.second;

      } else if (exp.second.toString == "t" || exp.third.toString == "t"){

         return new SExp("t");

      } else if (exp.second.toString != "nil" && exp.third.toString == "nil") {

         return exp.second;

      } else if (exp.third.toString != "nil" && exp.second.toString == "nil") {

         return exp.third;

      } else {

         return exp;

      }

   }

   /**
    * Evaluate an and expression
    * @type {expression}
    */
   private def andEval(exp: SExp): SExp={

      if (exp.second == exp.third){

         return exp.second;

      } else if (exp.second.toString == "nil" || exp.third.toString == "nil"){

         return new SExp("nil");

      } else if (exp.third.toString == "t" && exp.second.toString != "nil"){

         return exp.second;

      } else if (exp.second.toString == "t" && exp.third.toString != "nil"){

         return exp.third;

      } else{

         return exp;

      }

   }

   /**
    * Evaluate a not expression
    * @type {expression}
    */
   private def notEval(exp: SExp): SExp={

      if (exp.second.toString == "t"){

         return new SExp("nil");

      } else if (exp.second.toString == "nil"){

         return new SExp("t");

      } else {

         return exp;

      }

   }

   def evalCNF(exp: SExp): SExp={

      if (exp.isEmpty){

         return exp;

      } else if (!exp.isList){

         return exp;

      } else {

         // (or a (and b c)) -> (and (or a b) (or a c))
         // (not (or p q)) -> (or (not p) (not q))

         if (exp.first.toString.toLowerCase == "or"){

            return (distributeOr(new SExp("( or " +
               (if(exp.second.isList) evalCNF(exp.second).toString else exp.second.toString) + " " +
               (if(exp.third.isList) evalCNF(exp.third).toString else exp.third.toString) + ")")));

         } else if (exp.first.toString.toLowerCase == "not"){

            return (new SExp((if(exp.second.isList) distributeNot(exp).toString else exp.second.toString)));

         } else if (exp.second.isList){

            return new SExp("( " + exp.first.toString + " " + evalCNF(exp.second).toString + " " + exp.third.toString + ")")

         } else if (exp.third.isList){

            return new SExp("( " + exp.first.toString + " " + exp.second.toString + " " + evalCNF(exp.third).toString + ")")

         }

      }

      return exp

   }

   private def distributeOr(exp: SExp): SExp={

      // (or (and a b) (and c d)) -> (and (or a c) (or a c) (or c b) (or c d))

      if (exp.second.first.toString.toLowerCase == "and" && exp.third.first.toString.toLowerCase == "and"){

         return (new SExp("( and ( or " + exp.second.second.toString + " " + exp.third.second.toString +
            ")(or " + exp.second.second.toString + " " + exp.third.third.toString +
            ")(or " + exp.third.second.toString + " " + exp.second.third.toString +
            ")(or " + exp.third.third.toString + " " + exp.second.third.toString +"))"));


      } else if (exp.second.first.toString.toLowerCase == "and" && !exp.third.isList){

         return (new SExp("( and ( or " + exp.third.toString + " " + exp.second.second.toString +
            ")(or " + exp.third.toString + " " + exp.second.third.toString + "))"));

      } else if (exp.third.first.toString.toLowerCase == "and" && !exp.second.isList){

         return (new SExp("( and ( or " + exp.second.toString + " " + exp.third.second.toString +
            ")(or " + exp.second.toString + " " + exp.third.third.toString + "))"));

      } else {

         return exp;

      }

   }

   private def distributeNot(exp: SExp): SExp={

      if (exp.second.first.toString.toLowerCase == "or"){

         return (new SExp("( and (not " +
         (if (exp.second.second.isList) evalCNF(exp.second.second).toString else exp.second.second.toString) + ") (not " +
         exp.second.third.toString + "))"))

      } else if (exp.second.first.toString.toLowerCase == "and"){

         return (new SExp("( or (not " +
         (if (exp.second.second.isList) evalCNF(exp.second.second).toString else exp.second.second.toString) + ") (not " +
         exp.second.third.toString + "))"))

      } else {

         return exp

      }

   }

   /**
    * Evaluate any expression
    * @type {expression}
    */
   def evalExp(exp: SExp, bindings: SExp): SExp={

      return simplify(substituteExp(exp,bindings));

   }

   def runTests(){

      println(simplify(new SExp("(or x nil)")));
      println(simplify(new SExp("(or nil x)")));
      println(simplify(new SExp("(or t x)")));
      println(simplify(new SExp("(or x t)")));
      println(simplify(new SExp("(and x nil)")));
      println(simplify(new SExp("(and nil x)")));
      println(simplify(new SExp("(and x t)")));
      println(simplify(new SExp("(and t x)")));
      println(simplify(new SExp("(not nil)")));
      println(simplify(new SExp("(not t)")));
      println(simplify(new SExp("(not (and x y))")));
      println(simplify(new SExp("(not (or x y))")));

   }

}

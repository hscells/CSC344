abstract class SExpList{

   def isList : Boolean

}

case class SExp(iexp : String) extends SExpList{

   val str_exp = iexp.replace("("," ( ").replace(")"," ) ").trim.split(" +").mkString(" ");
   val exp = if (str_exp.length > 0) str_exp.dropRight(1).reverse.dropRight(1).reverse.trim else str_exp;

   override def isList: Boolean={

      if (exp.length > 0 & exp(0) == '('){

         return true;

      }

      return false;

   }

   override def toString(): String={

      return str_exp;

   }

   def length(): Int={

      return exp.length;

   }

   def getRest(start: Int): String={

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
      if (c.toString()(0) == '('){

         return new SExp(getRest(index));

      }

      return c;

   }

   def car(): SExp={

      return indexOf(0);

   }

   def first(): SExp={

      return car();

   }

   def second(): SExp={

      return new SExp("(" + exp.replaceFirst(first().toString(),"").split(" +").mkString(" ") + " )").first();

   }

   def third(): SExp={

      return new SExp("(" + exp.replaceFirst(first().toString(),"").split(" +").mkString(" ") + " )").second();

   }

   def cdr(): SExp={

      return new SExp("( " + exp.replaceFirst(first().toString(),"") + " )");

   }

}

object BoolExp{

   def main(args: Array[String]){

      var p1 = SExp("(and x (or x (and y (not z))))");
      val p2 = SExp("(and x (and x y))");

      evalExp(p1,new SExp("()"));

   }


   def substituteExp(exp: String, bindings: SExp){



   }

   /**
    * Simplify an expression
    * @type {expression}
    */
   private def simplify(exp: Array[String]){



   }

   /**
    * Evaluate an or expression
    * @type {expression}
    */
   private def orEval(exp: Array[String]){



   }

   /**
    * Evaluate an and expression
    * @type {expression}
    */
   private def andEval(exp: Array[String]){



   }

   /**
    * Evaluate a not expression
    * @type {expression}
    */
   private def notEval(exp: Array[String]){



   }

   /**
    * Evaluate any expression
    * @type {expression}
    */
   def evalExp(exp: SExp, bindings: SExp){

      println(exp);
      println()
      println(exp.first)
      println(exp.second)
      println(exp.third)
      println()
      println(exp.third.first)
      println(exp.third.second)
      println(exp.third.third)
      //substituteExp(exp.toString(),bindings);

   }

   def evalList(exp: String){



   }

}

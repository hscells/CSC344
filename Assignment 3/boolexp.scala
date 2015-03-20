class ListExpression(exp: String){

   def toArray(): Array[Unit]={

      var depth = -1;
      var last_depth = depth;
      var arr = Array[Unit]();
      for (c <- exp.replace("("," ( ").replace(")"," ) ").split(" +")){
         println(c);
         if (c == "(") {
            depth+=1;
            arr(depth) = Array[Unit]();
         } else if (c == ")") depth-=1;

         if (last_depth == depth){
            arr(depth) :+ c;
         }
         
         println(depth);
         last_depth = depth;

      }

      return arr;

   }

   override def toString(): String={

      return exp;

   }

}

object BoolExp{

   def main(args: Array[String]){

      var p1 = new ListExpression("(and (or x y) y)");
      evalExp(p1,new ListExpression("()"));

   }


   def substituteExp(exp: String, bindings: ListExpression){



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
   def evalExp(exp: ListExpression, bindings: ListExpression){

      println(exp.toArray().mkString(","));
      substituteExp(exp.toString(),bindings);

   }

   def evalList(exp: String){


   }

}

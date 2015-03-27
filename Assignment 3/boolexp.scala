import scala.collection.mutable.ArrayBuffer

class SexpParser(){

   def parse(exp: String): ArrayBuffer[ArrayBuffer[String]] ={

      var depth = -1;
      var pos = 0;
      var internal_pos = 0;

      var last_depth = depth;
      var arr = new ArrayBuffer[ArrayBuffer[String]];

      for (c <- exp.replace("("," ( ").replace(")"," ) ").trim.split(" +")){
         println(c + "," + depth)
         if (c == "(") {

            depth+= 1 + pos;
            arr += new ArrayBuffer[String];
            arr(depth) += s"{d$depth:p$internal_pos}";

         } else if (c == ")"){

            internal_pos = 0;
            depth-=1;
            pos+=1;

         } else {

            internal_pos += 1;
            arr(depth) += c;

         }

         last_depth = depth;

      }

      //println(arr);
      return arr;

   }

}


class Sexpression(exp: ArrayBuffer[ArrayBuffer[String]]){

   override def toString(): String={

      var result = "( ";
      for (l <- exp){

         result += l + " ";

      }
      result+=")";
      return result;

   }

}


object BoolExp{

   def main(args: Array[String]){

      var parser = new SexpParser()

      var p1 = new Sexpression(parser.parse("(and (not x) (not y))"));
      evalExp(p1,new Sexpression(parser.parse("()")));

   }


   def substituteExp(exp: String, bindings: Sexpression){



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
   def evalExp(exp: Sexpression, bindings: Sexpression){

      println(exp.toString());
      //substituteExp(exp.toString(),bindings);

   }

   def evalList(exp: String){



   }

}

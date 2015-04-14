val x:Int = 3
val h:String = "Hello"
val w:String = "World"
val hw:String = h + " " + w

def add(a:Int, b:Int):Int = {

   return a + b

}

val a = add(x,5)

class Animal(name: String){

   var hunger = 0

   def eatFood():Int ={

      hunger++
      return hunger;

   }

}

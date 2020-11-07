package ImplicitsAndTypeClasses

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object MagnetPattern extends App {
  // This Pattern solves the problem created by method over loading
  class P2PRequest
  class P2PResponse
  class Serializer[T]
  trait Actor{
    def receive(statusCode:Int):Int
    def receive(request:P2PRequest) :Int
    def receive(response:P2PResponse):Int
    def receive[T](message:T)(implicit serializer:Serializer[T]):Int
    def receive [T : Serializer](message:T,statusCode:Int):Int
    def receive(future : Future[P2PRequest]): Int
    // Problems with overloading
  // 1:  def receive(future : Future[P2PResponse]): Int
    // this is not compiling because of the  type erasure
    // what happens here is that type of Generics is removed at run time
    // so it will look same
    /*
    2: Lifting doesnot work for all overloads
    val receive= receive _ // compiler will be confused at this case
    3: Code duplication : bcz impl for all these methods will be more or less same
    4: type inference and default arguments
      actor.receive(default argument we cant give bcz compiler again will get confuse)
     */
  }
  // this problem can be solved implicit conversion
  trait MessageMagnet[Result]{
    def apply():Result
  }
  // in actor api we will only have this receive method here
  // here to implement overloading smartly we will pass type class instances
  // so that receive method can consume all types of messages so messages are in form of type class instances
def receive[R](magnet: MessageMagnet[R]):R= magnet.apply()
  // now how we can make sure that this apply method somehow receive other types as well
  // this we can do by implicit conversion by using value classes
  // these are type class instances which are helping to implement overloading receive method
  implicit class FromP2PRequest(request:P2PRequest) extends MessageMagnet[Int]{
    override def apply(): Int = {
      // all logic to handle P2P request
      println("request:P2PRequest:->handling p2p request")
      202
    }
  }

  implicit class FromP2PResponse(request:P2PResponse) extends MessageMagnet[Int]{
    override def apply(): Int = {
      // all logic to handle P2P response
      println("handling p2p response")
      203
    }
  }
  receive(new P2PRequest)
  receive(new P2PResponse)
  /*
  This pattern is called Magnet Pattern
  It has some Advantages and Disadvantages
  1 No more type erasure now
  Example here to demonstrate that as follows
   */

  implicit class FromRequestFuture(future : Future[P2PRequest]) extends MessageMagnet[Int]{
    override def apply(): Int = {
      // all logic to handle  P2PRequest
      println("handling Future of P2PRequest")
      204
    }
  }
  implicit class FromResponseFuture(future : Future[P2PResponse]) extends MessageMagnet[Int]{
    override def apply(): Int = {
      // all logic to handle P2P response
      println("handling Future of P2PResponse")
      205
    }
  }

  println(receive(Future(new P2PRequest)))
  println(receive(Future(new P2PResponse)))
  // 2 Lifting also Works in this case
  trait MAthLib{
    def add(x:Int) = x+1
    def add(s:String) = s.toInt +1
  }
  trait AddMagnet{
    def apply(): Int
  }
  def add(magnet:AddMagnet):Int= magnet.apply()
  implicit class AddInt(x:Int) extends AddMagnet{
    def apply(): Int= x+1
  }

  implicit class AddString(x:String) extends AddMagnet{
    def apply(): Int= x.toInt +1
  }
  val addFv: AddMagnet => Int = add _
  println(addFv.apply(1))
  // because here we have used Generic type and compiler doesn't know while lifting thta
  // when converting def to Function What will be the Function Type so it gave by default
  // MessageMagnet[Nothing] => Nothing
  val reciveFv: MessageMagnet[Nothing] => Nothing = receive _

  /*
  Drawback of this pattern
  1 - Verbose
  2 - harder to read
  3- you cant name or place default argument
  4- callByName does not work correctly
  Lets proof this
   */
  class Handler{
    def handle(s: => String) ={
      println(s)
      println(s)
    }
    // hole bunch of overloaded handle defs..................
  }
  // Lets play this with magnet pattern
  trait HandleMagnet{
    def apply(): Unit
  }

  def handle(magnet:HandleMagnet):Unit= magnet.apply()
  implicit class StringHandler(s: => String) extends HandleMagnet{
    def apply(): Unit= {
      println(s)
      println(s)
    }
  }
  def sideEffectMethod():String ={
    println("hello-scala")
    "magnet"
  }
//  handle(sideEffectMethod)
  handle {
    println("hello-scala")
    "magnet" // this will convert into new StringHandle(magnet)
  }
}

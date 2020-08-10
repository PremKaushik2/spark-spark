package lazyevaluation

import java.util.NoSuchElementException

import scala.annotation.tailrec

object CustomStreamsUsingLazyEvaluation extends App {

  abstract class MyStream[+A] {
    def head: A

    def tail: MyStream[A]

    def filter(predicate: A => Boolean): MyStream[A]

    def isEmpty: Boolean


    def #::[B >: A](element: B): MyStream[B]

    def ++[B >: A](anotherStream: MyStream[B]): MyStream[B]

    def forEach(fx: A => Unit): Unit

    def map[B](fx: A => B): MyStream[B]

    def flatMap[B](fx: A => MyStream[B]): MyStream[B]

    def take(n: Int): MyStream[A]

    def takeAsList(n: Int): List[A] = take(n).toList()

    /*
    [1,2,3].toList([])=
    [2,3].toList([1]) =
     [3].toList([2,1])
     Empty.toList([3,2,1])
     */
    @tailrec
    final def toList[B >: A](accumulator: List[B] = Nil): List[B] =
      if (this.isEmpty) accumulator.reverse
      else tail.toList(head :: accumulator)
  }


  object EmptyStream extends MyStream[Nothing] {
    override def head: Nothing = throw new NoSuchElementException

    override def tail: MyStream[Nothing] = throw new NoSuchElementException

    override def isEmpty: Boolean = true


    override def #::[B >: Nothing](element: B): MyStream[B] = new Node[B](element, this)

    override def ++[B >: Nothing](anotherStream: MyStream[B]): MyStream[B] = anotherStream

    override def forEach(fx: Nothing => Unit): Unit = ()

    override def map[B](fx: Nothing => B): MyStream[B] = this

    override def flatMap[B](fx: Nothing => MyStream[B]): MyStream[B] = this

    override def take(n: Int): MyStream[Nothing] = this

    override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

    override def takeAsList(n: Int): List[Nothing] = Nil
  }

  class Node[+A](hd: A, lazyTail: => MyStream[A]) extends MyStream[A] {
    //tl: => MyStream[A] its callbyname expression which returns MyStream[A]
    override val head: A = hd

    override lazy val tail: MyStream[A] = lazyTail // it is called callByneed

    override def isEmpty: Boolean = false

    /*
    val s=new Node(EmptyStream)
    // here s is an expression which will be evaluated later
    val prepended=1 #:: s= new Cons(1,s)
    here s will be evaluated later bcz its lazy,so s remains unevaluated when prepend operator acts
    i.e until some one access (lazy val tail) it will not evaluated
null  i.e call by need

     */

    override def #::[B >: A](element: B): MyStream[B] = new Node[B](element, this)

    /*
    (lazy val lazytail=tail ++ anotherStream) this expression will remain unevaluated because it tail is lazy and byName
    here lazytail will be evaluated later bcz its lazy,
    so s remains unevaluated when prepend operator acts
    i.e until some one access (lazy val tail) it will not evaluated
  i.e call by need
     */

    override def ++[B >: A](anotherStream: MyStream[B]): MyStream[B] =
      new Node[B](head, tail ++ anotherStream)

    override def forEach(fx: A => Unit): Unit = {
      /*
      This is termination operation this will force the
      CallBy name Expression to be evaluated
      i.e  tail.forEach(fx) When we access the lazyTail
      Expression gets evaluated
       */
      fx.apply(head)
      tail.forEach(fx)
    }

    // lazyTail=tail.map(fx) this expression will be evaluated by need,
    // So map preserves the lazy evaluation
    /*
   lazyTail= tail.map(fx) this expression will be evaluated by need,
   So map preserves the lazy evaluation
    Lets understand with this example to understand
    lazyTail represents here Lazy evaluated EmptyStream reference which is at present not evaluated
    val s=new Node(1,lazyTail)
    so output of map operation will be
    mapped= s.map(_+1)= new Node(2,lazyTail)
    But actually lazytail=s.tail.map(_+1) is an expression now
    This is actually callByNAme expression for tail(  lazyTail=s.tail.map(_+1) )
    tail.map(_+1) that still not evaluated,Hence whole expression is not evaluated till now
    it will be not be evaluated until someone called
    mapped.lazyTail or mapped.foreach i.e if someone call for next iteration then it will be evaluated
    list refrence=mapped.lazyTail but again that list tail is still not evaluated only head is evluated
    because it is eager
     */
    override def map[B](fx: A => B): MyStream[B] = new Node[B](fx.apply(head), tail.map(fx))

    override def flatMap[B](fx: A => MyStream[B]): MyStream[B] = fx.apply(head) ++ tail.flatMap(fx)

    override def take(n: Int): MyStream[A] =
      if (n <= 0) EmptyStream
      else if (n == 1) new Node[A](this.head, EmptyStream)
        // here we have a Stream [0,1,2,3] we want to take n items out of this stream
      // new Node(head,[2,3]) after this is lazy evaluated
      // upto here is eager evaluation bcz this is first call
      //i.e when again take is call recursively tail.
      // so at first iteration we will get Node[A](0, tail.take(n - 1))
      // lazyTail=tail.take(n - 1) is lazyTail so it will become like this
      // stream=Node[A](0, lazyTail)
      // so when someone call stream.lazyTail then only tail will get evaluated
      // take(n-1) as this expression is lazy so it will be evaluated
      // here tail.take(n-1) this is an expression
      //startFrom0.take(5).forEach(println)
      //Here when first time foreach is called the first tail is evaluated
      // and so on as we call foreach
      else new Node[A](head, tail.take(n - 1))

    // here first element is eagerly evaluated and remains lazy evaluated
    //tail.filter(predicate) here first is eagerly evaluated
    override def filter(predicate: A => Boolean): MyStream[A] = if (predicate.apply(head))
      new Node[A](head, tail.filter(predicate))
    else tail.filter(predicate) //preservs lazy evaluation
  }

  object MyStream {
    //Here Tail will be evaluated on need basis not like in loop via recursion
    // once we ask for tail it is evaluated
    //i.e it will not piled up the function calls in stack like we do while looping using recursion
    //MyStream.from(accumulator)(fx) thi is the callByname expression its output is tail
    // so when someone uses node.tail then it will get evaluated
    def from[A](start: A)(fx: A => A): MyStream[A] = {
      var accumulator = fx.apply(start)
      new Node[A](start, MyStream.from(accumulator)(fx))
    }
  }

  val naturals: MyStream[Int] = MyStream.from(1)(_ + 1)
  println(naturals)
  println(naturals.head)
  // now tail will get evaluated here o.e the expression callbyname
  // MyStream.from(accumulator)(fx) will be evluated
  println(naturals tail)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)
  val startFrom0: MyStream[Int] = 0 #:: naturals // naturals.#::(0) because :: is right associative
  println(startFrom0.head)
  // [0,1,2,3]
  print(startFrom0.take(5))

  //Here When We call take on o/p of map then tail is get eveluated so we are calling take for each tail
  // so what foreach will do fx.apply(head) here fx is println
  //      tail.forEach(fx) it will evaluate all tails of Stream and print the head
  // so its is printing the new head and accesing the new tail
  startFrom0.take(5).forEach(print)

  // here tail is getting evaluated until all tail is accesed by map
  //on the top of that we called take(10)
  // on that stream which is complete now  we will call take to take n elements out
  // so after take when we will call toList then toList method will access all the tails
  // of each node tail expression will get evaluated for each call
  println(startFrom0.map(_ * 2).take(10).toList())

}
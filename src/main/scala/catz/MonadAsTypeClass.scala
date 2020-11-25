package catz

import java.util.concurrent.Executors

import scala.concurrent.{ExecutionContext, Future}

object MonadAsTypeClass  extends App {

  /*
  TODO
    You can think of monads as wrappers. You just take an object and wrap it with a monad.
   It’s like wrapping a present; you take a scarf, a good book or a fancy watch,
   and wrap it with some shiny paper and a red ribbon.
   We wrap gifts with shiny paper because they look pretty.
    We wrap objects with monads because monads provide us with the following two operations:
   identity (return in Haskell, unit in Scala)
   bind (>>= in Haskell, flatMap in Scala)
   */
   //TODO:
    // The bag with unit, map and flatten makes a Monad.
   case class Sugar(weight: Double)
  case class Bag[A](content: A) {
    def map[B](f: A => B): Bag[B] = Bag(f(content))
  // ETW pattern
    def flatMap[B](f: A => Bag[B]): Bag[B] = f(content)

    def flatten = content

  }
  val sugarBag= Bag.apply(Sugar.apply(1))
  def double = (sugar: Sugar) => Bag(Sugar(sugar.weight * 2))
  val doubleSugarBag1 = sugarBag.map(sugar => double(sugar)).flatten
   // val sugarBag= Bag.apply(Sugar.apply(1))
  val doubleSugarBag2 = sugarBag.flatMap(sugar => double(sugar))

  // TODO ---------------------------------Cats Monad---------------------------------------------------------
    // TODO  Cats encodes Monad as a type class, cats.Monad
    // it takes input as higherKinded Type and perform action named pure and flatMap on it
  //This type class transforms the value A into M[A]
  //TODO : pure :  For example if A is int then it will transform the value of type int to List[Int]
  // TODO : Like Option(2)
  // TODO :It also transforms the M[A] to M[B] via flatMap
  // TODO : Here f is used to implement ETW pattern

  trait MyMonad[M[_]]{
    def pure[A](value :A):M[A]
    //ETW pattern
  def flatMap[A,B](fa:M[A])(f: A=> M[B]):M[B]
  }

//TODO : Custom Monad type-class instance for type Option

  implicit def optionMonad() =
    new MyMonad[Option] {
      // Define flatMap using Option's flatten method
      override def flatMap[A, B](fa: Option[A])(f: A => Option[B]): Option[B] =
        fa.flatMap(f)

      // Reuse this definition from Applicative.it gives the value wrapped into Monad
      override def pure[A](a: A): Option[A] = Option(a)
    }

//TODO : This type class is provided by cats library
  import cats.Monad
  import cats.instances.option._ // implicit Monad[Option] type class instance

  val monadtypeClassInstanceForOption= Monad.apply[Option]
  val option4: Option[Int] =monadtypeClassInstanceForOption.pure(4)
  val f: Int => Option[Int] = x => if (x%4==0) Some(x+1) else None
  val aTransformedMonad= monadtypeClassInstanceForOption.flatMap(option4)(f)

 import cats.instances.list._ //
  val listMonadTypeClassInstance= Monad.apply[List]
  val listMonad= listMonadTypeClassInstance.pure(4)
  listMonadTypeClassInstance.flatMap(List(1,2,3))(x =>List(x*2))
  val transformedListMonad= listMonad.flatMap(x => List.apply(x,x+1))

  // TODO Monad of Future
  import cats.instances.future._
  implicit val ec: ExecutionContext = ExecutionContext.
    fromExecutorService(Executors.newFixedThreadPool(2))
  val MonadTypeClassInstanceFuture= Monad.apply[Future]
  val futureMonad: Future[Int] = MonadTypeClassInstanceFuture.pure(43)
  /*
  def flatMap[S](f: T => Future[S])(implicit executor: ExecutionContext): Future[S] = {
    import impl.Promise.DefaultPromise
    val p = new DefaultPromise[S]()
    onComplete {
      case f: Failure[_] => p complete f.asInstanceOf[Failure[S]]
      case Success(v) => try f(v) match {
        // If possible, link DefaultPromises to avoid space leaks
        case dp: DefaultPromise[_] => dp.asInstanceOf[DefaultPromise[S]].linkRootOf(p)
        case fut => fut.onComplete(p.complete)(internalExecutor)
      } catch { case NonFatal(t) => p failure t }
    }
    p.future
  }
   */
  val transFormedFutureMonad= futureMonad.flatMap(x=>Future.apply(x+1))

  // TODO : USe of Monad type class is to make General API or rest ENd point
  // TODO : Which Excepts Any kind of Monad i.e Higher Kinded type
  def getPairsList(numbers:List[Int], chars:List[Char]):List[(Int,Char)]={
    numbers.
      flatMap(n => chars.map(char => (n,char)))
  }
  //TODO : Lets Generalize it any kind of monads

  def getPairs[M[_],A,B](bag1:M[A],bag2:M[B])(implicit monad:Monad[M]):M[(A,B)] = {
    monad.flatMap(bag1)(a=> monad.map(bag2)(b=> (a,b)))
  }
  val numbersList= List(1,2,3,4)
  val charsList: List[Char] =List('a','b','c')
  import cats.instances.list._
  println(getPairs(numbersList,charsList))
  val numberOption= Option(2)
  val charOption=Option('c')
  import cats.instances.option._
  println(getPairs(numberOption,charOption))
}

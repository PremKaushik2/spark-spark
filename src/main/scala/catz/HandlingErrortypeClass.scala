package catz

import java.util.concurrent.Executors

import cats.data.Validated
import cats.{Applicative, ApplicativeError, Monad, MonadError}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/*
A monad that also allows you to raise and or handle an error value.
This type class allows one to abstract over error-handling monads.
 */
object HandlingErrortypeClass {

//TODO : this type class takes two arguments M[_] which is higher-kinded type
  // TODO and another one is Error type E becuase this monad also handles the error
  //MonadError  is  A monad that also allows you to raise and or handle an error value.
  trait MyMonadError[M[_],E] extends Monad[M] {
    // this type class has fundamental method raiseError to handle the error
    def raiseError[A] (e:E):M[A]
  }
  /*
    implicit def catsStdInstancesForEither[A]: MonadError[Either[A, *], A] with Traverse[Either[A, *]] =
    new MonadError[Either[A, *], A]  {

     def raiseError[B](e: A): Either[A, B] = Left(e)
      def pure[B](b: B): Either[A, B] = Right(b)

      def handleErrorWith[B](fea: Either[A, B])(f: A => Either[A, B]): Either[A, B] =
        fea match {
          case Left(e)      => f(e)
          case r @ Right(_) => r
        }

   */
  import cats.implicits.catsStdInstancesForEither
  type ErrorOr[A] = Either[String,A]
  val typeClassinstanceMonadError: MonadError[ErrorOr, String] =
    MonadError.apply[ErrorOr,String]
// Lets test monad error type class
  val success: ErrorOr[Int] = typeClassinstanceMonadError.pure[Int](2) // it will give Right[Int]
  // TODO to raise error MonadError or Monad gives this method while processing
  val failure: ErrorOr[Int] = typeClassinstanceMonadError.
                        raiseError[Int]("Exception-Occurred!!!!") // it will give Left[String]

  // TODO: tro recover from error type class gives additional method called handleError
  //def handleError[A](fa: F[A])(f: E => A): F[A]
 val handledError: ErrorOr[Int] = typeClassinstanceMonadError.
                handleError(failure){
                  case "badness" => 44
                  case _ => 89
                }

  val handleErrorWith: ErrorOr[Int] = typeClassinstanceMonadError
    .handleErrorWith(failure){
      case "badness" => typeClassinstanceMonadError.pure(44)
      case _ => Left("Inavlid Result")
    }
  //filter Turns a successful value into an error if it does not satisfy a given predicate.
  val filteredSuccess: ErrorOr[Int] = typeClassinstanceMonadError
    .ensure(success)("Number-too-Small-error")(_>100)

  //Todo MonadError type class instances for Try
  import cats.instances.try_._//implicit MonadError[Try,E] where E= throwable
  val exception= new RuntimeException("Really bad ")
  val MonaderrortypeclassinstanceforTry: MonadError[Try, Throwable] =
    MonadError.apply[Try,Throwable]
  /*
  type class instance
  implicit object MError extends MonadError[Try,Throwable]
   def handleErrorWith[A](ta: Try[A])(f: Throwable => Try[A]): Try[A] =
        ta.recoverWith { case t => f(t) }

      def raiseError[A](e: Throwable): Try[A] = Failure(e)


   */
  // val exception= new RuntimeException("Really bad ")
  val pureValue: Try[Int] = MonaderrortypeclassinstanceforTry.pure[Int](2)
  val pureException: Try[Int] = MonaderrortypeclassinstanceforTry.raiseError[Int](exception) // Failure(e)

  //TODO : Monad Error Type class instance for Future

  implicit val ec: ExecutionContext = ExecutionContext.
    fromExecutorService(Executors.newFixedThreadPool(2))
  import cats.instances.future._

  /*

  TODO implicit type class instance for MonadError[Future,Throwable]
  TODO
    implicit object ErrorFuture extends MonadError[Future, Throwable]{
   def pure[A](x: A): Future[A] = Future.successful(x)
     def handleErrorWith[A](fea: Future[A])(f: Throwable => Future[A]): Future[A] = fea.recoverWith { case t => f(t) }
       def raiseError[A](e: Throwable): Future[A] = Future.failed(e)
      override def handleError[A](fea: Future[A])(f: Throwable => A): Future[A] = fea.recover { case t => f(t) }
      }
   */
  /*
  TODO
   Creates an already completed Future with the specified exception.
   Returns:
   the newly created Future instance
   def raiseError[A](e: Throwable): Future[A] = Future.failed(e)

--------------------------------------------------------------------------------------------------------------------------------
Use Future.apply() or simply Future() (i.e., Future block): In the situations,
 where something to be done asynchronously that can complete sometime in future
 and may deal with some time consuming operations such as network calls, database operations communicate
  with one or many other services, processing huge data by consuming multiple cores and etc.

Use Future.successful: When a literal or already computed value to be passed back as a successful future response.
Use Future.failed: When a known and literal exception to be thrown back without performing any further actions in the future.
--------------------------------------------------------------------------------------------------------------------------------
   TODO
    Returns:
    the newly created Promise object
    def failed[T](exception: Throwable): Future[T] = Promise.failed(exception).future
    def failed[T](exception: Throwable): Promise[T] = fromTry(Failure(exception))
    def fromTry[T](result: Try[T]): Promise[T] = impl.Promise.KeptPromise.apply[T](result)

TODO
   def apply[T](result: Try[T]): scala.concurrent.Promise[T] =
      resolveTry(result) match {
        case s @ Success(_) => new Successful(s)
        case f @ Failure(_) => new Failed(f)
      }
  }
  final class Failed[T](val result: Failure[T]) extends Kept[T]
   */
  val monadErrortypeclassInstanceForFuture: MonadError[Future, Throwable] =
    MonadError[Future,Throwable]

   val futureOfException: Future[Int] = monadErrortypeclassInstanceForFuture.
     raiseError[Int](exception)

//TODO Monad Error Type class instance for Validated

  import cats.instances.list._//implicit Semigroup[List] because validated required semigroup
  //import cats.implicits.catsKernelStdMonoidForList
  /*
  An Applicative that also allows you to raise and or handle an error value.
 *
 * This type class allows one to abstract over error-handling applicatives.
   */
  /*
  TODO type class instance
     implicit def catsDataApplicativeErrorForValidated[E](implicit E: Semigroup[E]): ApplicativeError[Validated[E, *], E] =
    new ValidatedApplicative[E] with ApplicativeError[Validated[E, *], E] {

      TODO
        def handleErrorWith[A](fa: Validated[E, A])(f: E => Validated[E, A]): Validated[E, A] =
        fa match {
          case Validated.Invalid(e)   => f(e)
          case v @ Validated.Valid(_) => v
        }
        def raiseError[A](e: E): Validated[E, A] = Validated.Invalid(e)

      TODO
        def recover[A](fa: F[A])(pf: PartialFunction[E, A]): F[A] =
       handleErrorWith(fa)(e => (pf.andThen(pure _)).applyOrElse(e, raiseError _))
    }
   */
  type ErrorsOR[T] = Validated[List[String],T]
  val applicativeErrorTypeclass: ApplicativeError[ErrorsOR, List[String]] =
    ApplicativeError.apply[ErrorsOR, List[String]]

    /*
   TODO
     Actually the diffrence between ApplicativeError and MonadError is that raiseError is fundamental method of
     Applicative error not the monad error
   */

    trait MyApplicativeError[M[_], E] extends Applicative[M] {
      // this type class has fundamental method raiseError
      def raiseError[A](e: E): M[A]

      //second fundamental method of ApplicativeError is
      def handleErrorWith[A](fa: M[A])(f: E => M[A]): M[A]

      // third fundamental method of   ApplicativeError is
      def handleError[A](fa: M[A])(f: E => A): M[A] = handleErrorWith(fa)(e => pure(f(e)))

      // and at last the pure method from Applicative but this is auxiallary method
      def pure[A](x: A): M[A]
    }
// this is the exact structure of MonadError type class
    trait MyMonadError1[M[_], E] extends MyApplicativeError[M,E] with  Monad[M] {
      // this type class has fundamental method raiseError
      def ensure[A](fa: M[A])(error: E)(predicate: A => Boolean): M[A]
    }
    // TODO : Extension methods for ApplicativeError and MonadError
    import cats.syntax.applicative._ // pure is here
    /*
      TODO
          implicit class ApplicativeIdOps[A](private val a: A) extends AnyVal {
          def pure[F[_]](implicit F: Applicative[F]): F[A] = F.pure(a)
              }
              i.e it will call Validated pure  def pure[A](a: A): Validated[E, A] = Validated.valid(a)
     */
    import cats.syntax.applicativeError._// will import raiseError ,handleError , handleErrorWith
  //type ErrorsOR[T] = Validated[List[String],T]
    val extendedSuccess: ErrorsOR[Int] = 42.pure[ErrorsOR] // it requires a implicit ApplicativeError[Errorsor,List[String]]


  /*
    TODO
      implicit class ApplicativeErrorIdOps[E](private val e: E) extends AnyVal {
      def raiseError[F[_], A](implicit F: ApplicativeError[F, _ >: E]): F[A] =
       F.raiseError(e)
       and that inturn will call further Validated
        def raiseError[A](e: E): Validated[E, A] = Validated.Invalid(e)

    }
}
   */
  // TODO we are raising error for validated which is List Validated[List[String],T]
    val extendedError: ErrorsOR[Int] = List("ValidationFailed-For this Validated").raiseError[ErrorsOR,Int]// will return List
// extendedError = Validated[List[String],Int]
  /*
    TODO
     implicit  class ApplicativeErrorOps[F[_], E, A](private val fa: F[A]) extends AnyVal {
     def handleError(f: E => A)(implicit F: ApplicativeError[F, E]): F[A] =
      F.handleError(fa)(f)
TODO
     def recover(pf: PartialFunction[E, A])(implicit F: ApplicativeError[F, E]): F[A] =
      F.recover(fa)(pf)

      type class instance recover

        TODO
          def recover[A](fa: F[A])(pf: PartialFunction[E, A]): F[A] =
          handleErrorWith(fa)(e => (pf(e).andThen(pure _)).applyOrElse(e, raiseError _))

           def handleErrorWith[A](fa: F[A])(f: E => F[A]): F[A]
   */
    val extendedRecover: ErrorsOR[Int] = extendedError.recover{
      case _ => 43
    }

   import cats.syntax.monadError._ // ensure
  /*
    implicit  class MonadErrorOps[F[_], E, A](private val fa: F[A]) extends AnyVal {
    def ensure(error: => E)(predicate: A => Boolean)(implicit F: MonadError[F, E]): F[A] =
    F.ensure(fa)(error)(predicate)

   */
  //val success: ErrorOr[Int] = typeClassinstanceMonadError.pure(2)
  //type ErrorOr[A] = Either[String,A]
  val testedSuccess: ErrorOr[Int] = success.ensure("Exception")(_>100)//Left("Exception")
  def main(args: Array[String]): Unit = {
    failure.foreach(println(_))
  }

}

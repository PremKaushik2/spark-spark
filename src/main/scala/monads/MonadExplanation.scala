package monads

object MonadExplanation  extends App{
/*
Monads are kind of Abstract type which have some fundamental operations
Like mentioned below
All operations must satisfy the monad laws
left identity
if you built a basic monad from an element and you flatmap
it should give you the function you used
in flatMap
here unit(x) is equal MonadCompanionObject.apply()
unit(x).flatmap(fx) ==f(x)
------------------------------------------------------------------
val instance=Some(x)
right identity
amonadInstance.flatMap(unit)== amonadInstance
if you have a monadicInstance and you flat map it using the unit function then it should give
a same amonadInstance
------------------------------------------------------------------------------------------
3rd law is associativity
m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))
IF you have monadeInstance and you flatMap it with two functions (f and g)
in a sequence i.e in cascading manner like shown here m.flatMap(f).flatMap(g)
 then it should give you the same thing as
when we do flatmapping the monade instance with composite function
that does f.flatMap(g) for every element
imp point is here composite function= f.flatMap(g)
 */
  trait MonadTemplate[A] {

    def unit (value :A): MonadTemplate[A]// it is like apply method
    def flatMap[B](fx: A=>MonadTemplate[B]): MonadTemplate[B]
  }

// Proof  of all three laws using List
  /*
  First LAw : left identity

  List.apply(x).flatMap(f)= f(x) ++ Nil.flatMap(f)
  that will be equal to
  f(x) ++ Nil.flatMap(f)
  where Nil.flatMap(f)=Nil
  then final output is f(x) hence proved


  ---------------------------------------------------
  Second Law right identity
  list.flatMap(x => List(x))= list
  hence proved


  ThirdLaw: associativity
  [a,b,c].flatMap(f).flatMap(g) =
  when we decompose it will look like this taking the refrence of flatMap
   flatmapImpl :
      fx.apply(this.head) ++ this.tail.flatMap(fx)

  (f(a) ++ f(b) ++ f(c)).flatMap(g)
  but if we further decompose it
  f(a).flatMap(g) ++ f(b).flatMap(g) ++ f(c).flatMap(g)
  further In the whole above expression because all three components are similar we can write that
  it is like now over the list we apply flatmap
  and in flatmap we apply the function on each element
  and as we know that f returns list so we applied flatMap over it
  I mean it is same Like  f(a).flatMap(g) ++ f(b).flatMap(g) ++ f(c).flatMap(g)
  but much short and nicer way to write
  [a,b,c].flatMap(f(_).flatMap(g))
  but this is we also    can write  as well like that
  [a,b,c].flatMap(x=> f(x).flatMap(g))
  hence proved because as we know the composing the functions looks like that
  x => function1(function2(x))
  here x=> f(x).flatMap(g) is equal to function2(x)
  --------------------------------------------------------------------------------------------
  Lets Proof all rules using Options
  Option(x).flatMap(f)= f(x)
  Some(x).flatMap(f) = f.apply(x)
 here  Some(x).flatMap(f) this operation will result Some(x)
 and f.apply(x)= let say f=  x=> Some(x)
  hence proved

  2 Law of
  val opt=Some(5)
  opt.flatMap(x=>Option(x)) = opt
  Some(v).flatMAp(x=>xOption(x)) which will be equal to Option(v)
  and which in turn equal to Some(v) that value we started with hence this points to same refrence
  we started with i.e opt
  Hence proved
  -------------------------------------------------------------------------
  3 Law
  If we want to compose an option by flatMapping it with two functions
  in sequence that should be the same as flatMapping the same option with composite function =
   x=> f.apply(x).flatMap(g)
   here we have flatMapped the option with composing function x=> f.apply(x).flatMap(g)
  which for the x element contain within the option it apply f(x).flatMap(g)

  o.flatMap(f).flatMap(g) == o.flatMap( x=> f.apply(x).flatMap(g))

  Composing brief understanding
  function1 compose function2
  or h(x) = f(g(x)).
  or it is like that
  val fab : Option[String] = for{
  a <- fa
  b <- fb(a)
} yield ab

so lets proof it
Some(v).flatMap(f).flatMAp(g) = f.apply(v).flatMap(g)
here as we know that
Some(v).flatMap(f) = f.apply(v) // thats what Some case class say in its impl
so  we can write this way
Some(v).flatMap(f).flatMAp(g) = f.apply(v).flatMap(g)
here Some(v).flatMap(f)= f.apply(v)
now here (f.apply(v).flatMap(g)) it is composite function to Option beacuse
here we have merged two functions

now lets evaluate  it from Right hand side and try to proof both evaluate same ans
We can write this
 x => f.apply(x).flatMap(g) == f.apply(v).flatMap(g)
because both are same thing in terms that both are composite function and both does same work
because we are applying the composite function on value contained in option by flatMapping it
Some(v).flatMap(x => f.apply(x).flatMap(g)) = f.apply(v).flatMap(g)
so we can say now
Some(v).flatMap(compositeFunction)= compositeFunction(x)
as per standard definition of option case class

Hence both equations on RHS and LHS are reulting same vaue
i.e the compositeFunction= x => f.apply(x).flatMap(g) alias  f.apply(v).flatMap(g)

Hence proved


   */



}

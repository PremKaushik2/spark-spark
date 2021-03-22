package variance

object VarianceUnderstanding {

  /*
  TODO
    If A is a subtype of B, then should Thing[A] be a subtype of Thing[B]?
    This is the variance question.
TODO
  If our Thing is a collection, such as a list, the answer is yes:
  if Dogs are Animals, then a collection of Dogs is also a collection of Animals.
 So from a substitution perspective, we could assign a collection of Dogs to a collection of Animals:

val dogs: MyList[Animal] = new MyList[Dog]
   */
  class MyList[+T](val animal: T) {

  }

  class Animal

  class Dog extends Animal

  class Cat extends Animal

  val dogs: MyList[Animal] = new MyList[Dog](new Dog)

  /*
TODO
    In other words, if A is a subtype of B, then Thing[B] is a subtype of Thing[A].
    This is called contravariant, and we mark it with a - sign at the class declaration.
    For the Animals use case, a good contravariant example would be a Vet:
    A Vet[Animal] is a proper replacement for a Vet[Dog],
    because a Vet can treat any Animal, and so she/he can treat a Dog too.
    Golden Rule
    Covariant = retrieves or produces T.
    Contravariant = acts on, or consumes T. //alias type class pattern
   */
  class Vet[-T]

  val lassiesVet: Vet[Dog] = new Vet[Animal]

  /*
  TODO
       The Variance Positions ProblemPermalink
     Now that we’ve found that a collection should be covariant,
      we get to work and write our own list, because of course we can:
  TODO
    Now that we’ve found that a collection should be covariant,
     we get to work and write our own list, because of course we can:

  TODO
     We start from the very basics. But before we even write a proper subtype for MyList, we hit a wall:
     Error: covariant type T occurs in contravariant position in type T of value elem
     in add method
   */
  abstract class VList[+T] {
    def head: T

    def tail: MyList[T]

    //def add(elem: T): MyList[T]
  }

  /*
  TODO
       Types of vals Are in Covariant PositionPermalink
       Let’s say we had a Vet. As discussed before, a Vet should be a contravariant type.
       Let’s also imagine this vet had a favorite animal val field, of the same type she can treat:
   */



  //TODO Assuming this was possible, then the following code would compile:
  /*
  TODO
       See any trouble here?
     lassiesVet is declared as Vet[Dog];
      as per contravariance we can assign a Vet[Animal]
    theVet is a Vet[Animal] (typed correctly),
    but is constructed with a Cat (a legit Animal)
   lassiesVet.favoriteAnimal is supposed to be a Dog per the generic type declared,
    but it’s really a Cat per its construction
   No-no. This is a type conflict.
   A sound type checker will not compile this code.
   We say that the types of val fields are in covariant position,
    and this is what the compiler will show you

TODO
   class PetBox[-T](val favoriteAnimal: T)
   Error : ->  contravariant type T occurs in covariant position in type T of value favoriteAnimal
   val garfield = new Cat
   val theVet: PetBox[Animal] = new PetBox[Animal](garfield)
   def petApi(petBox: PetBox[Dog]) = {
    // here petBox of Dog is having the Cat
    val petBox: PetBox[Dog] = theVet // type conflict if we access that pet it will cat
   }
   val lassiesVet: PetBox[Dog] = theVet
    We say that the types of val fields are in covariant position,
   */
/*
TODO
    Types of vars Are Also in Contravariant PositionPermalink
   If our contravariant Vet example had a var field,
   we’d have the exact same problem right at initialization.
  Therefore, types of var members are in covariant position as well.
   Because vars can be reassigned, they come with an extra restriction.
  Let’s think about Option[T] for a second. Should it be covariant or contravariant?
  Spoiler: it’s covariant. If Dog extends Animal then Option[Dog] should be a subtype of Option[Animal].
 Pick your favorite reason (both are true):
 If a dog is an animal, then a maybe-dog is also a maybe-animal.
 Think of an Option as a list with at most one element. If a list is covariant, then an option is covariant.
 Now, imagine that (for whatever reason) we had a mutable version of an option.
 Let’s call it MutableOption[+T] with a subtype containing a var member, e.g. class MutableSome[+T](var contents: T)
 */
  /*
TODO
  The first line is perfectly legal: because MutableSome is covariant,
   we can assign a maybe-dog on the right-hand side of the assignment.
  Now, because we declared maybeAnimal to be a MutableSome[Animal],
 the compiler would allow us to change the contents variable to another kind of Animal.
 Because a Cat is an Animal, then a Cat is a legal value to use,
 which would also blow up the type guarantee.
 That is because the original MutableSome[Dog]
 we used on the first line also comes with a guarantee that the contents are of type Dog, but we’ve just ruined it.
 Therefore, we say that types of var fields are in contravariant position.
 But wait, didn’t we say that they were in covariant position as per the earlier argument?
  Yes! That’s true as well.
   The types of var fields are in covariant AND contravariant position.
  The only way they would work is if the generic type were invariant,
  which eliminates the problem (same type argument everywhere, no need for substitution).
   */
 // class MutableOption[+T]
  //class MutableSome[+T](var contents: T) extends MutableOption[T]

 // val maybeAnimal: MutableSome[Animal] = new MutableSome[Dog](new Dog)
 // def mutableApi(mutable: MutableSome[Animal]) = {
  //  mutable.contents = new Cat // type conflict because mutable is of Dog type we are assigning Cat
  //}
  //mutableApi(new MutableSome[Dog](new Dog))

/*
TODO
     Types of Method Arguments Are in Contravariant PositionPermalink
   This says it all. How do we prove it? We try the reverse and see how it’s wrong.
  Let’s take the (now) classical example of a list. A list is covariant, we know that. So what would be wrong with

abstract class MyList[+T] {
  def head: T
  def tail: MyList[T]
  def add(elem: T): MyList[T]
}
 */
/*
TODO
  Let’s imagine this code compiled.
  Let’s also imagine we gave some dummy implementations to these methods,
   as the implementation doesn’t matter, only their signature. As per the covariance declaration, we could say
TODO
 val animals: MyList[Animal] = new MyList[Cat]
 val moreAnimals = animals.add(new Dog)
 class ContraList[+T] {
  def head: T
  def tail: ContraList[T]
  def add(elem: T): ContraList[T]
}
TODO
  val animals: ContraList[Animal] = new ContraList[Cat]
  def ContraApi(list:ContraList[Animal])={
    list.add(new Cat)
  }
  ContraApi(new ContraList[Dog])
  val moreAnimals = animals.add(new Dog) // Wrong adding Dog into list of cats

TODO
  which again breaks the type guarantees.
 Line 2 allows us to add an Animal to animals,
 and a Dog is an Animal, so we can. However, at line 1
 , we are using a MyList[Cat],
 which bears the guarantee that the list contains just cats,
  and we’ve just added a Dog to it, which breaks the type checker.
   Therefore, we say that the type of method arguments is in contravariant position.
 */
class ContraVet[-T] {
  def heal(animal: T): Boolean = true // implementation unimportant
}
   def CVetApi(vet:ContraVet[Dog])={
     vet.heal(new Dog) // correct; it's a Dog, which is also an Animal; Vet[Animal] can heal any Animal
     //vet.heal(new Cat) // legitimate error: Lassie's vet heals Dogs, so Cats aren't allowed
   }
  val catVet: ContraVet[Dog] = new ContraVet[Animal]
  CVetApi(catVet)

/*
TODO
  Method Return Types Are in Covariant PositionPermalink
 Again, we can prove the title by trying the reverse.
 Assume a contravariant type (again, our favorite Vet) and write a method returning a T:

abstract class Vet[-T] {
  def rescueAnimal(): T
}
 */
/*
TODO
   abstract class CovVet[-T] {
    def rescueAnimal(): T
  }
  val covet: CovVet[Animal] = new CovVet[Animal] {
    override def rescueAnimal(): Animal = new Cat // because cat is animal
  }
  def CovVetApi(vet:CovVet[Dog])={
    val rescuedDog: Dog = vet.rescueAnimal // This code will blow expecting Dog but got Cat
  }
  CovVetApi(covet)

 TODO
  Again, breaking the type guarantees:
  we use a Vet[Animal] whose method returns a Cat,
  so when we finally invoke the CovVetApi method on covet (which is declared as Vet[Dog]),
  the type checker expects a Dog but we get a Cat per its real implementation! Not funny.
  Therefore, we say method return types are in covariant position. A covariant example works fine for this case.
 */
/*
TODO
    How to Solve the Variance Positions ProblemPermalink
  We proved that a covariant list cannot have an add(elem: T) method
   because it breaks type guarantees. However, does that forbid us from ever adding an element to a list?!
 Hell, no.
 Let’s take this back to first principles.
  We said that we can’t add an add(elem: T) method to a list, because otherwise we could write
  What if our add(elem: T): MyList[T]
  received an argument of a different type and
  returned a result of a different type? Allow me to make a suggestion:
  In other words, if we happen to add an element of a different type than the original list,
  we’ll let the compiler infer the lowest type S
   which describes both the element being added
   AND the existing elements of the list. As a result,
   we’ll obtain a MyList[S]. In our cats vs dogs example,
   if we add a cat to a list of dogs, we’ll obtain a list of animals.
   If we add a flower to it, we’ll obtain a list of life forms.
    If we add a number, we’ll obtain a list of Any. You get the idea.

This is how we solve the cryptic “covariant type T occurs in contravariant position”:
 */
 class MyListC[+T]{
  def head: T = ???
  def tail: MyListC[T] = ???
  def add[S>:T](elem: S): MyListC[S] = new MyListC[S]
}

  //Similarly, we can solve the opposite “contravariant type occurs in covariant position” with the opposite type bound:

   class VetContra[-T] {
    def rescueAnimal[S <: T](): S = ???
  }
  //Assuming we can actually implement this method in such general terms,
  // the compiler would be happy: we can force the Vet to return an instance of a particular type:

  val vetContra: VetContra[Dog] = new VetContra[Animal]
  def contraApi(vet:VetContra[Dog]) ={
    val rescuedDog: Dog = vet.rescueAnimal[Dog] // type checking passes now this code will not blow
  }
 contraApi(vetContra)
}

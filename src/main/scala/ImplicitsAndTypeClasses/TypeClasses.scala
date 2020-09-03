package ImplicitsAndTypeClasses

object TypeClasses  extends App {
// DDesign to Serialize domain objects to render html
  trait HtmlSerializable{
    def serializeToHtml:String
  }
  // This is the POJO or BO  we want to map that HTML object
case class User(name:String, age:Int , email:String) extends HtmlSerializable {
  override def serializeToHtml: String = s"<div> $name {$age} <a href = $email /> </div>"
}
  val user=User("Prem", 34, "prem.kaushik@outlook.com")
  println(user.serializeToHtml)
  /*
  Disadvantages of this design
  1: This only works for the Types we write i.e it is per implementation specific
  2: This one implementation out of quite a number
   */

  // Design number 2
  /*
  In this design we will pattern match the Instances
  so that it can be reused for other Types of instances
  DisAdvantages:
  We lost the type safety
  No use of Generic i.e this impl is not generic
  We need to modify the code everytime we add new match
   */

  object HtmlSerializablePM{
    def serializableToHtml(value :Any) = value match{
      case User(n,a,e) =>
      case _ =>
    }
  }
  // Better DEsign than last two
  /*
  Advantages with this approach
  We can define the  serializers for the generic types
  We can define Multiple Serializers for the same type as well
  Here in scala this trait HtmlSerializer[T] is called Type Class
  because it defines the certain operations which can be applied
  to Type passed to it
  And these all implementations of this trait are type class instances
  that's why we make then singleton object
   */
  trait HtmlSerliazer[T]{
    def serialize(value : T):String
  }
  object UserSerializer extends HtmlSerliazer[User] {
    override def serialize(user: User): String =
        s"<div> $user.name {$user.age yo} <a href = $user.email /> </div>"
  }
  import java.util.Date
  object DateSerializer extends HtmlSerliazer[Date] {
    override def serialize(value: Date): String =  s"<div>${value.toString}</div>"
  }
  // these are those users who are not logged in
  object PartialUserSerializer extends HtmlSerliazer[User] {
    override def serialize(user: User): String =
      s"<div> $user.name /> </div>"
  }
  println(PartialUserSerializer.serialize(user))
}
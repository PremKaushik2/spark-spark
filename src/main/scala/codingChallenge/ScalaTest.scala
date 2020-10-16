package codingChallenge

object ScalaTest  extends App {



  import java.time.LocalDate

  // sunroofType is None if vehicle doesn't have one, model is not case sensitive
  case class Vehicle(
                      speed: Int,
                      model: String,
                      numberOfWheels: Int,
                      sunroofType: Option[String]
                    )
  case class ModelDetails(size: Int, doors: Int)

  // Do not extend, we only have details for some models
  val modelToDetails = Map[String, ModelDetails](
    "HONDA" -> ModelDetails(100, 4),
    "TOYOTA" -> ModelDetails(80, 6),
    "FIAT" -> ModelDetails(120, 2),
    "JEEP" -> ModelDetails(200, 8)
  )

  val vehiclesUK = List(
    Vehicle(50, "Honda", 2, Some("AUTO")),
    Vehicle(20, "Toyota", 5, Some("MANUAL")),
    Vehicle(100, "BMW", 4, None)
  )
  val vehicleUS = List(
    Vehicle(60, "Merc", 2, Some("MANUAL")),
    Vehicle(40, "Fiat", 3, Some("MANUAL")),
    Vehicle(10, "Civic", 4, Some("AUTO")),
    Vehicle(0, "Punto", 0, Some("MANUAL"))
  )
  val vehiclesEU = List(
    Vehicle(70, "Jeep", 1, Some("AUTO")),
    Vehicle(25, "Fiat", 6, None),
    Vehicle(15, "Jaguar", 3, None),
    Vehicle(35, "Punto", 2, None)
  )

  // given a list of numbers return the sum of them all
  def sum(ints: List[Int]): Int = {
     ints.sum
  }

  // given a list of numbers return only the even ones
  def onlyEvenNumbers(ints: List[Int]): List[Int] = {
    ints.filter(_%2==0)
  }

  // generate a list of dates from the start date forward to the end of the range
  def generateListOfDates(startDate: LocalDate, range: Int): List[String] = {
    val endDate: LocalDate = startDate.plusDays(range)
/*
If you do need a lazily-evaluated list, then Stream is appropriate.
 I suggest using iterate instead of cons in that case.
 */

    def dayIterator(start: LocalDate, end: LocalDate) = {
      Iterator.iterate(start)(_ plusDays 1) takeWhile (_ isBefore end)
     /*
     val itr=Iterator.iterate(startDate) (startDate => startDate.plusDays(1) )
     val itr1: Iterator[LocalDate] = itr.dropWhile(date => date.equals(endDate))
      itr1

      */
    }
    dayIterator(startDate,endDate).toList.map(_.toString)

  }

  // filter a list of vehicles by the given sunroofType
  def filterVehiclesBySunroof(
                               inputVehicles: List[Vehicle],
                               sunroofType: String
                             ): List[Vehicle] = {
    inputVehicles.filter(_.equals(sunroofType))
  }

  // filter a list of vehicles by the given boundries, exclusively,
  // if boundary not given ignore
  def filterVehiclesBySpeed(
                             inputVehicles: List[Vehicle],
                             lowerBoundary: Option[Int],
                             upperBoundary: Option[Int]
                           ): List[Vehicle] = {
    for{
      vehicle <- inputVehicles
      ub <- upperBoundary
      lb <- lowerBoundary
      if ( vehicle.speed > lb && vehicle.speed<=ub )
    } yield vehicle

  }

  // generate list of vehicles with model capitalised
  def generateVehiclesCapitalised(
                                   inputVehicles: List[Vehicle]
                                 ): List[Vehicle] = {
    inputVehicles
  }

  // returns a string based on the values in the provided Vehicle case class
  // check test for further info
  def personalisedMessage(vehicle: Vehicle): String = {
    // Certain car manufacturers have asked for a specific message to be applied for their cars
    // Honda would like to display "Hello super fast Honda driver, zoom zoom!" for their fast(speed > 60) Honda drivers
    // Fiat would like to display "If it's a nice day, don't forget to roll down your fancy sunroof" for their Fiat drivers with
    // MANUAL sunroofs
    // For anything not specified above display a simple "Have a good day driver"
    // Add in some more test cases if you can!
     vehicle.model match {
       case "Honda" => "Super Honda"
       case  "Fiat"  => "If it's a nice day, don't forget to roll down your fancy sunroof"
       case _ => "Have a good day driver"
     }
  }

  // returns a list of models and the total number of wheels for those models,
  // given the input vehicles
  // it's a bit bad, make it better, only works for certain models?
  // mutable state? correct count?
  def totalNumberWheelsByModel(
                                inputVehicles: List[Vehicle]
                              ): List[(String, Int)] = {


    val result: Seq[(String, Int)] =inputVehicles.map(
      vehicle => (vehicle.model, vehicle.numberOfWheels))

        /*
    var toyotaCount = 0
    var hondaCount = 0
    for (
      vehicle <- inputVehicles
    ) {
      if (vehicle.model == "TOYOTA") toyotaCount = toyotaCount + 1
      if (vehicle.model == "HONDA") hondaCount = hondaCount + 1
    }

    List(("HONDA", hondaCount), ("TOYOTA", toyotaCount))

         */
  result.toList

  }

  // return a vehicle model details ModelDetails or None if the details cannot be found
  def getSingleVehicleModelDetails(
                                    inputVehicle: Vehicle
                                  ): Option[ModelDetails] = {
    val result: Option[ModelDetails] =modelToDetails.get(inputVehicle.model)
    result
  }

  // returns a list of model details given a list of vehicles,
  // we only have details for some models, do not extend the map,
  // if a vehicle model is not in the list do not include it in
  // the return list
  def getVehicleListModelDetails(
                                  inputVehicles: List[Vehicle]
                                ): List[ModelDetails] = {
    inputVehicles.filter(vehicle => modelToDetails.
          contains(vehicle.model)).
      map( vehicle => modelToDetails(vehicle.model))
  }


  //
  // TEST FUNCTIONS BELOW
  //

  // add values in list together and return
  val ints = List(1, 2, 3, 4, 5)
  assert(sum(ints) == 15)
  println("SUM TESTS PASSED")

  // filter list to only even numbers
  val filterInts = List(1, 2, 3, 4, 5)
  assert(onlyEvenNumbers(filterInts) == List(2, 4))
  println("EVEN FILTER TESTS PASSED")

  // from start date add next dates, if 0 return start date
  val startDate = LocalDate.of(1992, 9, 1)
  assert(
    generateListOfDates(startDate, 2) == List(
      "1992-09-01",
      "1992-09-02",
      "1992-09-03"
    )
  )
  assert(generateListOfDates(startDate, 0) == List("1992-09-01"))
  println("DATE FORMAT TESTS PASSED")

  // filter list of vehicles by the given sunroof type, possible values are AUTO, MANUAL or the option is None
  // if the car doesn't have a sunroof, however only strings can be passed to the function, if the string is empty,
  // assume it means no sunroof
  assert(
    filterVehiclesBySunroof(vehiclesEU, "AUTO").toSet == Set(
      Vehicle(70, "Jeep", 1, Some("AUTO"))
    )
  )
  assert(
    filterVehiclesBySunroof(vehiclesEU ::: vehicleUS, "MANUAL").toSet
      == Set(
      Vehicle(0, "Punto", 0, Some("MANUAL")),
      Vehicle(40, "Fiat", 3, Some("MANUAL")),
      Vehicle(60, "Merc", 2, Some("MANUAL"))
    )
  )
  println("SUNROOF FILTER TESTS PASSED")

  // filter list of vehicles between the given speed boundaries none inclusive,
  // if the boundary is None ignore that limit
  assert(
    filterVehiclesBySpeed(vehiclesUK, Some(40), None).toSet == Set(
      Vehicle(100, "BMW", 4, None),
      Vehicle(50, "Honda", 2, Some("AUTO"))
    )
  )
  assert(
    filterVehiclesBySpeed(vehicleUS ::: vehiclesUK, Some(55), None).toSet
      == Set(
      Vehicle(60, "Merc", 2, Some("MANUAL")),
      Vehicle(100, "BMW", 4, None)
    )
  )
  assert(
    filterVehiclesBySpeed(vehiclesEU, None, None).toSet == vehiclesEU.toSet
  )
  assert(
    filterVehiclesBySpeed(vehiclesEU, Some(15), Some(70)).toSet
      == Set(Vehicle(25, "Fiat", 6, None), Vehicle(35, "Punto", 2, None))
  )
  println("SPEED FILTER TESTS PASSED")

  // return a list of the same vehicles with the model capitalised
  assert(
    generateVehiclesCapitalised(vehiclesEU).toSet
      == Set(
      Vehicle(70, "JEEP", 1, Some("AUTO")),
      Vehicle(25, "FIAT", 6, None),
      Vehicle(15, "JAGUAR", 3, None),
      Vehicle(35, "PUNTO", 2, None)
    )
  )
  println("CAPITALISE TESTS PASSED")

  // Certain car manufacturers have asked for a specific message to be applied for their cars
  // Honda would like to display "Hello super fast Honda driver, zoom zoom!" for their fast(speed > 60) Honda drivers
  // Fiat would like to display "If it's a nice day, don't forget to roll down your fancy sunroof" for their Fiat drivers with
  // MANUAL sunroofs
  // For anything not specified above display a simple "Have a good day driver"
  // Add in some more test cases if you can!
  assert(
    personalisedMessage(Vehicle(70, "HONDA", 5, Some("MANUAL"))) == "Hello super fast Honda driver, zoom zoom!"
  )
  println("MESSAGE TESTS PASSED")

  // return the total number of wheels by model for the given vehicle list
  // there is a bad implementation of this already, can you improve?
  assert(
    totalNumberWheelsByModel(vehiclesEU :+ Vehicle(40, "Punto", 3, None)).toSet
      == Set(("Jeep", 1), ("Fiat", 6), ("Jaguar", 3), ("Punto", 5))
  )
  println("TOTAL WHEELS TESTS PASSED")

  // Get single model details
  assert(
    getSingleVehicleModelDetails(Vehicle(40, "HonDa", 3, None)) == Some(ModelDetails(
      100,
      4
    ))
  )
  println("SINGLE MODEL TESTS PASSED")

  // Return None if no model
  val noDetails =
    getSingleVehicleModelDetails(Vehicle(40, "Ferrari", 3, None))
  assert(noDetails.isEmpty)
  println("NO MODEL TESTS PASSED")

  // Return list of details for given vehicles
  assert(
    getVehicleListModelDetails(vehiclesEU).toSet == Set(
      ModelDetails(200, 8),
      ModelDetails(120, 2)
    )
  )
  println("LIST OF MODEL DETAILS TESTS PASSED")


}

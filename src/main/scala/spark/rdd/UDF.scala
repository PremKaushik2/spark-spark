package spark.rdd

object UDF {

  def remove_leading(regex: String): String => String = _.replaceAll(regex.toString(), "")
}
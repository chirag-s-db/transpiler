package splunkql

import org.apache.logging.log4j.scala.Logging
import org.apache.spark.sql.SparkSession
import org.scalatest.funsuite.AnyFunSuite

class SearchesFileTest extends AnyFunSuite with Logging {

  private def res = getClass.getResourceAsStream(_)

  test("att&ck") {
    val sf = new SearchesFile(res("/savedsearches.conf"))
  }

  test("att") {
    import org.apache.spark.sql.functions._
    val spark = SparkSession.builder().master("local[1]").getOrCreate()
    val df = spark.range(10)
      .withColumn("b", expr("id % 2"))
      .groupBy("b")
      .agg(sum("id") as "a")
      .withColumn("c", lit(1))
      .withColumn("d", lit(2))
        .join(spark.table("x"), Seq("a"), "left")
    logger.info(df.queryExecution.logical)
    logger.info("printed")
  }

  test("expansion") {
    val sc = SplunkContext(
      new SearchesFile(res("/savedsearches.conf")),
      new MacrosFile(res("/macros.conf")))

    val plan = sc.generatePython("[T1101] Security Support Provider")
    logger.info(s"Generated code: \n${plan}")
  }
}

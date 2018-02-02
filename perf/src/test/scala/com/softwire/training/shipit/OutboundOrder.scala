package com.softwire.training.shipit

import io.gatling.core.Predef._
import io.gatling.core.feeder.RecordSeqFeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

/**
  * Test with the following profile:
  * * GET /status.xml
  * * GET /product.xml?action=get&gtin=?  (orderLinesPerOrder times)
  * * POST /outboundOrder.xml  (once with orderLinesPerOrder products)
  *
  * This test will fail if there is ever too little stock held, so it's easiest to run it against a copy of the database
  * with very high stock levels
  */
class OutboundOrder extends Simulation {
  val baseURL = "http://warehouselargerinst-env.eu-west-1.elasticbeanstalk.com"

  val httpConf: HttpProtocolBuilder = http
    .baseURL(baseURL)
    .acceptHeader("text/xml")
    .acceptEncodingHeader("gzip, deflate")

  setUp(OutboundOrder.outboundOrder.inject(
    constantUsersPerSec(OutboundOrder.ordersPerSecond) during OutboundOrder.testLength
  ).protocols(httpConf))
}

object OutboundOrder {
  // Configurable values
  val users = 1 * 1000 * 1000
  val ordersPerWeekPerUser = 1
  val orderLinesPerOrder = 10
  val testLength = 10 minutes

  val ordersPerSecond = (users * ordersPerWeekPerUser).toFloat / 7.days.toSeconds

  // Very occasionally, the gtin feeder will return duplicates.  Rather than write our own feeder, it's easier to
  // just ignore errors of this type
  val responseSuccessful: HttpCheck = xpath(
    "/shipit/response/success[contains(., 'true')]| /shipit/response/code[contains(., '2')]").exists
  val gtinFeeder: RecordSeqFeederBuilder[String] = csv("gtins.csv").random

  val orderXml: String = {
    val orderLines = (0 to orderLinesPerOrder).map(i =>
      s"""
         |<orderLine>
         |  <gtin>$${gtin${i + 1}}</gtin>
         |  <quantity>1</quantity>
         |</orderLine>
      """.stripMargin).mkString("")

    s"""
       |<shipit>
       |  <outboundOrder>
       |    <warehouseId>1</warehouseId>
       |    <orderLines>$orderLines</orderLines>
       |  </outboundOrder>
       |</shipit>
    """.stripMargin
  }

  val outboundOrder: ScenarioBuilder = scenario("OutboundOrder")
    .exec(http("status")
      .get("/status.xml")
      .check(responseSuccessful))
    .pause(100 milliseconds)
    .feed(gtinFeeder)
    .repeat(orderLinesPerOrder, "getProducts") {
      pace(100 milliseconds)
        .exec(http("product")
          .get("/product.xml?action=get&gtin=${gtin}")
          .check(responseSuccessful))
    }
    .pause(100 milliseconds)
    .feed(gtinFeeder, orderLinesPerOrder + 1)
    .exec(http("order")
      .post("/outboundOrder.xml")
      .body(StringBody(orderXml))
      .check(responseSuccessful))
}

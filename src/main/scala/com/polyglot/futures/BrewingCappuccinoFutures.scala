package com.polyglot.futures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Random
import scala.util.Success
import scala.util.control.NonFatal
import scala.concurrent.Await
import scala.concurrent.duration._

object BrewingCappuccinoFutures extends App {

  //take coffee beans
  type CoffeeBeans = String
  //grind coffee beans
  type GroundCoffee = String

  //take water and heat it to required temp
  case class Water(temp: Int)

  //take milk
  type Milk = String
  //frothed Milk
  type FrothedMilk = String

  //brew groundCoffee and heated water to make an expresso
  type Expresso = String
  //combine expresso with frothed milk
  type Cappuccino = String

  //exceptions
  case class GrindingException(msg: String) extends Exception(msg)
  case class WaterBoilingException(msg: String) extends Exception(msg)
  case class FrothingException(msg: String) extends Exception(msg)
  case class BrewingException(msg: String) extends Exception(msg)

  def grind(beans: CoffeeBeans): Future[GroundCoffee] = Future {
    println(s"Start Grinding coffee Beans: $beans ......")
    Thread.sleep(Random.nextInt(2000))
    if (beans == "baked Beans") throw GrindingException("Are you joking?")
    println(s"Finished Grinding coffee Beans: $beans ......")
    s"(beans: $beans)"
  }

  def heatWater(water: Water): Future[Water] = Future {
    println(s"Start heating water with temp:${water.temp} ......")
    Thread.sleep(Random.nextInt(2000)+2000)
    println(s"Finished heating to 85 temp .....")
    water.copy(temp = 85)
  }
  def frothMilk(milk: Milk): Future[FrothedMilk] = Future {
    println(s"Start milk frothing:$milk ......")
    Thread.sleep(Random.nextInt(2000))
    println(s"Finished milk forthing $milk .....")
    s"(frothedmilk: $milk)"
  }
  def brew(groundCoffee: GroundCoffee, water: Water): Future[Expresso] = Future {
    println(s"Start brewing ground coffe:$groundCoffee with water:$water ......")
    Thread.sleep(Random.nextInt(2000))
    println(s"Finished brewing ground coffe:$groundCoffee with water:$water ......")
    s"(Expresso of groundCoffee: $groundCoffee and water temp:${water.temp})"
  }
  def combine(expresso: Expresso, frothedMilk: FrothedMilk): Cappuccino = s"Cappuccino with $expresso and $frothedMilk"

  def prepareCappuccino(beans: CoffeeBeans, water: Water, milk: Milk): Future[Cappuccino] = {
    //pre instantiate your future before using for comprehension otherwise they will be serial not multithreaded operaations
    
    val beansFuture = grind(beans)
    val waterFuture = heatWater(water)
    val frothMilkFuture = frothMilk(milk)
    
    beansFuture.onComplete { 
      case Success(groundCoffee) => println(s"callback:  $groundCoffee") 
      case Failure(NonFatal(ex)) => println(s"Error callback: ${ex.getMessage} ")  
    }
    
    
    
    //in for comprehension first specify tasks that does not depend on other tasks in order of their expected latency
    //e.g if waterFuture is expected to take longer start that future first before starting other futures
    for {
      water <- waterFuture
      ground <- beansFuture
      foam <- frothMilkFuture
      expresso <- brew(ground, water)     
    } yield combine(expresso, foam)
  }
  
  
    println("Preparing Cappuccino")
    val cappuccinoFuture: Future[Cappuccino] = prepareCappuccino("baked Beans1", Water(25), "Whole Milk")
    cappuccinoFuture.onComplete {
      case Success(cap) => println(s"Cappuccino Ready: $cap")
      case Failure(NonFatal(ex)) => println(ex.getMessage)
    }
   
    try {
      Await.result(cappuccinoFuture, 15 seconds)
    } catch {
      case ex:Throwable => println(s"Exception: ${ex.getMessage} \n ${ex.printStackTrace()}")
    }
    
    println("Finished Preparing Cappuccino")

  

}
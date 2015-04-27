package com.polyglot.futures

import scala.util.Try
import scala.util.Success
import scala.util.Failure

object BrewingCappuccino {
  
  
  //take coffee beans
  type CoffeeBeans = String
  //grind coffee beans
  type GroundCoffee = String
  
  //take water and heat it to required temp
  case class Water(temp:Int)
  
  //take milk
  type Milk = String
  //frothed Milk
  type FrothedMilk = String
  
  //brew groundCoffee and heated water to make an expresso
  type Expresso = String
  //combine expresso with frothed milk
  type Cappuccino = String
  
  def grind(beans:CoffeeBeans):GroundCoffee = s"ground coffee of $beans"
  def heatWater(water:Water):Water = water.copy(temp=85)
  def frothMilk(milk:Milk):FrothedMilk = s"frother $milk"
  def brew(groundCoffee:GroundCoffee,water:Water):Expresso = s"Expresso with $groundCoffee and water temp:${water.temp}"
  def combine(expresso:Expresso,frothedMilk:FrothedMilk):Cappuccino = s"Cappuccino with $expresso and $frothedMilk"
  
  case class GrindingException(msg:String) extends Exception(msg)
  case class WaterBoilingException(msg:String) extends Exception(msg)
  case class FrothingException(msg:String) extends Exception(msg)
  case class BrewingException(msg:String) extends Exception(msg)
  
  
  def prepareCappuccino():Try[Cappuccino] = for {
    ground <- Try(grind("arabia beans"))
    water <- Try(heatWater(Water(25)))
    expresso <- Try(brew(ground,water))
    foam <- Try(frothMilk("milk"))
  }  yield combine(expresso, foam)
  
  
  def main(args:Array[String]){
    println("Preparing Cappuccino")
    val cappuccionTry = prepareCappuccino()
    cappuccionTry match {
      case Success(cappuccino) => println(cappuccino)
      case Failure(ex) => println(ex)
    }
    
  }

}
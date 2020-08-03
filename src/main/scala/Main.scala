
import java.io.{File, PrintWriter}

import scala.io.Source
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx.{Edge, Graph, lib}
import org.apache.spark.graphx._

/***
 * author:eren ozel
 *
 */
object Main {

  def main(args: Array[String]): Unit = {


    dijkstraOnGraph
    barictriangleComputation
  }

  /***
   * grapn implementation of djskarta algorithm with using spark graphx
   *
   */
  def dijkstraOnGraph():Unit={


    val conf = new SparkConf().setAppName("suprnation").setMaster("local[2]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")

    val vertex = sc.textFile("src/main/resources/vertex.txt").map { line =>
      val fields = line.split(",")
      (fields(0).toLong, fields(1))
    }

    val edges = sc.textFile("src/main/resources/edge.txt").map { line =>
      val fields = line.split(",")
      Edge(fields(0).toLong, fields(1).toLong, fields(2).toDouble)
    }

    val myGraph = Graph(vertex, edges)
    val result = dijkstra(myGraph, 0L).vertices.map(_._2).collect

    println("graph result: ")
    result.foreach(println)


    val writer = new PrintWriter(new File("src/main/resources/graphresult.txt"))
    writer.write(result.mkString("\n"))
    writer.close()
  }

  def dijkstra[VD](g:Graph[VD,Double], origin:VertexId) = {
    var g2 = g.mapVertices(
      (vid,vd) => (false, if (vid == origin) 0 else Double.MaxValue,
        List[VertexId]()))

    for (i <- 1L to g.vertices.count-1) {
      val currentVertexId =
        g2.vertices.filter(!_._2._1)
          .fold((0L,(false,Double.MaxValue,List[VertexId]())))((a,b) =>
            if (a._2._2 < b._2._2) a else b)
          ._1

      val newDistances = g2.aggregateMessages[(Double,List[VertexId])](
        ctx => if (ctx.srcId == currentVertexId)
          ctx.sendToDst((ctx.srcAttr._2 + ctx.attr,
            ctx.srcAttr._3 :+ ctx.srcId)),
        (a,b) => if (a._1 < b._1) a else b)

      g2 = g2.outerJoinVertices(newDistances)((vid, vd, newSum) => {
        val newSumVal =
          newSum.getOrElse((Double.MaxValue,List[VertexId]()))
        (vd._1 || vid == currentVertexId,
          math.min(vd._2, newSumVal._1),
          if (vd._2 < newSumVal._1) vd._3 else newSumVal._2)})
    }

    g.outerJoinVertices(g2.vertices)((vid, vd, dist) =>
      (vd, dist.getOrElse((false,Double.MaxValue,List[VertexId]()))
        .productIterator.toList.tail))
  }


  /***
   * basic implementation of dijscatra algorithm to get min path from an array
   *
   */
  def barictriangleComputation():Unit={

    val tree = Source.fromFile("src/main/resources/basictree.txt").getLines.map{ line =>
      line.split(",").map(_.toInt)
    }.toArray

    val result = dijkstraBasic(tree)

    println(s"basic result: $result")

    val writer = new PrintWriter(new File("src/main/resources/basicresult.txt"))
    writer.write(result.toString)
    writer.close()

  }

  def dijkstraBasic(triangle: Array[Array[Int]]) = {

    val levels = triangle.size
    var dp = new Array[Int](levels)
    dp = triangle(levels - 1)
    for (l <- levels - 2 to 0 by -1) {
      for (i <- 0 to l) {
        dp(i) = Math.min(dp(i), dp(i + 1)) + triangle(l)(i)
      }
    }
    dp(0)
  }

}

import org.scalatest.{FlatSpec, Matchers}

class TestDijkarta extends FlatSpec with Matchers{

  import Main._

  behavior of "DijkartaAlgorithm"

  it should "result Should be " in {
    val TriangleTest = Array(Array(7), Array(6, 3), Array(3, 8, 5),Array(11, 2, 10,9))

    val j1 =dijkstraBasic(TriangleTest)
    j1 should be(18)
  }


}
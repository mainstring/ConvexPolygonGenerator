import Generator.printPolygon
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

object GeneratorTest {


    @JvmStatic
    fun main(args: Array<String>) {
        val polygon = Generator2.generate()
        Generator2.randomShifting(polygon)
        println(polygon.size)
//        printPolygon(polygon)
        val c = convex(polygon)
        println(c)
//        printPolygon(polygon)
    }

    fun convex(polygon: ArrayList<Point>): Boolean {

        polygon.sortWith(Comparator { o1, o2 ->
            if(o1.y < o2.y || (o1.y == o2.y && o1.x < o2.x)) -1
            else if(o1.x == o2.x && o1.y == o2.y) 0
            else 1
        })

        var p1 = polygon[0]
        polygon.sortWith(Comparator{ o1, o2 ->
            run {
                val o = orientation(p1, o1, o2)
                return@run if (o == 0) {
                    if (distSq(p1, o2) > distSq(p1, o1)) -1
                    else 1
                } else {
                    if(o == 2) -1
                    else 1
                }
            }
        })

        p1 = polygon[0]
        var p2 = polygon[1]
        val stack = Stack<Point>()
        stack.push(p1)
        stack.push(p2)
        if(!Generator2.checkLimits(p1) || !Generator2.checkLimits(p2)) return false

        for(i in 2 until polygon.size) {
            val p3 = polygon[i % polygon.size]
            if(!Generator2.checkLimits(p3)) return false
            val o = orientation(p1, p2, p3)
            if(o < 2) {
                stack.pop()
//                println("  $i")
            }
            else p1 = p2
            stack.push(p3)
            p2 = p3
        }
        println(stack.size)
        polygon.clear()
        while (!stack.empty()) {
            polygon.add(stack.pop())
        }
        polygon.reverse()
        return true
    }
    // To find orientation of ordered triplet (p, q, r).
// The function returns following values
// 0 --> p, q and r are colinear
// 1 --> Clockwise
// 2 --> Counterclockwise
    fun orientation(p: Point, q: Point, r: Point): Int {
        val z = (q.y - p.y) * (r.x - q.x) -
                (q.x - p.x) * (r.y - q.y)
        if (z == 0L) return 0 // colinear
        return if (z > 0) 1 else 2 // clock or counterclock wise
    }

    // A utility function to return square of distance
// between p1 and p2
    fun distSq(p1: Point, p2: Point): Int {
        return ((p1.x - p2.x) * (p1.x - p2.x) +
                (p1.y - p2.y) * (p1.y - p2.y)).toInt()
    }
}
import java.security.SecureRandom
import kotlin.math.*

object Generator {
    private const val GRID_LIMIT: Long = 100
    private const val LEFT = -GRID_LIMIT
    private const val TOP = GRID_LIMIT
    private const val RIGHT = GRID_LIMIT
    private const val BOTTOM = -GRID_LIMIT
    private val rng = SecureRandom()
    private const val count = 10
    private const val eps = 1e-12

    @JvmStatic
    fun main(args: Array<String>) {
        val polygon = generate()
        println(polygon.size)
//        for (point in polygon) {
//            println("" + point.x + " " + point.y)
//        }
    }

    fun printPolygon(polygon: ArrayList<Point>) {
        for (point in polygon) {
            println("(" + point.x + " , " + point.y + ")")
        }
    }

    fun generate(): ArrayList<Point> {
        val ang = rng.nextDouble()*(PI/2.0)
        var height = GRID_LIMIT/2
//        height /= 2
        val p1 = Point((height.toDouble() * cos(ang)).toLong(), (height.toDouble() * sin(ang)).toLong())
        val p2 = Point(-(height.toDouble() * cos(ang)).toLong(), -(height.toDouble() * sin(ang)).toLong())

        val polygon = ArrayList<Point>()
        polygon.add(p1)
        polygon.addAll(clockwiseHalf(p1, p2, (count-2)/2))
        polygon.add(p2)
        val secondHalf = antiClockwiseHalf(p1, p2, count-2 - ((count-2)/2))
        secondHalf.reverse()
        polygon.addAll(secondHalf)
        return polygon
    }

    private fun clockwiseHalf(p1: Point, p2: Point, count: Int): ArrayList<Point> {
        val result = ArrayList<Point>()

        var point1 = p1
//        var prevVector = VectorD(Point(point1.y- p2.y, p2.x-point1.x))
        var prevVector = VectorD(p2.toPointD(), p1.toPointD())
        for(i in 0 until count) {
            val p = nextPoint(point1, p2, prevVector, true)
            p?: return result
            result.add(p)
            prevVector = VectorD(Point(p.x-point1.x, p.y-point1.x).toPointD())
            point1 = p
        }
        return result
    }

    private fun antiClockwiseHalf(p1: Point, p2: Point, count: Int, prev: VectorD? = null): ArrayList<Point> {
        val result = ArrayList<Point>()

        var point1 = p1
//        var prevVector = prev?: VectorD(Point(p2.y- point1.y, point1.x-p2.x))
        var prevVector = prev?: VectorD(p1.toPointD(), p2.toPointD())
        for(i in 0 until count) {
            val p = nextPoint(point1, p2, prevVector, false)
            p?: return result
            result.add(p)
            prevVector = VectorD(Point(p.x-point1.x, p.y-point1.x).toPointD())
            point1 = p
        }
        return result
    }

    private fun nextPoint(p1: Point, p2: Point, prevVector: VectorD, clockwise: Boolean): Point? {
        var angle = VectorD(p1.toPointD(), p2.toPointD()).angle(prevVector)
        if(angle < eps) {
            println("clockwise: $clockwise angle too small")
            return null
        }

        val angleMin = angle * .05F
        val angleRange = angle * .9F
        angle = angleMin + angleRange* rng.nextDouble()
        val rotated = prevVector.rotate(angle, clockwise)

        val xDiff: Double = if(rotated.x < 0F) {
            if(clockwise) {
                (p1.x - p2.x).toDouble()
            } else {
                (p1.x - LEFT).toDouble()
            }
        } else {
            if(!clockwise) {
                (p2.x - p1.x).toDouble()
            } else {
                (RIGHT - p1.x).toDouble()
            }
        }
        val yDiff: Double = if(rotated.y < 0F) {
            if(!clockwise) {
                (p1.y - p2.y).toDouble()
            } else {
                (p1.y - BOTTOM).toDouble()
            }
        } else {
            if(clockwise) {
                (p2.y - p1.y).toDouble()
            } else {
                (TOP - p1.y).toDouble()
            }
        }

        var t = if(xDiff* abs(rotated.y) > yDiff* abs(rotated.y)) {
            xDiff/ abs(rotated.x)
        } else {
            xDiff/abs(rotated.y)
        }
        var x = (rotated.x*t).toLong()
        var y = (rotated.y*t).toLong()
        if(x == 0L && y == 0L) {
            println("clockwise: $clockwise change too short")
            return null
        }

        t *= centerDenseRandom()
        when {
            t > 1000000 -> t *= .000005F
            t > 100000 -> t *= .00005F
            t > 10000 -> t *= .0005F
            t > 1000 -> t *= .005F
            t > 100 -> t *= .05F
        }
        val xTemp = (rotated.x*t).toLong()
        val yTemp = (rotated.y*t).toLong()
        if(xTemp != 0L || yTemp != 0L) {
            x = xTemp
            y = yTemp
        }
        return Point(p1.x + x, p1.y + y)
    }

    private fun leftDenseRandom(factor: Int = 2): Double {
        val r = rng.nextDouble()
        return r.pow(factor)
    }

    private fun rightDenseRandom(factor: Int = 2): Double {
        val r = rng.nextDouble()
        return 1F - r.pow(factor)
    }

    private fun centerDenseRandom(factor: Int = 2): Double {
        val r = rng.nextInt(2)
        return if(r == 0) {
            rightDenseRandom(factor)*.5F
        } else {
            leftDenseRandom(factor)*.5F + .5F
        }
    }
}
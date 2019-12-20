import java.io.File
import java.io.FileWriter
import java.security.SecureRandom
import kotlin.math.*

const val eps = 1e-12
val ORIGIN = Point()
val ORIGIN_F = PointD()


object Generator2 {
    private const val GRID_LIMIT: Long = 1000000000
    private const val LEFT = -GRID_LIMIT
    private const val TOP = GRID_LIMIT
    private const val RIGHT = GRID_LIMIT
    private const val BOTTOM = -GRID_LIMIT
    private val rng = SecureRandom()
    private const val count = 500000
    private const val P9 = 1000000000
    private const val P6 = 1000000


    @JvmStatic
    fun main(args: Array<String>) {

        val polygon = generate()
        randomShifting(polygon)
        val b = GeneratorTest.convex(polygon)
        if(b) {
            val N = rng.nextInt(P9 - 2) + 3
            val L = rng.nextInt(P9 - 2) + 3
//            val N = P9
//            val L = P9 - rng.nextInt(1000)

            val file = File("./input12.txt")
            file.createNewFile()
            val writer = FileWriter(file)
            writer.write("$N $L")
            writer.append("\n")
            writer.append("${polygon.size}")
            writer.append("\n")

            for (point in polygon) {
                writer.append("${point.x} ${point.y}\n")
            }

            writer.flush()
            writer.close()
        }
    }

    fun generate(): ArrayList<Point> {
        val ang = rng.nextDouble()*(PI /2.0)
//        val ang = 0.5
        var height = (GRID_LIMIT/2).toDouble()
//        height /= 2
        val angleResolution = (PI + PI)/count.toDouble()
        val p1 = Point((height * cos(ang)).toLong(), (height * sin(ang)).toLong())
        val polygon = ArrayList<Point>()
        polygon.add(p1)
        var currentAngle = PI/2.0 - ang
        var prevPoint = p1
        var limitVectorF = VectorD(p1.toPointD(), ORIGIN_F).rotate((PI/2.0).toDouble(), false)

        for(i in 1 until count) {
            var nextAngle = currentAngle + angleResolution*.9
            val r = rng.nextInt(2)
            val noise = angleResolution*.1* rng.nextDouble()
            if(r == 0) nextAngle += noise
            else nextAngle -= noise
            val currentVector = VectorD(ORIGIN_F, PointD(Y = 1.0)).rotate(nextAngle, true)
            var limit = height*1.9f
            val line1 = Line(prevPoint.toPointD(), limitVectorF)
            val line2 = Line(ORIGIN_F, currentVector)
            if(line1.intersects(line2)) {
                limit = VectorD(ORIGIN_F, line1.getIntersection(line2)!!).length().coerceAtMost(limit)
            }
            limit *= rng.nextDouble()/10F + .9F
            if(limit < eps) {
                println("short on length")
                break
            }
            currentVector.adjustLength(limit)
            val p = Point(currentVector.x.toLong(), currentVector.y.toLong())
            polygon.add(p)
            limitVectorF = VectorD(prevPoint.toPointD(), p.toPointD())
            prevPoint = p
            currentAngle = nextAngle
        }
        return polygon
    }

    fun randomShifting(polygon: List<Point>) {
        var leftBound = Long.MAX_VALUE
        var rightBound = Long.MAX_VALUE
        var topBound = Long.MAX_VALUE
        var bottomBound = Long.MAX_VALUE

        for(point in polygon) {
            leftBound = min(leftBound, point.x - LEFT)
            rightBound = min(leftBound, RIGHT - point.x)
            topBound = min(leftBound, TOP - point.y)
            bottomBound = min(leftBound, point.y - BOTTOM)
        }
        if(leftBound + rightBound < 2L || topBound + bottomBound < 2L) return
        val xShift = rng.nextLong().absoluteValue%(leftBound + rightBound - 1) - (leftBound - 1)
        val yShift = rng.nextLong().absoluteValue%(topBound + bottomBound - 1) - (bottomBound - 1)

        println("leftBound: $leftBound rightBound $rightBound topBound $topBound bottomBound $bottomBound")
        println("xShift $xShift yShift $yShift")
        for (i in polygon.indices) {
            polygon[i].x += xShift
            polygon[i].y += yShift
        }
    }

    fun checkLimits(p: Point): Boolean {
        return p.x.absoluteValue <= GRID_LIMIT && p.y.absoluteValue <= GRID_LIMIT
    }
}
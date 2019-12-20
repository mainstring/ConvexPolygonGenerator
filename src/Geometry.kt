import java.lang.IllegalArgumentException
import kotlin.math.*

open class PointD(var X: Double = 0.0, var Y: Double = 0.0)
class Point(var x: Long = 0L, var y: Long = 0L) {
    fun toPointD(): PointD {
        return PointD(x.toDouble(), y.toDouble())
    }
}

class VectorD(var x: Double, var y: Double) {
    constructor(p: PointD): this(p.X, p.X)
    constructor(p1: PointD, p2: PointD): this((p2.X-p1.X), (p2.Y-p1.Y))

    fun length(): Double {
        return sqrt(x*x + y*y)
    }

    fun unit(): VectorD {
        return VectorD(x/length(), y/length())
    }

    fun adjustLength(length: Double) {
        if (length < eps) throw IllegalArgumentException("length cannot be 0")

        val prevLen = length()
        x *= length/prevLen
        y *= length/prevLen
    }

    fun dot(other: VectorD): Double {
        return x*other.x + y*other.y
    }

    fun cross(other: VectorD): Double {
        return x*other.y - y*other.x
    }

    fun angle(other: VectorD): Double {
        return asin(unit().cross(other.unit()))
    }

    fun rotate(angle: Double, clockwise: Boolean): VectorD {
        return if (clockwise) {
            rotateClockwise(angle)
        } else {
            rotateAntiClockwise(angle)
        }
    }
    private fun rotateAntiClockwise(angle: Double): VectorD {
        return VectorD(cos(angle) *x - sin(angle) *y, sin(angle) *x + cos(angle) *y).unit()
    }

    private fun rotateClockwise(angle: Double): VectorD {
        return VectorD(cos(angle) *x + sin(angle) *y, -sin(angle) *x + cos(angle) *y).unit()
    }
}

class Line(private val p: PointD,private val vector: VectorD) {

    constructor(p1: PointD, p2: PointD): this(p1, VectorD(p1,p2))

    val A = vector.y
    val B = -vector.x
    val C = A* p.X + B*p.Y

    fun intersects(other: Line): Boolean {
        val angle = vector.angle(other.vector)
        val a = angle.absoluteValue
        return a > eps
    }

    fun getIntersection(other: Line): PointD? {
        if(!intersects(other)) return null
        val result = PointD()
        when {
            A.absoluteValue < eps -> {
                result.Y = C/B
                result.X = (other.C - other.B*result.Y)/other.A
            }
            other.A.absoluteValue < eps -> {
                result.Y = other.C/ other.B
                result.X = (C - B*result.Y)/A
            }
            else -> {
                val factor = other.A/A
                val b = B*factor - other.B
                val c = C*factor - other.C
                result.Y = c/b
                result.X = (other.C - other.B*result.Y)/other.A
            }
        }
        return result
    }
}
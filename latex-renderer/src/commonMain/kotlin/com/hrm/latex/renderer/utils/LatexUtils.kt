package com.hrm.latex.renderer.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.hrm.latex.parser.model.LatexNode
import com.hrm.latex.parser.model.LatexNode.Space.SpaceType
import com.hrm.latex.renderer.model.RenderStyle

/**
 * 分割多行内容
 */
fun splitLines(nodes: List<LatexNode>): List<List<LatexNode>> {
    val result = mutableListOf<MutableList<LatexNode>>()
    var current = mutableListOf<LatexNode>()
    nodes.forEach { node ->
        if (node is LatexNode.NewLine) {
            result.add(current)
            current = mutableListOf()
        } else {
            current.add(node)
        }
    }
    if (current.isNotEmpty() || result.isEmpty()) result.add(current)
    return result
}

/**
 * 计算行间距
 */
fun lineSpacingPx(style: RenderStyle, density: Density): Float =
    with(density) { (style.fontSize * 0.25f).toPx() }

/**
 * 计算空白宽度
 */
fun spaceWidthPx(style: RenderStyle, type: SpaceType, density: Density): Float {
    val factor = when (type) {
        SpaceType.THIN -> 0.166f
        SpaceType.MEDIUM -> 0.222f
        SpaceType.THICK -> 0.277f
        SpaceType.QUAD -> 1f
        SpaceType.QQUAD -> 2f
        SpaceType.NORMAL -> 0.25f
        SpaceType.NEGATIVE_THIN -> -0.166f
    }
    return with(density) { (style.fontSize * factor).toPx() }
}

/**
 * 解析尺寸字符串 (如 "1cm", "10pt", "-5mm")
 */
fun parseDimension(dimension: String, style: RenderStyle, density: Density): Float {
    val dim = dimension.trim()
    if (dim.isEmpty()) return 0f
    
    // 正则提取数值和单位
    // 简单解析，不支持复杂表达式
    var numEnd = 0
    while (numEnd < dim.length && (dim[numEnd].isDigit() || dim[numEnd] == '.' || dim[numEnd] == '-')) {
        numEnd++
    }
    
    val value = dim.substring(0, numEnd).toFloatOrNull() ?: 0f
    val unit = dim.substring(numEnd).trim().lowercase()
    
    return with(density) {
        when (unit) {
            "pt" -> value.dp.toPx() // CSS pt ~= Android dp (approx) or use standard conversion
            "px" -> value
            "mm" -> (value * 3.78f).dp.toPx() // 1mm ~= 3.78px (at 96dpi) -> map to dp
            "cm" -> (value * 37.8f).dp.toPx()
            "in" -> (value * 96f).dp.toPx()
            "em" -> style.fontSize.toPx() * value
            "ex" -> style.fontSize.toPx() * 0.5f * value
            else -> value.dp.toPx() // default to dp
        }
    }
}

/**
 * 大型运算符符号映射
 */
fun mapBigOp(op: String): String {
    val name = op.trim()
    return when (name) {
        "sum" -> "∑"
        "prod" -> "∏"
        "coprod" -> "∐"
        "int" -> "∫"
        "oint" -> "∮"
        "iint" -> "∬"
        "iiint" -> "∭"
        "bigcap" -> "⋂"
        "bigcup" -> "⋃"
        "bigsqcup" -> "⨆"
        "bigvee" -> "⋁"
        "bigwedge" -> "⋀"
        "bigoplus" -> "⨁"
        "bigotimes" -> "⨂"
        "biguplus" -> "⨄"
        else -> name
    }
}

/**
 * 解析颜色字符串
 */
fun parseColor(color: String): Color? {
    val trimmed = color.trim().removePrefix("#").lowercase()
    if (trimmed.isEmpty()) return null
    
    return try {
        when (trimmed.length) {
            6 -> {
                // RGB 格式: RRGGBB
                val rgb = trimmed.toLongOrNull(16) ?: return null
                if (rgb > 0xFFFFFF) return null
                // 将 RGB 转换为 ARGB (添加 alpha = FF)
                val argb = (0xFF000000 or rgb).toInt()
                Color(argb)
            }
            8 -> {
                // ARGB 格式: AARRGGBB
                val argb = trimmed.toLongOrNull(16) ?: return null
                if (argb > 0xFFFFFFFF) return null
                Color(argb.toInt())
            }
            else -> when (trimmed) {
                "red" -> Color.Red
                "blue" -> Color.Blue
                "green" -> Color.Green
                "black" -> Color.Black
                "white" -> Color.White
                "gray", "grey" -> Color.Gray
                "yellow" -> Color.Yellow
                "cyan" -> Color.Cyan
                "magenta" -> Color.Magenta
                "orange" -> Color(0xFFFFA500.toInt())
                "purple" -> Color(0xFF800080.toInt())
                "brown" -> Color(0xFFA52A2A.toInt())
                "pink" -> Color(0xFFFFC0CB.toInt())
                "lime" -> Color(0xFF00FF00.toInt())
                "navy" -> Color(0xFF000080.toInt())
                "teal" -> Color(0xFF008080.toInt())
                "violet" -> Color(0xFFEE82EE.toInt())
                else -> {
                    println("⚠️ Unknown color: '$color' (trimmed: '$trimmed')")
                    null
                }
            }
        }
    } catch (e: Exception) {
        println("❌ Error parsing color '$color': ${e.message}")
        e.printStackTrace()
        null
    }
}

// 括号绘制相关
enum class Side { LEFT, RIGHT }

/**
 * 绘制各种类型的括号/定界符
 */
fun DrawScope.drawBracket(
    type: LatexNode.Matrix.MatrixType,
    side: Side,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    stroke: Float,
    color: Color
) {
    val path = Path()
    when (type) {
        LatexNode.Matrix.MatrixType.PAREN -> {
            // Curve
            val x0 = if (side == Side.LEFT) x + width else x
            val x1 = if (side == Side.LEFT) x else x + width
            path.moveTo(x0, y)
            path.quadraticTo(x1, y + height / 2, x0, y + height)
        }

        LatexNode.Matrix.MatrixType.BRACKET -> {
            val x0 = if (side == Side.LEFT) x + width else x
            val x1 = if (side == Side.LEFT) x + stroke else x + width - stroke
            path.moveTo(x0, y)
            path.lineTo(x1, y)
            path.lineTo(x1, y + height)
            path.lineTo(x0, y + height)
        }

        LatexNode.Matrix.MatrixType.BRACE -> {
            // 花括号: { 或 }
            // 分为三段：上半部分、中间尖端、下半部分
            val midY = y + height / 2
            val tipX = if (side == Side.LEFT) x else x + width
            val baseX = if (side == Side.LEFT) x + width else x
            val controlOffset = width * 0.4f
            
            // 上半部分（顶部到中间）
            path.moveTo(baseX, y)
            path.quadraticTo(
                baseX - (if (side == Side.LEFT) controlOffset else -controlOffset),
                y + height * 0.2f,
                baseX,
                y + height * 0.4f
            )
            path.quadraticTo(
                baseX + (if (side == Side.LEFT) -controlOffset else controlOffset),
                midY - height * 0.1f,
                tipX,
                midY
            )
            
            // 下半部分（中间到底部）
            path.quadraticTo(
                baseX + (if (side == Side.LEFT) -controlOffset else controlOffset),
                midY + height * 0.1f,
                baseX,
                y + height * 0.6f
            )
            path.quadraticTo(
                baseX - (if (side == Side.LEFT) controlOffset else -controlOffset),
                y + height * 0.8f,
                baseX,
                y + height
            )
        }

        LatexNode.Matrix.MatrixType.VBAR -> {
            val mx = x + width / 2
            path.moveTo(mx, y)
            path.lineTo(mx, y + height)
        }

        LatexNode.Matrix.MatrixType.DOUBLE_VBAR -> {
            val mx1 = x + width / 3
            val mx2 = x + width * 2 / 3
            path.moveTo(mx1, y); path.lineTo(mx1, y + height)
            path.moveTo(mx2, y); path.lineTo(mx2, y + height)
        }

        LatexNode.Matrix.MatrixType.PLAIN -> {}
    }
    drawPath(path, color, style = Stroke(stroke))
}

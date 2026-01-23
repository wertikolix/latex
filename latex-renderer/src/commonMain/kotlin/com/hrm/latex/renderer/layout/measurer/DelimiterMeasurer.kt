/*
 * Copyright (c) 2026 huarangmeng
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.hrm.latex.renderer.layout.measurer

import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import com.hrm.latex.parser.model.LatexNode
import com.hrm.latex.renderer.layout.NodeLayout
import com.hrm.latex.renderer.model.RenderContext
import com.hrm.latex.renderer.model.textStyle
import com.hrm.latex.renderer.utils.LayoutUtils

/**
 * 定界符测量器
 *
 * 负责测量：
 * 1. 自动伸缩的括号 (\left( ... \right))
 * 2. 手动控制大小的括号 (\big, \Big, \bigg, \Bigg)
 */
internal class DelimiterMeasurer : NodeMeasurer<LatexNode> {

    override fun measure(
        node: LatexNode,
        context: RenderContext,
        measurer: TextMeasurer,
        density: Density,
        measureGlobal: (LatexNode, RenderContext) -> NodeLayout,
        measureGroup: (List<LatexNode>, RenderContext) -> NodeLayout
    ): NodeLayout {
        return when (node) {
            is LatexNode.Delimited -> measureDelimited(
                node,
                context,
                measurer,
                density,
                measureGroup
            )

            is LatexNode.ManualSizedDelimiter -> measureManualSizedDelimiter(
                node,
                context,
                measurer,
                density
            )

            else -> throw IllegalArgumentException("Unsupported node type: ${node::class.simpleName}")
        }
    }

    /**
     * 测量自动伸缩的定界符 (\left ... \right)
     *
     * 逻辑：
     * 1. 测量内部内容。
     * 2. 内容的高度决定了括号的高度。
     * 3. 如果括号类型支持绘制（如 () [] {}），则绘制矢量图形。
     * 4. 否则（如文本），回退到普通文本测量。
     */
    private fun measureDelimited(
        node: LatexNode.Delimited,
        context: RenderContext,
        measurer: TextMeasurer,
        density: Density,
        measureGroup: (List<LatexNode>, RenderContext) -> NodeLayout
    ): NodeLayout {
        val contentLayout = measureGroup(node.content, context)

        val leftStr = node.left
        val rightStr = node.right

        val leftLayout = if (leftStr != ".") {
            measureDelimiterScaled(leftStr, context, measurer, contentLayout.height)
        } else null

        val rightLayout = if (rightStr != ".") {
            measureDelimiterScaled(rightStr, context, measurer, contentLayout.height)
        } else null

        val leftW = leftLayout?.width ?: 0f
        val rightW = rightLayout?.width ?: 0f

        val width = leftW + contentLayout.width + rightW
        val height = contentLayout.height
        val baseline = contentLayout.baseline

        return NodeLayout(width, height, baseline) { x, y ->
            var curX = x

            // 计算数学轴的绝对 Y 坐标
            // 数学轴是相对于 baseline 的位置,用于居中运算符和括号
            val axisHeight = LayoutUtils.getAxisHeight(density, context, measurer)
            val contentAxisY = y + baseline - axisHeight

            // 绘制左侧括号:括号的几何中心应该对齐到数学轴
            if (leftLayout != null) {
                val delimiterTopY = contentAxisY - leftLayout.height / 2f
                leftLayout.draw(this, curX, delimiterTopY)
                curX += leftLayout.width
            }

            // 绘制内容
            contentLayout.draw(this, curX, y)
            curX += contentLayout.width

            // 绘制右侧括号:与左侧相同的逻辑
            if (rightLayout != null) {
                val delimiterTopY = contentAxisY - rightLayout.height / 2f
                rightLayout.draw(this, curX, delimiterTopY)
            }
        }
    }

    private fun delimiterContext(
        context: RenderContext,
        delimiter: String,
        scale: Float = 1.0f
    ): RenderContext {
        // 根据缩放比例连续调整 fontWeight (100-400)：
        // - scale = 1.0: FontWeight(400) = Normal（正常大小）
        // - scale = 1.5: FontWeight(300) = Light
        // - scale = 2.0: FontWeight(200) = ExtraLight
        // - scale >= 2.5: FontWeight(100) = Thin（最细）
        // 使用线性插值计算中间值
        val weight = when {
            scale <= 1.0f -> 400  // 正常或更小，使用 Normal
            scale >= 2.5f -> 100  // 很高的括号，使用 Thin
            else -> {
                // 线性插值: scale 从 1.0 到 2.5，weight 从 400 到 100
                val t = (scale - 1.0f) / 1.5f  // 归一化到 [0, 1]
                (400 - t * 300).toInt().coerceIn(100, 400)
            }
        }

        val fontWeight = FontWeight(weight)

        return context.copy(
            fontStyle = FontStyle.Normal,
            fontFamily = context.fontFamilies?.symbol ?: context.fontFamily,
            fontWeight = fontWeight
        )
    }

    private fun measureDelimiterScaled(
        delimiter: String,
        context: RenderContext,
        measurer: TextMeasurer,
        targetHeight: Float
    ): NodeLayout {
        // 先用默认 weight 测量获取基础高度
        val baseLayout = measureDelimiterText(delimiter, delimiterContext(context, delimiter), measurer)
        if (baseLayout.height <= 0f || targetHeight <= 0f) {
            return baseLayout
        }

        val scale = targetHeight / baseLayout.height

        // 根据实际缩放比例重新测量（应用动态 fontWeight）
        val adjustedContext = delimiterContext(context, delimiter, scale)
        val adjustedLayout = measureDelimiterText(delimiter, adjustedContext, measurer)

        // 使用 Canvas scale 而不是字体大小缩放，避免笔画变粗
        return NodeLayout(
            width = adjustedLayout.width * scale,
            height = targetHeight,
            baseline = adjustedLayout.baseline * scale
        ) { x, y ->
            scale(scale, scale, pivot = androidx.compose.ui.geometry.Offset(x, y)) {
                adjustedLayout.draw(this, x, y)
            }
        }
    }

    private fun measureDelimiterText(
        delimiter: String,
        delimiterStyle: RenderContext,
        measurer: TextMeasurer
    ): NodeLayout {
        val textStyle = delimiterStyle.textStyle()
        val result = measurer.measure(AnnotatedString(delimiter), textStyle)
        return NodeLayout(
            result.size.width.toFloat(),
            result.size.height.toFloat(),
            result.firstBaseline
        ) { x, y ->
            drawText(result, topLeft = androidx.compose.ui.geometry.Offset(x, y))
        }
    }

    /**
     * 测量手动大小的定界符 (\big, \Big 等)
     *
     * 这些定界符作为独立的符号存在,不包裹内容。
     */
    private fun measureManualSizedDelimiter(
        node: LatexNode.ManualSizedDelimiter,
        context: RenderContext,
        measurer: TextMeasurer,
        density: Density
    ): NodeLayout {
        val delimiter = node.delimiter
        val scaleFactor = node.size

        // 根据 scaleFactor 动态调整 fontWeight
        val delimiterStyle =
            delimiterContext(context, delimiter, scaleFactor).copy(fontSize = context.fontSize * scaleFactor)
        val result = measurer.measure(AnnotatedString(delimiter), delimiterStyle.textStyle())

        return NodeLayout(
            result.size.width.toFloat(),
            result.size.height.toFloat(),
            result.firstBaseline
        ) { x, y ->
            drawText(result, topLeft = androidx.compose.ui.geometry.Offset(x, y))
        }
    }
}

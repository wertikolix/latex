package com.hrm.latex.renderer.layout

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import com.hrm.latex.parser.model.LatexNode
import com.hrm.latex.renderer.layout.measurer.AccentMeasurer
import com.hrm.latex.renderer.layout.measurer.DelimiterMeasurer
import com.hrm.latex.renderer.layout.measurer.ExtensibleArrowMeasurer
import com.hrm.latex.renderer.layout.measurer.MathMeasurer
import com.hrm.latex.renderer.layout.measurer.MatrixMeasurer
import com.hrm.latex.renderer.layout.measurer.SpecialEffectMeasurer
import com.hrm.latex.renderer.layout.measurer.StackMeasurer
import com.hrm.latex.renderer.layout.measurer.TextContentMeasurer
import com.hrm.latex.renderer.model.RenderContext
import com.hrm.latex.renderer.model.applyMathStyle
import com.hrm.latex.renderer.model.applyStyle
import com.hrm.latex.renderer.model.withColor
import com.hrm.latex.renderer.utils.lineSpacingPx
import com.hrm.latex.renderer.utils.splitLines

/**
 * 测量节点尺寸与布局
 *
 * Refactored to use component-based architecture
 */
internal fun measureNode(
    node: LatexNode, context: RenderContext, measurer: TextMeasurer, density: Density
): NodeLayout {
    // 实例化组件 (In a real app, these should be cached or singletons if possible, but they are stateless mostly)
    val textMeasurerComp = TextContentMeasurer()
    val mathMeasurer = MathMeasurer()
    val matrixMeasurer = MatrixMeasurer()
    val accentMeasurer = AccentMeasurer()
    val delimiterMeasurer = DelimiterMeasurer()
    val extensibleArrowMeasurer = ExtensibleArrowMeasurer()
    val stackMeasurer = StackMeasurer()
    val specialEffectMeasurer = SpecialEffectMeasurer()

    // 递归函数引用
    val measureGlobal = { n: LatexNode, s: RenderContext ->
        measureNode(n, s, measurer, density)
    }
    val measureGroupRef = { nodes: List<LatexNode>, s: RenderContext ->
        measureGroup(nodes, s, measurer, density)
    }

    return when (node) {
        is LatexNode.Text, is LatexNode.TextMode, is LatexNode.Symbol,
        is LatexNode.Operator, is LatexNode.Command, is LatexNode.Space,
        is LatexNode.HSpace ->
            textMeasurerComp.measure(
                node,
                context,
                measurer,
                density,
                measureGlobal,
                measureGroupRef
            )

        is LatexNode.Fraction, is LatexNode.Root, is LatexNode.Superscript,
        is LatexNode.Subscript, is LatexNode.BigOperator, is LatexNode.Binomial ->
            mathMeasurer.measure(node, context, measurer, density, measureGlobal, measureGroupRef)

        is LatexNode.Matrix, is LatexNode.Array, is LatexNode.Cases, is LatexNode.Aligned,
        is LatexNode.Split, is LatexNode.Multline, is LatexNode.Eqnarray, is LatexNode.Subequations ->
            matrixMeasurer.measure(node, context, measurer, density, measureGlobal, measureGroupRef)

        is LatexNode.Delimited, is LatexNode.ManualSizedDelimiter ->
            delimiterMeasurer.measure(
                node,
                context,
                measurer,
                density,
                measureGlobal,
                measureGroupRef
            )

        is LatexNode.Accent ->
            accentMeasurer.measure(node, context, measurer, density, measureGlobal, measureGroupRef)

        is LatexNode.ExtensibleArrow ->
            extensibleArrowMeasurer.measure(
                node,
                context,
                measurer,
                density,
                measureGlobal,
                measureGroupRef
            )

        is LatexNode.Stack ->
            stackMeasurer.measure(node, context, measurer, density, measureGlobal, measureGroupRef)

        is LatexNode.Boxed, is LatexNode.Phantom ->
            specialEffectMeasurer.measure(
                node,
                context,
                measurer,
                density,
                measureGlobal,
                measureGroupRef
            )

        is LatexNode.NewCommand -> NodeLayout(
            width = 0f,
            height = 0f,
            baseline = 0f
        ) { _, _ -> /* NewCommand 不渲染 */ }

        is LatexNode.NewLine -> NodeLayout(
            0f, lineSpacingPx(context, density), 0f
        ) { _, _ -> }

        is LatexNode.Group -> measureGroup(node.children, context, measurer, density)
        is LatexNode.Document -> measureGroup(node.children, context, measurer, density)

        is LatexNode.Style -> measureGroup(
            node.content, context.applyStyle(node.styleType), measurer, density
        )

        is LatexNode.Color -> measureGroup(
            node.content, context.withColor(node.color), measurer, density
        )

        is LatexNode.MathStyle -> measureGroup(
            node.content, context.applyMathStyle(node.mathStyleType), measurer, density
        )

        is LatexNode.Environment -> measureGroup(node.content, context, measurer, density)
    }
}

/**
 * 测量节点组（处理行内排列和多行）
 */
internal fun measureGroup(
    nodes: List<LatexNode>, context: RenderContext, measurer: TextMeasurer, density: Density
): NodeLayout {
    // 简单处理多行逻辑：按 NewLine 分割，测量各行，垂直堆叠
    val lines = splitLines(nodes)
    if (lines.size > 1) {
        return measureVerticalLines(lines, context, measurer, density)
    }

    // 单行 (InlineRow)
    val measuredNodes = nodes.map { measureNode(it, context, measurer, density) }

    var totalWidth = 0f
    var maxAscent = 0f // 基线以上高度
    var maxDescent = 0f // 基线以下高度

    measuredNodes.forEach {
        val ascent = it.baseline
        val descent = it.height - it.baseline
        if (ascent > maxAscent) maxAscent = ascent
        if (descent > maxDescent) maxDescent = descent
        totalWidth += it.width
    }

    val height = maxAscent + maxDescent
    val baseline = maxAscent

    return NodeLayout(totalWidth, height, baseline) { x, y ->
        var currentX = x
        measuredNodes.forEach { child ->
            val childY = y + (baseline - child.baseline)
            child.draw(this, currentX, childY)
            currentX += child.width
        }
    }
}

private fun measureVerticalLines(
    lines: List<List<LatexNode>>, context: RenderContext, measurer: TextMeasurer, density: Density
): NodeLayout {
    val measuredLines = lines.map { measureGroup(it, context, measurer, density) }
    val maxWidth = measuredLines.maxOfOrNull { it.width } ?: 0f
    val spacing = lineSpacingPx(context, density)

    var totalHeight = 0f
    val positions = measuredLines.map {
        val y = totalHeight
        totalHeight += it.height + spacing
        y
    }
    if (positions.isNotEmpty()) totalHeight -= spacing // 移除最后一个间隙

    // 整个块的基线通常与第一行的基线对齐
    val baseline = measuredLines.firstOrNull()?.baseline ?: 0f

    return NodeLayout(maxWidth, totalHeight, baseline) { x, y ->
        measuredLines.forEachIndexed { i, line ->
            // 默认左对齐
            line.draw(this, x, y + positions[i])
        }
    }
}

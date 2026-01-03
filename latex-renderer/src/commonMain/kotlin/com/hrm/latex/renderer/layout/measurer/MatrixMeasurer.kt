package com.hrm.latex.renderer.layout.measurer

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import com.hrm.latex.parser.model.LatexNode
import com.hrm.latex.renderer.layout.NodeLayout
import com.hrm.latex.renderer.model.RenderContext
import com.hrm.latex.renderer.utils.Side
import com.hrm.latex.renderer.utils.drawBracket
import kotlin.math.max

/**
 * 矩阵与数组测量器
 *
 * 负责测量表格类结构，包括：
 * - 矩阵 (matrix, pmatrix, bmatrix 等)
 * - 数组 (array)
 * - 对齐环境 (aligned)
 * - 分段函数 (cases)
 */
internal class MatrixMeasurer : NodeMeasurer<LatexNode> {

    override fun measure(
        node: LatexNode,
        context: RenderContext,
        measurer: TextMeasurer,
        density: Density,
        measureGlobal: (LatexNode, RenderContext) -> NodeLayout,
        measureGroup: (List<LatexNode>, RenderContext) -> NodeLayout
    ): NodeLayout {
        return when (node) {
            is LatexNode.Matrix -> measureMatrix(node, context, measurer, density, measureGlobal)
            is LatexNode.Array -> measureMatrixLike(node.rows, context, measurer, density, measureGlobal)
            is LatexNode.Cases -> measureCases(node, context, measurer, density, measureGlobal)
            is LatexNode.Aligned -> measureAligned(node.rows, context, measurer, density, measureGlobal)
            is LatexNode.Split -> measureAligned(node.rows, context, measurer, density, measureGlobal)
            is LatexNode.Eqnarray -> measureEqnarray(node.rows, context, measurer, density, measureGlobal)
            is LatexNode.Multline -> measureMultline(node, context, measurer, density, measureGlobal)
            is LatexNode.Subequations -> measureSubequations(node, context, measurer, density, measureGlobal, measureGroup)
            else -> throw IllegalArgumentException("Unsupported node type: ${node::class.simpleName}")
        }
    }

    /**
     * 测量矩阵
     *
     * 1. 测量内部网格内容。
     * 2. 根据矩阵类型添加对应的定界符（圆括号、方括号等）。
     */
    private fun measureMatrix(
        node: LatexNode.Matrix,
        context: RenderContext,
        measurer: TextMeasurer,
        density: Density,
        measureGlobal: (LatexNode, RenderContext) -> NodeLayout
    ): NodeLayout {
        val contentLayout = measureMatrixLike(node.rows, context, measurer, density, measureGlobal)
        val bracketType = node.type
        if (bracketType == LatexNode.Matrix.MatrixType.PLAIN) return contentLayout

        val bracketWidth = with(density) { (context.fontSize * 0.5f).toPx() }
        val strokeWidth = with(density) { (context.fontSize * 0.05f).toPx() }

        val width = contentLayout.width + bracketWidth * 2
        val height = contentLayout.height
        val baseline = contentLayout.baseline

        return NodeLayout(width, height, baseline) { x, y ->
            drawBracket(bracketType, Side.LEFT, x, y, bracketWidth, height, strokeWidth, context.color)
            contentLayout.draw(this, x + bracketWidth, y)
            drawBracket(bracketType, Side.RIGHT, x + width - bracketWidth, y, bracketWidth, height, strokeWidth, context.color)
        }
    }

    /**
     * 通用网格测量逻辑 (Array/Matrix 核心)
     *
     * 算法：
     * 1. 测量所有单元格。
     * 2. 计算每列的最大宽度。
     * 3. 计算每行的最大高度（基线上和基线下）。
     * 4. 计算总宽高达，并进行网格布局。
     */
    private fun measureMatrixLike(
        rows: List<List<LatexNode>>,
        context: RenderContext,
        measurer: TextMeasurer,
        density: Density,
        measureGlobal: (LatexNode, RenderContext) -> NodeLayout
    ): NodeLayout {
        val measuredRows = rows.map { row ->
            row.map { node -> measureGlobal(node, context) }
        }

        val colCount = measuredRows.maxOfOrNull { it.size } ?: 0
        val rowCount = measuredRows.size

        val colWidths = FloatArray(colCount)
        val rowHeights = FloatArray(rowCount)
        val rowBaselines = FloatArray(rowCount)

        // 计算列宽
        for (c in 0 until colCount) {
            var maxW = 0f
            for (r in 0 until rowCount) {
                if (c < measuredRows[r].size) {
                    maxW = max(maxW, measuredRows[r][c].width)
                }
            }
            colWidths[c] = maxW
        }

        // 计算行高和基线
        for (r in 0 until rowCount) {
            var maxAscent = 0f
            var maxDescent = 0f
            for (c in 0 until measuredRows[r].size) {
                val cell = measuredRows[r][c]
                maxAscent = max(maxAscent, cell.baseline)
                maxDescent = max(maxDescent, cell.height - cell.baseline)
            }
            rowHeights[r] = maxAscent + maxDescent
            rowBaselines[r] = maxAscent
        }

        val colSpacing = with(density) { (context.fontSize * 0.5f).toPx() }
        val rowSpacing = with(density) { (context.fontSize * 0.2f).toPx() }

        val totalWidth = colWidths.sum() + colSpacing * max(0, colCount - 1)
        val totalHeight = rowHeights.sum() + rowSpacing * max(0, rowCount - 1)

        val axisHeight = with(density) { (context.fontSize * 0.25f).toPx() }
        val baseline = totalHeight / 2 + axisHeight

        return NodeLayout(totalWidth, totalHeight, baseline) { x, y ->
            var currentY = y
            for (r in 0 until rowCount) {
                var currentX = x
                val rowBaseY = currentY + rowBaselines[r]

                for (c in 0 until measuredRows[r].size) {
                    val cell = measuredRows[r][c]
                    // 单元格居中对齐
                    val cellX = currentX + (colWidths[c] - cell.width) / 2
                    val cellY = rowBaseY - cell.baseline

                    cell.draw(this, cellX, cellY)
                    currentX += colWidths[c] + colSpacing
                }
                currentY += rowHeights[r] + rowSpacing
            }
        }
    }

    /**
     * 测量分段函数 (cases)
     *
     * 类似于一个两列的表格，但左侧有大括号，且默认左对齐。
     */
    private fun measureCases(
        node: LatexNode.Cases,
        context: RenderContext,
        measurer: TextMeasurer,
        density: Density,
        measureGlobal: (LatexNode, RenderContext) -> NodeLayout
    ): NodeLayout {
        // 将 cases 转换为 "表达式 if 条件" 的三列结构或两列结构
        val rows = node.cases.map { (cond, expr) ->
            listOf(expr, LatexNode.Text(" if "), cond)
        }

        val matrixLayout = measureMatrixLike(rows, context, measurer, density, measureGlobal)
        val bracketWidth = with(density) { (context.fontSize * 0.5f).toPx() }
        val strokeWidth = with(density) { (context.fontSize * 0.05f).toPx() }

        val width = bracketWidth + matrixLayout.width
        val height = matrixLayout.height
        val baseline = matrixLayout.baseline

        return NodeLayout(width, height, baseline) { x, y ->
            // 绘制左大括号
            drawBracket(
                LatexNode.Matrix.MatrixType.BRACE,
                Side.LEFT,
                x,
                y,
                bracketWidth,
                height,
                strokeWidth,
                context.color
            )
            matrixLayout.draw(this, x + bracketWidth, y)
        }
    }

    /**
     * 测量 multline 环境
     * 第一行左对齐，最后一行右对齐，中间行居中
     */
    private fun measureMultline(
        node: LatexNode.Multline,
        context: RenderContext,
        measurer: TextMeasurer,
        density: Density,
        measureGlobal: (LatexNode, RenderContext) -> NodeLayout
    ): NodeLayout {
        if (node.lines.isEmpty()) {
            return NodeLayout(0f, 0f, 0f) { _, _ -> }
        }

        // 测量每一行
        val lineLayouts = node.lines.map { line ->
            measureGlobal(line, context)
        }

        // 计算总宽度（取最宽行）
        val maxWidth = lineLayouts.maxOfOrNull { it.width } ?: 0f
        val rowSpacing = with(density) { (context.fontSize * 0.3f).toPx() }
        
        // 计算总高度
        val totalHeight = lineLayouts.sumOf { it.height.toDouble() }.toFloat() +
                rowSpacing * (lineLayouts.size - 1)
        
        // 基线为第一行的基线
        val baseline = lineLayouts.first().baseline

        return NodeLayout(maxWidth, totalHeight, baseline) { x, y ->
            var currentY = y
            lineLayouts.forEachIndexed { index, layout ->
                // 第一行左对齐，最后一行右对齐，中间行居中
                val offsetX = when {
                    index == 0 -> 0f  // 第一行左对齐
                    index == lineLayouts.lastIndex -> maxWidth - layout.width  // 最后一行右对齐
                    else -> (maxWidth - layout.width) / 2  // 中间行居中
                }
                layout.draw(this, x + offsetX, currentY)
                currentY += layout.height + rowSpacing
            }
        }
    }

    /**
     * 测量 aligned/split 环境
     * 支持 & 对齐符号，偶数列右对齐，奇数列左对齐
     */
    private fun measureAligned(
        rows: List<List<LatexNode>>,
        context: RenderContext,
        measurer: TextMeasurer,
        density: Density,
        measureGlobal: (LatexNode, RenderContext) -> NodeLayout
    ): NodeLayout {
        val measuredRows = rows.map { row ->
            row.map { node -> measureGlobal(node, context) }
        }

        val colCount = measuredRows.maxOfOrNull { it.size } ?: 0
        val rowCount = measuredRows.size

        val colWidths = FloatArray(colCount)
        val rowHeights = FloatArray(rowCount)
        val rowBaselines = FloatArray(rowCount)

        // 计算列宽
        for (c in 0 until colCount) {
            var maxW = 0f
            for (r in 0 until rowCount) {
                if (c < measuredRows[r].size) {
                    maxW = max(maxW, measuredRows[r][c].width)
                }
            }
            colWidths[c] = maxW
        }

        // 计算行高和基线
        for (r in 0 until rowCount) {
            var maxAscent = 0f
            var maxDescent = 0f
            for (c in 0 until measuredRows[r].size) {
                val cell = measuredRows[r][c]
                maxAscent = max(maxAscent, cell.baseline)
                maxDescent = max(maxDescent, cell.height - cell.baseline)
            }
            rowHeights[r] = maxAscent + maxDescent
            rowBaselines[r] = maxAscent
        }

        val colSpacing = with(density) { (context.fontSize * 0.3f).toPx() }
        val rowSpacing = with(density) { (context.fontSize * 0.2f).toPx() }

        val totalWidth = colWidths.sum() + colSpacing * max(0, colCount - 1)
        val totalHeight = rowHeights.sum() + rowSpacing * max(0, rowCount - 1)

        val axisHeight = with(density) { (context.fontSize * 0.25f).toPx() }
        val baseline = totalHeight / 2 + axisHeight

        return NodeLayout(totalWidth, totalHeight, baseline) { x, y ->
            var currentY = y
            for (r in 0 until rowCount) {
                var currentX = x
                val rowBaseY = currentY + rowBaselines[r]

                for (c in 0 until measuredRows[r].size) {
                    val cell = measuredRows[r][c]
                    // 偶数列（0,2,4...）右对齐，奇数列（1,3,5...）左对齐
                    val cellX = if (c % 2 == 0) {
                        // 右对齐
                        currentX + colWidths[c] - cell.width
                    } else {
                        // 左对齐
                        currentX
                    }
                    val cellY = rowBaseY - cell.baseline

                    cell.draw(this, cellX, cellY)
                    currentX += colWidths[c] + colSpacing
                }
                currentY += rowHeights[r] + rowSpacing
            }
        }
    }

    /**
     * 测量 eqnarray 环境
     * 三列结构：右对齐 - 居中 - 左对齐
     */
    private fun measureEqnarray(
        rows: List<List<LatexNode>>,
        context: RenderContext,
        measurer: TextMeasurer,
        density: Density,
        measureGlobal: (LatexNode, RenderContext) -> NodeLayout
    ): NodeLayout {
        val measuredRows = rows.map { row ->
            row.map { node -> measureGlobal(node, context) }
        }

        val colCount = measuredRows.maxOfOrNull { it.size } ?: 0
        val rowCount = measuredRows.size

        val colWidths = FloatArray(colCount)
        val rowHeights = FloatArray(rowCount)
        val rowBaselines = FloatArray(rowCount)

        // 计算列宽
        for (c in 0 until colCount) {
            var maxW = 0f
            for (r in 0 until rowCount) {
                if (c < measuredRows[r].size) {
                    maxW = max(maxW, measuredRows[r][c].width)
                }
            }
            colWidths[c] = maxW
        }

        // 计算行高和基线
        for (r in 0 until rowCount) {
            var maxAscent = 0f
            var maxDescent = 0f
            for (c in 0 until measuredRows[r].size) {
                val cell = measuredRows[r][c]
                maxAscent = max(maxAscent, cell.baseline)
                maxDescent = max(maxDescent, cell.height - cell.baseline)
            }
            rowHeights[r] = maxAscent + maxDescent
            rowBaselines[r] = maxAscent
        }

        val colSpacing = with(density) { (context.fontSize * 0.5f).toPx() }
        val rowSpacing = with(density) { (context.fontSize * 0.2f).toPx() }

        val totalWidth = colWidths.sum() + colSpacing * max(0, colCount - 1)
        val totalHeight = rowHeights.sum() + rowSpacing * max(0, rowCount - 1)

        val axisHeight = with(density) { (context.fontSize * 0.25f).toPx() }
        val baseline = totalHeight / 2 + axisHeight

        return NodeLayout(totalWidth, totalHeight, baseline) { x, y ->
            var currentY = y
            for (r in 0 until rowCount) {
                var currentX = x
                val rowBaseY = currentY + rowBaselines[r]

                for (c in 0 until measuredRows[r].size) {
                    val cell = measuredRows[r][c]
                    // 第0列右对齐，第1列居中，第2列左对齐
                    val cellX = when (c) {
                        0 -> currentX + colWidths[c] - cell.width  // 右对齐
                        1 -> currentX + (colWidths[c] - cell.width) / 2  // 居中
                        else -> currentX  // 左对齐
                    }
                    val cellY = rowBaseY - cell.baseline

                    cell.draw(this, cellX, cellY)
                    currentX += colWidths[c] + colSpacing
                }
                currentY += rowHeights[r] + rowSpacing
            }
        }
    }

    /**
     * 测量 subequations 环境
     * 简单地渲染其内部内容
     */
    private fun measureSubequations(
        node: LatexNode.Subequations,
        context: RenderContext,
        measurer: TextMeasurer,
        density: Density,
        measureGlobal: (LatexNode, RenderContext) -> NodeLayout,
        measureGroup: (List<LatexNode>, RenderContext) -> NodeLayout
    ): NodeLayout {
        // subequations 主要用于编号，在渲染层面直接渲染内容即可
        return measureGroup(node.content, context)
    }
}

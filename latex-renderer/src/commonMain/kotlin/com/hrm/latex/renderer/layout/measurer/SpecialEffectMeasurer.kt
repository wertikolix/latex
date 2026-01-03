package com.hrm.latex.renderer.layout.measurer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.hrm.latex.parser.model.LatexNode
import com.hrm.latex.renderer.layout.NodeLayout
import com.hrm.latex.renderer.model.RenderContext

/**
 * 测量特殊效果节点（boxed, phantom）
 */
internal class SpecialEffectMeasurer {

    fun measure(
        node: LatexNode,
        context: RenderContext,
        measurer: TextMeasurer,
        density: Density,
        measureGlobal: (LatexNode, RenderContext) -> NodeLayout,
        measureGroup: (List<LatexNode>, RenderContext) -> NodeLayout
    ): NodeLayout {
        return when (node) {
            is LatexNode.Boxed -> measureBoxed(node, context, measurer, density, measureGroup)
            is LatexNode.Phantom -> measurePhantom(node, context, measurer, density, measureGroup)
            else -> throw IllegalArgumentException("Unsupported node type: ${node::class.simpleName}")
        }
    }

    /**
     * 测量 \boxed{...} 方框效果
     * 
     * 在内容周围绘制矩形边框，添加内边距
     */
    private fun measureBoxed(
        node: LatexNode.Boxed,
        context: RenderContext,
        measurer: TextMeasurer,
        density: Density,
        measureGroup: (List<LatexNode>, RenderContext) -> NodeLayout
    ): NodeLayout {
        val contentLayout = measureGroup(node.content, context)
        
        // 内边距（相对于字体大小）
        val padding = with(density) { (context.fontSize * 0.15f).toPx() }
        val borderWidth = with(density) { 1.dp.toPx() }
        
        val totalWidth = contentLayout.width + 2 * padding
        val totalHeight = contentLayout.height + 2 * padding
        val baseline = contentLayout.baseline + padding
        
        return NodeLayout(totalWidth, totalHeight, baseline) { x, y ->
            // 绘制内容
            contentLayout.draw(this, x + padding, y + padding)
            
            // 绘制边框
            drawRect(
                color = context.color,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(totalWidth, totalHeight),
                style = Stroke(width = borderWidth)
            )
        }
    }

    /**
     * 测量 \phantom{...} 幻影效果
     * 
     * 占据空间但不绘制内容（用于对齐）
     */
    private fun measurePhantom(
        node: LatexNode.Phantom,
        context: RenderContext,
        measurer: TextMeasurer,
        density: Density,
        measureGroup: (List<LatexNode>, RenderContext) -> NodeLayout
    ): NodeLayout {
        val contentLayout = measureGroup(node.content, context)
        
        // 保留尺寸 and 基线，但不绘制任何内容
        return NodeLayout(
            contentLayout.width,
            contentLayout.height,
            contentLayout.baseline
        ) { _, _ ->
            // 不绘制任何内容（幻影）
        }
    }
}

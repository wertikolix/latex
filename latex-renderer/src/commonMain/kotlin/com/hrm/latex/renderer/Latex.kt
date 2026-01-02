package com.hrm.latex.renderer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import com.hrm.latex.parser.LatexParser
import com.hrm.latex.parser.model.LatexNode
import com.hrm.latex.renderer.layout.measureGroup
import com.hrm.latex.renderer.model.RenderStyle

/**
 * Latex 渲染组件
 *
 * @param latex LaTeX 字符串
 * @param modifier 修饰符
 * @param style 渲染样式（包含颜色、背景色、深浅色模式配置、字体大小等）
 * @param isDarkTheme 是否为深色模式（默认跟随系统）
 * @param parser Latex 解析器
 */
@Composable
fun Latex(
    latex: String,
    modifier: Modifier = Modifier,
    style: RenderStyle = RenderStyle(),
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    parser: LatexParser = remember { LatexParser() }
) {
    // 确定最终文本颜色
    val resolvedTextColor = if (isDarkTheme) {
        if (style.darkColor != Color.Unspecified) style.darkColor else Color.White
    } else {
        if (style.color != Color.Unspecified) style.color else Color.Black
    }

    // 确定最终背景颜色
    val resolvedBackgroundColor = if (isDarkTheme) {
        style.darkBackgroundColor
    } else {
        style.backgroundColor
    }

    // 构建最终样式
    val resolvedStyle = style.copy(
        color = resolvedTextColor,
        backgroundColor = resolvedBackgroundColor
    )

    // 异步解析状态
    var document by remember { mutableStateOf<LatexNode.Document?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    // 当 latex 变化时，在后台线程解析
    LaunchedEffect(latex, parser) {
        withContext(Dispatchers.Default) {
            try {
                val result = parser.parse(latex)
                withContext(Dispatchers.Main) {
                    document = result
                    error = null
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = e.message
                    // 解析失败时，如果之前有结果，可以保留显示，或者置空
                    // 这里选择置空以反馈错误
                    document = null 
                }
            }
        }
    }

    if (document != null) {
        LatexDocument(
            modifier = modifier,
            children = document!!.children,
            style = resolvedStyle
        )
    } else if (error != null) {
        Text(text = "Error: $error", color = Color.Red, modifier = modifier)
    } else {
        // 解析中或空状态
        // 可以选择显示一个占位符，或者什么都不显示
    }
}

/**
 * Latex 文档渲染组件
 *
 * @param modifier 修饰符
 * @param children 文档根节点
 * @param style 渲染样式
 */
@Composable
fun LatexDocument(
    modifier: Modifier = Modifier,
    children: List<LatexNode>,
    style: RenderStyle = RenderStyle()
) {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val layout = remember(children, style, density) {
        measureGroup(children, style, measurer, density)
    }

    val widthDp = with(density) { layout.width.toDp() }
    val heightDp = with(density) { layout.height.toDp() }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.size(widthDp, heightDp)) {
            // 绘制背景
            if (style.backgroundColor != Color.Unspecified) {
                drawRect(color = style.backgroundColor)
            }
            // 绘制内容
            layout.draw(this, 0f, 0f)
        }
    }
}

package com.hrm.latex.renderer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hrm.latex.parser.IncrementalLatexParser
import com.hrm.latex.parser.model.LatexNode
import com.hrm.latex.renderer.model.RenderStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * 增量渲染的 LaTeX 组件
 * 支持逐步输入并实时渲染 LaTeX 内容
 *
 * @param latex 当前的 LaTeX 内容（可以逐步更新）
 * @param modifier 修饰符
 * @param isDarkTheme 是否使用暗色主题
 * @param style 渲染样式
 */
@Composable
fun IncrementalLatex(
    latex: String,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    style: RenderStyle = RenderStyle()
) {
    // 使用 State 来保存解析结果
    var document by remember { mutableStateOf(LatexNode.Document(emptyList())) }
    // 记录上次解析的内容，避免重复解析
    var lastParsedLatex by remember { mutableStateOf("") }

    // 当 latex 变化时更新解析（在后台线程执行，避免阻塞主线程）
    LaunchedEffect(latex) {
        // 防抖：如果内容没变，跳过
        if (latex == lastParsedLatex) {
            return@LaunchedEffect
        }

        // 再次检查是否有新的更新
        if (latex != lastParsedLatex) {
            lastParsedLatex = latex

            // 切换到 Default 调度器执行解析
            val result = withContext(Dispatchers.Default) {
                try {
                    // 每次重新创建解析器，避免状态累积
                    val parser = IncrementalLatexParser()
                    parser.append(latex)
                    parser.getCurrentDocument()
                } catch (e: Exception) {
                    // 解析失败时返回空文档
                    LatexNode.Document(emptyList())
                }
            }
            // 回到主线程更新 UI
            document = result
        }
    }

    LatexDocument(
        modifier = modifier,
        children = document.children,
        style = style
    )
}

/**
 * 带打字机效果的增量 LaTeX 组件
 * 自动逐字符显示 LaTeX 内容
 *
 * @param fullLatex 完整的 LaTeX 内容
 * @param typingSpeed 打字速度（毫秒/字符）
 * @param modifier 修饰符
 * @param isDarkTheme 是否使用暗色主题
 * @param style 渲染样式
 * @param onComplete 完成回调
 */
@Composable
fun TypewriterLatex(
    fullLatex: String,
    typingSpeed: Long = 50L,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    style: RenderStyle = RenderStyle(),
    onComplete: (() -> Unit)? = null
) {
    var currentText by remember(fullLatex) { mutableStateOf("") }

    // 打字机效果
    LaunchedEffect(fullLatex) {
        currentText = ""
        var currentIndex = 0

        while (currentIndex < fullLatex.length) {
            delay(typingSpeed)
            currentIndex++
            currentText = fullLatex.take(currentIndex)
        }

        onComplete?.invoke()
    }

    IncrementalLatex(
        latex = currentText,
        modifier = modifier,
        isDarkTheme = isDarkTheme,
        style = style
    )
}

/**
 * 带流式加载效果的增量 LaTeX 组件
 * 模拟网络流式传输的效果
 *
 * @param fullLatex 完整的 LaTeX 内容
 * @param chunkSize 每次加载的块大小
 * @param chunkDelay 块之间的延迟（毫秒）
 * @param modifier 修饰符
 * @param isDarkTheme 是否使用暗色主题
 * @param style 渲染样式
 * @param onComplete 完成回调
 */
@Composable
fun StreamingLatex(
    fullLatex: String,
    chunkSize: Int = 5,
    chunkDelay: Long = 100L,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    style: RenderStyle = RenderStyle(),
    onComplete: (() -> Unit)? = null
) {
    var currentText by remember(fullLatex) { mutableStateOf("") }

    // 流式加载效果
    LaunchedEffect(fullLatex) {
        currentText = ""
        var currentIndex = 0

        while (currentIndex < fullLatex.length) {
            delay(chunkDelay)
            val nextIndex = kotlin.math.min(currentIndex + chunkSize, fullLatex.length)
            currentText = fullLatex.take(nextIndex)
            currentIndex = nextIndex
        }

        onComplete?.invoke()
    }

    IncrementalLatex(
        latex = currentText,
        modifier = modifier,
        isDarkTheme = isDarkTheme,
        style = style
    )
}

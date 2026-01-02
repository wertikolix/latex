package com.hrm.latex.parser

import com.hrm.latex.base.log.HLog
import com.hrm.latex.parser.model.LatexNode

/**
 * 增量 LaTeX 解析器
 * 支持逐步输入 LaTeX 内容并实时解析渲染
 *
 * 使用场景：
 * - 实时输入/打字效果
 * - 流式传输的 LaTeX 内容
 * - 实时预览
 *
 * 示例：
 * ```kotlin
 * val parser = IncrementalLatexParser()
 * parser.append("\\int_{-\\")
 * val result1 = parser.getCurrentDocument() // 解析已有内容
 * parser.append("infty}^{\\infty}")
 * val result2 = parser.getCurrentDocument() // 更新解析结果
 * ```
 */
class IncrementalLatexParser {

    /**
     * 累积的输入内容
     */
    private val buffer = StringBuilder()

    /**
     * 最后一次成功解析的位置
     */
    private var lastSuccessfulPosition = 0

    /**
     * 缓存的解析结果
     */
    private var cachedDocument: LatexNode.Document? = null

    /**
     * 基础解析器实例
     */
    private val baseParser = LatexParser()

    companion object {
        private const val TAG = "IncrementalLatexParser"
    }

    /**
     * 追加新的 LaTeX 内容
     * @param text 新增的文本内容
     */
    fun append(text: String) {
        buffer.append(text)
        HLog.d(TAG, "追加内容: $text, 当前缓冲区: $buffer")

        // 触发重新解析
        reparseFromLastPosition()
    }

    /**
     * 清空所有内容和状态
     */
    fun clear() {
        buffer.clear()
        lastSuccessfulPosition = 0
        cachedDocument = null
        HLog.d(TAG, "清空解析器状态")
    }

    /**
     * 获取当前可解析的文档
     * 会尽可能解析已有内容，即使内容不完整
     */
    fun getCurrentDocument(): LatexNode.Document {
        if (cachedDocument == null) {
            reparseFromLastPosition()
        }
        return cachedDocument ?: LatexNode.Document(emptyList())
    }

    /**
     * 获取当前缓冲区的完整内容
     */
    fun getCurrentInput(): String = buffer.toString()

    /**
     * 从上次成功位置重新解析
     */
    private fun reparseFromLastPosition() {
        val input = buffer.toString()
        if (input.isEmpty()) {
            cachedDocument = LatexNode.Document(emptyList())
            return
        }

        // 快速路径：如果输入很短，直接尝试解析
        if (input.length <= 5) {
            try {
                cachedDocument = baseParser.parse(input)
                lastSuccessfulPosition = input.length
                return
            } catch (e: Exception) {
                cachedDocument = LatexNode.Document(emptyList())
                return
            }
        }

        try {
            // 尝试完整解析
            cachedDocument = baseParser.parse(input)
            lastSuccessfulPosition = input.length
            HLog.d(TAG, "完整解析成功")
        } catch (e: Exception) {
            // 完整解析失败，尝试部分解析
            HLog.d(TAG, "完整解析失败，尝试部分解析: ${e.message}")
            cachedDocument = parsePartial(input)
        }
    }

    /**
     * 部分解析：处理不完整的 LaTeX 内容
     * 策略：从最长开始线性回退，找到最长可解析前缀
     */
    private fun parsePartial(input: String): LatexNode.Document {
        HLog.d(TAG, "开始部分解析，输入长度: ${input.length}")

        // 策略：从末尾开始逐字符回退，找到最长可解析前缀
        // 这种方式比二分查找更可靠，因为解析有效性不是单调的
        // 示例：\int_{ 失败，但 \int 成功

        // 第一阶段：精细回退 (最近 100 字符)
        // 通常增量输入时，错误点就在末尾附近
        var length = input.length
        val firstStageLimit = maxOf(1, input.length - 100)

        while (length >= firstStageLimit) {
            try {
                val testInput = input.substring(0, length)
                val doc = baseParser.parse(testInput)
                lastSuccessfulPosition = length
                return doc
            } catch (e: Exception) {
                length--
            }
        }

        // 第二阶段：快速回退 (如果错误在很前面)
        // 步进加大，牺牲一点精度换取性能
        while (length > 0) {
            try {
                val testInput = input.substring(0, length)
                val doc = baseParser.parse(testInput)
                lastSuccessfulPosition = length
                return doc
            } catch (e: Exception) {
                length -= 5 // 步进 5
                if (length < 0) length = 0
            }
        }

        HLog.w(TAG, "部分解析失败，返回空文档")
        return LatexNode.Document(emptyList())
    }

    /**
     * 获取解析进度（已成功解析的字符数 / 总字符数）
     */
    fun getProgress(): Float {
        val total = buffer.length
        return if (total > 0) {
            lastSuccessfulPosition.toFloat() / total
        } else {
            1f
        }
    }

    /**
     * 获取未解析的部分（可用于调试或显示）
     */
    fun getUnparsedContent(): String {
        val total = buffer.toString()
        return if (lastSuccessfulPosition < total.length) {
            total.substring(lastSuccessfulPosition)
        } else {
            ""
        }
    }
}

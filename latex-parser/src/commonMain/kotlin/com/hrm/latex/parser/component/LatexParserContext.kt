package com.hrm.latex.parser.component

import com.hrm.latex.parser.model.LatexNode
import com.hrm.latex.parser.tokenizer.LatexToken

/**
 * 解析器上下文接口，用于解决循环依赖和提供通用解析能力
 */
interface LatexParserContext {
    val tokenStream: LatexTokenStream

    fun parseExpression(): LatexNode?
    fun parseArgument(): LatexNode?
    fun parseGroup(): LatexNode.Group
}

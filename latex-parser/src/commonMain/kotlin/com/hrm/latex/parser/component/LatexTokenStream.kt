package com.hrm.latex.parser.component

import com.hrm.latex.parser.tokenizer.LatexToken

/**
 * 封装 Token 流的操作，如 peek, advance, expect
 */
class LatexTokenStream(private val tokens: List<LatexToken>) {
    private var position = 0

    fun peek(offset: Int = 0): LatexToken? {
        val pos = position + offset
        return if (pos < tokens.size) tokens[pos] else null
    }

    fun advance(): LatexToken? {
        val token = peek()
        position++
        return token
    }

    fun isEOF(): Boolean {
        val token = peek()
        return token == null || token is LatexToken.EOF
    }

    fun expect(type: String, message: String? = null): LatexToken {
        val token = peek()
        if (token == null) {
            throw Exception(message ?: "期望 $type，但到达文件末尾")
        }
        advance()
        return token
    }
    
    fun reset() {
        position = 0
    }
}

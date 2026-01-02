package com.hrm.latex.parser.tokenizer

import com.hrm.latex.base.log.HLog

/**
 * LaTeX 词法分析器
 */
class LatexTokenizer(private val input: String) {
    private var position = 0
    private val tokens = mutableListOf<LatexToken>()

    companion object {
        private const val TAG = "LatexTokenizer"
    }

    /**
     * 执行词法分析
     */
    fun tokenize(): List<LatexToken> {
        HLog.d(TAG, "开始词法分析，输入长度: ${input.length}")

        while (position < input.length) {
            when (val char = peek()) {
                '\\' -> handleBackslash()
                '{' -> {
                    tokens.add(LatexToken.LeftBrace)
                    advance()
                }

                '}' -> {
                    tokens.add(LatexToken.RightBrace)
                    advance()
                }

                '[' -> {
                    tokens.add(LatexToken.LeftBracket)
                    advance()
                }

                ']' -> {
                    tokens.add(LatexToken.RightBracket)
                    advance()
                }

                '^' -> {
                    tokens.add(LatexToken.Superscript)
                    advance()
                }

                '_' -> {
                    tokens.add(LatexToken.Subscript)
                    advance()
                }

                '&' -> {
                    tokens.add(LatexToken.Ampersand)
                    advance()
                }

                '\n', '\r' -> handleNewLine()
                ' ', '\t' -> handleWhitespace()
                else -> handleText()
            }
        }

        tokens.add(LatexToken.EOF)
        HLog.d(TAG, "词法分析完成，生成 ${tokens.size} 个 token")
        return tokens
    }

    private fun peek(offset: Int = 0): Char? {
        val pos = position + offset
        return if (pos < input.length) input[pos] else null
    }

    private fun advance(count: Int = 1): Char? {
        val char = peek()
        position += count
        return char
    }

    private fun handleBackslash() {
        advance() // 跳过 \

        if (peek() == '\\') {
            // \\ 表示换行
            tokens.add(LatexToken.NewLine)
            advance()
            return
        }

        // 读取命令名
        val commandName = buildString {
            while (position < input.length) {
                val char = peek() ?: break
                if (char.isLetter() || (isEmpty() && char == '@')) {
                    append(char)
                    advance()
                } else {
                    break
                }
            }
        }

        if (commandName.isEmpty()) {
            // 处理特殊符号，如 \{, \}, \$, \% 等
            val char = peek()
            if (char != null && !char.isWhitespace()) {
                tokens.add(LatexToken.Command(char.toString()))
                advance()
            }
            return
        }

        // 检查是否是环境开始或结束
        when (commandName) {
            "begin" -> {
                val envName = readEnvironmentName()
                if (envName != null) {
                    tokens.add(LatexToken.BeginEnvironment(envName))
                } else {
                    tokens.add(LatexToken.Command(commandName))
                }
            }

            "end" -> {
                val envName = readEnvironmentName()
                if (envName != null) {
                    tokens.add(LatexToken.EndEnvironment(envName))
                } else {
                    tokens.add(LatexToken.Command(commandName))
                }
            }

            else -> {
                tokens.add(LatexToken.Command(commandName))
            }
        }
    }

    private fun readEnvironmentName(): String? {
        skipWhitespace()
        if (peek() != '{') return null

        advance() // 跳过 {
        val name = buildString {
            while (position < input.length) {
                val char = peek() ?: break
                if (char == '}') break
                append(char)
                advance()
            }
        }

        if (peek() == '}') {
            advance() // 跳过 }
            return name
        }
        return null
    }

    private fun handleText() {
        val text = buildString {
            while (position < input.length) {
                val char = peek() ?: break
                if (char in setOf('\\', '{', '}', '[', ']', '^', '_', '&', '\n', '\r')) {
                    break
                }
                if (char == ' ' || char == '\t') {
                    break
                }
                append(char)
                advance()
            }
        }

        if (text.isNotEmpty()) {
            tokens.add(LatexToken.Text(text))
        }
    }

    private fun handleWhitespace() {
        val whitespace = buildString {
            while (position < input.length) {
                val char = peek() ?: break
                if (char != ' ' && char != '\t') break
                append(char)
                advance()
            }
        }
        tokens.add(LatexToken.Whitespace(whitespace))
    }

    private fun handleNewLine() {
        val newline = buildString {
            while (position < input.length) {
                val char = peek() ?: break
                if (char != '\n' && char != '\r') break
                append(char)
                advance()
            }
        }
        // 暂时不生成 token，跳过换行符
        // 如果需要保留换行信息，可以取消注释：
        // tokens.add(LatexToken.Whitespace(newline))
    }

    private fun skipWhitespace() {
        while (position < input.length) {
            val char = peek() ?: break
            if (!char.isWhitespace()) break
            advance()
        }
    }
}

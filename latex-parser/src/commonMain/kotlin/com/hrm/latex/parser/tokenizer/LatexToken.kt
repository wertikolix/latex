package com.hrm.latex.parser.tokenizer

/**
 * LaTeX 词法单元
 */
sealed class LatexToken {
    data class Text(val content: String) : LatexToken()
    data class Command(val name: String) : LatexToken()
    data class BeginEnvironment(val name: String) : LatexToken()
    data class EndEnvironment(val name: String) : LatexToken()
    data object LeftBrace : LatexToken()
    data object RightBrace : LatexToken()
    data object LeftBracket : LatexToken()
    data object RightBracket : LatexToken()
    data object Superscript : LatexToken()
    data object Subscript : LatexToken()
    data object Ampersand : LatexToken()
    data object NewLine : LatexToken()
    data class Whitespace(val content: String) : LatexToken()
    data object EOF : LatexToken()
}

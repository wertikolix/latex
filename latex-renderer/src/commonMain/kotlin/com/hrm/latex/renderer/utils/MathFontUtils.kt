package com.hrm.latex.renderer.utils

/**
 * 数学字体 Unicode 转换工具
 * 
 * 在无法直接加载 .ttf 字体文件时，通过映射 Unicode 数学字母块
 * 来实现 \mathbb, \mathcal, \mathfrak 等效果。
 */
object MathFontUtils {
    
    /**
     * 转换为双线体 (Blackboard Bold) - \mathbb
     * 映射范围: A-Z (U+1D538), a-z (U+1D552), 0-9 (U+1D7D8)
     */
    fun toBlackboardBold(text: String): String {
        return text.map { char ->
            when (char) {
                in 'A'..'Z' -> {
                    // 特殊情况：C, H, N, P, Q, R, Z 在 Unicode 中不在连续块内
                    when (char) {
                        'C' -> 'ℂ'
                        'H' -> 'ℍ'
                        'N' -> 'ℕ'
                        'P' -> 'ℙ'
                        'Q' -> 'ℚ'
                        'R' -> 'ℝ'
                        'Z' -> 'ℤ'
                        else -> char + (0x1D538 - 'A'.code)
                    }
                }
                in 'a'..'z' -> char + (0x1D552 - 'a'.code)
                in '0'..'9' -> char + (0x1D7D8 - '0'.code)
                else -> char
            }
        }.joinToString("")
    }

    /**
     * 转换为花体 (Calligraphic) - \mathcal
     * 映射范围: A-Z (U+1D49C)
     */
    fun toCalligraphic(text: String): String {
        return text.map { char ->
            when (char) {
                in 'A'..'Z' -> {
                    // 特殊情况：B, E, F, H, I, L, M, R 在 Unicode 中不在连续块内
                    when (char) {
                        'B' -> 'ℬ'
                        'E' -> 'ℰ'
                        'F' -> 'ℱ'
                        'H' -> 'ℋ'
                        'I' -> 'ℐ'
                        'L' -> 'ℒ'
                        'M' -> 'ℳ'
                        'R' -> 'ℛ'
                        else -> char + (0x1D49C - 'A'.code)
                    }
                }
                else -> char
            }
        }.joinToString("")
    }
}

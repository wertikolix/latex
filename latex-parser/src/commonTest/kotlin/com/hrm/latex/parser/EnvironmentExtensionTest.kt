package com.hrm.latex.parser

import com.hrm.latex.parser.model.LatexNode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * 环境扩展测试
 * 测试 split, multline, eqnarray, subequations 环境
 */
class EnvironmentExtensionTest {
    private val parser = LatexParser()

    // ========== Split 环境测试 ==========

    @Test
    fun testSplitBasic() {
        val doc = parser.parse(
            """
            \begin{split}
            x &= a + b \\
            &= c
            \end{split}
        """.trimIndent()
        )
        assertTrue(doc.children[0] is LatexNode.Split)
        val split = doc.children[0] as LatexNode.Split
        assertEquals(2, split.rows.size)
    }

    @Test
    fun testSplitMultipleAlignments() {
        val doc = parser.parse(
            """
            \begin{split}
            a &= b + c \\
            &= d + e \\
            &= f
            \end{split}
        """.trimIndent()
        )
        val split = doc.children[0] as LatexNode.Split
        assertEquals(3, split.rows.size)
    }

    @Test
    fun testSplitWithFractions() {
        val doc = parser.parse(
            """
            \begin{split}
            \frac{x}{y} &= \frac{a}{b} \\
            &= \frac{c}{d}
            \end{split}
        """.trimIndent()
        )
        assertTrue(doc.children[0] is LatexNode.Split)
    }

    // ========== Multline 环境测试 ==========

    @Test
    fun testMultlineBasic() {
        val doc = parser.parse(
            """
            \begin{multline}
            a + b + c + d \\
            + e + f + g
            \end{multline}
        """.trimIndent()
        )
        assertTrue(doc.children[0] is LatexNode.Multline)
        val multline = doc.children[0] as LatexNode.Multline
        assertEquals(2, multline.lines.size)
    }

    @Test
    fun testMultlineThreeLines() {
        val doc = parser.parse(
            """
            \begin{multline}
            \text{First line (left)} \\
            \text{Second line (center)} \\
            \text{Third line (right)}
            \end{multline}
        """.trimIndent()
        )
        val multline = doc.children[0] as LatexNode.Multline
        assertEquals(3, multline.lines.size)
    }

    @Test
    fun testMultlineWithComplexFormula() {
        val doc = parser.parse(
            """
            \begin{multline}
            \int_0^1 x^2 dx \\
            + \sum_{i=1}^n i \\
            = \frac{1}{3} + \frac{n(n+1)}{2}
            \end{multline}
        """.trimIndent()
        )
        assertTrue(doc.children[0] is LatexNode.Multline)
    }

    // ========== Eqnarray 环境测试 ==========

    @Test
    fun testEqnarrayBasic() {
        val doc = parser.parse(
            """
            \begin{eqnarray}
            x &=& 1 \\
            y &=& 2
            \end{eqnarray}
        """.trimIndent()
        )
        assertTrue(doc.children[0] is LatexNode.Eqnarray)
        val eqnarray = doc.children[0] as LatexNode.Eqnarray
        assertEquals(2, eqnarray.rows.size)
    }

    @Test
    fun testEqnarrayThreeColumns() {
        val doc = parser.parse(
            """
            \begin{eqnarray}
            a + b &=& c \\
            d - e &=& f \\
            g \times h &=& i
            \end{eqnarray}
        """.trimIndent()
        )
        val eqnarray = doc.children[0] as LatexNode.Eqnarray
        assertEquals(3, eqnarray.rows.size)
        // 每行应该有3个单元格（左边、关系符、右边）
        assertTrue(eqnarray.rows[0].size <= 3)
    }

    @Test
    fun testEqnarrayWithComplexExpressions() {
        val doc = parser.parse(
            """
            \begin{eqnarray}
            \frac{x}{y} &=& \sqrt{z} \\
            x^2 + y^2 &=& z^2
            \end{eqnarray}
        """.trimIndent()
        )
        assertTrue(doc.children[0] is LatexNode.Eqnarray)
    }

    // ========== Subequations 环境测试 ==========

    @Test
    fun testSubequationsBasic() {
        val doc = parser.parse(
            """
            \begin{subequations}
            \begin{equation}
            x = 1
            \end{equation}
            \end{subequations}
        """.trimIndent()
        )
        assertTrue(doc.children[0] is LatexNode.Subequations)
        val subequations = doc.children[0] as LatexNode.Subequations
        assertTrue(subequations.content.isNotEmpty())
    }

    @Test
    fun testSubequationsWithMultipleEquations() {
        val doc = parser.parse(
            """
            \begin{subequations}
            \begin{equation}
            a = b
            \end{equation}
            \begin{equation}
            c = d
            \end{equation}
            \end{subequations}
        """.trimIndent()
        )
        val subequations = doc.children[0] as LatexNode.Subequations
        assertTrue(subequations.content.size >= 2)
    }

    @Test
    fun testSubequationsWithAlign() {
        val doc = parser.parse(
            """
            \begin{subequations}
            \begin{align}
            x &= 1 \\
            y &= 2
            \end{align}
            \end{subequations}
        """.trimIndent()
        )
        assertTrue(doc.children[0] is LatexNode.Subequations)
    }

    // ========== 混合使用测试 ==========

    @Test
    fun testSplitInsideEquation() {
        val doc = parser.parse(
            """
            \begin{equation}
            \begin{split}
            a &= b + c \\
            &= d
            \end{split}
            \end{equation}
        """.trimIndent()
        )
        assertTrue(doc.children[0] is LatexNode.Environment)
        val env = doc.children[0] as LatexNode.Environment
        assertEquals("equation", env.name)
        assertTrue(env.content.any { it is LatexNode.Split })
    }

    @Test
    fun testNestedEnvironments() {
        val doc = parser.parse(
            """
            \begin{subequations}
            \begin{align}
            x &= 1 \\
            y &= 2
            \end{align}
            \begin{multline}
            a + b + c \\
            + d + e
            \end{multline}
            \end{subequations}
        """.trimIndent()
        )
        assertTrue(doc.children[0] is LatexNode.Subequations)
    }

    @Test
    fun testEmptyEnvironment() {
        val doc = parser.parse(
            """
            \begin{split}
            \end{split}
        """.trimIndent()
        )
        assertTrue(doc.children[0] is LatexNode.Split)
        val split = doc.children[0] as LatexNode.Split
        // 空环境可能产生0行或1个空行,都是合理的
        assertTrue(split.rows.size <= 1)
    }
}

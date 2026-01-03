package com.hrm.latex.parser

import com.hrm.latex.parser.model.LatexNode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpaceTest {

    @Test
    fun should_parse_negative_thin_space_correctly() {
        // Arrange
        val input = "a \\! b"
        val parser = LatexParser()

        // Act
        val result = parser.parse(input)

        // Assert
        // a, space, \!, space, b
        // The parser converts tokenizer whitespaces to LatexNode.Space(NORMAL) if handled in parseFactor
        // Let's verify structure
        val children = result.children
        // Expected: Text(a), Space(NORMAL), Space(NEGATIVE_THIN), Space(NORMAL), Text(b)
        // Wait, LatexParser parseFactor:
        // is LatexToken.Whitespace -> LatexNode.Space(LatexNode.Space.SpaceType.NORMAL)
        
        // Let's print to see what we get or inspect children
        assertTrue(children.isNotEmpty())
        
        val negativeSpaceNode = children.find { 
            it is LatexNode.Space && it.type == LatexNode.Space.SpaceType.NEGATIVE_THIN 
        }
        
        assertTrue(negativeSpaceNode != null, "Should contain negative thin space")
    }

    @Test
    fun should_parse_hspace_correctly() {
        // Arrange
        val input = "a \\hspace{1cm} b"
        val parser = LatexParser()

        // Act
        val result = parser.parse(input)

        // Assert
        val children = result.children
        val hSpaceNode = children.find { it is LatexNode.HSpace }
        
        assertTrue(hSpaceNode != null, "Should contain HSpace")
        assertEquals("1cm", (hSpaceNode as LatexNode.HSpace).dimension)
    }
    
    @Test
    fun should_parse_hspace_with_different_units() {
        val parser = LatexParser()
        
        // Test pt
        val res1 = parser.parse("\\hspace{10pt}")
        assertEquals("10pt", (res1.children.first() as LatexNode.HSpace).dimension)
        
        // Test em
        val res2 = parser.parse("\\hspace{2em}")
        assertEquals("2em", (res2.children.first() as LatexNode.HSpace).dimension)
        
        // Test negative
        val res3 = parser.parse("\\hspace{-5mm}")
        assertEquals("-5mm", (res3.children.first() as LatexNode.HSpace).dimension)
    }
}

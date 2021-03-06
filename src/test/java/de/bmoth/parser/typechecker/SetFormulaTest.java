package de.bmoth.parser.typechecker;

import org.junit.Test;

import java.util.Map;

import static de.bmoth.TestConstants.*;
import static de.bmoth.parser.typechecker.TestTypechecker.getFormulaTypes;
import static org.junit.Assert.assertEquals;

public class SetFormulaTest {

    @Test
    public void testInclusion() {
        String formula = "x <: {1}";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(POW_INTEGER, formulaTypes.get("x"));
    }

    @Test
    public void testNonInclusion() {
        String formula = "{x} <: {1}";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(INTEGER, formulaTypes.get("x"));
    }

    @Test
    public void testStrictInclusion() {
        String formula = "{1} <<: x";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(POW_INTEGER, formulaTypes.get("x"));
    }

    @Test
    public void testStrictNonInclusion() {
        String formula = "{1} /<<: {x}";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(INTEGER, formulaTypes.get("x"));
    }

    @Test
    public void testQuantifiedUnion() {
        String formula = "x = UNION(a).(a : 1..10 | {a|->a}) ";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(POW_INTEGER_INTEGER, formulaTypes.get("x"));
    }

    @Test
    public void testGeneralizedUnion() {
        String formula = "a = union({{1},{b},c}\\/d)";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(POW_INTEGER, formulaTypes.get("a"));
        assertEquals(INTEGER, formulaTypes.get("b"));
        assertEquals(POW_INTEGER, formulaTypes.get("c"));
        assertEquals("POW(POW(INTEGER))", formulaTypes.get("d"));
    }

}

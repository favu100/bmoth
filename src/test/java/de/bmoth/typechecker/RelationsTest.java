package de.bmoth.typechecker;

import org.junit.Test;

import java.util.Map;

import static de.bmoth.typechecker.TestTypechecker.getFormulaTypes;
import static org.junit.Assert.assertEquals;

public class RelationsTest {
    private static final String INTEGER = "INTEGER";
    private static final String POW_INTEGER = "POW(INTEGER)";
    private static final String POW_INTEGER_X_INTEGER = "POW(INTEGER*INTEGER)";

    @Test
    public void testDirectProduct() {
        String formula = "a = {1 |-> TRUE} >< {b |-> 1}";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("POW(INTEGER*(BOOL*INTEGER))", formulaTypes.get("a"));
        assertEquals(INTEGER, formulaTypes.get("b"));
    }

    @Test
    public void testOverwriteRelation() {
        String formula = "k = {x|->2} <+ {1|->y}";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(POW_INTEGER_X_INTEGER, formulaTypes.get("k"));
        assertEquals(INTEGER, formulaTypes.get("x"));
        assertEquals(INTEGER, formulaTypes.get("y"));
    }

    @Test
    public void testDomainRestriction() {
        String formula = "k = x <| {1|->1}";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(POW_INTEGER_X_INTEGER, formulaTypes.get("k"));
        assertEquals(POW_INTEGER, formulaTypes.get("x"));
    }

    @Test
    public void testDomainSubstraction() {
        String formula = "k = x <<| {1|->1}";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(POW_INTEGER_X_INTEGER, formulaTypes.get("k"));
        assertEquals(POW_INTEGER, formulaTypes.get("x"));
    }

    @Test
    public void testRangeRestriction() {
        String formula = "k = {1|->1} |> x";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(POW_INTEGER_X_INTEGER, formulaTypes.get("k"));
        assertEquals(POW_INTEGER, formulaTypes.get("x"));
    }

    @Test
    public void testRangeSubstraction() {
        String formula = "k = {1|->1} |>> x";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(POW_INTEGER_X_INTEGER, formulaTypes.get("k"));
        assertEquals(POW_INTEGER, formulaTypes.get("x"));
    }

    @Test
    public void testInverseRelation() {
        String formula = "k = {1|->TRUE}~";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("POW(BOOL*INTEGER)", formulaTypes.get("k"));
    }

}

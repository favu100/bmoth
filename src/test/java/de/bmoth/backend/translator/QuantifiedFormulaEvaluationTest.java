package de.bmoth.backend.translator;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Status;
import de.bmoth.TestUsingZ3;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QuantifiedFormulaEvaluationTest extends TestUsingZ3 {
    @Test
    public void testExistentialFormula() throws Exception {
        String formula = "#(x).(x=2)";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        z3Solver.add(constraint);
        Status check = z3Solver.check();

        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testUniversalFormula() throws Exception {
        String formula = "!(x).(x=TRUE or x=FALSE)";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        z3Solver.add(constraint);
        Status check = z3Solver.check();

        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testFailExistentialFormula() throws Exception {
        String formula = "#(x).(x=2 & x=5)";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        z3Solver.add(constraint);
        Status check = z3Solver.check();

        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testFailUniversalFormula() throws Exception {
        String formula = "!(x).(x=5)";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        z3Solver.add(constraint);
        Status check = z3Solver.check();

        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testFailUniversalExistentialFormula() throws Exception {
        String formula = "#(y).(y:NATURAL & !(x).(x=y))";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testUniversalExistentialFormula() throws Exception {
        String formula = "#(y).(y:NATURAL & !(x).(x*y=y))";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        assertEquals(Status.SATISFIABLE, check);
    }

}

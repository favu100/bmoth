package de.bmoth.backend.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import de.bmoth.backend.FormulaTranslator;

public class FormulaEvaluationTest {

	private Context ctx;
	private Solver s;

	@Before
	public void setup() {
		ctx = new Context();
		s = ctx.mkSolver();
	}

	@After
	public void cleanup() {
		ctx.close();
	}

	@Test
	public void testAdditionFormula() throws Exception {
		String formula = "x = 2 + 3";
		// getting the translated z3 representation of the formula
		BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

		s.add(constraint);
		Status check = s.check();

		Expr x = ctx.mkIntConst("x");

		assertEquals(Status.SATISFIABLE, check);
		assertEquals(ctx.mkInt(5), s.getModel().eval(x, true));
	}

	@Test
	public void testSubtractionFormula() throws Exception {
		String formula = "x = 2 - 3";
		// getting the translated z3 representation of the formula
		BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

		s.add(constraint);
		Status check = s.check();

		Expr x = ctx.mkIntConst("x");

		assertEquals(Status.SATISFIABLE, check);
		assertEquals(ctx.mkInt(-1), s.getModel().eval(x, true));
	}

	@Test
	public void testInequalityFormula() throws Exception {
		String formula = "x /= 0";
		// getting the translated z3 representation of the formula
		BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

		s.add(constraint);
		Status check = s.check();

		Expr x = ctx.mkIntConst("x");

		assertEquals(Status.SATISFIABLE, check);
		assertNotEquals(ctx.mkInt(0), s.getModel().eval(x, true));
	}

	@Test
	public void testModuloFormula() throws Exception {
		String formula = "x = 3 mod 2";
		// getting the translated z3 representation of the formula
		BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

		s.add(constraint);
		Status check = s.check();

		Expr x = ctx.mkIntConst("x");

		assertEquals(Status.SATISFIABLE, check);
		assertEquals(ctx.mkInt(1), s.getModel().eval(x, true));
	}
}
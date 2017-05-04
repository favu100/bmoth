package de.bmoth.backend;

import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.Z3_decl_kind;

import de.bmoth.parser.ast.nodes.DeclarationNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class SolutionFinder {
    private Solver solver;
    private Context z3Context;

    /**
     * Solution finder expects the constraint to be already added to the
     * corresponding solver
     *
     * @param constraint
     *            the constraint to find solutions for
     * @param solver
     *            corresponding z3 solver
     * @param z3Context
     *            corresponding z3 context
     */
    public SolutionFinder(BoolExpr constraint, Solver solver, Context z3Context) {
        this.solver = solver;
        this.z3Context = z3Context;
        this.solver.add(constraint);
    }

    /**
     * Evaluate a single solution from solver over all variables, constraints
     * have to be satisfiable!
     *
     * @return a solution
     */
    private BoolExpr findSolution() {
        Model m = solver.getModel();
        FuncDecl[] constants = m.getConstDecls();

        BoolExpr result = null;
        for (FuncDecl var : constants) {
            Expr value = solver.getModel().eval(var.apply(), true);

            if (result == null) {
                result = z3Context.mkEq(var.apply(), value);
            } else {
                result = z3Context.mkAnd(result, z3Context.mkEq(var.apply(), value));
            }
        }
        return result;
    }

    /**
     * Evaluates all solutions up to a given maximum of iterations
     * <p>
     * credit goes to Taylor
     *
     * @param maxIterations
     *            the maximum nr of iterations
     * @return list of found solution
     * @see <a href=
     *      "http://stackoverflow.com/questions/13395391/z3-finding-all-satisfying-models#answer-13398853">Taylor's
     *      answer on so.com</a>
     */
    public Set<Model> findSolutions(int maxIterations) {
        Set<Model> result = new HashSet<>();

        // create a solution finding scope to not pollute original one
        solver.push();

        // as long as formula is satisfiable:
        for (int i = 0; solver.check() == Status.SATISFIABLE && i < maxIterations; i++) {
            result.add(solver.getModel());

            // find a solution ...
            BoolExpr solution = findSolution();

            // ... and add it as an exclusion constraint to solver stack
            solver.add(z3Context.mkNot(solution));
        }

        // delete solution finding scope to remove all exclusion constraints
        // from solver stack
        solver.pop();

        // TODO getModel() invocation fails if solver.check() hasn't been called
        // in advance. Is here a dummy call necessary?

        return result;
    }
}
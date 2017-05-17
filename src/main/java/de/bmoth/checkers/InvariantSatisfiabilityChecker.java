package de.bmoth.checkers;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.backend.MachineToZ3Translator;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.MachineNode;

public class InvariantSatisfiabilityChecker {
    public static InvariantSatisfiabilityCheckingResult doInvariantSatisfiabilityCheck(String machineAsString) {
        MachineNode machineAsSemanticAst = Parser.getMachineAsSemanticAst(machineAsString);
        return doInvariantSatisfiabilityCheck(machineAsSemanticAst);
    }

    public static InvariantSatisfiabilityCheckingResult doInvariantSatisfiabilityCheck(MachineNode machine) {
        Context ctx = new Context();
        Solver solver = ctx.mkSolver();
        MachineToZ3Translator machineTranslator = new MachineToZ3Translator(machine, ctx);

        final BoolExpr invariant = machineTranslator.getInvariantConstraint();
        solver.add(invariant);
        Status check = solver.check();
        return new InvariantSatisfiabilityCheckingResult(check);
    }
}

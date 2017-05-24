package de.bmoth.modelchecker;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ModelCheckerMachineFilesTest {
    private String dir = "src/test/resources/machines/";

    @Test
    public void testSimpleModelsWithoutOperations() throws IOException {
        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "OnlyInitViolation.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(false, result.isCorrect());

        MachineNode simpleMachineWithoutViolation = Parser.getMachineFileAsSemanticAst(dir + "OnlyInitNoViolation.mch");
        result = ModelChecker.doModelCheck(simpleMachineWithoutViolation);
        assertEquals(true, result.isCorrect());
    }

    @Test
    public void testSimpleModelWithoutVariables() throws IOException {
        MachineNode simpleMachineWithViolation = Parser.getMachineFileAsSemanticAst(dir + "OnlyConstants.mch");
        ModelCheckingResult result = ModelChecker.doModelCheck(simpleMachineWithViolation);
        assertEquals(true, result.isCorrect());
    }

}

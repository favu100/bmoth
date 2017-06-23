package de.bmoth.modelchecker.kinduction;

import de.bmoth.TestParser;
import de.bmoth.modelchecker.kind.KInductionModelChecker;
import de.bmoth.modelchecker.kind.KInductionModelCheckingResult;
import org.junit.Ignore;
import org.junit.Test;

import static de.bmoth.modelchecker.kind.KInductionModelCheckingResult.Type.*;
import static org.junit.Assert.assertEquals;

public class KInductionModelCheckerTest extends TestParser {

    @Test
    public void test1() {
        String machine = "MACHINE infInc\n" +
            "VARIABLES c\n" +
            "INVARIANT c : NATURAL\n" +
            "INITIALISATION c := 0\n" +
            "OPERATIONS\n" +
            "\tinc = BEGIN c := c + 1 END\n" +
            "END\n";

        KInductionModelCheckingResult result = new KInductionModelChecker(parseMachine(machine), 20).check();
        assertEquals(EXCEEDED_MAX_STEPS, result.getType());
        assertEquals(20, result.getSteps());
    }

    @Test
    public void test2() {
        String machine = "MACHINE ebr\n" +
            "VARIABLES c, b\n" +
            "INVARIANT c : INTEGER &\n" +
            "\tb = TRUE\n" +
            "INITIALISATION c := 0 || b := TRUE\n" +
            "OPERATIONS\n" +
            "\tinc = ANY x WHERE x:INTEGER THEN c := c + x END;\n" +
            "\terr = PRE c > 99999 THEN b := FALSE END\n" +
            "END\n";

        KInductionModelCheckingResult result = new KInductionModelChecker(parseMachine(machine), 20).check();
        assertEquals(COUNTER_EXAMPLE_FOUND, result.getType());
        assertEquals("{b=false, c=100000}", result.getLastState().toString());
        assertEquals(1, result.getSteps());
    }

    @Test
    @Ignore
    public void testCounterCorrect() {
        String machine = "MACHINE ebr\n" +
            "VARIABLES c\n" +
            "INVARIANT c : INTEGER\n" +
            "INITIALISATION c := 0\n" +
            "OPERATIONS\n" +
            "\tinc = c := c + 1\n" +
            "END\n";

        KInductionModelCheckingResult result = new KInductionModelChecker(parseMachine(machine), 20).check();
        assertEquals(CORRECT, result.getType());
    }
}
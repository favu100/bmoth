package de.bmoth.modelchecker.esmc;

import de.bmoth.modelchecker.ModelCheckingResult;
import org.junit.Test;

import static de.bmoth.TestConstants.MACHINE_NAME;
import static org.junit.Assert.assertEquals;
import static de.bmoth.TestParser.*;

public class ModelCheckerMachineTest {

    @Test
    public void testIfThenElseSubstitutition() {
        String machine = MACHINE_NAME;
        machine += "VARIABLES a \n";
        machine += "INVARIANT a : BOOL & a = TRUE \n";
        machine += "INITIALISATION IF 1=1 THEN a := TRUE ELSE a := FALSE END \n";
        machine += "END";

        ModelCheckingResult result = ExplicitStateModelChecker.check(parseMachine(machine));
        assertEquals(true, result.isCorrect());
    }

    @Test
    public void testIfThenSubstitutition() {
        String machine = MACHINE_NAME;
        machine += "VARIABLES a, b \n";
        machine += "INVARIANT a : BOOL & a = TRUE & b = TRUE\n";
        machine += "INITIALISATION a:= TRUE || b := TRUE \n";
        machine += "OPERATIONS foo = IF 1=2 THEN a := FALSE|| b := FALSE END \n";
        machine += "END";
        ModelCheckingResult result = ExplicitStateModelChecker.check(parseMachine(machine));
        assertEquals(true, result.isCorrect());
    }

    @Test
    public void testBecomesElementOfSubstitutition() {
        String machine = MACHINE_NAME;
        machine += "VARIABLES a,b \n";
        machine += "INVARIANT a = 3 & b : 4..5\n";
        machine += "INITIALISATION a :: {3} || b :: {4,5} \n";
        machine += "END";
        ModelCheckingResult result = ExplicitStateModelChecker.check(parseMachine(machine));
        assertEquals(true, result.isCorrect());
    }
}
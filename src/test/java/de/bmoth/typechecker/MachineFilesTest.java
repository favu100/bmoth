package de.bmoth.typechecker;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.MachineNode;

public class MachineFilesTest {

	@Test
	public void testReadSimpleMachineFromFile() throws Exception {
		String dir = "src/test/resources/machines/";
		MachineNode machineNode = Parser.getMachineFileAsSemanticAst(dir + "SimpleMachine.mch");
		List<DeclarationNode> variables = machineNode.getVariables();
		assertEquals(1, variables.size());
		assertEquals("x", variables.get(0).getName());
		assertEquals("INTEGER", variables.get(0).getType().toString());
		List<DeclarationNode> constants = machineNode.getConstants();
		assertEquals(1, constants.size());
		assertEquals("con", constants.get(0).getName());
		assertEquals("BOOL", constants.get(0).getType().toString());
	}
}
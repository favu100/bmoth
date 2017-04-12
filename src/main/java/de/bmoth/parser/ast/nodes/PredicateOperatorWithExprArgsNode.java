package de.bmoth.parser.ast.nodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.PredicateOperatorWithExprArgsContext;

public class PredicateOperatorWithExprArgsNode extends PredicateNode {

	public static enum PredOperatorExprArgs {
		EQUAL, NOT_EQUAL, ELEMENT_OF, LESS_EQUAL, LESS, GREATER_EQUAL, GREATER
	}

	private static final Map<Integer, PredOperatorExprArgs> map = new HashMap<>();
	static {
		map.put(BMoThParser.EQUAL, PredOperatorExprArgs.EQUAL);
		map.put(BMoThParser.NOT_EQUAL, PredOperatorExprArgs.NOT_EQUAL);
		map.put(BMoThParser.ELEMENT_OF, PredOperatorExprArgs.ELEMENT_OF);
		map.put(BMoThParser.COLON, PredOperatorExprArgs.ELEMENT_OF);
		map.put(BMoThParser.LESS_EQUAL, PredOperatorExprArgs.LESS_EQUAL);
		map.put(BMoThParser.LESS, PredOperatorExprArgs.LESS);
		map.put(BMoThParser.GREATER_EQUAL, PredOperatorExprArgs.GREATER_EQUAL);
		map.put(BMoThParser.GREATER, PredOperatorExprArgs.GREATER);
	}

	private final List<ExprNode> expressionNodes;
	private final String operatorString;
	private PredOperatorExprArgs operator;

	public PredicateOperatorWithExprArgsNode(PredicateOperatorWithExprArgsContext ctx, List<ExprNode> expressionNodes) {
		this.expressionNodes = expressionNodes;
		this.operatorString = ctx.operator.getText();
		this.operator = loopUpOperator(ctx.operator.getType());
	}

	private PredOperatorExprArgs loopUpOperator(int type) {
		if (map.containsKey(type)) {
			return map.get(type);
		}
		throw new AssertionError("Operator not implemented: " + operatorString);
	}

	public PredOperatorExprArgs getOperator() {
		return operator;
	}

	public List<ExprNode> getExpressionNodes() {
		return expressionNodes;
	}

}
package de.bmoth.parser.ast.nodes;

public class SingleAssignSubstitutionNode implements SubstitutionNode {

	private final IdentifierExprNode identifier;
	private final ExprNode value;

	public SingleAssignSubstitutionNode(IdentifierExprNode identifier, ExprNode expr) {
		this.identifier = identifier;
		this.value = expr;
	}

	public IdentifierExprNode getIdentifier() {
		return identifier;
	}

	public ExprNode getValue() {
		return value;
	}

    @Override
    public String toString() {
        return identifier + " := " + value;
    }
}

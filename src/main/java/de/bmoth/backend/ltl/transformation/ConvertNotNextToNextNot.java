package de.bmoth.backend.ltl.transformation;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;

public class ConvertNotNextToNextNot implements ASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof LTLPrefixOperatorNode;
    }

    @Override
    public Node transformNode(Node node) {
        LTLPrefixOperatorNode notOperator = (LTLPrefixOperatorNode) node;
        if (notOperator.getKind() == LTLPrefixOperatorNode.Kind.NOT) {
            LTLNode argument = notOperator.getArgument();
            if (argument instanceof LTLPrefixOperatorNode) {
                LTLPrefixOperatorNode nextOperator = (LTLPrefixOperatorNode) argument;
                if (nextOperator.getKind() == LTLPrefixOperatorNode.Kind.NEXT) {
                    LTLPrefixOperatorNode newNot = new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.NOT,
                        nextOperator.getArgument());
                    return new LTLPrefixOperatorNode(LTLPrefixOperatorNode.Kind.NEXT, newNot);
                }
            }
        }
        return node;
    }

}

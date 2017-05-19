package de.bmoth.parser.ast.nodes;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.PredicateOperatorContext;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode.PredOperatorExprArgs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PredicateOperatorNode extends PredicateNode {
    public enum PredicateOperator {
        AND, OR, IMPLIES, EQUIVALENCE, NOT, TRUE, FALSE
    }

    private static final Map<Integer, PredicateOperator> map = new HashMap<>();

    static {
        map.put(BMoThParser.AND, PredicateOperator.AND);
        map.put(BMoThParser.OR, PredicateOperator.OR);
        map.put(BMoThParser.IMPLIES, PredicateOperator.IMPLIES);
        map.put(BMoThParser.EQUIVALENCE, PredicateOperator.EQUIVALENCE);
        map.put(BMoThParser.NOT, PredicateOperator.NOT);
        map.put(BMoThParser.TRUE, PredicateOperator.TRUE);
        map.put(BMoThParser.FALSE, PredicateOperator.FALSE);
    }

    private List<PredicateNode> predicateArguments;
    private String operatorString;
    private PredicateOperator operator;

    public PredicateOperatorNode(PredicateOperatorContext ctx, List<PredicateNode> predicateArguments) {
        this.predicateArguments = predicateArguments;
        this.operatorString = ctx.operator.getText();
        this.setOperator(lookUpOperator(ctx.operator.getType()));
    }

    public PredicateOperatorNode(PredicateOperator operator, List<PredicateNode> predicateArguments) {
        this.predicateArguments = predicateArguments;
        this.operator = operator;
    }

    private PredicateOperator lookUpOperator(int type) {
        if (map.containsKey(type)) {
            return map.get(type);
        }
        throw new AssertionError("Operator not implemented: " + operatorString);
    }

    public List<PredicateNode> getPredicateArguments() {
        return predicateArguments;
    }

    public PredicateOperator getOperator() {
        return operator;
    }

    public void setOperator(PredicateOperator operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.operator.name());
        Iterator<PredicateNode> iter = predicateArguments.iterator();
        if (iter.hasNext()) {
            sb.append("(");
            while (iter.hasNext()) {
                sb.append(iter.next().toString());
                if (iter.hasNext()) {
                    sb.append(",");
                }
            }
            sb.append(")");
        }
        return sb.toString();
    }

    public void setPredicateList(List<PredicateNode> list) {
        this.predicateArguments = list;
    }
}

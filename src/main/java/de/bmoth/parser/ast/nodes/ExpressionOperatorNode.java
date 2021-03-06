package de.bmoth.parser.ast.nodes;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.ExpressionOperatorContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExpressionOperatorNode extends ExprNode implements OperatorNode<ExpressionOperatorNode.ExpressionOperator> {

    public enum ExpressionOperator {

        // arithmetic
        NATURAL, NATURAL1, INTEGER, INT, NAT, NAT1, MININT, MAXINT//
        , BOOL, TRUE, FALSE, POWER_OF //
        , PLUS, MINUS, MULT, DIVIDE, MOD, INTERVAL//
        , UNARY_MINUS//
        , MAX, MIN//
        // set operators
        , SET_ENUMERATION, EMPTY_SET, SET_SUBTRACTION, UNION, INTERSECTION//
        , GENERALIZED_UNION, GENERALIZED_INTER//
        // relations
        , DOMAIN, RANGE, CARTESIAN_PRODUCT//
        , CARD, DOMAIN_RESTRICTION, OVERWRITE_RELATION, DIRECT_PRODUCT//
        , DOMAIN_SUBTRACTION, RANGE_RESTRICTION, RANGE_SUBTRATION//
        , INVERSE_RELATION, SET_RELATION
        // function
        , FUNCTION_CALL
        // sequence operators
        , FIRST, LAST, FRONT, TAIL, CONC, SEQ_ENUMERATION, EMPTY_SEQUENCE//
        , CONCAT, INSERT_FRONT, INSERT_TAIL, RESTRICT_FRONT, RESTRICT_TAIL//
        , SEQ, SEQ1, ISEQ, ISEQ1
        // special
        , COUPLE
    }

    private static final Map<Integer, ExpressionOperator> map = new HashMap<>();

    static {
        map.put(BMoThParser.PLUS, ExpressionOperator.PLUS);
        map.put(BMoThParser.NATURAL, ExpressionOperator.NATURAL);
        map.put(BMoThParser.NATURAL1, ExpressionOperator.NATURAL1);
        map.put(BMoThParser.INTEGER, ExpressionOperator.INTEGER);
        map.put(BMoThParser.NAT, ExpressionOperator.NAT);
        map.put(BMoThParser.NAT1, ExpressionOperator.NAT1);
        map.put(BMoThParser.INT, ExpressionOperator.INT);
        map.put(BMoThParser.MININT, ExpressionOperator.MININT);
        map.put(BMoThParser.MAXINT, ExpressionOperator.MAXINT);

        map.put(BMoThParser.BOOL, ExpressionOperator.BOOL);
        map.put(BMoThParser.TRUE, ExpressionOperator.TRUE);
        map.put(BMoThParser.FALSE, ExpressionOperator.FALSE);
        map.put(BMoThParser.POWER_OF, ExpressionOperator.POWER_OF);
        map.put(BMoThParser.MULT, ExpressionOperator.MULT);
        map.put(BMoThParser.DIVIDE, ExpressionOperator.DIVIDE);
        map.put(BMoThParser.MOD, ExpressionOperator.MOD);
        map.put(BMoThParser.SET_SUBTRACTION, ExpressionOperator.SET_SUBTRACTION);
        map.put(BMoThParser.INTERVAL, ExpressionOperator.INTERVAL);
        map.put(BMoThParser.UNION, ExpressionOperator.UNION);
        map.put(BMoThParser.INTERSECTION, ExpressionOperator.INTERSECTION);
        map.put(BMoThParser.MAPLET, ExpressionOperator.COUPLE);
        map.put(BMoThParser.DOM, ExpressionOperator.DOMAIN);
        map.put(BMoThParser.RAN, ExpressionOperator.RANGE);

        map.put(BMoThParser.MIN, ExpressionOperator.MIN);
        map.put(BMoThParser.MAX, ExpressionOperator.MAX);

        // relations
        map.put(BMoThParser.OVERWRITE_RELATION, ExpressionOperator.OVERWRITE_RELATION);
        map.put(BMoThParser.DIRECT_PRODUCT, ExpressionOperator.DIRECT_PRODUCT);
        map.put(BMoThParser.CONCAT, ExpressionOperator.CONCAT);
        map.put(BMoThParser.DOMAIN_RESTRICTION, ExpressionOperator.DOMAIN_RESTRICTION);
        map.put(BMoThParser.DOMAIN_SUBTRACTION, ExpressionOperator.DOMAIN_SUBTRACTION);
        map.put(BMoThParser.RANGE_RESTRICTION, ExpressionOperator.RANGE_RESTRICTION);
        map.put(BMoThParser.RANGE_SUBTRACTION, ExpressionOperator.RANGE_SUBTRATION);
        map.put(BMoThParser.TILDE, ExpressionOperator.INVERSE_RELATION);
        map.put(BMoThParser.SET_RELATION, ExpressionOperator.SET_RELATION);

        // sets
        map.put(BMoThParser.GENERALIZED_UNION, ExpressionOperator.GENERALIZED_UNION);
        map.put(BMoThParser.GENERALIZED_INTER, ExpressionOperator.GENERALIZED_INTER);
        map.put(BMoThParser.CARD, ExpressionOperator.CARD);

        // sequence operators
        map.put(BMoThParser.FIRST, ExpressionOperator.FIRST);
        map.put(BMoThParser.LAST, ExpressionOperator.LAST);
        map.put(BMoThParser.FRONT, ExpressionOperator.FRONT);
        map.put(BMoThParser.TAIL, ExpressionOperator.TAIL);
        map.put(BMoThParser.CONC, ExpressionOperator.CONC);
        map.put(BMoThParser.INSERT_FRONT, ExpressionOperator.INSERT_FRONT);
        map.put(BMoThParser.INSERT_TAIL, ExpressionOperator.INSERT_TAIL);
        map.put(BMoThParser.RESTRICT_FRONT, ExpressionOperator.RESTRICT_FRONT);
        map.put(BMoThParser.RESTRICT_TAIL, ExpressionOperator.RESTRICT_TAIL);
        map.put(BMoThParser.SEQ, ExpressionOperator.SEQ);
        map.put(BMoThParser.SEQ1, ExpressionOperator.SEQ1);
        map.put(BMoThParser.ISEQ, ExpressionOperator.ISEQ);
        map.put(BMoThParser.ISEQ1, ExpressionOperator.ISEQ1);

    }

    private List<ExprNode> expressionNodes;

    private String operatorString;
    private ExpressionOperator operator;

    public ExpressionOperatorNode(ExpressionOperatorContext ctx, List<ExprNode> expressionNodes,
                                  String operatorString) {
        super(ctx);
        this.expressionNodes = expressionNodes;
        this.operatorString = operatorString;
        this.operator = loopUpOperator(ctx.operator.getType());
    }

    public ExpressionOperatorNode(ParserRuleContext parseTree, List<ExprNode> expressionNodes,
                                  ExpressionOperator operator) {
        // used for set enumeration, e.g. {1,2,3}
        super(parseTree);
        this.expressionNodes = expressionNodes;
        this.operatorString = null;
        this.operator = operator;
    }

    private ExpressionOperator loopUpOperator(int type) {
        if (type == BMoThParser.MINUS) {
            if (this.expressionNodes.size() == 1) {
                return ExpressionOperator.UNARY_MINUS;
            } else {
                return ExpressionOperator.MINUS;
            }
        }
        if (map.containsKey(type)) {
            return map.get(type);
        }
        throw new AssertionError("Operator not implemented: " + operatorString);
    }

    @Override
    public ExpressionOperator getOperator() {
        return operator;
    }

    @Override
    public void setOperator(ExpressionOperator operator) {
        this.operator = operator;
    }

    public List<ExprNode> getExpressionNodes() {
        return expressionNodes;
    }

    public int getArity() {
        return expressionNodes.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.operator.name());
        Iterator<ExprNode> iter = expressionNodes.iterator();
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

    public void setExpressionList(List<ExprNode> list) {
        this.expressionNodes = list;
    }

    @Override
    public boolean equalAst(Node other) {
        if (!NodeUtil.isSameClass(this, other)) {
            return false;
        }

        ExpressionOperatorNode that = (ExpressionOperatorNode) other;
        return this.operator.equals(that.operator)
            && NodeUtil.equalAst(this.expressionNodes, that.expressionNodes);
    }

}

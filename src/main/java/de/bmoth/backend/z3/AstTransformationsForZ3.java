package de.bmoth.backend.z3;

import com.google.common.reflect.ClassPath;
import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.visitors.ASTTransformationVisitor;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AstTransformationsForZ3 {
    private final ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private final Logger logger = Logger.getLogger(getClass().getName());

    private static AstTransformationsForZ3 instance;

    private final List<AbstractASTTransformation> transformationList;

    private AstTransformationsForZ3() {
        this.transformationList = new ArrayList<>();

        try {
            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith("de.bmoth.backend.z3.transformation")) {
                    final Class<?> clazz = info.load();
                    transformationList.add((AbstractASTTransformation) clazz.newInstance());
                }
            }
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error loading LTL transformation rules", e);
        }
    }

    public static AstTransformationsForZ3 getInstance() {
        if (null == instance) {
            instance = new AstTransformationsForZ3();
        }
        return instance;
    }

    public static PredicateNode transformPredicate(PredicateNode predNode) {
        AstTransformationsForZ3 astTransformationForZ3 = AstTransformationsForZ3.getInstance();
        ASTTransformationVisitor visitor = new ASTTransformationVisitor(astTransformationForZ3.transformationList);
        return visitor.transformPredicate(predNode);
    }

    public static ExprNode transformExprNode(ExprNode value) {
        AstTransformationsForZ3 astTransformationForZ3 = AstTransformationsForZ3.getInstance();
        ASTTransformationVisitor visitor = new ASTTransformationVisitor(astTransformationForZ3.transformationList);
        return visitor.transformExpr(value);
    }

}

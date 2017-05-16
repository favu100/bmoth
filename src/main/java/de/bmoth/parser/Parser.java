package de.bmoth.parser;

import de.bmoth.antlr.BMoThLexer;
import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.FormulaContext;
import de.bmoth.antlr.BMoThParser.StartContext;
import de.bmoth.parser.ast.FormulaAnalyser;
import de.bmoth.parser.ast.MachineAnalyser;
import de.bmoth.parser.ast.SemanticAstCreator;
import de.bmoth.parser.ast.TypeChecker;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.util.Utils;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Parser {

    private BMoThParser getParser(String inputString) {
        ANTLRInputStream inputStream = new ANTLRInputStream(inputString);
        final BMoThLexer lexer = new BMoThLexer(inputStream);
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BMoThParser bMoThParser = new BMoThParser(tokens);
        bMoThParser.removeErrorListeners();
        bMoThParser.addErrorListener(new DiagnosticErrorListener());
        ErrorListener errorListener = new ErrorListener();
        bMoThParser.addErrorListener(errorListener);
        return bMoThParser;
    }

    public StartContext parseMachine(String inputString) {
        BMoThParser parser = getParser(inputString);
        StartContext start = parser.start();
        return start;
    }

    public FormulaContext parseFormula(String inputString) {
        BMoThParser parser = getParser(inputString);
        return parser.formula();
    }

    public MachineNode getMachineAst(StartContext start) {
        MachineAnalyser machineAnalyser = new MachineAnalyser(start);
        SemanticAstCreator astCreator = new SemanticAstCreator(machineAnalyser);
        return (MachineNode) astCreator.getAstNode();
    }

    public FormulaNode getFormulaAst(FormulaContext formula) {
        FormulaAnalyser formulaAnalyser = new FormulaAnalyser(formula);
        SemanticAstCreator astCreator = new SemanticAstCreator(formulaAnalyser);
        return (FormulaNode) astCreator.getAstNode();
    }

    public static MachineNode getMachineFileAsSemanticAst(String file) throws FileNotFoundException, IOException {
        Parser parser = new Parser();
        String fileContent = Utils.readFile(new File(file));
        StartContext start = parser.parseMachine(fileContent);
        MachineNode ast = parser.getMachineAst(start);
        new TypeChecker(ast);
        return ast;
    }

    public static MachineNode getMachineAsSemanticAst(String inputString) {
        Parser parser = new Parser();
        StartContext start = parser.parseMachine(inputString);
        MachineNode ast = parser.getMachineAst(start);
        new TypeChecker(ast);
        return ast;
    }

    public static FormulaNode getFormulaAsSemanticAst(String inputString) {
        Parser parser = new Parser();
        FormulaContext formulaContext = parser.parseFormula(inputString);
        FormulaNode formulaNode = parser.getFormulaAst(formulaContext);
        new TypeChecker(formulaNode);
        return formulaNode;
    }

}

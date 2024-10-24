package nl.han.ica.icss.checker;

import nl.han.ica.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;



public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public Checker() {
        variableTypes = new HANLinkedList<>();
    }

    public void check(AST ast) {
        variableTypes.addFirst(new HashMap<>());
        checkNode(ast.root);
    }

    private void checkNode(ASTNode node) {
        if (node instanceof VariableAssignment) {
            // Variable assignment: track the variable in the current scope
            String variableName = ((VariableAssignment) node).name.name;
            ExpressionType expressionType = inferExpressionType(((VariableAssignment) node).expression);
            variableTypes.getFirst().put(variableName, expressionType);
        } else if (node instanceof VariableReference) {
            // Check if the variable has been defined in any scope
            String variableName = ((VariableReference) node).name;
            if (!isVariableDefined(variableName)) {
                node.setError("Variable " + variableName + " is not defined.");
            }
        }

        // Recursively check child nodes
        for (ASTNode child : node.getChildren()) {
            checkNode(child);
        }
    }

    private boolean isVariableDefined(String variableName) {
        for (int i = 0; i < variableTypes.getSize(); i++) {
            HashMap<String, ExpressionType> scope = variableTypes.get(i);
            if (scope.containsKey(variableName)) {
                return true;
            }
        }
        return false;
    }

    private ExpressionType inferExpressionType(Expression expression) {
        // Determine the type of the expression (PIXEL, PERCENTAGE, etc.)
        if (expression instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (expression instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (expression instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else if (expression instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (expression instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else if (expression instanceof VariableReference) {
            // Get the type of the referenced variable
            String variableName = ((VariableReference) expression).name;
            for (int i = 0; i < variableTypes.getSize(); i++) {
                HashMap<String, ExpressionType> scope = variableTypes.get(i);
                if (scope.containsKey(variableName)) {
                    return scope.get(variableName);
                }
            }
        }
        return ExpressionType.UNDEFINED;
    }

}
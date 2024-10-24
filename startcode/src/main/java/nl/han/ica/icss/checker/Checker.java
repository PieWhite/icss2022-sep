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
            checkVariableAssignment((VariableAssignment) node);
        } else if (node instanceof AddOperation || node instanceof SubtractOperation || node instanceof MultiplyOperation) {
            checkOperation((Operation) node);
        }
        // Add more checks for other node types as needed

        for (ASTNode child : node.getChildren()) {
            checkNode(child);
        }
    }

    private void checkVariableAssignment(VariableAssignment varAssign) {
        String varName = varAssign.name.name;
        ExpressionType varType = determineExpressionType(varAssign.expression);
        variableTypes.getFirst().put(varName, varType);
    }

    private void checkOperation(Operation operation) {
        ExpressionType leftType = determineExpressionType(operation.lhs);
        ExpressionType rightType = determineExpressionType(operation.rhs);

        if (leftType != rightType && !(isScalarOperation(leftType, rightType))) {
            operation.setError("Type mismatch in operation: " + leftType + " and " + rightType);
            System.out.println(new SemanticError("Type mismatch in operation: " + leftType + " and " + rightType));
        }
    }

    private boolean isScalarOperation(ExpressionType leftType, ExpressionType rightType) {
        return (leftType == ExpressionType.SCALAR || rightType == ExpressionType.SCALAR) &&
                (leftType != ExpressionType.UNDEFINED && rightType != ExpressionType.UNDEFINED);
    }

    private ExpressionType getVariableType(VariableReference varRef) {
        String varName = varRef.name;
        for (int i = 0; i < variableTypes.getSize(); i++) {
            HashMap<String, ExpressionType> scope = variableTypes.get(i);
            if (scope.containsKey(varName)) {
                return scope.get(varName);
            }
        }
        return ExpressionType.UNDEFINED;
    }
    private ExpressionType determineExpressionType(ASTNode node) {
        if (node instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (node instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (node instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else if (node instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (node instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else if (node instanceof VariableReference) {
            return getVariableType((VariableReference) node);

        }
        return ExpressionType.UNDEFINED;
    }
}
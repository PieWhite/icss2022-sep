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

        // deze checkt of variablen alleen binnen scope gebruikt worden
        if (node instanceof Stylerule || node instanceof IfClause || node instanceof ElseClause) {
            // Enter a new scope for blocks like Stylerule and IfClause
            variableTypes.addFirst(new HashMap<>());
        }

        // deze checkt of de variabelen bestaan
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
        // deze checkt of kleuren in sommen zitten
        if (node instanceof AddOperation || node instanceof SubtractOperation || node instanceof MultiplyOperation) {
            // Color literals are not allowed in these operations
            Expression left = ((Operation) node).lhs;
            Expression right = ((Operation) node).rhs;

            if (inferExpressionType(left) == ExpressionType.COLOR || inferExpressionType(right) == ExpressionType.COLOR) {
                node.setError("Colors cannot be used in arithmetic operations.");
            }
        }
        // deze checkt of de waardes wel het zelfde zijn dus px + px
        if (node instanceof Declaration) {
            Declaration declaration = (Declaration) node;
            String propertyName = declaration.property.name;
            ExpressionType valueType = inferExpressionType(declaration.expression);

            // Check based on property name
            if (propertyName.equals("width") || propertyName.equals("height")) {
                if (valueType != ExpressionType.PIXEL) {
                    node.setError(propertyName + " must have a pixel value.");
                }
            } else if (propertyName.equals("color")) {
                if (valueType != ExpressionType.COLOR) {
                    node.setError("color must have a color value.");
                }
            }
        }
        // deze checkt of de som wel klopt dus  10px + 10px en niet 10px + 10%
        if (node instanceof AddOperation || node instanceof SubtractOperation) {
            // Both sides of the operation should have the same type
            Expression left = ((Operation) node).lhs;
            Expression right = ((Operation) node).rhs;

            ExpressionType leftType = inferExpressionType(left);
            ExpressionType rightType = inferExpressionType(right);

            if (leftType != rightType) {
                node.setError("Operands of " + (node instanceof AddOperation ? "+" : "-") + " must be of the same type.");
            }
        }



        // Recursively check child nodes
        for (ASTNode child : node.getChildren()) {
            checkNode(child);
        }
        if (node instanceof Stylerule || node instanceof IfClause || node instanceof ElseClause) {
            // Leave the scope when exiting the block
            variableTypes.removeFirst();
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
            //dit fixt dat sommen van variabelen niet werken dus parwidth + 2 * 10px
        } else if (expression instanceof Operation) {
            ExpressionType leftType = inferExpressionType(((Operation) expression).lhs);
            ExpressionType rightType = inferExpressionType(((Operation) expression).rhs);
            if (leftType == rightType) {
                return leftType;
            } else if (leftType == ExpressionType.SCALAR && rightType == ExpressionType.PIXEL) {
                return ExpressionType.PIXEL;
            } else if (leftType == ExpressionType.PIXEL && rightType == ExpressionType.SCALAR) {
                return ExpressionType.PIXEL;
            }

        }
        return ExpressionType.UNDEFINED;
    }



}
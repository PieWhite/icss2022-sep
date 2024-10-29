package nl.han.ica.icss.transforms;

import nl.han.ica.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
        pushScope();
    }

    @Override
    public void apply(AST ast) {
        evaluateAST(ast.root, null);
    }

    private void evaluateAST(ASTNode node, ASTNode parent) {
        // First, traverse the children
        List<ASTNode> children = new ArrayList<>(node.getChildren());
        for (ASTNode child : children) {
            evaluateAST(child, node);
        }

        // Now, process the current node
        if (node instanceof VariableAssignment) {
            // Evaluate variable assignments
            VariableAssignment varAssign = (VariableAssignment) node;
            Literal value = evaluateExpression(varAssign.expression);
            variableValues.getFirst().put(varAssign.name.name, value);
            varAssign.expression = value;
        } else if (node instanceof Declaration) {
            // Evaluate declarations
            Declaration declaration = (Declaration) node;
            Literal value = evaluateExpression(declaration.expression);
            declaration.expression = value;
        } else if (node instanceof IfClause) {
            // Evaluate if/else clauses
            evaluateIfClause((IfClause) node, parent);
        }
    }

    private void evaluateIfClause(IfClause ifClause, ASTNode parent) {
        Literal condition = evaluateExpression(ifClause.conditionalExpression);
        if (condition instanceof BoolLiteral) {
            boolean conditionValue = ((BoolLiteral) condition).value;
            int index = parent.getChildren().indexOf(ifClause);
            parent.getChildren().remove(ifClause);
            List<ASTNode> newNodes = new ArrayList<>();
            if (conditionValue) {
                // Evalueer en voeg de body van het if-statement toe
                for (ASTNode child : ifClause.body) {
                    evaluateAST(child, ifClause);
                    newNodes.add(child);
                }
            } else if (ifClause.elseClause != null) {
                // Evalueer en voeg de body van het else-statement toe
                for (ASTNode child : ifClause.elseClause.body) {
                    evaluateAST(child, ifClause.elseClause);
                    newNodes.add(child);
                }
            }
            // Voeg de geëvalueerde nodes toe aan de parent
            parent.getChildren().addAll(index, newNodes);
        }
    }

    private Literal evaluateExpression(Expression expression) {
        if (expression instanceof Literal) {
            return (Literal) expression;
        } else if (expression instanceof VariableReference) {
            String name = ((VariableReference) expression).name;
            Literal value = lookupVariable(name);
            if (value != null) {
                return value;
            }
            // Behandel ongedefinieerde variabelen indien nodig
        } else if (expression instanceof Operation) {
            Literal left = evaluateExpression(((Operation) expression).lhs);
            Literal right = evaluateExpression(((Operation) expression).rhs);
            if (left != null && right != null) {
                if (expression instanceof AddOperation) {
                    return performAddition(left, right);
                } else if (expression instanceof SubtractOperation) {
                    return performSubtraction(left, right);
                } else if (expression instanceof MultiplyOperation) {
                    return performMultiplication(left, right);
                }
            }
        }
        return null; // Of handel foutafhandeling af
    }

    private Literal lookupVariable(String name) {
        for (int i = 0; i < variableValues.getSize(); i++) {
            HashMap<String, Literal> scope = variableValues.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }

    private Literal performAddition(Literal left, Literal right) {
        // Implementeer optelling op basis van Literal types
        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            int result = ((PixelLiteral) left).value + ((PixelLiteral) right).value;
            return new PixelLiteral(result);
        } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            int result = ((PercentageLiteral) left).value + ((PercentageLiteral) right).value;
            return new PercentageLiteral(result);
        } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            int result = ((ScalarLiteral) left).value + ((ScalarLiteral) right).value;
            return new ScalarLiteral(result);
        }
        // Voeg hier andere combinaties toe indien nodig
        return null;
    }

    private Literal performSubtraction(Literal left, Literal right) {
        // Implementeer aftrekking op basis van Literal types
        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            int result = ((PixelLiteral) left).value - ((PixelLiteral) right).value;
            return new PixelLiteral(result);
        } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            int result = ((PercentageLiteral) left).value - ((PercentageLiteral) right).value;
            return new PercentageLiteral(result);
        } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            int result = ((ScalarLiteral) left).value - ((ScalarLiteral) right).value;
            return new ScalarLiteral(result);
        }
        // Voeg hier andere combinaties toe indien nodig
        return null;
    }

    private Literal performMultiplication(Literal left, Literal right) {
        // Implementeer vermenigvuldiging op basis van Literal types
        if (left instanceof ScalarLiteral && right instanceof PixelLiteral) {
            int result = ((ScalarLiteral) left).value * ((PixelLiteral) right).value;
            return new PixelLiteral(result);
        } else if (left instanceof PixelLiteral && right instanceof ScalarLiteral) {
            int result = ((PixelLiteral) left).value * ((ScalarLiteral) right).value;
            return new PixelLiteral(result);
        } else if (left instanceof ScalarLiteral && right instanceof PercentageLiteral) {
            int result = ((ScalarLiteral) left).value * ((PercentageLiteral) right).value;
            return new PercentageLiteral(result);
        } else if (left instanceof PercentageLiteral && right instanceof ScalarLiteral) {
            int result = ((PercentageLiteral) left).value * ((ScalarLiteral) right).value;
            return new PercentageLiteral(result);
        } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            int result = ((ScalarLiteral) left).value * ((ScalarLiteral) right).value;
            return new ScalarLiteral(result);
        }
        // Voeg hier andere combinaties toe indien nodig
        return null;
    }

    private void pushScope() {
        variableValues.addFirst(new HashMap<>());
    }

    private void popScope() {
        variableValues.removeFirst();
    }
}
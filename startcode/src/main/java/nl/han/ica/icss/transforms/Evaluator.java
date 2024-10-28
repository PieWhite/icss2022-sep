package nl.han.ica.icss.transforms;

import nl.han.ica.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;


import java.util.HashMap;


public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
        // Push the initial global scope
        pushScope();
    }

    @Override
    public void apply(AST ast) {
        Stylesheet stylesheet = ast.root;
        this.transformStylesheet(stylesheet);
    }

    private void transformStylesheet(ASTNode astNode) {
        for (ASTNode child : astNode.getChildren()) {
            if (child instanceof VariableAssignment) {
                this.transformVariableAssignment((VariableAssignment) child);
            } else if (child instanceof Stylerule) {
                this.transformStylerule((Stylerule) child);
            }
        }
    }

    private void transformStylerule(Stylerule stylerule) {
        for (ASTNode child : stylerule.body) {
            this.transformRuleBody(child);
        }
    }

    private void transformRuleBody(ASTNode astNode) {
        if (astNode instanceof VariableAssignment) {
            this.transformVariableAssignment((VariableAssignment) astNode);
        } else if (astNode instanceof Declaration) {
            this.transformDeclaration((Declaration) astNode);
        } else if (astNode instanceof IfClause) {
            this.transformIfClause((IfClause) astNode);
        }
    }

    private void transformIfClause(IfClause ifClause) {
        boolean condition = evaluateCondition(ifClause.conditionalExpression);

        // Push a new scope for the body of the if or else clause
        pushScope();

        if (condition) {
            // Execute the body of the IfClause if the condition is true
            for (ASTNode node : ifClause.body) {
                transformRuleBody(node);
            }
        } else if (ifClause.elseClause != null) {
            // Execute the body of the ElseClause if the condition is false
            for (ASTNode node : ifClause.elseClause.body) {
                transformRuleBody(node);
            }
        }

        // Pop the scope after processing the IfClause
        popScope();
    }

    private boolean evaluateCondition(Expression expression) {
        if (expression instanceof BoolLiteral) {
            return ((BoolLiteral) expression).value;
        }
        return false;
    }

    private void transformDeclaration(Declaration declaration) {
        declaration.expression = this.transformExpression(declaration.expression);
    }

    private void transformVariableAssignment(VariableAssignment variableAssignment) {
        // Evaluate the expression and store it in the current scope
        Literal evaluatedValue = this.transformExpression(variableAssignment.expression);
        variableValues.getFirst().put(variableAssignment.name.name, evaluatedValue);
    }

    private Literal transformExpression(Expression expression) {
        if (expression instanceof VariableReference) {
            return getVariableValue(((VariableReference) expression).name);
        } else if (expression instanceof Literal) {
            return (Literal) expression; // Direct return if it's a literal
        }
        return null; // Handle unexpected cases
    }

    private Literal getVariableValue(String name) {
        // Traverse the linked list to find the variable
        for (int i = 0; i < variableValues.getSize(); i++) {
            HashMap<String, Literal> scope = variableValues.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null; // or throw an exception if variable is not found
    }

    private void pushScope() {
        variableValues.addFirst(new HashMap<>()); // Add a new scope
    }

    private void popScope() {
        if (variableValues.getSize() > 0) {
            variableValues.removeFirst(); // Remove the last scope
        }
    }
}

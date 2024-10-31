package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import java.util.List;

import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

public class Generator {

    public String generate(AST ast) {
        StringBuilder css = new StringBuilder();
        generateNode(ast.root, css, 0);
        return css.toString();
    }

    private void generateNode(ASTNode node, StringBuilder css, int indentLevel) {
        String indent = "  ".repeat(indentLevel); // Two spaces per scope level

        if (node instanceof Stylesheet) {
            for (ASTNode child : node.getChildren()) {
                generateNode(child, css, indentLevel);
            }
        } else if (node instanceof Stylerule) {
            Stylerule stylerule = (Stylerule) node;
            // Append selectors
            css.append(indent);
            List<Selector> selectors = stylerule.selectors;
            for (int i = 0; i < selectors.size(); i++) {
                css.append(selectors.get(i).toString());
                if (i < selectors.size() - 1) {
                    css.append(", ");
                }
            }
            css.append(" {\n");
            // Generate declarations and nested rules
            for (ASTNode child : stylerule.getChildren()) {
                generateNode(child, css, indentLevel + 1);
            }
            css.append(indent).append("}\n");
        } else if (node instanceof Declaration) {
            Declaration declaration = (Declaration) node;
            css.append(indent).append(declaration.property.name).append(": ");
            css.append(expressionToString(declaration.expression)).append(";\n");
    	}
	}

    private String expressionToString(Expression expression) {
        if (expression instanceof PixelLiteral) {
            PixelLiteral pixel = (PixelLiteral) expression;
            return pixel.value + "px";
        } else if (!(expression instanceof PercentageLiteral)) {
			if (expression instanceof ScalarLiteral) {
				ScalarLiteral scalar = (ScalarLiteral) expression;
				return String.valueOf(scalar.value);
			} else if (expression instanceof ColorLiteral) {
				ColorLiteral color = (ColorLiteral) expression;
				return color.value;
			} else if (expression instanceof BoolLiteral) {
				BoolLiteral bool = (BoolLiteral) expression;
				return String.valueOf(bool.value);
			} else if (expression instanceof Operation) {
				Operation operation = (Operation) expression;
				String left = expressionToString(operation.lhs);
				String right = expressionToString(operation.rhs);
				String operator = "";

				if (operation instanceof AddOperation) {
					operator = " + ";
				} else if (operation instanceof SubtractOperation) {
					operator = " - ";
				} else if (operation instanceof MultiplyOperation) {
					operator = " * ";
				}
				return "(" + left + operator + right + ")";
			} else if (expression instanceof VariableReference) {
				return ((VariableReference) expression).name;
			}
		} else {
			PercentageLiteral percentage = (PercentageLiteral) expression;
			return percentage.value + "%";
		}
        return "";
    }
}
package nl.han.ica.icss.parser;

import java.util.Stack;


import nl.han.ica.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}

	public AST getAST() {
		return ast;
	}

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		ASTNode stylesheet = new Stylesheet();
		currentContainer.push(stylesheet);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		ast.setRoot((Stylesheet) currentContainer.pop());
	}

	@Override
	public void enterSelectorRule(ICSSParser.SelectorRuleContext ctx) {
		ASTNode selectorRule = new Stylerule();
		currentContainer.push(selectorRule);
	}

	@Override
	public void exitSelectorRule(ICSSParser.SelectorRuleContext ctx) {
		ASTNode selectorRule = currentContainer.pop();
		currentContainer.peek().addChild(selectorRule);
	}

	@Override
	public void enterSelector(ICSSParser.SelectorContext ctx) {
		ASTNode selector = null;

		if (ctx.CLASS_IDENT() != null) {
			selector = new ClassSelector(ctx.CLASS_IDENT().getText());
		} else if (ctx.ID_IDENT() != null) {
			selector = new IdSelector(ctx.ID_IDENT().getText());
		} else if (ctx.LOWER_IDENT() != null) {
			selector = new TagSelector(ctx.LOWER_IDENT().getText());
		}

		if (selector != null) {
			currentContainer.push(selector); // Push the selector onto the stack
		}
	}

	@Override
	public void exitSelector(ICSSParser.SelectorContext ctx) {
		if (ctx.CLASS_IDENT() != null || ctx.ID_IDENT() != null || ctx.LOWER_IDENT() != null) {
			ASTNode selector = currentContainer.pop(); // Pop from the stack
			currentContainer.peek().addChild(selector); // Add the selector to the parent node
		}
	}



	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		ASTNode variableAssignment = new VariableAssignment();
		currentContainer.push(variableAssignment);
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		ASTNode variableAssignment = currentContainer.pop();
		currentContainer.peek().addChild(variableAssignment);
	}


	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		ASTNode declaration = new Declaration();
		currentContainer.push(declaration);


	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		ASTNode declaration = currentContainer.pop();
		currentContainer.peek().addChild(declaration);
	}

	@Override
	public void enterPropertyName(ICSSParser.PropertyNameContext ctx) {
		// When entering a property name, create a PropertyName node and attach it to the current Declaration node
		ASTNode propertyName = new PropertyName(ctx.getText());
		currentContainer.peek().addChild(propertyName); // Attach to the current declaration
	}


	@Override
	public void enterIfClause(ICSSParser.IfClauseContext ctx) {
		ASTNode ifClause = new IfClause();
		currentContainer.push(ifClause);
	}

	@Override
	public void exitIfClause(ICSSParser.IfClauseContext ctx) {
		ASTNode ifClause = currentContainer.pop();
		currentContainer.peek().addChild(ifClause);
	}

	@Override
	public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
		ASTNode elseClause = new ElseClause();
		currentContainer.push(elseClause);
	}

	@Override
	public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
		ASTNode elseClause = currentContainer.pop();
		currentContainer.peek().addChild(elseClause);
	}

	@Override
	public void enterPixelSize(ICSSParser.PixelSizeContext ctx) {
		ASTNode pixelSize = new PixelLiteral(ctx.getText());
		currentContainer.peek().addChild(pixelSize);
	}
	@Override
	public void enterPercentage(ICSSParser.PercentageContext ctx) {
		ASTNode percentage = new PercentageLiteral(ctx.getText());
		currentContainer.peek().addChild(percentage);
	}

	@Override
	public void enterScalar(ICSSParser.ScalarContext ctx) {
		ASTNode scalar = new ScalarLiteral(ctx.getText());
		currentContainer.peek().addChild(scalar);
	}

	@Override
	public void enterColor(ICSSParser.ColorContext ctx) {
		ASTNode color = new ColorLiteral(ctx.getText());
		currentContainer.peek().addChild(color);
	}

	@Override
	public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
		ASTNode variableReference = new VariableReference(ctx.getText());
		currentContainer.peek().addChild(variableReference);
	}
	@Override
	public void enterBool(ICSSParser.BoolContext ctx) {
		ASTNode bool = new BoolLiteral(ctx.getText());
		currentContainer.peek().addChild(bool);
	}


//	@Override
//	public void enterLiteral(ICSSParser.LiteralContext ctx) {
//		ASTNode literal = null;
//
//		if (ctx.PIXELSIZE() != null) {
//			literal = new PixelLiteral(ctx.PIXELSIZE().getText());
//		} else if (ctx.PERCENTAGE() != null) {
//			literal = new PercentageLiteral(ctx.PERCENTAGE().getText());
//		} else if (ctx.SCALAR() != null) {
//			literal = new ScalarLiteral(ctx.SCALAR().getText());
//		} else if (ctx.COLOR() != null) {
//			literal = new ColorLiteral(ctx.COLOR().getText());
//		} else if (ctx.variableReference() != null) {
//			literal = new VariableReference(ctx.variableReference().getText());
//		} else if (ctx.TRUE() != null) {
//			literal = new BoolLiteral("TRUE");
//		} else if (ctx.FALSE() != null) {
//			literal = new BoolLiteral("FALSE");
//		}
//
//		if (literal != null) {
//			currentContainer.peek().addChild(literal);
//		}
//	}

	@Override
	public void enterExpression(ICSSParser.ExpressionContext ctx) {
		ASTNode operation = null;
		if (ctx.MUL() != null) {
			operation = new MultiplyOperation();
		} else if (ctx.ADD() != null) {
			operation = new AddOperation();
		} else if (ctx.SUB() != null) {
			operation = new SubtractOperation();
		}
		if (operation != null) {
			currentContainer.push(operation); // Push the selector onto the stack

		}
	}

	@Override
	public void exitExpression(ICSSParser.ExpressionContext ctx) {
		if (ctx.MUL() != null || ctx.ADD() != null || ctx.SUB() != null) {
			ASTNode operation = currentContainer.pop();
			currentContainer.peek().addChild(operation);// Pop the operation
		}
	}

}
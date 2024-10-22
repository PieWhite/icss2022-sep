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
		Stylesheet stylesheet = new Stylesheet();
		ast.setRoot(stylesheet);
		currentContainer.push(stylesheet);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterSelectorRule(ICSSParser.SelectorRuleContext ctx) {
		Stylerule stylerule = new Stylerule();
		currentContainer.peek().addChild(stylerule);
		currentContainer.push(stylerule);
	}

	@Override
	public void exitSelectorRule(ICSSParser.SelectorRuleContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterSelector(ICSSParser.SelectorContext ctx) {
		if (ctx.CLASS_IDENT() != null) {
			ClassSelector classSelector = new ClassSelector(ctx.CLASS_IDENT().getText());
			currentContainer.peek().addChild(classSelector);
			currentContainer.push(classSelector);
		} else if (ctx.ID_IDENT() != null) {
			IdSelector idSelector = new IdSelector(ctx.ID_IDENT().getText());
			currentContainer.peek().addChild(idSelector);
			currentContainer.push(idSelector);
		} else if (ctx.LOWER_IDENT() != null) {
			TagSelector tagSelector = new TagSelector(ctx.LOWER_IDENT().getText());
			currentContainer.peek().addChild(tagSelector);
			currentContainer.push(tagSelector);
		}
	}

	@Override
	public void exitSelector(ICSSParser.SelectorContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment varAssign = new VariableAssignment();
		currentContainer.peek().addChild(varAssign);
		currentContainer.push(varAssign);
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterIfClause(ICSSParser.IfClauseContext ctx) {
		IfClause ifClause = new IfClause();
		currentContainer.peek().addChild(ifClause);
		currentContainer.push(ifClause);
	}

	@Override
	public void exitIfClause(ICSSParser.IfClauseContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
		ElseClause elseClause = new ElseClause();
		currentContainer.peek().addChild(elseClause);
		currentContainer.push(elseClause);
	}

	@Override
	public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = new Declaration();
		currentContainer.peek().addChild(declaration);
		currentContainer.push(declaration);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterExpression(ICSSParser.ExpressionContext ctx) {
		if(ctx.MUL() != null) {
			VariableReference varRef = new VariableReference(ctx.MUL().getText());
			currentContainer.peek().addChild(varRef);
		} else if(ctx.DIV() != null) {
			VariableReference varRef = new VariableReference(ctx.DIV().getText());
			currentContainer.peek().addChild(varRef);
		} else if(ctx.ADD() != null) {
			VariableReference varRef = new VariableReference(ctx.ADD().getText());
			currentContainer.peek().addChild(varRef);
		} else if(ctx.SUB() != null) {
			VariableReference varRef = new VariableReference(ctx.SUB().getText());
			currentContainer.peek().addChild(varRef);
		}
	}

	@Override
	public void exitExpression(ICSSParser.ExpressionContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterPrimaryExpression(ICSSParser.PrimaryExpressionContext ctx) {
		if(ctx.PIXELSIZE() != null) {
			PixelLiteral pixel = new PixelLiteral(ctx.PIXELSIZE().getText());
			currentContainer.peek().addChild(pixel);
		} else if(ctx.PERCENTAGE() != null) {
			PercentageLiteral percentage = new PercentageLiteral(ctx.PERCENTAGE().getText());
			currentContainer.peek().addChild(percentage);
		} else if(ctx.SCALAR() != null) {
			ScalarLiteral scalar = new ScalarLiteral(ctx.SCALAR().getText());
			currentContainer.peek().addChild(scalar);
		} else if(ctx.COLOR() != null) {
			ColorLiteral color = new ColorLiteral(ctx.COLOR().getText());
			currentContainer.peek().addChild(color);
		} else if(ctx.CAPITAL_IDENT() != null) {
			VariableReference varRef = new VariableReference(ctx.CAPITAL_IDENT().getText());
			currentContainer.peek().addChild(varRef);
		} else if(ctx.TRUE() != null) {
			BoolLiteral bool = new BoolLiteral(ctx.TRUE().getText());
			currentContainer.peek().addChild(bool);
		} else if(ctx.FALSE() != null) {
			BoolLiteral bool = new BoolLiteral(ctx.FALSE().getText());
			currentContainer.peek().addChild(bool);
		}
	}
}
package sk.tuke.yin.syntaxer.example.imperative.model.statement;

import sk.tuke.yin.syntaxer.example.imperative.model.expression.Expression;
import yajco.annotation.After;

public class ExpressionStatement implements Statement {
    private final Expression expression;

    public ExpressionStatement(@After("SEMICOLON") Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public void execute() {
        getExpression().eval();
    }

    /**
     * @return the expression
     */
    public Expression getExpression() {
        return expression;
    }
}

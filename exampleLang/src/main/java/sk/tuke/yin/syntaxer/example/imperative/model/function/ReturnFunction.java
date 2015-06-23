package sk.tuke.yin.syntaxer.example.imperative.model.function;

import sk.tuke.yin.syntaxer.example.imperative.machine.ReturnException;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.Expression;
import sk.tuke.yin.syntaxer.example.imperative.model.statement.Statement;
import yajco.annotation.After;
import yajco.annotation.Before;

public final class ReturnFunction implements Statement {
    private final Expression expression;

    public ReturnFunction(
            @Before("RETURN") @After("SEMICOLON") Expression expression) {
        this.expression = expression;
    }

    public void execute() {
        throw new ReturnException(getExpression().eval());
    }

    /**
     * @return the expression
     */
    public Expression getExpression() {
        return expression;
    }
}

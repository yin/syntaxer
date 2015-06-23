package sk.tuke.yin.syntaxer.example.imperative.model.statement;

import sk.tuke.yin.syntaxer.example.imperative.machine.Machine;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.Expression;
import yajco.annotation.After;
import yajco.annotation.Before;

public class Write implements Statement {
    private final Expression expression;
    
    public Write(@Before("WRITE") @After("SEMICOLON") Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public void execute() {
        Machine.getInstance().write(getExpression().eval());
    }

    /**
     * @return the expression
     */
    public Expression getExpression() {
        return expression;
    }
}

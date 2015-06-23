package sk.tuke.yin.syntaxer.example.imperative.model.statement;

import sk.tuke.yin.syntaxer.example.imperative.model.expression.Expression;
import yajco.annotation.After;
import yajco.annotation.Before;

public class DoWhile implements Statement {
    private final Expression expression;

    private final Statement statement;

    public DoWhile(
            @Before("DO") Statement statement,
            @Before({"WHILE", "LPAR"}) @After({"RPAR", "SEMICOLON"}) Expression expression
            ) {
        this.expression = expression;
        this.statement = statement;
    }

    @Override
    public void execute() {
        do {
            getStatement().execute();
        } while (getExpression().eval() != 0);
    }

    /**
     * @return the expression
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * @return the statement
     */
    public Statement getStatement() {
        return statement;
    }
}

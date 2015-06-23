package sk.tuke.yin.syntaxer.example.imperative.model.expression.relational;

import sk.tuke.yin.syntaxer.example.imperative.model.expression.BinaryOperation;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.Expression;
import yajco.annotation.Before;
import yajco.annotation.Operator;

public final class Equal extends BinaryOperation {
    @Operator(priority = 8)
    public Equal(Expression expression1, @Before("EQ") Expression expression2) {
        super(expression1, expression2);
    }

    public long eval() {
        return getExpression1().eval() == getExpression2().eval() ? 1 : 0;
    }
}

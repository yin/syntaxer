package sk.tuke.yin.syntaxer.example.imperative.model.expression.relational;

import sk.tuke.yin.syntaxer.example.imperative.model.expression.BinaryOperation;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.Expression;
import yajco.annotation.Before;
import yajco.annotation.Operator;

public final class LowerThan extends BinaryOperation {
    @Operator(priority = 9)
    public LowerThan(Expression expression1, @Before("LT") Expression expression2) {
        super(expression1, expression2);
    }

    public long eval() {
        return getExpression1().eval() < getExpression2().eval() ? 1 : 0;
    }
}

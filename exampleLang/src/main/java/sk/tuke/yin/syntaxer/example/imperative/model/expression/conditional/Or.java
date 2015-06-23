package sk.tuke.yin.syntaxer.example.imperative.model.expression.conditional;

import sk.tuke.yin.syntaxer.example.imperative.model.expression.BinaryOperation;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.Expression;
import yajco.annotation.Before;
import yajco.annotation.Operator;

public final class Or extends BinaryOperation {
    @Operator(priority = 3)
    public Or(Expression expression1, @Before("OR") Expression expression2) {
        super(expression1, expression2);
    }

    public long eval() {
        return (getExpression1().eval() != 0 || getExpression2().eval() != 0) ? 1 : 0;
    }
}

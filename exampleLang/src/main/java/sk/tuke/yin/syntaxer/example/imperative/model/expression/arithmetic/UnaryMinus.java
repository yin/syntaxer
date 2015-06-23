package sk.tuke.yin.syntaxer.example.imperative.model.expression.arithmetic;

import sk.tuke.yin.syntaxer.example.imperative.model.expression.Expression;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.UnaryOperation;
import yajco.annotation.Before;
import yajco.annotation.Operator;

public final class UnaryMinus extends UnaryOperation {
    @Operator(priority = 13)
    public UnaryMinus(@Before("MINUS") Expression expression) {
        super(expression);
    }

    @Override
    public long eval() {
        return -getExpression().eval();
    }
}

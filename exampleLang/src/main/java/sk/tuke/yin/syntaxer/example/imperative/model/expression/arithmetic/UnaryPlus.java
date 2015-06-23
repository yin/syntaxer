package sk.tuke.yin.syntaxer.example.imperative.model.expression.arithmetic;

import sk.tuke.yin.syntaxer.example.imperative.model.expression.Expression;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.UnaryOperation;
import yajco.annotation.Before;
import yajco.annotation.Operator;

public final class UnaryPlus extends UnaryOperation {
    @Operator(priority = 13)
    public UnaryPlus(@Before("PLUS") Expression expression) {
        super(expression);
    }

    @Override
    public long eval() {
        return getExpression().eval();
    }
}

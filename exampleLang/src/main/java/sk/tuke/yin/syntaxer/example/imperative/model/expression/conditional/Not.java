package sk.tuke.yin.syntaxer.example.imperative.model.expression.conditional;

import sk.tuke.yin.syntaxer.example.imperative.model.expression.Expression;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.UnaryOperation;
import yajco.annotation.Before;
import yajco.annotation.Operator;

public final class Not extends UnaryOperation {
    @Operator(priority = 13)
    public Not(@Before("EXCL") Expression expression) {
        super(expression);
    }

    @Override
    public long eval() {
        return getExpression().eval() == 0 ? 1 : 0;
    }
}

package sk.tuke.yin.syntaxer.example.imperative.model.expression.assignment;

import sk.tuke.yin.syntaxer.example.imperative.machine.Machine;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.BinaryOperation;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.Expression;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.Variable;
import yajco.annotation.Before;
import yajco.annotation.Operator;
import yajco.model.pattern.impl.Associativity;

public class Assignment extends BinaryOperation {
    @Operator(priority = 1, associativity = Associativity.RIGHT)
    public Assignment(Expression expression1, @Before("ASSIGN") Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public long eval() {
        long value = getExpression2().eval();
        String ident = ((Variable) getExpression1()).getIdent();
        Machine.getInstance().setValue(ident, value);
        return value;
    }
}

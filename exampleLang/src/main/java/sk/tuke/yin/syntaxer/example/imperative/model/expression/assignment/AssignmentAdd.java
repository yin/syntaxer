package sk.tuke.yin.syntaxer.example.imperative.model.expression.assignment;

import sk.tuke.yin.syntaxer.example.imperative.model.expression.Expression;
import yajco.model.pattern.impl.Associativity;
import yajco.annotation.Before;
import yajco.annotation.Operator;

public class AssignmentAdd extends AssignmentGeneric {
    @Operator(priority = 1, associativity = Associativity.RIGHT)
    public AssignmentAdd(Expression expression1, @Before("ASSIGNADD") Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected long eval(long value1, long value2) {
        return value1 + value2;
    }
}

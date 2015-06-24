package sk.tuke.yin.syntaxer.example.imperative.model.expression.assignment;

import sk.tuke.yin.syntaxer.example.imperative.model.expression.Expression;
import yajco.model.pattern.impl.Associativity;
import yajco.annotation.Before;
import yajco.annotation.Operator;

public class AssignmentMul extends AssignmentGeneric {
    @Operator(priority = 1, associativity = Associativity.RIGHT)
    public AssignmentMul(Expression expression1, @Before("ASSIGNMUL") Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected long eval(long value1, long value2) {
        return value1 * value2;
    }
}

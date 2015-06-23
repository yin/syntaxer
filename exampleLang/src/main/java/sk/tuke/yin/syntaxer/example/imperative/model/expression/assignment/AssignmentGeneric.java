package sk.tuke.yin.syntaxer.example.imperative.model.expression.assignment;

import sk.tuke.yin.syntaxer.example.imperative.machine.Machine;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.BinaryOperation;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.Expression;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.Variable;

public abstract class AssignmentGeneric extends BinaryOperation {
    public AssignmentGeneric(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public long eval() {
        long value = getExpression2().eval();
        String ident = ((Variable) getExpression1()).getIdent();
        value = eval(Machine.getInstance().getValue(ident), value);
        Machine.getInstance().setValue(ident, value);
        return value;
    }

    protected abstract long eval(long value1, long value2);
}

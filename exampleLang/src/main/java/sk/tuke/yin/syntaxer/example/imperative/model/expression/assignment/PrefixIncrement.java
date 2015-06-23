package sk.tuke.yin.syntaxer.example.imperative.model.expression.assignment;

import sk.tuke.yin.syntaxer.example.imperative.machine.Machine;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.Expression;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.UnaryOperation;
import sk.tuke.yin.syntaxer.example.imperative.model.expression.Variable;
import yajco.model.pattern.impl.Associativity;
import yajco.annotation.Before;
import yajco.annotation.Operator;

public class PrefixIncrement extends UnaryOperation {
    @Operator(priority = 14, associativity = Associativity.NONE)
    public PrefixIncrement(@Before("PLUSPLUS") Expression expression) {
        super(expression);
    }

    @Override
    public long eval() {
        String ident = ((Variable) getExpression()).getIdent();
        long value = Machine.getInstance().getValue(ident) + 1;
        Machine.getInstance().setValue(ident, value);
        return value;
    }
}

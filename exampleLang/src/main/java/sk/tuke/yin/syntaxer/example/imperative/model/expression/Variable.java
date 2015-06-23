package sk.tuke.yin.syntaxer.example.imperative.model.expression;

import sk.tuke.yin.syntaxer.example.imperative.machine.Machine;

public final class Variable implements Expression {
    private final String ident;
    
    public Variable(String ident) {
        this.ident = ident;
    }

    public long eval() {
        return Machine.getInstance().getValue(ident);
    }

    public String getIdent() {
        return ident;
    }
}

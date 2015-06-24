package sk.tuke.yin.syntaxer.example.imperative.model.function;

import yajco.annotation.reference.Identifier;

public class Parameter {
    @Identifier(unique = "../sk.tuke.yin.syntaxer.example.imperative.model.function.Parameter")
    private final String name;

    public Parameter(String ident) {
        this.name = ident;
    }

    public String getName() {
        return name;
    }

    public String getIdent() {
        return name;
    }
}

package sk.tuke.yin.syntaxer.example.imperative.model.statement;

import sk.tuke.yin.syntaxer.example.imperative.machine.Machine;
import yajco.annotation.After;
import yajco.annotation.Before;

public class Read implements Statement {
    private final String ident;
    
    public Read(@Before("READ") @After("SEMICOLON") String ident) {
        this.ident = ident;
    }
    
    @Override
    public void execute() {
        Machine.getInstance().read(getIdent());
    }

    /**
     * @return the ident
     */
    public String getIdent() {
        return ident;
    }
}

package sk.tuke.yin.syntaxer.example.imperative.model;

import sk.tuke.yin.syntaxer.example.imperative.model.function.Function;
import sk.tuke.yin.syntaxer.example.imperative.model.statement.Block;

public class Program {
    private final Function[] functions;

    private final Block main;

    public Program(Function[] functions, Block main) {
        this.functions = functions;
        this.main = main;
    }

    public void execute() {
        getMain().execute();
    }

    /**
     * @return the functions
     */
    public Function[] getFunctions() {
        return functions;
    }

    /**
     * @return the main
     */
    public Block getMain() {
        return main;
    }
}

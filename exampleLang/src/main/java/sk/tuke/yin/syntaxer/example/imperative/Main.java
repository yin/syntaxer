package sk.tuke.yin.syntaxer.example.imperative;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;

import sk.tuke.yin.syntaxer.example.imperative.machine.Machine;
import sk.tuke.yin.syntaxer.example.imperative.model.Program;
import sk.tuke.yin.syntaxer.example.imperative.parser.Parser;

public class Main {
    public static void main(String[] args) throws Exception {
        Reader reader = new BufferedReader(new InputStreamReader(
                Main.class.getResourceAsStream("/program")));
        Program program = new Parser().parse(reader);

        program.execute();
        System.out.println(Machine.getInstance());
    }
}

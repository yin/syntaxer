package sk.tuke.yin.syntaxer.model;

import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class SimpleModel implements Model {
    private Multimap<String, String> tokens = ArrayListMultimap.<String, String>create();
    private Map<String, String> values = Maps.newHashMap();
    
    @Override
    public void addToken(String type, String regexp) {
        tokens.put(type, regexp);
    }
    
    @Override
    public Iterable<String> getTokens(String type) {
        return tokens.get(type);
    }

    public void getAll(String type) {
        System.out.println(tokens);
    }
    
    @Override
    public void setValue(String type, String value) {
        values.put(type, value);
    }
    
    @Override
    public String getValue(String type) {
        return values.get(type);
    }
}

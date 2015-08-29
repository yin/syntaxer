package sk.tuke.yin.syntaxer.model;

import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class SimpleModel implements Model {
    private Multimap<String, String> tokens = ArrayListMultimap.<String, String>create();
    private Map<String, String> values = Maps.newHashMap();
    private Multimap<String, Object> objects = ArrayListMultimap.<String, Object>create();
    
    @Override
    public void addToken(String type, String regexp) {
        tokens.put(type, regexp);
    }
    
    @Override
    public Iterable<String> getTokens(String type) {
        return tokens.get(type);
    }

    public void debug() {
        System.out.println(tokens);
        System.out.println(values);
        System.out.println(objects);
    }
    
    @Override
    public void setValue(String type, String value) {
        values.put(type, value);
    }
    
    @Override
    public String getValue(String type) {
        return values.get(type);
    }

	@Override
	public void addObject(String type, Object object) {
		objects.put(type, object);
        System.out.println(type+"++++++++++"+object.getClass().getName()+" "+object.toString());
		
	}
	@Override
	public Iterable<Object> getObject(String type) {
		return objects.get(type);
	}
}

package sk.tuke.yin.syntaxer.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.auto.value.AutoValue;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class HighlightingModel {
    private String name;
    private List<String> keywords = new ArrayList<String>();
    private List<String> operators = new ArrayList<String>();
    private List<Literal> literals = 
            new ArrayList<Literal>();
	
	public void setLanguageName(String name) {
	    this.name = name;
	}
	
	public String getLanguageName() {
	    return name;
	}
	
	public void addKeyword(String token) {
		keywords.add(token);
	}

    public Iterable<String> getKeywords() {
        return keywords;
    }
    
    public void addOperator(String operator) {
        operators.add(operator);
    }

    public Iterable<String> getOperators() {
        return operators;
    }

    public void addLiteral(String literal, LiteralType type) {
        if (type != null) {
            type = LiteralType.OTHER;
        }
        literals.add(Literal.create(literal, type));
    }

    public Iterable<String> getLiterals(@Nonnull final LiteralType type) {
        return Collections2.transform(Collections2.filter(literals,  new Predicate<Literal>() {
            @Override public boolean apply(Literal literal) {
                return type == literal.type();
            }
        }), new Function<Literal, String>() {
            @Override public String apply(Literal literal) {
                return literal.regexp();
            }
        });
    }

    // Consider using strategy pattern
    public static enum LiteralType {
        STRING, NUMBER, CHAR, OTHER;
    }
    
    @AutoValue
    abstract static class Literal {
        static Literal create(String literal, LiteralType type) {
            return new AutoValue_HighlightingModel_Literal(literal, type);
        }
        abstract String regexp();
        abstract LiteralType type();
    }
}

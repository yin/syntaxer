package sk.tuke.yin.syntaxer;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.io.VelocityWriter;

import sk.tuke.yin.syntaxer.model.ColorMapping;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ColorMapping colors = new ColorMapping();
        Velocity.init();
        //Engine ve = new VelocityEngine();
        VelocityContext context = new VelocityContext();
        context.put("name", "Kate01");
        Template t = Velocity.getTemplate("test.vm");
        StringWriter w = new StringWriter();
        
        t.merge(context, w);
        
        String ret = w.toString();
        System.out.println(ret);
    }
}

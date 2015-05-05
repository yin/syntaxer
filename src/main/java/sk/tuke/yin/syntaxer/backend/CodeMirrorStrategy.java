package sk.tuke.yin.syntaxer.backend;

import java.util.Map;

import org.apache.velocity.VelocityContext;

import sk.tuke.yin.syntaxer.model.Model;

import com.google.common.collect.Maps;

public class CodeMirrorStrategy extends GeneralEditorStrategy {

    public CodeMirrorStrategy(String editorId, String editorName, String templateFile,
            String outputSuffix) {
        super(editorId, editorName, templateFile, outputSuffix);
    }

    @Override
    public VelocityContext getContext(Model model) {
        VelocityContext context = super.getContext(model);
        return context;
    }
    
    class State {
        Map<String, State> nexts = Maps.newHashMap();
        
    }
}

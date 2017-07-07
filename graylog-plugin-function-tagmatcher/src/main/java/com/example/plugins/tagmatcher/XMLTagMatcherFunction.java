package com.example.plugins.tagmatcher;

import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.expressions.Expression;
import org.graylog.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import org.graylog.plugins.pipelineprocessor.ast.functions.Function;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

/**
 * This is the plugin. Your class should implement one of the existing plugin
 * interfaces. (i.e. AlarmCallback, MessageInput, MessageOutput)
 */
public class XMLTagMatcherFunction extends AbstractFunction<String> {
	public static final String NAME = "tagmatcher";
    private static final String TAG = "tag";
    private static final String SOURCE = "string";

    
    @Override
    public String evaluate(FunctionArgs functionArgs, EvaluationContext evaluationContext) {
    	String tag = tagParam.required(functionArgs, evaluationContext);

        if (tag == null) {
            return null;
        }
        String source = sourceParam.required(functionArgs, evaluationContext);

        if (source == null) {
            return null;
        }
        
        // First outer loop
        Integer resultstart=0,resultend=0,i,j=0;
        Integer taglen = tag.length();
        Integer state = 0;
        
        for (i=0;i<source.length();i++) {
        	switch (state) {
        		case 0:	// find initial < of opening tag
        			if (source.charAt(i) == '<') {	
        				state=1;	
        				j=0;	
        			}	
        			break;
        		case 1: // check opening tag
        			if (source.charAt(i) == tag.charAt(j)) {
        				if (j == taglen) state = 2;
        			} else {
        				state = 0;
        			}
        			j++;
        			break;
        		case 2: // check opening tag closing >
        			if  (source.charAt(i) == '>') {	
        				state=3;	
        				resultstart=i+1;
        			} else {
        				state=0; // the tag was something else
        			}
        			break;
        		case 3: // find initial < of closing tag
        			if (source.charAt(i) == '<') {	
        				resultend=i;
        				state=4;
        			}	
        			break;
        		case 4: // find initial / of closing tag
        			if (source.charAt(i) == '/') {	
        				state=5;
        				j=0;
        			} else {
        				state=3; // was not a closing tag, continue searching the closing tag
        			}
        			break;
        		case 5: // check closing tag
        			if (source.charAt(i) == tag.charAt(j)) {
        				if (j == taglen) state=6;
        			} else {
        				state=3;  // the result string may contain other tags, continue search for the closing tag
        			}
        			j++;
        			break;
        		case 6: // check closing tag closing >
        			if  (source.charAt(i) == '>') {	
        				state=7;		
        			} else {
        				state=3;
        			}
        			break;
        		case 7:
        		default:
        			i= source.length();
        			break;
        	}
        }
        	
        if (state != 7) return null;
        	
        return source.substring(resultstart,resultend);
    }
    
    private final ParameterDescriptor<String,String> tagParam = ParameterDescriptor
    	       .string(TAG)
    	       .description("The tag to find.  E.g. for <key>ddd</key> this would be 'key'.")
    	         .build();

    private final ParameterDescriptor<String,String> sourceParam = ParameterDescriptor
 	       .string(SOURCE)
 	       .description("The string to match.")
 	         .build();
    
    @Override
    public FunctionDescriptor<String> descriptor() {
    	return FunctionDescriptor.<String>builder()
                .name(NAME)
                .description("Returns the string within tags, e.g. tagmatcher(string tag, string source) finds the first occurrence of <tag>ddd</tag> and returns ddd.")
                .params(tagParam,sourceParam)
                .returnType(String.class)
                .build();
    }
}

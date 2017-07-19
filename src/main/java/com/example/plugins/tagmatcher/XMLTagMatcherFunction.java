package com.example.plugins.tagmatcher;

import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the plugin. Your class should implement one of the existing plugin
 * interfaces. (i.e. AlarmCallback, MessageInput, MessageOutput)
 */
public class XMLTagMatcherFunction extends AbstractFunction<String> {
	
    private static final Logger LOG = LoggerFactory.getLogger(XMLTagMatcherFunction.class);
	
    public static final String NAME = "tagmatcher";
    private static final String TAG = "tag";
    private static final String SOURCE = "string";

    private final ParameterDescriptor<String,String> tagParam = ParameterDescriptor
    		.string(TAG)
    		.description("The tag to match against.")
 	        .build();

    private final ParameterDescriptor<String,String> sourceParam = ParameterDescriptor
	        .string(SOURCE)
	        .description("The string to match.")
	        .build();
    
    @Override
    public String evaluate(FunctionArgs functionArgs, EvaluationContext evaluationContext) {
    	String matchtag = tagParam.required(functionArgs, evaluationContext);
        String matchsource = sourceParam.required(functionArgs, evaluationContext);

        if (matchtag == null) {
	    LOG.error("Empty tag in tagmatcher pipeline function.");
        	return null;
        }
        if (matchsource == null) {
	    LOG.error("Empty source string in tagmatcher pipeline function.");
        	return null;
        }
        
        LOG.debug("Running tagmatcher pipeline function with tag [{}] and source string [{}].",matchtag,matchsource);
        
        // First outer loop
        int resultstart=0; 	// starting point of the match found
        int resultend=0;  	// ending point of the match found
		int i,j=0;			// indices for comparing the strings.  i for the matchsource and j for the matchtag
        int taglen = matchtag.length()-1;
        int matchstate = 0;	// matching uses a state machine approach.
							// State 0 is the initial state and state 7 the state of a successful match
        
        for (i=0;i<matchsource.length();i++) {
        	switch (matchstate) {
        		case 0:	// find initial < of opening tag
        			if (matchsource.charAt(i) == '<') {	
        				matchstate=1;	
        				j=0;
					}	
        			break;
        		case 1: // check opening tag
        			if (matchsource.charAt(i) == matchtag.charAt(j)) {
        				if (j == taglen) matchstate = 2;
					} else {
        				matchstate = 0;
					}
        			j++;
        			break;
        		case 2: // check opening tag closing >
        			if  (matchsource.charAt(i) == '>') {	
        				matchstate=3;	
        				resultstart=i+1;
					} else {
        				matchstate=0; // the tag was something else
					}
        			break;
        		case 3: // find initial < of closing tag
        			if (matchsource.charAt(i) == '<') {	
        				resultend=i;
        				matchstate=4;
					}	
        			break;
        		case 4: // find initial / of closing tag
        			if (matchsource.charAt(i) == '/') {	
        				matchstate=5;
        				j=0;
					} else {
        				matchstate=3; // was not a closing tag, continue searching the closing tag
					}
        			break;
        		case 5: // check closing tag
        			if (matchsource.charAt(i) == matchtag.charAt(j)) {
        				if (j == taglen) matchstate=6;
					} else {
        				matchstate=3;  // the result string may contain other tags, continue search for the closing tag
					}
        			j++;
        			break;
        		case 6: // check closing tag closing >
			    if  (matchsource.charAt(i) == '>') {	
				matchstate=7;
				} else {
				matchstate=3;
			    }
			    break;
        		case 7:
        		default:
			    i= matchsource.length();
			    break;
        	}
        }
        	
        if (matchstate != 7) return null;
        	
        return matchsource.substring(resultstart,resultend);
    }
    
    
    
    @Override
    public FunctionDescriptor<String> descriptor() {
    	return FunctionDescriptor.<String>builder()
                .name(NAME)
                .description("Returns the string within XML tags.")
                .params(tagParam,sourceParam)
                .returnType(String.class)
                .build();
    }
}

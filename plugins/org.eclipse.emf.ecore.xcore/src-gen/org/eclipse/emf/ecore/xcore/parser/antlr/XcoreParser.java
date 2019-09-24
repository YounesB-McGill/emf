/*
 * generated by Xtext
 */
package org.eclipse.emf.ecore.xcore.parser.antlr;

import com.google.inject.Inject;

import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.emf.ecore.xcore.services.XcoreGrammarAccess;

public class XcoreParser extends org.eclipse.xtext.parser.antlr.AbstractAntlrParser {
	
	@Inject
	private XcoreGrammarAccess grammarAccess;
	
	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT");
	}
	
	@Override
	protected org.eclipse.emf.ecore.xcore.parser.antlr.internal.InternalXcoreParser createParser(XtextTokenStream stream) {
		return new org.eclipse.emf.ecore.xcore.parser.antlr.internal.InternalXcoreParser(stream, getGrammarAccess());
	}
	
	@Override 
	protected String getDefaultRuleName() {
		return "XPackage";
	}
	
	public XcoreGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}
	
	public void setGrammarAccess(XcoreGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
	
}

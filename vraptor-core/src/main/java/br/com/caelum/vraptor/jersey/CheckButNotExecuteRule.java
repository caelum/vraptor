package br.com.caelum.vraptor.jersey;

import java.util.Iterator;
import java.util.Map;

import com.sun.jersey.server.impl.uri.PathPattern;
import com.sun.jersey.server.impl.uri.rules.RootResourceClassesRule;
import com.sun.jersey.server.impl.uri.rules.UriRulesFactory;
import com.sun.jersey.server.probes.UriRuleProbeProvider;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import com.sun.jersey.spi.uri.rules.UriRules;

/**
 * Checks if a jersey rule supports something, but do not invoke it.
 * 
 * @author Guilherme Silveira
 */
class CheckButNotExecuteRule implements UriRule {

	public static final String RULES_FOUND = "br.com.caelum.vraptor.jersey.RULES_FOUND";
	private final UriRules<UriRule> rules;

	public CheckButNotExecuteRule(Map<PathPattern, UriRule> rulesMap) {
		this.rules = UriRulesFactory.create(rulesMap);
	}

	public boolean accept(CharSequence path, Object resource,
			UriRuleContext context) {
		if (context.getProperties().containsKey(RULES_FOUND)) {
			return false;
		}
		UriRuleProbeProvider.ruleAccept(RootResourceClassesRule.class
				.getSimpleName(), path, resource);

		if (context.isTracingEnabled()) {
			context.trace("accept root resource classes: \"" + path + "\"");
		}

		final Iterator<UriRule> matches = rules.match(path, context);
		context.getProperties().put(RULES_FOUND, matches);
		return matches.hasNext();
	}

}

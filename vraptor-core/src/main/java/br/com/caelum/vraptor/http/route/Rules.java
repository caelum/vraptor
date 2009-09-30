
package br.com.caelum.vraptor.http.route;

import java.util.ArrayList;
import java.util.List;

/**
 * Rules for resource localization.
 *
 * By default, Routes added by this class will have higher priority (will have
 * lower value of priority), so will be tested first at Router.parse method.
 *
 * @author Guilherme Silveira
 */
public abstract class Rules {
    private final Router router;
	private final List<RouteBuilder> routesToBuild = new ArrayList<RouteBuilder>();

    public Rules(Router router) {
        this.router = router;
        routes();
        for(RouteBuilder builder : routesToBuild) {
            router.add(builder.build());
        }
    }

    public abstract void routes();

    protected final RouteBuilder routeFor(String uri) {
        RouteBuilder rule = router.builderFor(uri);
        rule.withPriority(Integer.MIN_VALUE);
        this.routesToBuild.add(rule);
        return rule;
    }

    protected final PatternBasedType type(String pattern) {
        return new PatternBasedType(pattern);
    }

    protected final PatternBasedType method(String pattern) {
        return new PatternBasedType(pattern);
    }

}

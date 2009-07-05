package br.com.caelum.vraptor.ioc;

/**
 * @author: Fabio Kung
*/
@Component
public class TheComponentFactory implements ComponentFactory<NeedsCustomInstantiation> {
    public NeedsCustomInstantiation getInstance() {
        return new NeedsCustomInstantiation();
    }

    public Class<NeedsCustomInstantiation> getType() {
        return NeedsCustomInstantiation.class;
    }
}

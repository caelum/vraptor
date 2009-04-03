package br.com.caelum.vraptor.http.ognl;

public class CachedCustomHandlers implements CustomHandlers {

    private final CustomHandlers handlers;

    public CachedCustomHandlers(CustomHandlers handlers) {
        this.handlers = handlers;
    }

    public void register(CustomHandler handler) {
        handlers.register(handler);
    }

    public CustomHandler to(Object target) {
        return handlers.to(target);
    }

}

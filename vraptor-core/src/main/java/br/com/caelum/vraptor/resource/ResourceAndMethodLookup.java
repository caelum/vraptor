package br.com.caelum.vraptor.resource;

public interface ResourceAndMethodLookup {
    public ResourceMethod methodFor(String id, String methodName);

}

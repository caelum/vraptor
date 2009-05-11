package br.com.caelum.vraptor.proxy;

/**
 * Implementations of this interface are used to create Proxy instances whenever needed.
 *
 * @author Fabio Kung
 */
public interface Proxifier {
    <T> T proxify(Class<T> type, MethodInvocation<? super T> handler);
}

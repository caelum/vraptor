package br.com.caelum.vraptor.serialization;

/**
 * SerializerBuilder used on internal API to build a {@link Serializer} object.
 *
 * @author Tomaz Lavieri
 * @since 3.1.2
 */
public interface SerializerBuilder extends Serializer {
	<T> Serializer from(T object);
	<T> Serializer from(T object, String alias);
}
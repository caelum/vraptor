package br.com.caelum.vraptor.serialization.gson.adapters;

import java.lang.reflect.Type;

import org.hibernate.proxy.HibernateProxy;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class HibernateProxySerializer implements JsonSerializer<HibernateProxy> {

	public JsonElement serialize(HibernateProxy proxyObj, Type type, JsonSerializationContext ctx) {
		Object deProxied = proxyObj.getHibernateLazyInitializer().getImplementation();
		return ctx.serialize(deProxied);
	}
}
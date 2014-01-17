package br.com.caelum.vraptor.restfulie.serialization;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;

public class MethodValueSupportConverterTest {
	
	public static class Player {
		@XStreamSerialize
		public String getName() {
			return "custom_name";
		}
	}
	
	public static class Friend {
		@XStreamSerialize
		public Player getPlayer() {
			return new Player();
		}
	}
	
	@Test
	public void whenMethodIsAnnotatedAddsItsContent() {
		XStream x = new XStream();
		x.alias("player", Player.class);
		x.registerConverter(new MethodValueSupportConverter(new ReflectionConverter(x.getMapper(), x.getReflectionProvider())), XStream.PRIORITY_LOW);
		String content = x.toXML(new Player());
		assertThat(content, is(equalTo("<player>\n  <name>custom_name</name>\n</player>")));
	}

	@Test
	public void whenMethodReturnsAComplexTypeSerializesIt() {
		XStream x = new XStream();
		x.alias("friend", Friend.class);
		x.alias("player", Player.class);
		x.registerConverter(new MethodValueSupportConverter(new ReflectionConverter(x.getMapper(), x.getReflectionProvider())), XStream.PRIORITY_LOW);
		String content = x.toXML(new Friend());
		assertThat(content, is(equalTo("<friend>\n  <player>\n    <name>custom_name</name>\n  </player>\n</friend>")));
	}

}

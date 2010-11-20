package br.com.caelum.vraptor.ruby;

import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.jruby.RubyClass;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.StereotypeHandler;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;

public class ControllerLoader {
	
	private final List<StereotypeHandler> stereotypeHandlers;
	private final ApplicationContext beanFactory;

	public ControllerLoader(List<StereotypeHandler> stereotypeHandlers, ApplicationContext beanFactory) {
		this.stereotypeHandlers = stereotypeHandlers;
		this.beanFactory = beanFactory;
	}
	
	@PostConstruct
	protected void handleRefresh() {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("jruby");

		try {
			engine.eval(new InputStreamReader(ControllerLoader.class
					.getResourceAsStream("cute.rb")));
			Object result = engine.eval("MyController");
			System.out.println(result.getClass());
			for(String name:((RubyClass) result).getMethods().keySet()) {
				System.out.println(name);
				String[] s = read("cute.rb", "MyController", name);
				System.out.println(Arrays.toString(s));
			}
			Proxifier profixier = beanFactory.getBean(Proxifier.class);
			Class type = profixier.proxify(Class.class, new MethodInvocation<Class>() {

				public Object intercept(Class proxy, Method method, Object[] args,
						SuperMethod superMethod) {
					System.out.println("Invocadndo");
					return null;
				}
			});
			invoke_for(type);
		} catch (BeansException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
	
	private String[] read(String file, String type, String method) {
		InputStreamReader reader = new InputStreamReader(ControllerLoader.class
				.getResourceAsStream(file));
		Scanner sc = new Scanner(reader);
		boolean found = false;
		method = "def " + method;
		while(sc.hasNextLine()) {
			String line = sc.nextLine();
			if(found && line.contains(method)) {
				return trims(line.substring(line.indexOf(method)+ method.length(), line.length()));
			} else if(line.contains("class " + type)) {
				found = true;
			}
		}
		return new String[0];
	}

	private String[] trims(String substring) {
		substring = substring.replaceAll(" ", "");
		if(substring.startsWith("(")) {
			substring = substring.substring(1, substring.length());
		}
		if(substring.endsWith(")")) {
			substring = substring.substring(0, substring.length()-1);
		}
		return substring.split(",");
	}

	protected void invoke_for(Class<?> beanType) {
		for (StereotypeHandler handler : stereotypeHandlers) {
			if (beanType.isAnnotationPresent(handler.stereotype())) {
				handler.handle(beanType);
			}
		}
	}
}

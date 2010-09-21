package br.com.caelum.vraptor.scan;

import java.util.Collection;

/**
 * A static Scanner that generates an static WebAppBootstrap
 * 
 * @author Sérgio Lopes
 */
public class VRaptorStaticScanning {

	/**
	 * @param args[0] (optional) The WEB-INF/web.xml location
	 */
	public static void main(String[] args) {
		VRaptorStaticScanning app = new VRaptorStaticScanning();
		app.start(args.length == 1 ? args[0] : null);
	}
	
	void start(String webxml) {
		System.out.println("Starting VRaptor's static classpath scanning");
		
		ClasspathResolver cpr;
		if (webxml == null)
			cpr = new StandaloneClasspathResolver();
		else
			cpr = new StandaloneClasspathResolver(webxml);
		
		System.out.print("Initiating the scanning...");
		ComponentScanner scanner = new ScannotationComponentScanner();
		Collection<String> classes = scanner.scan(cpr);
		System.out.println(" done.");
		
		System.out.print("Generating the static registry...");
		BootstrapGenerator generator = new JavassistBootstrapGenerator();
		generator.generate(classes, cpr);
		System.out.println(" done.");
	}
}

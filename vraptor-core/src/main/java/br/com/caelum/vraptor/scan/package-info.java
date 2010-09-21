/**
 * Classpath scanning for VRaptor projects. It can scan everything dynamically when 
 * the context starts or can scan in build time and generate proper artifacts to prevent
 * performance degradation in runtime. 
 * 
 * VRaptor will scan:
 * 
 *  - all components in WEB-INF/classes/
 *  - all base packages from JARs in WEB-INF/classes
 * 
 * There are two options to configure the base packages:
 * 
 *  - configure the br.com.caelum.vraptor.packages context-param in web.xml
 *  - create a META-INF/br.com.caelum.vraptor.packages file inside some JARs
 * 
 * @author Sérgio Lopes
 * @since 3.2
 */
package br.com.caelum.vraptor.scan;


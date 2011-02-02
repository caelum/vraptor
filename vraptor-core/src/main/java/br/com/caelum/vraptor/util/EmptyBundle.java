/**
 *
 */
package br.com.caelum.vraptor.util;

import java.util.ListResourceBundle;

/**
 * Representing an empty resource bundle.
 * 
 * @author Lucas Cavalcanti
 */
public class EmptyBundle extends ListResourceBundle {
	@Override
	protected Object[][] getContents() {
	    return new Object[0][0];
	}
}
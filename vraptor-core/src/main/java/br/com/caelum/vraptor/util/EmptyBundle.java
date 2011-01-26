/**
 *
 */
package br.com.caelum.vraptor.util;

import java.util.ListResourceBundle;

public class EmptyBundle extends ListResourceBundle {
	@Override
	protected Object[][] getContents() {
	    return new Object[0][0];
	}
}
package br.com.caelum.vraptor.restfulie.relation;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import br.com.caelum.vraptor.core.Routes;

public class ControllerBasedRelationTest {
	
	class CustomController {
		private int id;

		public void show(int id) {
			this.id=id;
		}
	}

	@Test
	public void shouldRetrieveTheURIForARoute() throws SecurityException, NoSuchMethodException {
		Routes routes = mock(Routes.class);
		ControllerBasedRelation relation = new ControllerBasedRelation(CustomController.class, null, CustomController.class.getDeclaredMethod("show", int.class), new Object[]{15}, routes);
		CustomController controller = new CustomController();
		when(routes.uriFor(CustomController.class)).thenReturn(controller);
		when(routes.getUri()).thenReturn("http://caelumobjects.com");
		assertEquals("http://caelumobjects.com", relation.getUri());
		assertEquals(15, controller.id);
	}
	
}

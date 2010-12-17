package br.com.caelum.vraptor.interceptor;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import br.com.caelum.vraptor.Intercepts;

public class TopologicalSetTest {

	static interface A extends Interceptor {}

	@Intercepts(before=A.class)
	static interface B extends Interceptor {}

	@Intercepts(after=A.class)
	static interface C extends Interceptor {}

	@Intercepts(after=A.class, before=C.class)
	static interface D extends Interceptor {}

	@Intercepts(before=A.class, after=C.class)
	static interface E extends Interceptor {}

	static interface F extends Interceptor {}

	@Test
	public void returnsAddedClasses() throws Exception {
		TopologicalSet set = new TopologicalSet();
		set.addAll(A.class, B.class, C.class);
		List<Class<? extends Interceptor>> list = set.toList();

		assertThat(list, hasSize(3));
		assertThat(list, hasItems(A.class, B.class, C.class));
	}

	@Test
	public void respectsAfterAttribute() throws Exception {
		TopologicalSet set = new TopologicalSet();
		set.add(A.class);
		set.add(C.class);
		assertThat(set.toList(), contains(A.class, C.class));

		set = new TopologicalSet();
		set.add(C.class);
		set.add(A.class);
		assertThat(set.toList(), contains(A.class, C.class));

	}

	@Test
	public void respectsBeforeAndAfterAttribute() throws Exception {
		TopologicalSet set = new TopologicalSet();
		set.add(A.class);
		set.add(C.class);
		set.add(D.class);
		assertThat(set.toList(), contains(A.class, D.class, C.class));

		set = new TopologicalSet();
		set.add(C.class);
		set.add(D.class);
		set.add(A.class);
		assertThat(set.toList(), contains(A.class, D.class, C.class));

	}

	@Test(expected=IllegalStateException.class)
	public void failsOnCycles() throws Exception {
		TopologicalSet set = new TopologicalSet();
		set.add(A.class);
		set.add(C.class);
		set.add(E.class);
		set.toList();
	}

	@Test
	public void respectsInsertionOrderIfNoRelationIsSet() throws Exception {
		TopologicalSet set = new TopologicalSet();
		set.add(A.class);
		set.add(F.class);
		assertThat(set.toList(), contains(A.class, F.class));

		set = new TopologicalSet();
		set.add(F.class);
		set.add(A.class);
		assertThat(set.toList(), contains(F.class, A.class));
	}

	@Test
	public void respectsBeforeAttribute() throws Exception {
		TopologicalSet set = new TopologicalSet();
		set.add(A.class);
		set.add(B.class);
		assertThat(set.toList(), contains(B.class, A.class));

		set = new TopologicalSet();
		set.add(B.class);
		set.add(A.class);
		assertThat(set.toList(), contains(B.class, A.class));

	}
}

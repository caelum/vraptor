package br.com.caelum.vraptor.mydvds.user;

import org.junit.Test;

import br.com.caelum.vraptor.mydvds.IntegrationTestCase;

public class UserRegistrationTest extends IntegrationTestCase {

	@Test
	public void registeringAnInvalidUser() throws Exception {
		openRootPage()
			.fillRegisterForm()
				.withName("I am")
				.withLogin("too")
				.withPassword("short")
				.andSubmit();
		assertContainsErrors();
	}

	@Test
	public void logginInWithAnInvalidUser() throws Exception {
		openRootPage()
			.fillRegisterForm()
				.withLogin("doesnt")
				.withPassword("exist")
				.andSubmit();
		assertContainsErrors();
	}
}

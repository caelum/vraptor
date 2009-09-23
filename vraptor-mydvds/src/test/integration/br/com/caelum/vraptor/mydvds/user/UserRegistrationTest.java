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
			.fillLoginForm()
				.withLogin("doesnt")
				.withPassword("exist")
				.andSubmit();
		assertContainsErrors();
	}

	@Test
	public void registeringAValidUser() throws Exception {
		openRootPage()
			.fillRegisterForm()
				.withName("John Paul")
				.withLogin("john")
				.withPassword("paulie")
				.andSubmit();
		assertContainsMessage("new user added successfully");

	}

	@Test
	public void logginInWithAValidUser() throws Exception {
		thereIsAUserNamed("johnny");
		openRootPage()
			.fillLoginForm()
				.withLogin("johnny")
				.withPassword("johnny")
				.andSubmit();
		assertLoggedUserIs("johnny");
	}
}

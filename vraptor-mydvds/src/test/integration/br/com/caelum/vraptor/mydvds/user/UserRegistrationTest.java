package br.com.caelum.vraptor.mydvds.user;

import org.junit.Test;

import br.com.caelum.vraptor.mydvds.IntegrationTestCase;

public class UserRegistrationTest extends IntegrationTestCase {

	@Test
	public void registeringAnInvalidUser() throws Exception {
		openRootPage();
	}
}

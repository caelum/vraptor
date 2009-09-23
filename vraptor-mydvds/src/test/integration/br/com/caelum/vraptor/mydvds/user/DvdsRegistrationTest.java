package br.com.caelum.vraptor.mydvds.user;

import org.junit.Test;

import br.com.caelum.vraptor.mydvds.IntegrationTestCase;

/**
 * Integration tests for dvds registration and searching
 *
 * @author Lucas Cavalcanti
 *
 */
public class DvdsRegistrationTest extends IntegrationTestCase {

	@Test
	public void registeringAnInvalidDvd() throws Exception {
		loginAs("vraptorguy")
			.fillRegisterDvdForm()
				.withTitle("")
				.withDescription("")
				.andSend();
		assertContainsErrors();
	}
}

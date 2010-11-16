/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
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
	@Test
	public void registeringAValidDvd() throws Exception {
		loginAs("vraptorguy")
			.fillRegisterDvdForm()
				.withTitle("A Song")
				.withDescription("You should listen to this")
				.andSend();
		assertContainsMessage("A Song dvd added");
	}
}

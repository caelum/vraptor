/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.example;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.example.dao.Database;
import br.com.caelum.vraptor.validator.Hibernate;
import br.com.caelum.vraptor.validator.Validations;
import br.com.caelum.vraptor.view.Results;

@Resource
public class ClientsController {

	private final Result result;

	private final Database database;

	private final Validator validator;

	public ClientsController() {
		this(null, null, null);
	}

	public ClientsController(Result result, Database database, Validator validator) {
		this.result = result;
		this.database = database;
		this.validator = validator;
	}

	@Path("/clients")
	@Get
	public void list() {
		result.include("clients", database.all());
	}

	@Path("/clients")
	@Post
	public void add(final Client client) {
		validator.checking(new Validations() {
			{
				// has the same result as:
				// if(client!=null)
				// but cuter?!
				that(client).shouldBe(notNullValue()).otherwise(new Validations() {
					public void check() {
						that(client.getAge(), is(greaterThan(10)));
					}
				});
				and(Hibernate.validate(client));
			}
		});
		database.add(client);
	}

	@Path("/clients/{client.id}")
	@Delete
	public void delete(Client client) {
		database.remove(client);
		result.use(Results.logic()).redirectClientTo(ClientsController.class).list();
	}

	@Path("/clients/{client.id}")
	@Get
	public void view(Client client) {
		result.include("client", database.find(client.getId()));
	}

	public void sendEmail() {
		result.use(EmptyResult.class);
	}

	public void random() {
		ArrayList<Client> all = new ArrayList<Client>(database.all());
		Client client = all.get((int) (Math.random() * all.size()));
		result.use(Results.logic()).redirectClientTo(ClientsController.class).view(client);
	}

}

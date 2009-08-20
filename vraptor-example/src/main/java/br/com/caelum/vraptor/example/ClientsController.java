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

import static br.com.caelum.vraptor.view.Results.logic;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.example.dao.Repository;
import br.com.caelum.vraptor.validator.Hibernate;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.validator.Validations;

@Resource
public class ClientsController {

	private final Result result;

	private final Repository repository;

	private final Validator validator;

	public ClientsController(Result result, Repository repository, Validator validator) {
		this.result = result;
		this.repository = repository;
		this.validator = validator;
	}

	@Get
	@Path("/clients")
	public Collection<Client> list() {
		return repository.all();
	}

	@Post
	@Path("/clients")
	public void add(final Client client) {
		validator.onError().goTo(ClientsController.class).list();
		if(client.getName().equals("guilherme")) {
			validator.add(new ValidationMessage("", "ha!"));
		}
		validator.validate();
		validator.checking(new Validations() {{
			that("client", "should_not_be_null", client != null);
			if(client != null) {
				that("client", "should_not_be_null", client.getAge() > 10);
			}
			and(Hibernate.validate(client));
		}});
		repository.add(client);
	}

	@Delete
	@Path("/clients/{client.id}")
	public void delete(Client client) {
		repository.remove(client);
		result.use(logic()).redirectTo(ClientsController.class).list();
	}

	@Get
	@Path("/clients/{client.id}")
	public void view(Client client) {
		result.include("client", repository.find(client.getId()));
	}

	public void sendEmail() {
		result.use(EmptyResult.class);
	}

	public void random() {
		ArrayList<Client> all = new ArrayList<Client>(repository.all());
		Client client = all.get((int) (Math.random() * all.size()));
		result.use(logic()).redirectTo(ClientsController.class).view(client);
	}

	public File download(Client client) {
		final Client found = repository.find(client.getId());
		validator.onError().goTo(ClientsController.class).view(client);
		validator.checking(new Validations() {{
			that(found.getFile()).shouldBe("download", "client_without_file", notNullValue());
		}});
		return found.getFile().getFile();
	}

    public void form() {
        // TODO
    }
}

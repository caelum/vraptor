/***
 * 
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
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
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.example.dao;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.example.Client;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * A simple implementation of in memory database.
 * 
 * @author guilherme silveira
 */
@ApplicationScoped
public class InMemoryDatabase implements Database {

	private final Map<Long, Client> clients = new HashMap<Long, Client>();
	
	private static final Logger logger = LoggerFactory.getLogger(InMemoryDatabase.class);

	public void add(Client c) {
		clients.put(c.getId(), c);
	}

	public Collection<Client> all() {
		return clients.values();
	}

	@PostConstruct
	public void startup() {
		logger.info("Starting up the database... configuration should be done here");
		Client guilherme = new Client();
		guilherme.setId(1L);
		guilherme.setName("Guilherme Silveira");
		guilherme.setEmails(Arrays.asList("guilherme.silveira@caelum.com.br"));
		guilherme.setAge(27);
		add(guilherme);
	}

	public void remove(Client client) {
		clients.remove(client.getId());
	}

	public Client find(Long id) {
		return clients.get(id);
	}

}

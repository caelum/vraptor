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
package br.com.caelum.vraptor.ioc.pico;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.http.Router;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Searchs for resources in the web's web-inf classes directory.
 * 
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class WebInfClassesScanner implements ResourceLoader {

	private static final Logger logger = LoggerFactory.getLogger(WebInfClassesScanner.class);

	private final File classes;

	private final DirScanner<?> scanner;

	private final InterceptorRegistry interceptors;

	private final Router router;

	@SuppressWarnings("unchecked")
	public WebInfClassesScanner(ServletContext context, DirScanner scanner, Router router,
			InterceptorRegistry interceptors) {
		this.router = router;
		this.interceptors = interceptors;
		String path = context.getRealPath("");
		this.classes = new File(path, "WEB-INF/classes");
		this.scanner = scanner;
	}

	public void loadAll() {
		logger.info("Starting looking for " + classes.getAbsolutePath());
		scanner.scan(classes, new ResourceAcceptor(router));

		List<Class<? extends Interceptor>> interceptors = new ArrayList<Class<? extends Interceptor>>();
		scanner.scan(classes, new InterceptorAcceptor(interceptors));
		this.interceptors.register(interceptors);
	}

}

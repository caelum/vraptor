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

package br.com.caelum.vraptor.core;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles the content if the request corresponds to static content (like images, CSS, JS and so on).
 * 
 * @author based on VRaptor2
 */
public interface StaticContentHandler {

    /**
     * Return true if the requested file is a static content, false otherwise.
     */
    boolean requestingStaticFile(HttpServletRequest request) throws MalformedURLException;

    /**
     * Defer the process to container.
     */
    void deferProcessingToContainer(FilterChain filterChain, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException;


}

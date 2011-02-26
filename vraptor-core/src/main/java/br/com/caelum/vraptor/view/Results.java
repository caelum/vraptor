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

package br.com.caelum.vraptor.view;

import br.com.caelum.vraptor.serialization.JSONPSerialization;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.RepresentationResult;
import br.com.caelum.vraptor.serialization.XMLSerialization;

/**
 * Some common results for most web based logics.
 *
 * @author Guilherme Silveira
 */
public class Results {

    /**
     * Offers server side forward and include for web pages.<br>
     * Should be used only with end results (not logics), otherwise you might
     * achieve the server-redirect-hell (f5 problem) issue.
     */
    public static Class<? extends PageResult> page() {
        return PageResult.class;
    }

    /**
     * Server and client side forward to another logic.
     */
    public static Class<LogicResult> logic() {
        return LogicResult.class;
    }

    /**
     * Uses an empty page.
     */
    public static Class<EmptyResult> nothing() {
    	return EmptyResult.class;
    }

    /**
     * Sends information through the HTTP protocol, like
     * status codes and header
     */
    public static Class<HttpResult> http() {
    	return HttpResult.class;
    }

    /**
     * Uses Referer header to redirect or forward
     */
    public static Class<RefererResult> referer() {
    	return RefererResult.class;
    }

    /**
     * Uses pure header status.
     */
    public static Class<Status> status() {
    	return Status.class;
    }

    /**
     * Uses xml serialization.
     */
    public static Class<XMLSerialization> xml() {
    	return XMLSerialization.class;
    }

    /**
     * Uses json serialization.
     */
    public static Class<JSONSerialization> json() {
    	return JSONSerialization.class;
    }

    /**
     * Uses jsonp serialization.
     */
    public static Class<JSONPSerialization> jsonp() {
    	return JSONPSerialization.class;
    }

    /**
     * Uses a representation of an object based on request accept format.
     */
    public static Class<RepresentationResult> representation() {
    	return RepresentationResult.class;
    }



}

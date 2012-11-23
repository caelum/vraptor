/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.serialization.xstream;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.serialization.*;
import br.com.caelum.vraptor.view.ResultException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;

/**
 * XStream implementation for JSONSerialization
 *
 * @author Lucas Cavalcanti
 * @since 3.0.2
 */
@Component
public class XStreamJSONSerialization implements JSONSerialization {

    protected final HttpServletResponse response;
    protected final TypeNameExtractor extractor;
    protected final ProxyInitializer initializer;
    protected final XStreamBuilder builder;

    public XStreamJSONSerialization(HttpServletResponse response, TypeNameExtractor extractor, ProxyInitializer initializer, XStreamBuilder builder) {
        this.response = response;
        this.extractor = extractor;
        this.initializer = initializer;
        this.builder = builder;
    }

    public boolean accepts(String format) {
        return "json".equals(format);
    }

    public <T> Serializer from(T object) {
        return from(object, null);
    }

    public <T> Serializer from(T object, String alias) {

        response.setContentType("application/json");
        return getSerializer().from(object, alias);
    }

    protected SerializerBuilder getSerializer() {
        try {
            return new XStreamSerializer(getXStream(), response.getWriter(), extractor, initializer);
        } catch (IOException e) {
            throw new ResultException("Unable to serialize data", e);
        }
    }

    /**
     * You can override this method for configuring Driver before serialization
     */
    public <T> NoRootSerialization withoutRoot() {
       	builder.withoutRoot();
        return this;
    }
    
    public JSONSerialization indented() {
        builder.indented();
        return this;
    }

    /**
     * You can override this method for configuring XStream before serialization
     *
     * @deprecated prefer overwriting XStreamBuilder
     * @return a configured instance of xstream
     */
    @Deprecated
    protected XStream getXStream() {
        return builder.jsonInstance();
    }

    /**
     * You can override this method for configuring Driver before serialization
     * @deprecated Override this method on XStreamBuilderImpl instead. WARN: this method will be ignored!
     * @return configured hierarchical driver
     */
    @Deprecated
    protected HierarchicalStreamDriver getHierarchicalStreamDriver() { return null; }

}

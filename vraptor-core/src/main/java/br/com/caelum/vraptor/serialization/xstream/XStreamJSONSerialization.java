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
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.NoRootSerialization;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.view.ResultException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;
import com.thoughtworks.xstream.mapper.MapperWrapper;

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
    private HierarchicalStreamDriver driver;
    
    private boolean withoutRoot;
    private boolean indented;
    
    protected static final String DEFAULT_NEW_LINE = "";
    protected static final char[] DEFAULT_LINE_INDENTER = {};
    
    protected static final String INDENTED_NEW_LINE = "\n";
    protected static final char[] INDENTED_LINE_INDENTER = { ' ', ' '};

    public XStreamJSONSerialization(HttpServletResponse response, TypeNameExtractor extractor, ProxyInitializer initializer) {
        this.response = response;
        this.extractor = extractor;
        this.initializer = initializer;
    }

    public boolean accepts(String format) {
        return "json".equals(format);
    }

    public <T> Serializer from(T object) {
        return from(object, null);
    }

    public <T> Serializer from(T object, String alias) {
        final String newLine = (indented ? INDENTED_NEW_LINE : DEFAULT_NEW_LINE);
        final char[] lineIndenter = (indented ? INDENTED_LINE_INDENTER : DEFAULT_LINE_INDENTER);

        driver = new JsonHierarchicalStreamDriver() {
            public HierarchicalStreamWriter createWriter(Writer writer) {
                if (withoutRoot) {
                    return new JsonWriter(writer, lineIndenter, newLine, JsonWriter.DROP_ROOT_MODE);
                }

                return new JsonWriter(writer, lineIndenter, newLine);
            }
        };

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
     * You can override this method for configuring XStream before serialization
     */
    protected XStream getXStream() {
        return new XStream(getHierarchicalStreamDriver()) {
            {setMode(NO_REFERENCES);}
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new VRaptorClassMapper(next, extractor);
            }
        };
    }

    /**
     * You can override this method for configuring Driver before serialization
     */
    protected HierarchicalStreamDriver getHierarchicalStreamDriver() {
        return this.driver;
    }

    public <T> NoRootSerialization withoutRoot() {
        withoutRoot = true;
        return this;
    }
    
    public JSONSerialization indented() {
        indented = true;
        return this;
    }
}

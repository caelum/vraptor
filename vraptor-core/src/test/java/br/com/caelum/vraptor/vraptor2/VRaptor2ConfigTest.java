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
package br.com.caelum.vraptor.vraptor2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.VRaptorException;
import org.vraptor.plugin.VRaptorPlugin;
import org.vraptor.webapp.WebApplication;

public class VRaptor2ConfigTest {

    private Mockery mockery;
    private ServletContext context;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.context = mockery.mock(ServletContext.class);
    }

    @Test
    public void isAbleToReadRegexViewManager() throws IOException, ConfigException {
        final File file = create("<regex-view-manager>/custom/$component/$logic.$result.jsp</regex-view-manager>");
        mockery.checking(new Expectations() {
            {
                one(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                will(returnValue(file.getAbsolutePath()));
                one(context).getRealPath("/WEB-INF/classes/views.properties");
                will(returnValue(new File("unknown").getAbsolutePath()));
            }
        });
        VRaptor2Config config = new VRaptor2Config(context);
        assertThat(config.getViewPattern(), is(equalTo("/custom/$component/$logic.$result.jsp")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void readsViewsPropertiesIfFound() throws IOException, ConfigException {
        final File file = create("key = value");
        mockery.checking(new Expectations() {
            {
                one(context).getRealPath("/WEB-INF/classes/views.properties");
                will(returnValue(file.getAbsolutePath()));
                one(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                will(returnValue(new File("unknown").getAbsolutePath()));
            }
        });
        VRaptor2Config config = new VRaptor2Config(context);
        assertThat(config.getForwardFor("key"), is(equalTo("value")));
        mockery.assertIsSatisfied();
    }

    private File create(String content) throws IOException {
        File file = File.createTempFile("_vraptor3_test", "xml");
        FileWriter fw = new FileWriter(file);
        fw.write("<vraptor>\n\t" + content + "\n</vraptor>");
        fw.close();
        return file;
    }

    @Test
    public void usesTheDefaultRegexViewManager() throws IOException, ConfigException {
        final File file = create("");
        mockery.checking(new Expectations() {
            {
                one(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                will(returnValue(file.getAbsolutePath()));
                one(context).getRealPath("/WEB-INF/classes/views.properties");
                will(returnValue(new File("unknown").getAbsolutePath()));
            }
        });
        VRaptor2Config config = new VRaptor2Config(context);
        assertThat(config.getViewPattern(), is(equalTo("/$component/$logic.$result.jsp")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void usesTheDefaultRegexViewManagerIfFileDoesNotExist() throws IOException, ConfigException {
        mockery.checking(new Expectations() {
            {
                one(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                will(returnValue(new File("unknown_file").getAbsolutePath()));
                one(context).getRealPath("/WEB-INF/classes/views.properties");
                will(returnValue(new File("unknown").getAbsolutePath()));
            }
        });
        VRaptor2Config config = new VRaptor2Config(context);
        assertThat(config.getViewPattern(), is(equalTo("/$component/$logic.$result.jsp")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void readsConverters() throws IOException, ConfigException {
        final File file = create("<converter>myCustomConverter</converter>");
        mockery.checking(new Expectations() {
            {
                one(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                will(returnValue(file.getAbsolutePath()));
                one(context).getRealPath("/WEB-INF/classes/views.properties");
                will(returnValue(new File("unknown").getAbsolutePath()));
            }
        });
        VRaptor2Config config = new VRaptor2Config(context);
        assertThat(config.getConverters(), hasItem("myCustomConverter"));
        assertThat(config.getConverters(), hasSize(1));
        mockery.assertIsSatisfied();
    }

    class MyPluginType implements VRaptorPlugin {
        public void init(WebApplication application) throws VRaptorException {
        }
    }
    @Test
    public void readsPlugins() throws IOException, ConfigException {
        final File file = create("<plugin>" + MyPluginType.class.getName() + "</plugin>");
        mockery.checking(new Expectations() {
            {
                one(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                will(returnValue(file.getAbsolutePath()));
                one(context).getRealPath("/WEB-INF/classes/views.properties");
                will(returnValue(new File("unknown").getAbsolutePath()));
            }
        });
        VRaptor2Config config = new VRaptor2Config(context);
        assertThat(config.hasPlugin(MyPluginType.class.getName()), is(equalTo(true)));
        mockery.assertIsSatisfied();
    }

}

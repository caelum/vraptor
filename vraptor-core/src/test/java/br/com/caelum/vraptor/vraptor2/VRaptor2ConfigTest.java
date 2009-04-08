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

public class VRaptor2ConfigTest {

    private Mockery mockery;
    private ServletContext context;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.context = mockery.mock(ServletContext.class);
    }

    @Test
    public void isAbleToReadRegexViewManager() throws IOException {
        final File file = create("<regex-view-manager>/custom/$component/$logic.$result.jsp</regex-view-manager>");
        mockery.checking(new Expectations() {
            {
                one(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                will(returnValue(file.getAbsolutePath()));
            }
        });
        VRaptor2Config config = new VRaptor2Config(context);
        assertThat(config.getViewPattern(), is(equalTo("/custom/$component/$logic.$result.jsp")));
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
    public void usesTheDefaultRegexViewManager() throws IOException {
        final File file = create("");
        mockery.checking(new Expectations() {
            {
                one(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                will(returnValue(file.getAbsolutePath()));
            }
        });
        VRaptor2Config config = new VRaptor2Config(context);
        assertThat(config.getViewPattern(), is(equalTo("/$component/$logic.$result.jsp")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void usesTheDefaultRegexViewManagerIfFileDoesNotExist() throws IOException {
        final File file = create("");
        mockery.checking(new Expectations() {
            {
                one(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                will(returnValue(new File("unknown_file").getAbsolutePath()));
            }
        });
        VRaptor2Config config = new VRaptor2Config(context);
        assertThat(config.getViewPattern(), is(equalTo("/$component/$logic.$result.jsp")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void readsConverters() throws IOException {
        final File file = create("<converter>myCustomConverter</converter>");
        mockery.checking(new Expectations() {
            {
                one(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                will(returnValue(file.getAbsolutePath()));
            }
        });
        VRaptor2Config config = new VRaptor2Config(context);
        assertThat(config.getConverters(), hasItem("myCustomConverter"));
        assertThat(config.getConverters(), hasSize(1));
        mockery.assertIsSatisfied();
    }

}

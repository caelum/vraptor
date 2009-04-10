package br.com.caelum.vraptor.vraptor2;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.resource.DefaultResourceAndMethodLookup;
import br.com.caelum.vraptor.resource.Resource;

public class VRaptor2MethodLookupTest {

    private Mockery mockery;
    private Resource resource;
    private VRaptor2MethodLookup lookup;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.resource = mockery.mock(Resource.class);
        this.lookup = new VRaptor2MethodLookup(resource);
    }
    
    class NonVRaptorComponent {
        public void name() {
        }
    }

    @Test
    public void shouldUseVRaptor3AlgorithmIfNotAVRaptor2Component() {
        mockery.checking(new Expectations() {
            {
                exactly(3).of(resource).getType(); will(returnValue(NonVRaptorComponent.class));
            }
        });
        assertThat(lookup.methodFor("id", "name"), is(equalTo(new DefaultResourceAndMethodLookup(resource).methodFor(
                "id", "name"))));
    }

}

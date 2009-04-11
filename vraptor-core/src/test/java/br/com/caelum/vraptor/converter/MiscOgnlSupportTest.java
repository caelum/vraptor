package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.http.ognl.ListAccessor;
import br.com.caelum.vraptor.http.ognl.ReflectionBasedNullHandler;

/**
 * Unfortunately OGNL sucks so bad in its design that we had to create a "unit"
 * test which accesses more than a single class to test the ognl funcionality.
 * Even worse, OGNL sucks with its static configuration methods in such a way
 * that tests are not thread safe. Summing up: OGNL api sucks, OGNL idea rulez.
 * Tests written here are "acceptance tests" for the Ognl support on http
 * parameters.
 * 
 * @author Guilherme Silveira
 * 
 */
public class MiscOgnlSupportTest {

    private Mockery mockery;
    private Converters converters;
    private OgnlContext context;
    private House house;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.converters = mockery.mock(Converters.class);
        this.house = new House();
        OgnlRuntime.setNullHandler(Object.class, new ReflectionBasedNullHandler());
        OgnlRuntime.setPropertyAccessor(List.class, new ListAccessor());
        this.context = (OgnlContext) Ognl.createDefaultContext(house);
        context.setTraceEvaluations(true);
        // OgnlRuntime.setPropertyAccessor(Set.class, new SetAccessor());
        // OgnlRuntime.setPropertyAccessor(Array.class, new ArrayAccessor());
        // OgnlRuntime.setPropertyAccessor(Map.class, new MapAccessor());
        Ognl.setTypeConverter(context, new OgnlToConvertersController(converters));
    }

    public static class Cat {
        private Leg firstLeg;

        public void setFirstLeg(Leg firstLeg) {
            this.firstLeg = firstLeg;
        }

        public Leg getFirstLeg() {
            return firstLeg;
        }
    }

    public static class Leg {
        private Integer id;
        private Calendar birthDay; // weird leg birthday!!

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setBirthDay(Calendar birthDay) {
            this.birthDay = birthDay;
        }

        public Calendar getBirthDay() {
            return birthDay;
        }
    }
    
    public static class House {
        private Cat cat;

        public void setCat(Cat cat) {
            this.cat = cat;
        }

        public Cat getCat() {
            return cat;
        }
        
    }

    @Test
    public void isCapableOfDealingWithEmptyParameterForInternalWrapperValue() throws OgnlException {
        mockery.checking(new Expectations() {{
            one(converters).to(Integer.class, null); will(returnValue(new IntegerConverter()));
        }});
        Ognl.setValue("cat.firstLeg.id", context, house, "");
        assertThat(house.cat.firstLeg.id, is(equalTo(null)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void isCapableOfDealingWithEmptyParameterForInternalValueWhichNeedsAConverter() throws OgnlException {
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        final VRaptorRequest webRequest = new VRaptorRequest(null, request, null);
        mockery.checking(new Expectations() {{
            exactly(2).of(request).getAttribute("javax.servlet.jsp.jstl.fmt.locale.request"); will(returnValue("pt_br"));
            one(converters).to(Calendar.class, null); will(returnValue(new LocaleBasedCalendarConverter(webRequest)));
        }});
        Ognl.setValue("cat.firstLeg.birthDay", context, house, "10/5/2010");
        assertThat(house.cat.firstLeg.birthDay, is(equalTo((Calendar) new GregorianCalendar(2010,4,10))));
        mockery.assertIsSatisfied();
    }
    
}

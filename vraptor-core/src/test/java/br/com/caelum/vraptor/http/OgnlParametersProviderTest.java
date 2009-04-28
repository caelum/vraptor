package br.com.caelum.vraptor.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import ognl.OgnlException;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.converter.LongConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.ValidationMessage;

public class OgnlParametersProviderTest {

    private VRaptorMockery mockery;
    private Converters converters;
    private OgnlParametersProvider provider;
    private TypeCreator creator;
    private Container container;
    private ParameterNameProvider nameProvider;
    private RequestParameters parameters;
    private EmptyElementsRemoval removal;
	private ArrayList<ValidationMessage> errors;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.converters = mockery.mock(Converters.class);
        this.parameters = mockery.mock(RequestParameters.class);
        this.creator = mockery.mock(TypeCreator.class);
        this.nameProvider = mockery.mock(ParameterNameProvider.class);
        this.container = mockery.mock(Container.class);
        this.removal = new EmptyElementsRemoval();
        this.provider = new OgnlParametersProvider(creator, container, converters, nameProvider, parameters, removal);
        this.errors = new ArrayList<ValidationMessage>();
        mockery.checking(new Expectations() {
            {
                allowing(converters).to((Class) with(an(Class.class)), with(any(Container.class))); will(returnValue(new LongConverter()));
            }
        });
    }

    public static class Cat {
        private String id;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
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

        public void setExtraCats(List<Cat> extraCats) {
            this.extraCats = extraCats;
        }

        public List<Cat> getExtraCats() {
            return extraCats;
        }

        public void setIds(Long[] ids) {
            this.ids = ids;
        }

        private List<String> owners;

        public Long[] getIds() {
            return ids;
        }

        public void setOwners(List<String> owners) {
			this.owners = owners;
		}

		public List<String> getOwners() {
			return owners;
		}

		private List<Cat> extraCats;

        private Long[] ids;

    }

    class MyResource {
        void buyA(House house) {
        }
    }

    public static class BuyASetter {
        private House House_;

        public void setHouse(House house_) {
            House_ = house_;
        }

        public House getHouse() {
            return House_;
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void isCapableOfDealingWithEmptyParameterForInternalWrapperValue() throws OgnlException,
            NoSuchMethodException {
        final Method method = MyResource.class.getDeclaredMethod("buyA", House.class);
        final Matcher<ResourceMethod> resourceMethod = VRaptorMatchers.resourceMethod(method);
        mockery.checking(new Expectations() {
            {
                one(parameters).get("house.cat.id");
                will(returnValue(new String[] { "guilherme" }));
                one(parameters).getNames();
                will(returnValue(new HashSet(Arrays.asList(new String[] { "house.cat.id" }))));
                one(creator).typeFor(with(resourceMethod));
                will(returnValue(BuyASetter.class));
                one(nameProvider).parameterNamesFor(method);
                will(returnValue(new String[] { "House" }));
            }
        });

        Object[] params = provider.getParametersFor(mockery.methodFor(MyResource.class, "buyA", House.class), errors, null);
        House house = (House) params[0];
        assertThat(house.cat.id, is(equalTo("guilherme")));
        mockery.assertIsSatisfied();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void removeFromTheCollectionIfAnElementIsCreatedWithinACollectionButNoFieldIsSet() throws SecurityException,
            NoSuchMethodException {
        final Method method = MyResource.class.getDeclaredMethod("buyA", House.class);
        final Matcher<ResourceMethod> resourceMethod = VRaptorMatchers.resourceMethod(method);
        mockery.checking(new Expectations() {
            {
                one(parameters).get("house.extraCats[1].id");
                will(returnValue(new String[] { "guilherme" }));
                one(parameters).getNames();
                will(returnValue(new HashSet(Arrays.asList(new String[] { "house.extraCats[1].id" }))));
                one(creator).typeFor(with(resourceMethod));
                will(returnValue(BuyASetter.class));
                one(nameProvider).parameterNamesFor(method);
                will(returnValue(new String[] { "House" }));
                allowing(container).instanceFor(EmptyElementsRemoval.class); will(returnValue(removal));
            }
        });
        Object[] params = provider.getParametersFor(mockery.methodFor(MyResource.class, "buyA", House.class), errors, null);
        House house = (House) params[0];
        assertThat(house.extraCats, hasSize(1));
        assertThat(house.extraCats.get(0).id, is(equalTo("guilherme")));
        mockery.assertIsSatisfied();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void removeFromTheCollectionIfAnElementIsCreatedWithinAnArrayButNoFieldIsSet() throws SecurityException,
            NoSuchMethodException {
        final Method method = MyResource.class.getDeclaredMethod("buyA", House.class);
        final Matcher<ResourceMethod> resourceMethod = VRaptorMatchers.resourceMethod(method);
        mockery.checking(new Expectations() {
            {
                one(parameters).get("house.ids[1]");
                will(returnValue(new String[] { "3" }));
                one(parameters).getNames();
                will(returnValue(new HashSet(Arrays.asList(new String[] { "house.ids[1]" }))));
                one(creator).typeFor(with(resourceMethod));
                will(returnValue(BuyASetter.class));
                one(nameProvider).parameterNamesFor(method);
                will(returnValue(new String[] { "House" }));
                one(container).instanceFor(EmptyElementsRemoval.class); will(returnValue(removal));
            }
        });
        Object[] params = provider.getParametersFor(mockery.methodFor(MyResource.class, "buyA", House.class), errors, null);
        House house = (House) params[0];
        assertThat(house.ids.length, is(equalTo(1)));
        assertThat(house.ids[0], is(equalTo(3L)));
        mockery.assertIsSatisfied();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void removeFromTheCollectionIfAnElementIsCreatedWithinACollectionButNoFieldIsSetAppartFromTheValueItselfNotAChild() throws SecurityException,
            NoSuchMethodException {
        final Method method = MyResource.class.getDeclaredMethod("buyA", House.class);
        final Matcher<ResourceMethod> resourceMethod = VRaptorMatchers.resourceMethod(method);
        mockery.checking(new Expectations() {
            {
                one(parameters).get("house.owners[1]");
                will(returnValue(new String[] { "guilherme" }));
                one(parameters).getNames();
                will(returnValue(new HashSet(Arrays.asList(new String[] { "house.owners[1]" }))));
                one(creator).typeFor(with(resourceMethod));
                will(returnValue(BuyASetter.class));
                one(nameProvider).parameterNamesFor(method);
                will(returnValue(new String[] { "House" }));
                one(container).instanceFor(EmptyElementsRemoval.class); will(returnValue(removal));
            }
        });
        Object[] params = provider.getParametersFor(mockery.methodFor(MyResource.class, "buyA", House.class));
        House house = (House) params[0];
        assertThat(house.owners, hasSize(1));
        assertThat(house.owners.get(0), is(equalTo("guilherme")));
        mockery.assertIsSatisfied();
    }


}

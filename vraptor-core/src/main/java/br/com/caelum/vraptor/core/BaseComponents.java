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

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.spi.DependencyProvider;
import br.com.caelum.iogi.spi.ParameterNamesProvider;
import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.config.ApplicationConfiguration;
import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.converter.BigDecimalConverter;
import br.com.caelum.vraptor.converter.BigIntegerConverter;
import br.com.caelum.vraptor.converter.BooleanConverter;
import br.com.caelum.vraptor.converter.ByteConverter;
import br.com.caelum.vraptor.converter.CharacterConverter;
import br.com.caelum.vraptor.converter.DoubleConverter;
import br.com.caelum.vraptor.converter.EnumConverter;
import br.com.caelum.vraptor.converter.FloatConverter;
import br.com.caelum.vraptor.converter.IntegerConverter;
import br.com.caelum.vraptor.converter.LocaleBasedCalendarConverter;
import br.com.caelum.vraptor.converter.LocaleBasedDateConverter;
import br.com.caelum.vraptor.converter.LongConverter;
import br.com.caelum.vraptor.converter.PrimitiveBooleanConverter;
import br.com.caelum.vraptor.converter.PrimitiveByteConverter;
import br.com.caelum.vraptor.converter.PrimitiveCharConverter;
import br.com.caelum.vraptor.converter.PrimitiveDoubleConverter;
import br.com.caelum.vraptor.converter.PrimitiveFloatConverter;
import br.com.caelum.vraptor.converter.PrimitiveIntConverter;
import br.com.caelum.vraptor.converter.PrimitiveLongConverter;
import br.com.caelum.vraptor.converter.PrimitiveShortConverter;
import br.com.caelum.vraptor.converter.ShortConverter;
import br.com.caelum.vraptor.converter.StringConverter;
import br.com.caelum.vraptor.converter.jodatime.LocalDateConverter;
import br.com.caelum.vraptor.converter.jodatime.LocalDateTimeConverter;
import br.com.caelum.vraptor.converter.jodatime.LocalTimeConverter;
import br.com.caelum.vraptor.deserialization.DefaultDeserializers;
import br.com.caelum.vraptor.deserialization.Deserializer;
import br.com.caelum.vraptor.deserialization.Deserializers;
import br.com.caelum.vraptor.deserialization.Deserializes;
import br.com.caelum.vraptor.deserialization.DeserializesHandler;
import br.com.caelum.vraptor.deserialization.JsonDeserializer;
import br.com.caelum.vraptor.deserialization.XMLDeserializer;
import br.com.caelum.vraptor.deserialization.XStreamXMLDeserializer;
import br.com.caelum.vraptor.http.DefaultFormatResolver;
import br.com.caelum.vraptor.http.DefaultResourceTranslator;
import br.com.caelum.vraptor.http.EncodingHandlerFactory;
import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.http.iogi.InstantiatorWithErrors;
import br.com.caelum.vraptor.http.iogi.IogiParametersProvider;
import br.com.caelum.vraptor.http.iogi.VRaptorDependencyProvider;
import br.com.caelum.vraptor.http.iogi.VRaptorInstantiator;
import br.com.caelum.vraptor.http.iogi.VRaptorParameterNamesProvider;
import br.com.caelum.vraptor.http.ognl.EmptyElementsRemoval;
import br.com.caelum.vraptor.http.ognl.OgnlFacade;
import br.com.caelum.vraptor.http.ognl.OgnlParametersProvider;
import br.com.caelum.vraptor.http.route.DefaultRouter;
import br.com.caelum.vraptor.http.route.DefaultTypeFinder;
import br.com.caelum.vraptor.http.route.Evaluator;
import br.com.caelum.vraptor.http.route.JavaEvaluator;
import br.com.caelum.vraptor.http.route.NoRoutesConfiguration;
import br.com.caelum.vraptor.http.route.PathAnnotationRoutesParser;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.http.route.RoutesConfiguration;
import br.com.caelum.vraptor.http.route.RoutesParser;
import br.com.caelum.vraptor.http.route.TypeFinder;
import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.interceptor.DeserializingInterceptor;
import br.com.caelum.vraptor.interceptor.ExceptionHandlerInterceptor;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.FlashInterceptor;
import br.com.caelum.vraptor.interceptor.ForwardToDefaultViewInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.interceptor.OutjectResult;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.interceptor.TopologicalSortedInterceptorRegistry;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.interceptor.download.DownloadInterceptor;
import br.com.caelum.vraptor.interceptor.multipart.CommonsUploadMultipartInterceptor;
import br.com.caelum.vraptor.interceptor.multipart.DefaultMultipartConfig;
import br.com.caelum.vraptor.interceptor.multipart.DefaultServletFileUploadCreator;
import br.com.caelum.vraptor.interceptor.multipart.MultipartConfig;
import br.com.caelum.vraptor.interceptor.multipart.MultipartInterceptor;
import br.com.caelum.vraptor.interceptor.multipart.NullMultipartInterceptor;
import br.com.caelum.vraptor.interceptor.multipart.Servlet3MultipartInterceptor;
import br.com.caelum.vraptor.interceptor.multipart.ServletFileUploadCreator;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFileConverter;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ConverterHandler;
import br.com.caelum.vraptor.ioc.InterceptorStereotypeHandler;
import br.com.caelum.vraptor.ioc.ResourceHandler;
import br.com.caelum.vraptor.ioc.StereotypeHandler;
import br.com.caelum.vraptor.proxy.CglibProxifier;
import br.com.caelum.vraptor.proxy.InstanceCreator;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.ObjenesisInstanceCreator;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.ReflectionInstanceCreator;
import br.com.caelum.vraptor.resource.DefaultMethodNotAllowedHandler;
import br.com.caelum.vraptor.resource.DefaultResourceNotFoundHandler;
import br.com.caelum.vraptor.resource.MethodNotAllowedHandler;
import br.com.caelum.vraptor.resource.ResourceNotFoundHandler;
import br.com.caelum.vraptor.restfulie.RestHeadersHandler;
import br.com.caelum.vraptor.restfulie.headers.DefaultRestDefaults;
import br.com.caelum.vraptor.restfulie.headers.DefaultRestHeadersHandler;
import br.com.caelum.vraptor.restfulie.headers.RestDefaults;
import br.com.caelum.vraptor.serialization.DefaultRepresentationResult;
import br.com.caelum.vraptor.serialization.HTMLSerialization;
import br.com.caelum.vraptor.serialization.HibernateProxyInitializer;
import br.com.caelum.vraptor.serialization.JSONPSerialization;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.NullProxyInitializer;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.serialization.RepresentationResult;
import br.com.caelum.vraptor.serialization.XMLSerialization;
import br.com.caelum.vraptor.serialization.xstream.XStreamBuilder;
import br.com.caelum.vraptor.serialization.xstream.XStreamBuilderImpl;
import br.com.caelum.vraptor.serialization.xstream.XStreamConverters;
import br.com.caelum.vraptor.serialization.xstream.XStreamJSONPSerialization;
import br.com.caelum.vraptor.serialization.xstream.XStreamJSONSerialization;
import br.com.caelum.vraptor.serialization.xstream.XStreamXMLSerialization;
import br.com.caelum.vraptor.validator.BeanValidator;
import br.com.caelum.vraptor.validator.DefaultValidator;
import br.com.caelum.vraptor.validator.DefaultBeanValidator;
import br.com.caelum.vraptor.validator.MethodValidatorCreator;
import br.com.caelum.vraptor.validator.ValidatorCreator;
import br.com.caelum.vraptor.validator.MessageConverter;
import br.com.caelum.vraptor.validator.MessageInterpolatorFactory;
import br.com.caelum.vraptor.validator.MethodValidatorInterceptor;
import br.com.caelum.vraptor.validator.NullBeanValidator;
import br.com.caelum.vraptor.validator.Outjector;
import br.com.caelum.vraptor.validator.ReplicatorOutjector;
import br.com.caelum.vraptor.validator.ValidatorFactoryCreator;
import br.com.caelum.vraptor.view.AcceptHeaderToFormat;
import br.com.caelum.vraptor.view.DefaultAcceptHeaderToFormat;
import br.com.caelum.vraptor.view.DefaultHttpResult;
import br.com.caelum.vraptor.view.DefaultLogicResult;
import br.com.caelum.vraptor.view.DefaultPageResult;
import br.com.caelum.vraptor.view.DefaultPathResolver;
import br.com.caelum.vraptor.view.DefaultRefererResult;
import br.com.caelum.vraptor.view.DefaultStatus;
import br.com.caelum.vraptor.view.DefaultValidationViewsFactory;
import br.com.caelum.vraptor.view.EmptyResult;
import br.com.caelum.vraptor.view.FlashScope;
import br.com.caelum.vraptor.view.HttpResult;
import br.com.caelum.vraptor.view.LogicResult;
import br.com.caelum.vraptor.view.PageResult;
import br.com.caelum.vraptor.view.PathResolver;
import br.com.caelum.vraptor.view.RefererResult;
import br.com.caelum.vraptor.view.SessionFlashScope;
import br.com.caelum.vraptor.view.Status;
import br.com.caelum.vraptor.view.ValidationViewsFactory;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * List of base components to vraptor.<br/>
 * Those components should be available with any chosen ioc implementation.
 *
 * @author guilherme silveira
 */
public class BaseComponents {

    static final Logger logger = LoggerFactory.getLogger(BaseComponents.class);

    private final static Map<Class<?>, Class<?>> APPLICATION_COMPONENTS = classMap(
    		EncodingHandlerFactory.class, 	EncodingHandlerFactory.class,
    		AcceptHeaderToFormat.class, 	DefaultAcceptHeaderToFormat.class,
    		Converters.class, 				DefaultConverters.class,
            InterceptorRegistry.class, 		TopologicalSortedInterceptorRegistry.class,
            InterceptorHandlerFactory.class,DefaultInterceptorHandlerFactory.class,
            InterceptorListPriorToExecutionExtractor.class, InterceptorListPriorToExecutionExtractor.class,
            MultipartConfig.class, 			DefaultMultipartConfig.class,
            UrlToResourceTranslator.class, 	DefaultResourceTranslator.class,
            Router.class, 					DefaultRouter.class,
            TypeNameExtractor.class, 		DefaultTypeNameExtractor.class,
            ResourceNotFoundHandler.class, 	DefaultResourceNotFoundHandler.class,
            MethodNotAllowedHandler.class,	DefaultMethodNotAllowedHandler.class,
            RoutesConfiguration.class, 		NoRoutesConfiguration.class,
            Deserializers.class,			DefaultDeserializers.class,
            Proxifier.class, 				getProxifier(),
            InstanceCreator.class,          getInstanceCreator(),
            ParameterNameProvider.class, 	ParanamerNameProvider.class,
            TypeFinder.class, 				DefaultTypeFinder.class,
            RoutesParser.class, 			PathAnnotationRoutesParser.class,
            Routes.class,					DefaultRoutes.class,
            RestDefaults.class,				DefaultRestDefaults.class,
            Evaluator.class,				JavaEvaluator.class,
            StaticContentHandler.class,		DefaultStaticContentHandler.class,
            SingleValueConverter.class,     XStreamConverters.NullConverter.class,
            ProxyInitializer.class,			getProxyInitializerImpl()
    );

    private final static Map<Class<?>, Class<?>> CACHED_COMPONENTS = classMap(
    );

    private static final Map<Class<?>, Class<?>> PROTOTYPE_COMPONENTS = classMap(
    		InterceptorStack.class, 						DefaultInterceptorStack.class,
    		RequestExecution.class, 						EnhancedRequestExecution.class,
    		XStreamBuilder.class, 							XStreamBuilderImpl.class
    );

    private static final Map<Class<?>, Class<?>> REQUEST_COMPONENTS = classMap(
            MethodInfo.class, 								DefaultMethodInfo.class,
            LogicResult.class, 								DefaultLogicResult.class,
            PageResult.class, 								DefaultPageResult.class,
            HttpResult.class, 								DefaultHttpResult.class,
            RefererResult.class, 							DefaultRefererResult.class,
            PathResolver.class, 							DefaultPathResolver.class,
            ValidationViewsFactory.class,					DefaultValidationViewsFactory.class,
            Result.class, 									DefaultResult.class,
            Validator.class, 								DefaultValidator.class,
            Outjector.class, 								ReplicatorOutjector.class,
            DownloadInterceptor.class, 						DownloadInterceptor.class,
            EmptyResult.class, 								EmptyResult.class,
            ExecuteMethodInterceptor.class, 				ExecuteMethodInterceptor.class,
            ExceptionHandlerInterceptor.class,              ExceptionHandlerInterceptor.class,
            ExceptionMapper.class,                          DefaultExceptionMapper.class,
            FlashInterceptor.class, 						FlashInterceptor.class,
            ForwardToDefaultViewInterceptor.class, 			ForwardToDefaultViewInterceptor.class,
            InstantiateInterceptor.class, 					InstantiateInterceptor.class,
            DeserializingInterceptor.class, 				DeserializingInterceptor.class,
            JsonDeserializer.class,							JsonDeserializer.class,
            Localization.class, 							JstlLocalization.class,
            OutjectResult.class, 							OutjectResult.class,
            ParametersInstantiatorInterceptor.class, 		ParametersInstantiatorInterceptor.class,
            ResourceLookupInterceptor.class, 				ResourceLookupInterceptor.class,
            Status.class,									DefaultStatus.class,
            XMLDeserializer.class,			                XStreamXMLDeserializer.class,
            XMLSerialization.class,							XStreamXMLSerialization.class,
            JSONSerialization.class,						XStreamJSONSerialization.class,
            JSONPSerialization.class,						XStreamJSONPSerialization.class,
            HTMLSerialization.class,						HTMLSerialization.class,
            RepresentationResult.class,						DefaultRepresentationResult.class,
            FormatResolver.class,							DefaultFormatResolver.class,
            Configuration.class,							ApplicationConfiguration.class,
            RestHeadersHandler.class,						DefaultRestHeadersHandler.class,
            FlashScope.class,								SessionFlashScope.class,
            XStreamConverters.class,                        XStreamConverters.class,
            MessageConverter.class,							MessageConverter.class
    );

    @SuppressWarnings({"unchecked", "rawtypes"})
	private static final Set<Class<? extends Converter<?>>> BUNDLED_CONVERTERS = new HashSet(Arrays.asList(
    		BigDecimalConverter.class,
    		BigIntegerConverter.class,
    		BooleanConverter.class,
    		ByteConverter.class,
    		CharacterConverter.class,
    		DoubleConverter.class,
    		EnumConverter.class,
    		FloatConverter.class,
    		IntegerConverter.class,
    		LocaleBasedCalendarConverter.class,
    		LocaleBasedDateConverter.class,
    		LongConverter.class,
    		PrimitiveBooleanConverter.class,
    		PrimitiveByteConverter.class,
    		PrimitiveCharConverter.class,
    		PrimitiveDoubleConverter.class,
    		PrimitiveFloatConverter.class,
			PrimitiveIntConverter.class,
			PrimitiveLongConverter.class,
			PrimitiveShortConverter.class,
			ShortConverter.class,
			StringConverter.class,
			UploadedFileConverter.class));


    @SuppressWarnings("unchecked")
	private static final Class<? extends StereotypeHandler>[] STEREOTYPE_HANDLERS = new Class[] {
		ResourceHandler.class,
		ConverterHandler.class,
		InterceptorStereotypeHandler.class,
		DeserializesHandler.class
	};

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] STEREOTYPES = new Class[] {
    	Resource.class,
    	Convert.class,
    	Component.class,
    	Deserializes.class,
    	Intercepts.class
    };

    private static final Set<Class<? extends Deserializer>> DESERIALIZERS = Collections.<Class<? extends Deserializer>>singleton(XMLDeserializer.class);


    public static Set<Class<? extends Deserializer>> getDeserializers() {
		return DESERIALIZERS;
	}

    private static Class<? extends ProxyInitializer> getProxyInitializerImpl() {
		try {
			Class.forName("org.hibernate.proxy.HibernateProxy");
			return HibernateProxyInitializer.class;
		} catch (ClassNotFoundException e) {
			return NullProxyInitializer.class;
		}
	}

    private static Class<? extends InstanceCreator> getInstanceCreator() {
        if (isClassPresent("org.objenesis.ObjenesisStd")) {
            return ObjenesisInstanceCreator.class;
        }

        return ReflectionInstanceCreator.class;
    }

    private static Class<? extends Proxifier> getProxifier() {
        if (isClassPresent("net.sf.cglib.proxy.Factory")) {
            return CglibProxifier.class;
        }

        return JavassistProxifier.class;
    }

	public static Map<Class<?>, Class<?>> getCachedComponents() {
		return Collections.unmodifiableMap(CACHED_COMPONENTS);
	}

    public static Map<Class<?>, Class<?>> getApplicationScoped() {
        if (!isClassPresent("ognl.OgnlRuntime")) {
            APPLICATION_COMPONENTS.put(DependencyProvider.class, VRaptorDependencyProvider.class);
        }
    	
        registerIfClassPresent(APPLICATION_COMPONENTS, "javax.validation.Validation",
                ValidatorCreator.class, ValidatorFactoryCreator.class, MessageInterpolatorFactory.class);
        
        registerIfClassPresent(APPLICATION_COMPONENTS, "org.hibernate.validator.method.MethodValidator",
                MethodValidatorCreator.class);
        
    	return Collections.unmodifiableMap(APPLICATION_COMPONENTS);
    }

    public static Map<Class<?>, Class<?>> getRequestScoped() {
    	if(isClassPresent("javax.validation.Validation")) {
    		REQUEST_COMPONENTS.put(BeanValidator.class, DefaultBeanValidator.class);
    	} else {
            REQUEST_COMPONENTS.put(BeanValidator.class, NullBeanValidator.class);
    	}

        registerIfClassPresent(REQUEST_COMPONENTS, "org.hibernate.validator.method.MethodValidator",
                MethodValidatorInterceptor.class);

        if (isClassPresent("org.apache.commons.fileupload.FileItem")) {
            REQUEST_COMPONENTS.put(MultipartInterceptor.class, CommonsUploadMultipartInterceptor.class);
            REQUEST_COMPONENTS.put(ServletFileUploadCreator.class, DefaultServletFileUploadCreator.class);
        } else if (isClassPresent("javax.servlet.http.Part")) {
            REQUEST_COMPONENTS.put(MultipartInterceptor.class, Servlet3MultipartInterceptor.class);
        } else {
    	    logger.warn("There is neither commons-fileupload nor servlet3 handlers registered. " +
    	    		"If you are willing to upload a file, please add the commons-fileupload in " +
    	    		"your classpath or use a Servlet 3 Container");
            REQUEST_COMPONENTS.put(MultipartInterceptor.class, NullMultipartInterceptor.class);
    	}
        
        if (isClassPresent("ognl.OgnlRuntime")) {
            REQUEST_COMPONENTS.put(ParametersProvider.class, OgnlParametersProvider.class);
            REQUEST_COMPONENTS.put(EmptyElementsRemoval.class, EmptyElementsRemoval.class);
            REQUEST_COMPONENTS.put(OgnlFacade.class, OgnlFacade.class);
        } else {
            REQUEST_COMPONENTS.put(ParametersProvider.class, IogiParametersProvider.class);
            REQUEST_COMPONENTS.put(ParameterNamesProvider.class, VRaptorParameterNamesProvider.class);
            REQUEST_COMPONENTS.put(InstantiatorWithErrors.class, VRaptorInstantiator.class);
            REQUEST_COMPONENTS.put(Instantiator.class, VRaptorInstantiator.class);
        }

        return Collections.unmodifiableMap(REQUEST_COMPONENTS);
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

	private static boolean registerIfClassPresent(Map<Class<?>, Class<?>> components, String className, Class<?>... types) {
		try {
			Class.forName(className);
			for (Class<?> type : types) {
				components.put(type, type);
			}
			return true;
		} catch (ClassNotFoundException e) {
			/* ok, don't register */
			return false;
		}
	}

	private static void registerIfClassPresent(Set<Class<? extends Converter<?>>> components, String className, Class<? extends Converter<?>>... types) {
		if (components.contains(types[0])) {
			return;
		}
		try {
    		Class.forName(className);
    		for (Class<? extends Converter<?>> type : types) {
    			components.add(type);
			}
    	} catch (ClassNotFoundException e) { /*ok, don't register*/ }
	}

    public static Map<Class<?>, Class<?>> getPrototypeScoped() {
		return Collections.unmodifiableMap(PROTOTYPE_COMPONENTS);
	}

    @SuppressWarnings("unchecked")
	public static Set<Class<? extends Converter<?>>> getBundledConverters() {
    	registerIfClassPresent(BUNDLED_CONVERTERS, "org.joda.time.LocalDate",
    			LocalDateConverter.class, LocalTimeConverter.class, LocalDateTimeConverter.class);
        return BUNDLED_CONVERTERS;
    }

    public static Class<? extends Annotation>[] getStereotypes() {
    	return STEREOTYPES;
    }

    public static Class<? extends StereotypeHandler>[] getStereotypeHandlers() {
    	return STEREOTYPE_HANDLERS;
    }

    private static Map<Class<?>, Class<?>> classMap(Class<?>... items) {
        HashMap<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
        Iterator<Class<?>> it = Arrays.asList(items).iterator();
        while (it.hasNext()) {
            Class<?> key = it.next();
            Class<?> value = it.next();
            if (value == null) {
                throw new IllegalArgumentException("The number of items should be even.");
            }
            map.put(key, value);
        }
        return map;
    }


}
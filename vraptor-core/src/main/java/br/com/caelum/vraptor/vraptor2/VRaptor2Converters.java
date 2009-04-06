package br.com.caelum.vraptor.vraptor2;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.DefaultConverters;
import br.com.caelum.vraptor.ioc.Container;

public class VRaptor2Converters implements Converters {

    private final DefaultConverters vraptor3;
    private final List<org.vraptor.converter.Converter> converterList = new ArrayList<org.vraptor.converter.Converter>();

    public VRaptor2Converters(Config config) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        List<String> list = config.getConverters();
        for (String l : list) {
            Class<? extends org.vraptor.converter.Converter> converterType = (Class<? extends org.vraptor.converter.Converter>) Class
                    .forName(l);
            converterList.add(converterType.newInstance());
        }
        this.vraptor3 = new DefaultConverters();
    }

    public Converter<?> to(Class<?> type, Container container) {
        for (org.vraptor.converter.Converter converter : converterList) {
            for(Class supported: converter.getSupportedTypes()) {
                if(supported.isAssignableFrom(type)) {
                    return new ConverterWrapper(converter);
                }
            }
        }
        return null;
    }

}

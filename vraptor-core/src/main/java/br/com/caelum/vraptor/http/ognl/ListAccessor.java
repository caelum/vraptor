package br.com.caelum.vraptor.http.ognl;

import java.util.List;
import java.util.Map;

import ognl.ListPropertyAccessor;
import ognl.OgnlException;

/**
 * This list accessor is responsible for setting null values up to the list
 * size.<br>
 * Compatibility issues might arrive (in previous vraptor versions, the object
 * was instantiated instead of null being set).
 * 
 * @author Guilherme Silveira
 * 
 */
public class ListAccessor extends ListPropertyAccessor {

    @SuppressWarnings("unchecked")
    public Object getProperty(Map map, Object target, Object object) throws OgnlException {
        try {
            return super.getProperty(map, target, object);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public void setProperty(Map map, Object target, Object key, Object parent) throws OgnlException {
        List<?> list = (List<?>) target;
        int index = (Integer) key;
        for (int i = list.size(); i <= index; i++) {
            list.add(null);
        }
        super.setProperty(map, target, key, parent);
    }

}

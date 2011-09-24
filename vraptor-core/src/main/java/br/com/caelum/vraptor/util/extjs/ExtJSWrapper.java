package br.com.caelum.vraptor.util.extjs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class ExtJSWrapper {

    private Object data;
    private List<Object> list;
    private Boolean success;
    private Integer total;
    private Object selected;

    public ExtJSWrapper(Object object) {
        if (object instanceof Collection) {
            this.list = new ArrayList<Object>((Collection<?>) object);
        } else {
            this.data = object;
        }
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Object getSelected() {
        return selected;
    }

    public void setSelected(Object selected) {
        this.selected = selected;
    }
}

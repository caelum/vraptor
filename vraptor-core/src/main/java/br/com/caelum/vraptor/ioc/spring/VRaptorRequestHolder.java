package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.core.RequestInfo;

/**
 * @author Fabio Kung
 */
public class VRaptorRequestHolder {
    private static final ThreadLocal<RequestInfo> vraptorRequests = new ThreadLocal<RequestInfo>();

    public static RequestInfo currentRequest() {
        return vraptorRequests.get();
    }

    public static void setRequestForCurrentThread(RequestInfo request) {
        vraptorRequests.set(request);
    }

    public static void resetRequestForCurrentThread() {
        vraptorRequests.set(null);
    }
}

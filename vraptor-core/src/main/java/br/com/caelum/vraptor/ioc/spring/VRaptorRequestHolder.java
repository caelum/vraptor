package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.core.VRaptorRequest;

/**
 * @author Fabio Kung
 */
public class VRaptorRequestHolder {
    private static final ThreadLocal<VRaptorRequest> vraptorRequests = new ThreadLocal();

    public static VRaptorRequest currentRequest() {
        return vraptorRequests.get();
    }

    public static void setRequestForCurrentThread(VRaptorRequest request) {
        vraptorRequests.set(request);
    }

    public static void resetRequestForCurrentThread() {
        vraptorRequests.set(null);
    }
}

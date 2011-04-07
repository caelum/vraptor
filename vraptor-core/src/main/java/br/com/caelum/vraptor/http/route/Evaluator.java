package br.com.caelum.vraptor.http.route;

/**
 * Created by IntelliJ IDEA.
 * User: lucascs
 * Date: 4/7/11
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Evaluator {
    Object get(Object root, String path);
}

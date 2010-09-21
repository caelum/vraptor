package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.Result;

/**
 * Store the exception mapping for exception handling.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.2
 */
public interface ExceptionMapper {
    
    Result record(Class<? extends Exception> exception);

    ExceptionRecorder<Result> findByException(Exception exception);

}
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

package br.com.caelum.vraptor.http;

import br.com.caelum.vraptor.VRaptorException;

/**
 * Some parameters sent by http were invalid.
 *
 * @author Guilherme Silveira
 */
public class InvalidParameterException extends VRaptorException {
    private static final long serialVersionUID = 4632893122633090126L;

    public InvalidParameterException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public InvalidParameterException(Throwable e) {
        super(e);
    }

    public InvalidParameterException(String msg) {
        super(msg);
    }
}

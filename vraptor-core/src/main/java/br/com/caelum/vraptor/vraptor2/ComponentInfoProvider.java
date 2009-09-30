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
package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.outject.Outjecter;

/**
 * Provides information related to vraptor 2 for the request.
 * 
 * @author Guilherme Silveira
 */
public interface ComponentInfoProvider {

    /**
     * Whether a show view should be shown for this request.
     */
    boolean shouldShowView(ResourceMethod method);

    /**
     * Whether this is an ajax request.
     */
    boolean isAjax();

    /**
     * Returns the specific outjecter for this ajax or non-ajax request.<br>
     * Ajax based requests uses the JsonOutjecter while non ajax based uses the
     * DefaulOutjecter.
     */
    Outjecter getOutjecter();

}

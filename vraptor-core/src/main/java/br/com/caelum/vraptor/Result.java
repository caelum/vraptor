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

package br.com.caelum.vraptor;

import java.util.Map;

import br.com.caelum.vraptor.view.LogicResult;
import br.com.caelum.vraptor.view.PageResult;

/**
 * A resource requisition result.
 *
 * @author Guilherme Silveira
 */
public interface Result {

    Result include(String key, Object value);

	<T extends View> T use(Class<T> view);

	/**
	 * Whether this result was used.
	 */
    boolean used();

    /**
     * All included attributes via Result.include();
     * @return
     */
    Map<String, Object> included();

    /**
     * A shortcut to result.use(page()).forward(uri);
     * @see PageResult#forward(String)
     */
	void forwardTo(String uri);

	/**
	 * A shortcut to result.use(logic()).forwardTo(controller)
	 * @see LogicResult#forwardTo(Class)
	 * @param controller
	 */
	<T> T forwardTo(Class<T> controller);

	/**
	 * A shortcut to result.use(logic()).redirectTo(controller)
	 * @see LogicResult#redirectTo(Class)
	 * @param controller
	 */
	<T> T redirectTo(Class<T> controller);

	/**
	 * A shortcut to result.use(page()).of(controller)
	 * @see PageResult#of(Class)
	 * @param controller
	 */
	<T> T of(Class<T> controller);

	/**
	 * A shortcut to result.use(logic()).redirectTo(controller.getClass())
	 * so you can use on your controller:<br>
	 *
	 * result.redirectTo(this).aMethod();
	 *
	 * @param controller
	 * @see LogicResult#redirectTo(Class)
	 */
	<T> T redirectTo(T controller);

	/**
	 * A shortcut to result.use(logic()).forwardTo(controller.getClass())
	 * so you can use on your controller:<br>
	 *
	 * result.forwardTo(this).aMethod();
	 *
	 * @param controller
	 * @see LogicResult#forwardTo(Class)
	 */
	<T> T forwardTo(T controller);

	/**
	 * A shortcut to result.use(page()).of(controller.getClass())
	 * so you can use on your controller:<br>
	 *
	 * result.of(this).aMethod();
	 *
	 * @param controller
	 * @see PageResult#of(Class)
	 */
	<T> T of(T controller);


}

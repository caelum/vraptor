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
package br.com.caelum.vraptor.interceptor;

import java.util.List;

public interface DogAlike {
	abstract void bark();
	abstract void bark(int times);
	abstract void bark(String phrase);
	abstract void eat(List<String> portions);
	abstract void dropDead(int[] positions);
	abstract void recurse(DogAlike[] dogs);
	abstract void jump(long times);
	abstract void dig(double amount);
	abstract void dig(float amount);
}

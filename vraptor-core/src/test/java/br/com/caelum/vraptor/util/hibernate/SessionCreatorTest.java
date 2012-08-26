/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.util.hibernate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SessionCreatorTest {
    
    private @Mock SessionFactory factory;
    private @Mock Session session;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void shouldReturnCreatedInstance() {
        when(factory.openSession()).thenReturn(session);
        
        SessionCreator creator = new SessionCreator(factory);
        creator.create();
        
        assertEquals(session, creator.getInstance());
    }
    
    @Test
    public void shouldCloseSessionOnDestroy() {
        when(factory.openSession()).thenReturn(session);
        
        SessionCreator creator = new SessionCreator(factory);
        creator.create();
        creator.destroy();
        
        verify(session).close();
    }
}

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

package br.com.caelum.vraptor.interceptor.download;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Implements {@link Download} from a byte array.
 *
 * @author filipesabella
 * @author Paulo Silveira
 * @author Otávio Scherer Garcia
 * @since 3.3.0
 * @see InputStreamDownload
 * @see FileDownload
 */
public class ByteArrayDownload implements Download {
    
	private final InputStreamDownload download;

    public ByteArrayDownload(byte[] buff, String contentType, String fileName) {
        this(buff, contentType, fileName, false);
    }

    public ByteArrayDownload(byte[] buff, String contentType, String fileName, boolean doDownload) {
        ByteArrayInputStream stream = new ByteArrayInputStream(buff);
        download = new InputStreamDownload(stream, contentType, fileName, doDownload, buff.length);
    }
    
	public void write(HttpServletResponse response) throws IOException {
		download.write(response);
	}
}

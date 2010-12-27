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

package br.com.caelum.vraptor.interceptor.multipart;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Default implementation for {@link MultipartConfig}.
 * 
 * TODO: should expose not a directory, but a way to define memory or file usage (commons upload has already a common
 * interface to it).
 *
 * @author Paulo Silveira
 */
@ApplicationScoped
public class DefaultMultipartConfig implements MultipartConfig {

    private final Logger logger = LoggerFactory.getLogger(DefaultMultipartConfig.class);

    public long getSizeLimit() {
        return 2 * 1024 * 1024;
    }

    public File getDirectory() {
        try {
            File tempFile = File.createTempFile("raptor.", ".upload");
            tempFile.delete();
            return tempFile.getParentFile();
        } catch (IOException e) {
            logger.warn("Unable to find temp directory, creating a dir inside the application", e);
            File tmp = new File(".tmp-multipart-upload");
            tmp.mkdirs();
            return tmp;
        }
    }

}

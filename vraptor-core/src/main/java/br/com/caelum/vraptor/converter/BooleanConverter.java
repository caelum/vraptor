/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.converter;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's Boolean converter.
 *
 * Supports boolean-like values.
 * Converts to TRUE the following strings : true, 1, yes, y, on
 * Converts to FALSE the following strings: false, 0, no, n, off
 *
 * @author Guilherme Silveira
 */
@Convert(Boolean.class)
@ApplicationScoped
public class BooleanConverter implements Converter<Boolean> {
    private static final Pattern IS_TRUE  = Pattern.compile("true|1|yes|y|on" , Pattern.CASE_INSENSITIVE);
    private static final Pattern IS_FALSE = Pattern.compile("false|0|no|n|off", Pattern.CASE_INSENSITIVE);

    public Boolean convert(String value, Class type, ResourceBundle bundle) {
	if (value == null) {
	    return null;
	}
	if (matches(IS_TRUE, value)) {
	    return true;
	} else if (matches(IS_FALSE, value)) {
	    return false;
	}
	throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_boolean"), value));
    }

    private boolean matches(Pattern p, String value) {
	return p.matcher(value).matches();
    }

}

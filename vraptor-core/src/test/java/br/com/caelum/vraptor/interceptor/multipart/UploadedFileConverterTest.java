package br.com.caelum.vraptor.interceptor.multipart;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.converter.ConversionError;

public class UploadedFileConverterTest {
	
	private @Mock HttpServletRequest request;
	private @Mock UploadedFile file;
	
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

	@Test
	public void testIfUploadedFileIsConverted() {
		when(request.getAttribute("myfile")).thenReturn(file);
		
		UploadedFileConverter converter = new UploadedFileConverter(request);
		
		UploadedFile uploadedFile = converter.convert("myfile", UploadedFile.class, null);
		assertEquals(file, uploadedFile);
	}

	@Test(expected = ConversionError.class)
	public void shouldThrowExceptionIfFileIsNull() {
		when(request.getAttribute("myfile")).thenReturn(null);
		
		UploadedFileConverter converter = new UploadedFileConverter(request);
		
		converter.convert("myfile", UploadedFile.class, null);
	}
}

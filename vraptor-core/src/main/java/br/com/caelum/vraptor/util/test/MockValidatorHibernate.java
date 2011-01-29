package br.com.caelum.vraptor.util.test;
 import java.util.Set;  
   
import javax.validation.ConstraintViolation;  
import javax.validation.Validator;  
import org.apache.log4j.Logger;  
import br.com.caelum.vraptor.validator.ValidationMessage;  
import br.com.caelum.vraptor.validator.ValidationException;  
import javax.validation.Validation;


/**
 * Mocked Validator for testing your controllers.
 *
 * You can use the idiom:
 * MockValidator validator = new MockValidator();
 * MyController controller = new MyController(..., validator);
 *
 * try {
 * 		controller.method();
 * 		Assert.fail();
 * } catch (ValidationError e) {
 * 		List<Message> errors = e.getErrors();
 * 		// asserts
 * }
 *
 * or
 *
 * @Test(expected=ValidationError.class)
 *
 * @author Lucas Cavalcanti, Wagner Ferreira e leandros 
 * link: http://www.guj.com.br/java/228861-problema-com-hibernate-validator-e-vraptor-320
 */

public class MockValidatorHibernate extends MockValidator{

	   private Validator validator;
	   private static final Logger log = Logger.getLogger(MockValidatorHibernate.class);

	   public MockValidatorHibernate() {
	      validator = Validation.buildDefaultValidatorFactory().getValidator();
	   }


	   @Override
	     public void validate(Object bean) {

	      log.debug("------------------------------------------ inicio validate ");
	      final Set<ConstraintViolation<Object>> violations = validator.validate(bean);
	      
	      for (ConstraintViolation<Object> constraintViolation : violations) {
	         
	         add(new ValidationMessage(constraintViolation.getMessage(), constraintViolation.getMessageTemplate().replaceAll("[{}]", "")));
	         
	      }

	      if (!getErrors().isEmpty()) {
	         log.debug("---------" + getErrors().size() + " mensagen(s) de erros foi(ram) identificada(s) - ATENÇÃO!");
	         throw new ValidationException(getErrors());
	      }

	   }

}

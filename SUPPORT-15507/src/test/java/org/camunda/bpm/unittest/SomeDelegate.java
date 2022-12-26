package org.camunda.bpm.unittest;

import java.util.logging.Logger;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

public class SomeDelegate implements JavaDelegate{
	
	  private final Logger LOGGER = Logger.getLogger(SomeDelegate.class.getName());
	  
	  @Override
	  public void execute(DelegateExecution execution) throws Exception {
		
		String foo = (String)execution.getVariable("foo");
		
		// variable foo is not accesible here
		LOGGER.info("Value of foo: " + foo);
		
	}

}

/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.unittest;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;


public class SimpleTestCase {

	private final Logger LOGGER = Logger.getLogger(SimpleTestCase.class.getName());

	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();

	@Test
	@Deployment(resources = {"sample.bpmn"})
	public void shouldExecuteProcess() {

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("foo", "foovalue");

		ProcessInstantiationBuilder processInstantiationBuilder =
				rule.getRuntimeService().createProcessInstanceByKey("sample")
				.setVariables(vars);

		// start before New_Task
		processInstantiationBuilder.startBeforeActivity("New_Task");
		ProcessInstance pid = processInstantiationBuilder.execute();

		rule.getRuntimeService().createMessageCorrelation("message1").correlate();

		TaskService taskService = rule.getTaskService();
		Task task = taskService.createTaskQuery().singleResult();
		assertEquals("New_SubTask", task.getName());

		// completes the New_SubTask
		taskService.complete(task.getId());

		// variable "foo" exists, however in different scope
		String value = rule.getRuntimeService()
				.createVariableInstanceQuery()
				.variableName("foo").singleResult().getValue().toString();
		LOGGER.info("Variable foo exists: " + value);
	}

}

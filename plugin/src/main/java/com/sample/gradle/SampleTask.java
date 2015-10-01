package com.sample.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class SampleTask extends DefaultTask {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@TaskAction
	public void samplePluginTasks() throws TaskExecutionException {
		log.info("Starting  sample task");
		try {
			SamplePluginExtension extension = getProject().getExtensions()
					.findByType(SamplePluginExtension.class);
			String filePath = extension.getSampleFilePath();
			log.debug("Sample file path is: {}",filePath);
			/* Here comes 
			 * 
			 * your main logic
			 * 
			 */
			log.info("Successfully completed sample Task");
		}catch(Exception e){
			log.error("",e);
			throw new TaskExecutionException(this,new Exception("Exception occured while processing sampleTask",e));
		}
	}
}

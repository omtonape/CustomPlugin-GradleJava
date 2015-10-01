				Writing Custom Gradle Plugin using Java	

	Gradle is build automation tool based on apache ant and maven.Gradle avoids traditional .xml file based configuration by introducing groovy based domain specific language.In gradle project have .gradle files instead of .pom files.Gradle was desinged for multi-project builds and supports incremental build.
	Gradle plugin groups together reusable pieces of build logic which can be then used across many different projects and builds.We can use any language whose compiled code gets converted to bytecode for  developing custom gradle plugin.As gradle is mainly designed using groovy language its very easy to develop gradle plugin using groovy but  lets see how to develop custom gradle plugin using Java language:

Steps :

1. Create new Java project using eclipse or any other IDE.
2. Create plugin folder in your project which will have source code of your custom gradle plugin.
3. create your package structure in this plugin folder.
    Eg: com.sample.gradle
    This package structure will have your custom plugins source code.
4. Create Plugin class which will implement Plugin<Project>  interface.Plugin is represents extension to gradle.This interface is having apply method which applies  configuration to gradle Project object.

Eg :

package com.sample.gradle;

import org.gradle.api.Project;
import org.gradle.api.Plugin;
import com.sample.gradle.SamplePluginExtension;

public class SampleGradlePlugin implements Plugin<Project> {
	

	@Override
    public void apply(Project target) {
		target.getExtensions().create("samplePlugin",
				SamplePluginExtension.class);
    }
}

5. All the user defined values to custom plugin are provided through extension object so creaete extension class and register it with plugin as shown above to receive inputs from user.If user doesnot provided input then default values will be assumed.

6. Create extension class which is similar to java pojo class it will contain user defined properties and their getter setter methods.If user provides values for these properties during run time then these values will be accepted otherwise default values will be considered.

Eg :  public class SamplePluginExtension {
	
	private String sampleFilePath="/home/mahendra/abc”;
	
	public void setSampleFilePath(String sampleFilePath){
		this.sampleFilePath=sampleFilePath;
	}
	public String getSampleFilePath(){
		return sampleFilePath;
	}

}

7. Create task class which will have your plugin logic this task class contains your main plugin logic.This task class extends org.gradle.api.DefaultTask class and defines method with @TaskAction annotation.This method will have actual logic.
Eg:
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

For logging into your custom plugin use slf4j or any other logging framework of your choice.If you want to fail the build on exception in your task then throw TaskExecutionException which will cause BuildFailures other exceptions will not cause build failure.TaskExecutionException accepts task object and Throwable object as input.

Here DefaultTask is standard gradle task implementation class and we need to extend it while implementing custom tasks.@TaskAction annotation makes method action method and whenever task executes this method will be executed.

8. Registering plugin class : create resources folder in plugin/src/main folder.Inside resources create META-INF/gradle-plugin folder, in this gradle-plugin folder create properties file.Name of this property file is used for registering plugin in build.gradle build file.

eg: sample-plugin.properties:
In sample-plugin.properties file add following line:
implementation-class=com.sample.gradle.SampleGradlePlugin

value of implementation-class if path of plugin class.

9. Create settings.gradle file in your plugin folder which will have below line:

rootProject.name = 'customGradlePlugin'

value of rootProject.name is name of your root project.

10. create build.gradle build file for you plugin in plugin folder as follows:
apply plugin: 'java'
dependencies {
    compile gradleApi()
}
apply plugin: 'maven-publish'
repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
}

dependencies {
    compile 'org.slf4j:slf4j-simple:1.6.1'
    testCompile 'junit:junit:4.11'
}
group = 'com.sample.gradle'
version = '1.0.0'
publishing {
	publications {
		maven(MavenPublication) {
			groupId "$group"
			artifactId 'CustomGradlePlugin'
			version "$version"
			from components.java
		}
	}
}
uploadArchives {
    repositories {
            mavenLocal()
    }
}
as we are developing gradle plugin using java add apply plugin:'java' line.whatever external dependencies your plugin is dependent upon add these dependencies in dependencies section.the repositories in which your plugin should look for should be mentioned in repositories section.mentioned group id,version of plugin in group and version tag. 
For making plugin available to other projects gradle plugin should be published to repository or its archives should be uploaded for this purpose either use publishing or uploadArchives functionality.


for publishing plugin use following command if you are publishing plugin to local maven repository:

“gradle clean build publishToMavenLocal” 

If you are uploading plugin to local maven repository then use below command:

“gradle clean build uploadToArchives”

11. For using plugin in another projects make following changes in build.gradle file of your project.


apply plugin: 'java'

buildscript {
repositories {
         mavenLocal()
         mavenCentral()
    }
    dependencies {
   		classpath "com.sample.gradle:CustomGradlePlugin:1.0.0"
    }
}
apply plugin: 'sample-plugin'
 task sampleTask(type: com.sample.gradle.SampleTask) {
  samplePlugin.sampleFilePath = "$sampleFilePath"
} 



here plugin dependency must be defined in buildscript section and  to tell gradle which repositories to scan for getting plugin dependencies  add repositories section in buildscript section this repository section must come ahead of dependencies section.Afer this add apply plugin line.

Sample task provided will be used for executing our plugin logic in task value of type is path of our custom task.whatever custom arguments we want to provide to plugin that we need to define in task section .if we want to run with default parameters then comment samplePlugin.sampleFilePath line in task section.

To run plugin with custom parameter use below command:

gradle clean build sampleTask -PsampleFilePath='/home/mahendra/abc.sample'

To run plugin without custom parameter use below command:

gradle clean build sampleTask.






Sample plugin project structure is as follows:
















 

















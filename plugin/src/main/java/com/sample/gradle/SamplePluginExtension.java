package com.sample.gradle;

public class SamplePluginExtension {
	
	private String sampleFilePath="/home/mahendra/abc";
	
	public void setSampleFilePath(String sampleFilePath){
		this.sampleFilePath=sampleFilePath;
	}
	public String getSampleFilePath(){
		return sampleFilePath;
	}

}

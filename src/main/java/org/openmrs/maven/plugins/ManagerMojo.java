/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.maven.plugins;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Starts and stops an instance of OpenMRS
 */
@Mojo(name = "run")
public class ManagerMojo extends AbstractMojo {
	
	private Log log = getLog();
	
	@Parameter(property = "pathToStandalone")
	private String pathToStandalone;
	
	private File standaloneDirectory;
	
	private final static String SUCCESS_START_MESSAGE = "Exiting method deleteOldReportRequests";
	
	public void execute() throws MojoExecutionException {
		try {
			if (pathToStandalone != null && pathToStandalone.length() > 0) {
				standaloneDirectory = new File(pathToStandalone);
			} else {
				standaloneDirectory = new File("standalone");
			}
			start();
		}
		catch (Exception e) {
			throw new MojoExecutionException("ERROR:", e);
		}
	}
	
	private void start() throws Exception {
		log.info("Starting Standalone...");
		
		BufferedReader normalStream = null;
		BufferedReader errorStream = null;
		try {
			Process process = Runtime.getRuntime().exec(
			    new String[] { "java", "-jar", "openmrs-standalone.jar", "-commandline", "-test" }, null,
			    standaloneDirectory);
			
			String output;
			normalStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((output = normalStream.readLine()) != null) {
				log.info(output);
				if (output.indexOf(SUCCESS_START_MESSAGE) > -1) {
					log.info("Successfully started the standalone");
					stop();
					break;
				}
				//Check for lines of the form below to check if required modules are started
				// In method AdministrationService.saveGlobalProperty. Arguments: GlobalProperty=property: moduleId.started value: true
				
			}
			
			String error;
			errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while ((error = errorStream.readLine()) != null) {
				log.error(error);
				//TODO we need to break out of this and throw an exception
			}
		}
		catch (Exception e) {
			throw new Exception("An error occurred while starting the standalone:" + e.getMessage());
		}
		finally {
			close(normalStream, errorStream);
		}
	}
	
	private void stop() throws Exception {
		log.info("Shutting down standalone...");
		File pidFile = new File(standaloneDirectory, ".standalone.pid");
		BufferedReader normalStream = null;
		BufferedReader errorStream = null;
		Scanner fileScanner = null;
		try {
			fileScanner = new Scanner(pidFile);
			if (fileScanner.hasNext()) {
				String processId = fileScanner.next().trim();
				fileScanner.close();
				if (fileScanner.ioException() != null) {
					throw fileScanner.ioException();
				}
				
				log.info("Found the standalone process id:" + processId);
				
				Process process = Runtime.getRuntime().exec("kill -9 " + processId);
				
				String output;
				normalStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
				while ((output = normalStream.readLine()) != null) {
					log.info(output);
				}
				
				String error;
				errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				while ((error = errorStream.readLine()) != null) {
					log.error(error);
					//TODO we need to break out of this and throw an exception
				}
				
				pidFile.deleteOnExit();
				log.info("Successfully shutdown the standalone...");
				
			} else {
				throw new Exception("Failed to acquire process id of standalone, please make sure it is running");
			}
		}
		catch (Exception e) {
			throw new Exception("An error occurred while shutting down the standalone:" + e.getMessage());
		}
		finally {
			close(normalStream, errorStream);
		}
	}
	
	private void close(Closeable... closeables) throws Exception {
		try {
			for (Closeable c : closeables) {
				if (c != null) {
					c.close();
				}
			}
		}
		catch (IOException e) {
			throw new Exception("An error occurred while closing input streams:" + e.getMessage());
		}
	}
}

maven-manager-plugin
====================

The plugin can be used to run and stop an instance of the OpenMRS standalone

###Building the plugin
 * Check out the project onto your machine 
 * Build the plugin by running the command below: 
 
  ``` 
  mvn clean install
  ```


###Running the plugin
* On the commandline, navigate to the root of your maven project
* Run this command below: 
  
  ```
  mvn manager:run -DpathToStandalone=/myPathToTheStandalone
  ```

If maven complains that it can't find the plugin, make sure that you have **org.openmrs.maven.plugins** added to your pluginGroups in the settings.xml file, If you have no pluginGroups added, you can add the text below to your settings.xml file

```
<pluginGroups>
  <pluginGroup>org.openmrs.maven.plugins</pluginGroup>
</pluginGroups>
```

Alternatively, you can copy the standalone folder to root of the project from which you are running the plugin, it MUST be named **standalone** for the plugin to find it. 

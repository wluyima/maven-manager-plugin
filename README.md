maven-manager-plugin
====================

This plugin can be used to run and stop an instance of the OpenMRS standalone

###Building the plugin
 * Check out the project onto your machine 
 * Build the plugin by running the command below: 
 
  ``` 
  mvn clean install
  ```


###Running the plugin
* Build a new version of the standalone from the code in the [managerplugin](https://github.com/openmrs/openmrs-standalone/tree/managerplugin) branch
* Extract the generated zip file of the standalone to some location, this is the location(path) you will pass in when running the plugin
* Run the command below from the root of your maven project 
  
  ```
  mvn manager:run -DpathToStandalone=/myPathToTheStandalone
  ```

If maven complains that it can't find the plugin, make sure that you have an entry for **org.openmrs.maven.plugins** under  pluginGroups in your maven settings.xml file, If you have no pluginGroups added, you can add it by including the text below to your settings.xml file

```
<pluginGroups>
  <pluginGroup>org.openmrs.maven.plugins</pluginGroup>
</pluginGroups>
```

The **pathToStandalone** argument is optional if the standalone folder is located in the same directory as that from which you are running the plugin, but in this case it MUST be named **standalone** for the plugin to auto find it.

#!/usr/bin/env groovy

 def getNexusArtifact( app, version, outvar){
   sh "mvn org.apache.maven.plugins:maven-dependency-plugin:2.4:get -DrepoUrl="+ env.NEXUS_SERVER +" -Dartifact="+ app + ":"+ version+ " -Ddest=" + outvar
}

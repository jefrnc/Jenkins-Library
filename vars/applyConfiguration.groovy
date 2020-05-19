#!/usr/bin/env groovy

class ApplyConfigurationParameters {
    String clusterUrl = ""
    String clusterToken = ""
    String project = ""
    String application
    String config
    String deploymentPatch
}

def call(applyConfigurationParameters) {
    call(new ApplyConfigurationParameters(applyConfigurationParameters))
}

def call(ApplyConfigurationParameters applyConfigurationParameters) {
    openshift.withCluster(applyConfigurationParameters.clusterUrl, applyConfigurationParameters.clusterToken) {
        openshift.withProject(applyConfigurationParameters.project) {
            openshift.replace(processConfiguration(config: applyConfigurationParameters.config, application: applyConfigurationParameters.application))
            
            // Until OpenShift 3.11, this workaround is necessary: https://github.com/openshift/origin/pull/20456
            try {
                openshift.patch("dc/${applyConfigurationParameters.application}", "'${readFile(applyConfigurationParameters.deploymentPatch)}'")                            
            } catch (Exception e) { 
                ;
            }
        } 
    }
}
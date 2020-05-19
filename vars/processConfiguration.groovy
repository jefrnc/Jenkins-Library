#!/usr/bin/env groovy

class ProcessConfigurationParameters {
    String clusterUrl = ""
    String clusterToken = ""
    String project = ""
    String config
    String application
}

def call(processConfigurationParameters) {
    call(new ProcessConfigurationParameters(processConfigurationParameters))
}

def call(ProcessConfigurationParameters processConfigurationParameters) {
    openshift.withCluster(processConfigurationParameters.clusterUrl, processConfigurationParameters.clusterToken) {
        openshift.withProject(processConfigurationParameters.project) {
            return openshift.process(readFile(file: processConfigurationParameters.config), "-p", "PARAM_APP_NAME=${processConfigurationParameters.application}")
        }
    }
}
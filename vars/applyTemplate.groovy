#!/usr/bin/env groovy

class ApplyTemplateParameters {
    String clusterUrl = ""
    String clusterToken = ""
    String project = ""
    String template
    String application
    String image
    String createBuildObjects
}

def call(applyTemplateParameters) {
    call(new ApplyTemplateParameters(applyTemplateParameters))
}

def call(ApplyTemplateParameters applyTemplateParameters) {
    openshift.withCluster(applyTemplateParameters.clusterUrl, applyTemplateParameters.clusterToken) {
        openshift.withProject(applyTemplateParameters.project) {
            openshift.apply(processTemplate(template: applyTemplateParameters.template, application: applyTemplateParameters.application, image: applyTemplateParameters.image, createBuildObjects: applyTemplateParameters.createBuildObjects))
        }
    }
}
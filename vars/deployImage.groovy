#!/usr/bin/env groovy

class DeployImageParameters {
    String clusterUrl = ""
    String clusterToken = ""
    String project = ""
    String application
    String image
    String tag = "latest"
}

def call(deployImageParameters) {
    call(new DeployImageParameters(deployImageParameters))
}

def call(DeployImageParameters deployImageParameters) {
    openshift.withCluster(deployImageParameters.clusterUrl, deployImageParameters.clusterToken) {
        openshift.withProject(deployImageParameters.project) {
            def dc = openshift.selector("dc/${deployImageParameters.application}").object()

            openshift.set("triggers", "dc/${deployImageParameters.application}", "--remove-all")
            openshift.set("triggers", "dc/${deployImageParameters.application}", "--from-image=${deployImageParameters.image}:${deployImageParameters.tag}", "-c ${dc.spec.template.spec.containers[0].name}") 
                  
            openshift.selector("dc", deployImageParameters.application).rollout().status()   
        }
    }
}
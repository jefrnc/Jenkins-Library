#!/usr/bin/env groovy

class RestartApplicationParameters {
    String clusterUrl = ""
    String clusterToken = ""
    String project = ""
    String application
    String image
    String tag = "latest"
}

def call(restartApplicationParameters) {
    call(new RestartApplicationParameters(restartApplicationParameters))
}

def call(RestartApplicationParameters restartApplicationParameters) {
    openshift.withCluster(restartApplicationParameters.clusterUrl, restartApplicationParameters.clusterToken) {
        openshift.withProject(restartApplicationParameters.project) {
            openshift.selector("dc", restartApplicationParameters.application).rollout().latest()
            openshift.selector("dc", restartApplicationParameters.application).rollout().status()   
        }
    }
}
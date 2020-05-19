#!/usr/bin/env groovy

class SelectTagParameters {
    String clusterUrl = ""
    String clusterToken = ""
    String project = ""
    String image
}

def call(selectTagParameters) {
    call(new SelectTagParameters(selectTagParameters))
}

def call(SelectTagParameters selectTagParameters) {
    openshift.withCluster(selectTagParameters.clusterUrl, selectTagParameters.clusterToken) {
        openshift.withProject(selectTagParameters.project) {
            def is = openshift.selector("is", selectTagParameters.image).object()
            def tags = ""
        
            for (version in is.status.tags)
                tags = version.tag + "\n" + tags
        
            def tag = input(message: "Select tag",
                            parameters: [steps.choice(choices: tags, description: 'Select a tag to deploy', name: 'Tags')])

            return tag
        }
    }
}
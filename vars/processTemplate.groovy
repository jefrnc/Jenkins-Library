#!/usr/bin/env groovy

class ProcessTemplateParameters {
    String clusterUrl = ""
    String clusterToken = ""
    String project = ""
    String template
    String application
    String image
    String createBuildObjects
}

def call(processTemplateParameters) {
    call(new ProcessTemplateParameters(processTemplateParameters))
}

def call(ProcessTemplateParameters processTemplateParameters) {
    openshift.withCluster(processTemplateParameters.clusterUrl, processTemplateParameters.clusterToken) {
        openshift.withProject(processTemplateParameters.project) {
            def objects = openshift.process(steps.readFile(file: processTemplateParameters.template), "-p", "PARAM_APP_NAME=${processTemplateParameters.application}", "-p", "PARAM_IMAGE_NAME=${processTemplateParameters.image}")
            echo "processTemplate"
            echo processTemplateParameters.createBuildObjects
            if (!processTemplateParameters.createBuildObjects) {
                echo "devuelvo todo"
                echo processTemplateParameters.createBuildObjects
                return objects
            }
            else {
                echo "filtro"
                def filteredObjects = []

                for (o in objects) {
                    // Prevents to promote the BuildConfig and ImageSream, the build is only done in development stages
                    if (o.kind != "BuildConfig" && o.kind != "ImageStream") 
                        filteredObjects.add(o)
                }

                return filteredObjects
            }
        }
    }
}
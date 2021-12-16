#!/usr/bin/env groovy

def getServiceCount(instance, service, region) {
    int count = sh(script: """
                              aws ecs describe-services --cluster ${instance} --services ${service} --region ${region} | jq '.services | length'
                           """, 
        returnStdout: true).trim()

    return count
}



def getTaskDefault(instance, service, region) {
    def currTaskDef = sh (
        returnStdout: true,
        script:  "aws ecs describe-services --cluster ${instance} --service ${service} \
                        --query \"services[].taskDefinition\" --region=${region} | jq -r \".[0]\" ").trim()
    return currTaskDef
}

def getTaskCurrent( taskname, region) {
    def currTaskDef = sh (
        returnStdout: true,
        script:  "                                                              \
            aws ecs describe-task-definition  --task-definition ${taskname}  --region ${region}   \
                                            | egrep 'revision'                  \
                                            | tr ',' ' '                        \
                                            | awk '{print \$2}'                 \
        "
        ).trim()
    return currTaskDef
}

def getTaskCurrenARNS( instance, taskname, region) {
    def currentTask = sh (
        returnStdout: true,
        script:  "                                                                \
            aws ecs list-tasks  --cluster ${instance}                  \
                                --family ${taskname}  --region ${region}    \
                                --output text                                     \
                                | egrep 'TASKARNS'                                \
                                | awk '{print \$2}'                               \
        "
    ).trim() 
    return currentTask
}


def downloadTask(taskname, region, filename) {
    try {
        sh "aws ecs describe-task-definition --task-definition ${taskname} --region=${region}  | jq -r  \".taskDefinition\" >> ${filename}"
    }
    catch(Exception e){

    } 
}

def downloadBatchTask(taskname, region, filename) {
    try {
        sh "aws batch describe-job-definitions --job-definition-name ${taskname} --status ACTIVE --region=${region}  --max-items 1 | jq -r  \".jobDefinitions\" | jq -r '.[0]' > ${filename}"
    }
    catch(Exception e){

    } 
}

 
def changeBatchImage(filenamein, imagename, filenameout) {
    sh "jq '.containerProperties.image= \"${imagename}\"' <<< cat ${filenamein} >> temp2-job.json"                                                                 
    //sh "jq '(.containerProperties.resourceRequirements[] | select(.type == \"VCPU\")).value =\"250\"' <<< cat temp2-job.json >> temp2x-job.json"
    
    sh "cat temp2-job.json"
    sh "jq 'del(.status)' temp2-job.json >> temp3-job.json"
    sh "jq 'del(.compatibilities)' temp3-job.json >> temp4-job.json"
    sh "jq 'del(.taskDefinitionArn)' temp4-job.json >> temp5-job.json"
    sh "jq 'del(.requiresAttributes)' temp5-job.json >> temp6-job.json"
    sh "jq 'del(.jobDefinitionArn)' temp6-job.json >> temp7-job.json"     
    //sh "jq 'del(.containerProperties.resourceRequirements)' temp7-job.json >> temp8-job.json"     
     
    sh "jq 'del(.revision)' temp7-job.json >> ${filenameout}"         
}

def changeTaskImage(filenamein, imagename, filenameout) {
    sh "jq '.containerDefinitions[].image= \"${imagename}\"' <<< cat ${filenamein} >> temp2.json"                                                                 
    sh "jq 'del(.status)' temp2.json >> temp3.json"
    sh "jq 'del(.compatibilities)' temp3.json >> temp4.json"
    sh "jq 'del(.taskDefinitionArn)' temp4.json >> temp5.json"
    sh "jq 'del(.requiresAttributes)' temp5.json >> temp6.json"
    sh "jq 'del(.revision)' temp6.json >> ${filenameout}"
    
}

def clearTaskImage(filenamein, filenameout) {
    sh "cat ${filenamein} >> tempo2.json"                                                                 
    sh "jq 'del(.status)' tempo2.json >> tempo3.json"
    sh "jq 'del(.compatibilities)' tempo3.json >> tempo4.json"
    sh "jq 'del(.taskDefinitionArn)' tempo4.json >> tempo5.json"
    sh "jq 'del(.requiresAttributes)' tempo5.json >> tempo6.json"
    sh "jq 'del(.revision)' tempo6.json >> ${filenameout}"
    
}


 

def createTask(taskname, region, file) {
    sh  " aws ecs register-task-definition  --family ${taskname}   \
                --region=${region}                                \
                --cli-input-json file://${file}"
}

def createBatchTask(taskname, region, file) {
    sh  " aws batch register-job-definition  --job-definition-name ${taskname}   \
                --region=${region}                                \
                --cli-input-json file://${file}"
}

 

def updateServiceDesiredZero(instance, service, region) 
{
    echo "Stopeamos instancias"
    try{
        sh "aws ecs update-service --desired-count 0 --cluster ${instance} --service ${service} --region ${region}"
    }
    catch(Exception e){

    }
}

def killTasks(instance, currentTask, region) 
{
    echo "Stopeamos tareas"
    try{
        sh "aws ecs stop-task --cluster ${instance} --task ${currentTask} --region ${region}"
    }
    catch(Exception e){

    }
}


def KillAllTask(instance, service,  region) {

    try{
        sh "aws ecs update-service --desired-count 0 --cluster ${instance} --service ${service} --region ${region}"
        
        for (i = 0; i <6; i++) {
            sh "aws ecs list-tasks --cluster ${instance} --service ${service} --region ${region} | jq -r \".taskArns[]\" | awk '{print \"aws ecs stop-task --region ${region} --cluster ${instance} --task \\\"\"\$0\"\\\"\"  }'  | sh "    
            sleep(10)
        }
    }
    catch(Exception e){

    }
}

def deployTask(instance, service, taskname, desired, region)
{
    try{
        echo "Deployamos nuevo servicio"
        sh "aws ecs update-service --cluster ${instance} --service ${service} --task-definition ${taskname} --force-new-deployment --desired-count ${desired} --region ${region}" 
    }
    catch(Exception e){

    }
}

def forceDeployment(instance, service,region)
{
    try{
        echo "Forzamos deployment de Lastest Image"
        sh "aws ecs update-service --cluster ${instance} --service ${service} --force-new-deployment --region ${region}"
    }
    catch(Exception e){

    }                    
}
                  
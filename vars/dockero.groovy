#!/usr/bin/env groovy

def loginECR(ecr, region) {
    echo "aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${ecr}"
    sh "aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${ecr}"
}

def buildRelease(image, version) {
              
    def currDate = sh (
        returnStdout: true,
        script:  "date --rfc-3339=seconds | sed 's/ /T/'").trim()
   
   
    sh "find ./  -type f -name Dockerfile | xargs sed -i -e 's/{BUILD_VERSION}/$version/g'"
    sh "find ./  -type f -name Dockerfile | xargs sed -i -e 's/{BUILD_DATE}/$currDate/g'"

    sh "cat Dockerfile"
    sh "docker build -t ${image}:v${version} ."
}

def tagRelease(image, ecr, version) {
    sh "docker tag ${image}:v${version} ${ecr}:v${version}"
    sh "docker tag ${image}:v${version} ${ecr}:lastest"
}

def removeLastestRelease(ecr,region) {    
    def (v, z) =  ecr.split('/')
    sh "aws ecr batch-delete-image --repository-name ${z} --image-ids imageTag=lastest --region ${region}"
}

def getPublishImage(ecr, version)
{
    return "${ecr}:v${version}"
}

def pushRelease(ecr, version) {
    sh "docker push ${ecr}:v${version}"    
    //sh "docker push ${ecr}:lastest"
}

def buildSnapshot(image, version) {
    def currDate = sh (
        returnStdout: true,
        script:  "date --rfc-3339=seconds | sed 's/ /T/'").trim()
   
    sh "find ./  -type f -name Dockerfile | xargs sed -i -e 's/{BUILD_VERSION}/$version/g'"
    sh "find ./  -type f -name Dockerfile | xargs sed -i -e 's/{BUILD_DATE}/$currDate/g'"

    sh "cat Dockerfile"
    sh "docker build -t ${image}:v${version}-develop ."
}

def tagSnapshot(image, ecr, version) {    
    sh "docker tag ${image}:v${version}-develop ${ecr}:v${version}-develop"    
    sh "docker tag ${image}:v${version}-develop ${ecr}:develop-lastest"
}

def removeLastestSnapshot(ecr,region) {    
    def (v, z) =  ecr.split('/')
    sh "aws ecr batch-delete-image --repository-name ${z} --image-ids imageTag=develop-lastest --region ${region}"
}

def pushSnapshot(ecr, version) {
    sh "docker push ${ecr}:v${version}-develop"
    sh "docker push ${ecr}:develop-lastest"
}

#!/usr/bin/env groovy



def getToken(domain, accountId, region) {

 
    def currTaskDef = sh (
        returnStdout: true,
        script:  "aws codeartifact get-authorization-token --domain ${domain} --domain-owner ${accountId} --query authorizationToken --output text --region ${region}").trim()
   
    return currTaskDef
}


def setRegistry(domain, accountId, repository, token, region) {
    sh ". ~/.nvm/nvm.sh && npm config set registry https://${domain}-${accountId}.d.codeartifact.${region}.amazonaws.com/npm/${repository}/ https://${domain}-${accountId}.d.codeartifact.${region}.amazonaws.com/npm/${repository}/:always-auth=true https://${domain}-${accountId}.d.codeartifact.${region}.amazonaws.com/npm/${repository}/:_authToken=${token}"
}

def changeVersion(oldversion, newversion, filename) {
     sh "sed -i \"s,${oldversion},${newversion},g\" ${filename} "
}

def downloadPackage(domain, accountId, repository, packagename, version,  asset, region) {
    sh "aws codeartifact get-package-version-asset --domain ${domain} --domain-owner ${accountId} --repository ${repository} --format npm --package ${packagename} --package-version ${version} --asset ${asset}  --region ${region} ${asset} "
}

def publishWithRegistry(domain, accountId, repository, token, region) {
    sh ". ~/.nvm/nvm.sh &&  npm config set registry https://${domain}-${accountId}.d.codeartifact.${region}.amazonaws.com/npm/${repository}/ //${domain}-${accountId}.d.codeartifact.${region}.amazonaws.com/npm/${repository}/:always-auth=true //${domain}-${accountId}.d.codeartifact.${region}.amazonaws.com/npm/${repository}/:_authToken ${token} && npm ci && npm install lodash && npm publish "     
}
#!/usr/bin/env groovy

def getTag(tech) {
    if (tech.equals("java"))
        return readMavenPom().getVersion()
    else if (tech.equals("nodejs")) 
        return getVersionFromPackageJSON()
}

def login(username){
   sh "jiracli login -u "+ username +"  --endpoint=" + env.JIRA_SERVER
}

def saveCredentials(pwd){
   sh "echo \""+ pwd +"\" | pass insert jira -e"
}


#!/usr/bin/env groovy

def getTag(tech) {
    if (tech.equals("java"))
        return readMavenPom().getVersion()
    else if (tech.equals("nodejs")) 
        return getVersionFromPackageJSON()
}

def login(username){

    SH_CMD = sh (
        script: "jiracli login -u "+ username +"  --endpoint=" + env.JIRA_SERVER,
        returnStdout: true
    ).trim()

    echo "Verificando conexion con JIRA: ${SH_CMD}"
}

def saveCredentials(pwd){
   sh "echo \""+ pwd +"\" | pass insert jira -e"
}

def getList(project, format) {

    println "Obteniendo lista de elementos del proyecto: " + project

    SH_CMD = sh (
        script: "jiracli list --template="+ format + " --query \"project = '"+ project +"'\"  --endpoint=" + env.JIRA_SERVER,
        returnStdout: true
    )
    println "${SH_CMD}"
 
}

 


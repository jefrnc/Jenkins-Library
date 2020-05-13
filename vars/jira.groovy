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
def getIssueByVersion(project, version) {
 
    println "Obteniendo tarjeta de version en JIRA: " + version 
    SH_CMD = sh (
 

        script: '''
                    set +x  
                    jiracli list --template table --query "project = 'APDP' and affectedVersion IN (''''' + version ''''')  ORDER BY priority asc, created" --endpoint=''' + env.JIRA_SERVER + ''' | grep -i APDP | awk -F '|' '{ print $2 }' | sed -e 's/^[[:space:]]*//'
                    set -x
                ''',
        returnStdout: true
    ).trim()

    if (SH_CMD) { 
        if (SH_CMD!= "") { 
          println "La tarjeta asociada es: " + SH_CMD 
          return SH_CMD
        }
    }

       
      jiraCheckMsg =  "No se encontraron tarjetas activas para el proyecto ${project} y la version ${version}"
      println jiraCheckMsg
      //error msg
      throw new Exception(jiraCheckMsg)


 
   
     //
}
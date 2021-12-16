#!/usr/bin/env groovy

def getTag(tech) {
    if (tech.equals("java"))
        return readMavenPom().getVersion()
    else if (tech.equals("nodejs")) 
        return getVersionFromPackageJSON()
}

def login(username){

    SH_CMD = sh (
        script: "/opt/jiracli/jiracli login -u "+ username +"  --endpoint=" + env.JIRA_SERVER,
        returnStdout: true
    ).trim()

    echo "Verificando conexion con JIRA: ${SH_CMD}"
}


def loginIterative(username, password){

    SH_CMD = sh (
        script: "/opt/jiracli/login_iterative.sh "+ username +" "+ password + " " + env.JIRA_SERVER,
        returnStdout: false
    ).trim()

    echo "Verificando conexion con JIRA: ${SH_CMD}"
}

def saveCredentials(user, pwd){
   sh "echo \""+ pwd +"\" | pass insert "+ user +" -e"
}


def requestApproval(project, issue, envName,  user) {
  sh "/opt/jiracli/jiracli request -M POST rest/api/2/issue/"+ issue + "/comment '{\"body\":\"El usuario " + user + " solicito al aprobacion al ambiente "+ envName + ".\" }' --endpoint=" + env.JIRA_SERVER
}

def saveApproval(project, issue, envName,  user) {
    sh "/opt/jiracli/jiracli request -M POST rest/api/2/issue/"+ issue + "/comment '{\"body\":\"El usuario " + user + " acepto el pasaje a "+ envName + ".\" }' --endpoint=" + env.JIRA_SERVER
}

def saveComment(project, issue, comment) {
    sh "/opt/jiracli/jiracli request -M POST rest/api/2/issue/"+ issue + "/comment '{\"body\":\"" + comment + "\" }' --endpoint=" + env.JIRA_SERVER
}

def getList(project, format) {

    println "Obteniendo lista de elementos del proyecto: " + project

    SH_CMD = sh (
        script: "/opt/jiracli/jiracli list --template="+ format + " --query \"project = '"+ project +"'\"  --endpoint=" + env.JIRA_SERVER,
        returnStdout: true
    )
    println "${SH_CMD}"
 
}


def getIssueByVersion(project, version) {
 
    println "Obteniendo tarjeta de version en JIRA: " + version 
    SH_CMD = sh (
 

        script: '''
                    set +x  
                    /opt/jiracli/jiracli list --template table --query "project = \'''' + project + '''\' and fixVersion IN (\'''' + version + '''\')  ORDER BY priority asc, created" --endpoint=''' + env.JIRA_SERVER + ''' | grep -i ''' + project + ''' | awk -F '|' '{ print $2 }' | sed -e 's/^[[:space:]]*//'
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
}

def getIssueByVersionAndStatus(project, version, status) {
 
    println "Obteniendo tarjeta de version en JIRA: " + version 
    SH_CMD = sh (

        script: '''
                    set +x  
                    /opt/jiracli/jiracli list --template table --query "project = \'''' + project + '''\' and fixVersion IN (\'''' + version + '''\') and status=\'''' + status + '''\'   ORDER BY priority asc, created" --endpoint=''' + env.JIRA_SERVER + ''' | grep -i ''' + project + ''' | awk -F '|' '{ print $2 }' | sed -e 's/^[[:space:]]*//'
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
}

def getIssueByaffectedVersion(project, version) {
 
    println "Obteniendo tarjeta de version en JIRA: " + version 
    SH_CMD = sh (
 

        script: '''
                    set +x  
                    /opt/jiracli/jiracli list --template table --query "project = \'''' + project + '''\' and affectedVersion IN (\'''' + version + '''\')  ORDER BY priority asc, created" --endpoint=''' + env.JIRA_SERVER + ''' | grep -i APDP | awk -F '|' '{ print $2 }' | sed -e 's/^[[:space:]]*//'
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
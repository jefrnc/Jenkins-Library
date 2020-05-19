#!/usr/bin/env groovy

def getTag(tech) {
    if (tech.equals("java"))
        return readMavenPom().getVersion()
    else if (tech.equals("nodejs")) 
        return getVersionFromPackageJSON()
}

def getTagFromPom(folder){
    def matcher = readFile(folder + 'pom.xml') =~ '<version>(.+)</version>'
    matcher ? matcher[0][1] : null
}

def getVersionFromPackageJSON() {
    sh "cat package.json | grep version | head -1 | awk -F: '{ print \$2 }' | sed 's/[\",]//g' | tr -d '[[:space:]]' > version"

    return readFile("version").trim()
}

def version(folder) {
    dir("application"){
        def matcher = readFile(folder + 'pom.xml') =~ '<version>(\\d*)\\.(\\d*)\\.(\\d*)(-SNAPSHOT)*</version>'
        matcher ? matcher[0] : null
    }
}
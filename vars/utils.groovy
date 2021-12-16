#!/usr/bin/env groovy


def getBranchName() {
    def currentBranch = sh (
                                    returnStdout: true,
                                    script:  "git branch | grep \\* | sed 's/* //g' "
                                ).trim() 
    return currentBranch
}

def getBranchTypeAndName(fullBranchName) 
{
    if (fullBranchName in ['develop', 'master']) {
        return [fullBranchName, fullBranchName]
    }
    if (fullBranchName.matches(/^(feature|bugfix)\/[.\d\-\w]+$/)) {
        return [fullBranchName.split('/')[0],
                fullBranchName.split('/')[1].toLowerCase().replaceAll(/[^.\da-z]/, '.')]
    }
    if (fullBranchName.matches(/^hotfix\/\d+(\.\d+){1,2}p\d+$/)) {
        return fullBranchName.split('/') as List
    }

    
    //if (fullBranchName.matches(/^release\/\d+(\.\d+){1,2}([ab]\d+)?$/)) {
    //    return fullBranchName.split('/') as List
    //}
    if (fullBranchName.matches(/^release\/\d+(\.\d+)(\.\d+)(=?\b-rc\b).*$/)) {
        return fullBranchName.split('/') as List
    }
    if (fullBranchName.matches(/^PR-\d+-?(merge|head)?$/)) {
        return ['PR', fullBranchName.split('-', 2)[1].replaceAll(/-/, '.')]
    }
    error "Enforcing Gitflow Workflow on '${fullBranchName}'. Ha!"
}

def getBuildVersion(projectVersion, fullBranchName, buildNumber)
{
 
    //string projectVersion = getProjectVersion()
    def branchTypeAndName = getBranchTypeAndName(fullBranchName)

    switch (branchTypeAndName[0]) {
        case 'master':
            return projectVersion
        case 'hotfix':
            return "${branchTypeAndName[1]}-rc.${buildNumber}"
        case 'develop':
            return "${projectVersion}+develop.dev${buildNumber}"
        case 'feature':
            return "${projectVersion}+feature.${branchTypeAndName[1]}.dev${buildNumber}"
        case 'bugfix':
            return "${projectVersion}+bugfix.${branchTypeAndName[1]}.dev${buildNumber}"
        case 'release':

            println  branchTypeAndName[0]
            println  branchTypeAndName[1]
            println  projectVersion
            assert branchTypeAndName[1] == (projectVersion + "-rc")
            return "${projectVersion}-rc.${buildNumber}"
        case 'PR':
            return "${projectVersion}+PR.${branchTypeAndName[1]}.${buildNumber}"
        default:
            error "Oops, we messed up! :("
    }
}

def getBuildType(fullBranchName )
{
    switch (getBranchTypeAndName(fullBranchName)[0]) {
        case 'master':
            return 'latest'
        case 'release':
            return 'next'
        default:
            return 'develop'
    }
}


def saveBranchList() {
    sh (returnStdout: true, script:  "git ls-remote --heads | awk '{print \$2}' | sed 's/refs\\/heads\\///g' > branches.txt")
}
def showRequestingUser() {     
    try{
        def build = currentBuild.rawBuild
        def cause = build.getCause(hudson.model.Cause.UserIdCause.class)
        def name = cause.getUserName()
        echo "Requesting user ${name}"
    }
    catch(Exception e){
        echo "Requesting for System "
    }
}

def getTag(tech) {
    if (tech.equals("java"))
        return readMavenPom().getVersion()
    else if (tech.equals("nodejs")) 
        return getVersionFromPackageJSON()
}

def getTagFromPom(pomFile){
    def matcher = readFile(pomFile) =~ '<version>(.+)</version>'
    matcher ? matcher[0][1] : null
}

def getTagFromValue(pomFile, versionPath){

    println pomFile
    //def matcher = pomFile.split('-')
    def matcher = pomFile.split(/-/)

    if (matcher!= null) {

 
        println "matcher = ${matcher}";
 
        def major = matcher[0];
        def minor = matcher[1];

        if (major == "0")
            major = "1";
            
        def majmin = "${major}.${minor}";
        def patch  = matcher[2];

   
        if (versionPath!= "") {
            patch = versionPath;
        }
        println "Version a usar ${majmin}.${patch}";
        return "${majmin}.${patch}"
    }
    
}
 
def getTagFromPomChange(pomFile, versionPath){
    
    def matcher = readFile(pomFile) =~ '<version>(\\d*)\\.(\\d*)\\.(\\d*)</version>'
    matcher = matcher ? matcher[0] : null

    if (matcher!= null) {

        println "matcher = ${matcher}";
 
        
        def major = matcher[1];
        def minor = matcher[2];

        if (major == "0")
            major = "1";
            
        def majmin = "${major}.${minor}";
        def patch  = matcher[3];


   
        if (versionPath!= "") {
            patch = versionPath;
        }
        println "Version a usar ${majmin}.${patch}";
        return "${majmin}.${patch}"
    }
    
}


def getVersionFromPackageJSON() {
    sh "cat package.json | grep version | head -1 | awk -F: '{ print \$2 }' | sed 's/[\",]//g' | tr -d '[[:space:]]' > version"

    return readFile("version").trim()
}

def dockerTest(String imageUnderTest) { 
  def randomImageId = UUID.randomUUID().toString()
  def testImage = docker.build(randomImageId, "--file Dockerfile.test .")
  testImage.withRun() { c ->
    steps.sh("docker exec ${c.id} goss validate --retry-timeout 35s --sleep 10s")
  }
}
#!/usr/bin/env groovy

def getParameterWithDecryp(parameter, region) {
              
    def currValue = sh (
        returnStdout: true,
        script:  "aws ssm get-parameters --name ${parameter} --region ${region} --query \"Parameters[0].Value\" --with-decryption").trim()
   
    return currValue
}

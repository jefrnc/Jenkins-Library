

def getAWSStoreParamter(credentialsId, regionId, parameterId) {
    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: credentialsId]]) {
        vConnectionString = awso.getParameterWithDecryp("ls-config.db_host", "us-east-1")
        def TEMP=vConnectionString.split("/")[2] 
        def TEMPA = ""
        DBP_HOST = TEMP.split(":")[0]
        DBP_PORT = TEMP.split(":")[1]
        TEMP = vConnectionString.split("/")[3]//.split('?')                         
        TEMP= TEMP.split("\\?")

        DBP_DB= TEMP[0]
        TEMP = TEMP[1].split('&')
         
        for(item in TEMP){
            TEMPA = item.split('=')
            if (TEMPA[0] == "user")
                DBP_USER = TEMPA[1]
            if (TEMPA[0] == "password")
                DBP_PWD = TEMPA[1]                                    
        }

        return [ "jdbc:postgresql://${DBP_HOST}:${DBP_PORT}/${DBP_DB}" , DBP_USER, DBP_PWD, DBP_HOST, DBP_PORT , DBP_DB  ]
    }

}


 


def getSchema(api, aws_account_id, dashboard_id)
{
    url = "${api}qs-schema?AccountId=${aws_account_id}&DashboardId=${dashboard_id}"
    shellcommand = "wget -O schema.json \"${url}\" "
    def ResultJson = sh(script: shellcommand, returnStdout: true).trim()    
}

def getFilesDatasets(){

    def ResultJson = sh(script: "ls -1 datasets_*.json | sort -t'_' -n -k2", returnStdout: true).trim()
    ResultJson = ResultJson.split('\n');
    return ResultJson

     
}

def getFilesDatasources(){
 
    def ResultJson = sh(script: "ls -1 datasource_*.json | sort -t'_' -n -k2", returnStdout: true).trim()
    ResultJson = ResultJson.split('\n');
    return ResultJson
}
 


def getTemplateExport(api, aws_account_id, dashboard_id, creator)
{

    url = "${api}qs-export?AccountId=${aws_account_id}&DashboardId=${dashboard_id}&UserCreator=${creator}"
    shellcommand = "wget -O export.json \"${url}\" "
    def ResultJson = sh(script: shellcommand, returnStdout: true).trim()
    sh "cat export.json | jq '.DashboardDocument' > dashboard.json"
    sh "cat export.json | jq '.AnalysisDocument' > analysis.json"
    sh "cat export.json | jq '.DataSetDocuments' > datasets.json"
    sh "cat export.json | jq '.DataSourceDocuments' > datasource.json"
    sh "cat export.json | jq '.PolicyDocument' > policy.json"
    sh "cat export.json | jq '.PolicyAdminDocument' > policy-admin.json"
     


    sh ''' xidel -s datasets.json --xquery \'
                for $x at $i in 1 to count($json())  
                count $i
                return
                file:write(
                    concat(
                    "datasets_",
                    (100+$i),
                    ".json"
                    ),
                    serialize-json(      
                        $json($x)              
                    )
                )
                \' 
    '''




    sh ''' xidel -s datasource.json --xquery \'
                for $x at $i in 1 to count($json())  
                count $i
                return
                file:write(
                    concat(
                    "datasource_",
                    (100+$i),
                    ".json"
                    ),
                    serialize-json(      
                        $json($x)              
                    )
                )
                \' 
    '''                
}                
def getDashboardSchema(file) {

    def DashboardId = sh (
                        returnStdout: true,
                        script:  "cat ${file} | jq '.DashboardId'").trim()
    return DashboardId
}


def getAnalysisSchema(file) {



    def DashboardId = sh (
                        returnStdout: true,
                        script:  "cat ${file} | jq '.AnalysisIdRef'").trim()
    return DashboardId
}

def getArnSchema(file, node) {
    def DataSets= sh (
                        returnStdout: true,
                        script:  "cat ${file} | jq '${node}'").trim()

    DataSets = DataSets.replace("[", "")
    DataSets = DataSets.replace("]", "")
    DataSets = DataSets.replace("\n", "")
    DataSets = DataSets.replace("\t", "")
    DataSets = DataSets.replace(" ", "")

    def String[] DataSetsId;
    DataSetsId = DataSets.split(',');
                        
    return DataSetsId
}
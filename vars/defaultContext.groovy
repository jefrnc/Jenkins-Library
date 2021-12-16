#!/usr/bin/env groovy
import hudson.model.ParametersAction

def getReferenceParameters(String lastJobBuildSuccess){

  def (jobName, jobBuildNumber) = lastJobBuildSuccess.split('#')

  jobName = jobName.trim()
  jobBuildNumber = jobBuildNumber.trim()

  def params=[:]
  def parameters = Jenkins.instance.getAllItems(Job)
                        .find {job -> job.fullName == jobName }
                        .getBuildByNumber(jobBuildNumber.toInteger())
                        .getAction(hudson.model.ParametersAction)

  params["INVOKE_BUILD_NUMBER"]=parameters.getParameter('INVOKE_BUILD_NUMBER').value
  params["TASK_PROMOTE"]=parameters.getParameter('TASK_PROMOTE').value
  params["IMAGE_PROMOTE"]=parameters.getParameter('IMAGE_PROMOTE').value
  params.each{ k, v -> println "${k} -> ${v}" }
  return params
}
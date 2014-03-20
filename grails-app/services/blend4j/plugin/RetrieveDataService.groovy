package blend4j.plugin

import grails.transaction.Transactional

@Transactional
class RetrieveDataService {

    def saveStatusOfExport(String nameOfTheExportJob, String nameOfTheLibrary) {
        try{
            def newJob = new StatusOfExport(id:nameOfTheExportJob,jobStatus:"Started",lastExportName: nameOfTheLibrary, lastExportTime:new Date());
            newJob.save();
        }catch(e){
            log.error("The export job for galaxy couldn't be saved")
            return false;
        }
        return true;
    }

    def updateStatusOfExport(String nameOfTheExportJob, String newState) {
        try{
            def newJob = StatusOfExport.get(nameOfTheExportJob);
            newJob.jobStatus = newState;
            newJob.save();
        }catch(e){
            log.error("The export job for galaxy couldn't be updated")
            return false;
        }
        return true;
    }
}

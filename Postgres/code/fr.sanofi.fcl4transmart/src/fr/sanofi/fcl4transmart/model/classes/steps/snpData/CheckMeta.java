package fr.sanofi.fcl4transmart.model.classes.steps.snpData;

import fr.sanofi.fcl4transmart.model.classes.dataType.SnpData;
import fr.sanofi.fcl4transmart.model.classes.workUI.SNPData.CheckMetaUI;
import fr.sanofi.fcl4transmart.model.interfaces.DataTypeItf;
import fr.sanofi.fcl4transmart.model.interfaces.StepItf;
import fr.sanofi.fcl4transmart.model.interfaces.WorkItf;

public class CheckMeta implements StepItf {
	private WorkItf workUI;
	private DataTypeItf dataType;
	public CheckMeta(DataTypeItf dataType) {
		this.workUI=new CheckMetaUI(dataType);
		this.dataType=dataType;
	}
	@Override
	public WorkItf getWorkUI() {
		return this.workUI;
	}

	@Override
	public String getDescription() {
		return "This step allows checking the loading of the meta tables.\n"+
				"Number of expected lines is got from the raw files, and number of inserted lines are got from the database, and displayed. It is also indicated if these values are the same in the two cases.\n"+
				"A database connection is needed for this step";
	}

	@Override
	public boolean isAvailable() {
		if(((SnpData)this.dataType).getRawFile()!=null && ((SnpData)this.dataType).getAnnotationFile()!=null 
				&& ((SnpData)this.dataType).getMappingFile()!=null && ((SnpData)this.dataType).checkMappingFileComplete()
				&& ((SnpData)this.dataType).isAnnotationLoaded()
				&& ((SnpData)this.dataType).isMetaLoaded()) return true;
	return false;
	}
	public String toString(){
		return "Check meta table loading";
	}
}

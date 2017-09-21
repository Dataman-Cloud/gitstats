package com.dataman.gitstats.param;

import javax.validation.constraints.NotNull;

public class AddProjectParam {

	@NotNull
	String aId;
	@NotNull
	String name;
	@NotNull
	String[] branchs;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getaId() {
		return aId;
	}
	public void setaId(String aId) {
		this.aId = aId;
	}
	public String[] getBranchs() {
		return branchs;
	}
	public void setBranchs(String[] branchs) {
		this.branchs = branchs;
	}
	
	
	
}

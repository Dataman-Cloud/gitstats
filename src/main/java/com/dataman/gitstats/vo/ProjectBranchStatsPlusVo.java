package com.dataman.gitstats.vo;

import java.util.List;

import com.dataman.gitstats.po.ProjectBranchStats;

public class ProjectBranchStatsPlusVo extends ProjectBranchStats{

	List<StatsByUserByDayVo> data;

	public List<StatsByUserByDayVo> getData() {
		return data;
	}

	public void setData(List<StatsByUserByDayVo> data) {
		this.data = data;
	}
	
	
}

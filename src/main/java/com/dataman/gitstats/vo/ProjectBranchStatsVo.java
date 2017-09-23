package com.dataman.gitstats.vo;

import java.util.List;

import com.dataman.gitstats.po.ProjectBranchStats;

public class ProjectBranchStatsVo extends ProjectBranchStats{

	List<CommitStatsVo> data;

	public List<CommitStatsVo> getData() {
		return data;
	}

	public void setData(List<CommitStatsVo> data) {
		this.data = data;
	}
	
}

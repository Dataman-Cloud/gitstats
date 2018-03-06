package com.dataman.gitstats.vo;

import com.dataman.gitstats.po.GroupStats;

import java.util.List;

public class GroupStatsVo extends GroupStats {

	List<CommitStatsVo> data;

	public List<CommitStatsVo> getData() {
		return data;
	}

	public void setData(List<CommitStatsVo> data) {
		this.data = data;
	}
	
}

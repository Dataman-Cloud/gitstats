package com.dataman.gitstats.vo;

import com.dataman.gitstats.po.GroupStats;

import java.util.List;

public class GroupStatsPlusVo extends GroupStats {

	List<StatsByUserByDayVo> data;

	public List<StatsByUserByDayVo> getData() {
		return data;
	}

	public void setData(List<StatsByUserByDayVo> data) {
		this.data = data;
	}
	
	
}

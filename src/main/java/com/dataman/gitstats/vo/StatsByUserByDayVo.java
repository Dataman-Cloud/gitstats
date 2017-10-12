package com.dataman.gitstats.vo;

import java.util.List;

public class StatsByUserByDayVo {

	String _id;
	int addrow;
	int removerow;
	double commit;
	List<CommitStatsVo> data;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public int getAddrow() {
		return addrow;
	}
	public void setAddrow(int addrow) {
		this.addrow = addrow;
	}
	public int getRemoverow() {
		return removerow;
	}
	public void setRemoverow(int removerow) {
		this.removerow = removerow;
	}
	public double getCommit() {
		return commit;
	}
	public void setCommit(double commit) {
		this.commit = commit;
	}
	public List<CommitStatsVo> getData() {
		return data;
	}
	public void setData(List<CommitStatsVo> data) {
		this.data = data;
	}
	
	
	
}

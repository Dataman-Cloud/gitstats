package com.dataman.gitstats.vo;

public class CommitStatsVo {

	String _id;
	int addrow;
	int removerow;
	double commit;
	
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
	
	
}

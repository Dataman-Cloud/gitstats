package com.dataman.gitstats.service;

import com.dataman.gitstats.po.CommitStatsPo;
import com.dataman.gitstats.po.ProjectBranchStats;
import com.dataman.gitstats.vo.CommitStatsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import  org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dataman.gitstats.repository.CommitStatsRepository;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommitStatsService {

	@Autowired
	CommitStatsRepository commitStatsRepository;

	@Autowired
	MongoTemplate mongoTemplate;


	/**
	 * @method: getStatsByBranchAndTime
	 * @author biancl
	 * @date 2017-10-11 17:23
	 * @param  branchId, startDate, endDate, dateUnit
	 * @return java.util.List<java.lang.Object>
	 */
	public List<Object> getStatsByBranchAndTime(String branchId,Date startDate,Date endDate,String dateUnit){

		switch (dateUnit){
			case ("year"):
				return getYearStatsByBranchAndTime(branchId,startDate,endDate);
			case("month"):
				return getMonthStatsByBranchAndTime(branchId, startDate, endDate);
			case("week"):
				return getMonthStatsByBranchAndTime(branchId,startDate,endDate);
			case("day"):
				return getMonthStatsByBranchAndTime(branchId,startDate,endDate);
			default:
				return getMonthStatsByBranchAndTime(branchId,startDate,endDate);
		}
	}

	public List<Object> getYearStatsByBranchAndTime(String branchId,Date startDate,Date endDate){
		Aggregation agg=Aggregation.newAggregation(
				Aggregation.match(new Criteria("branchId").is(branchId)
						.andOperator(new Criteria("createdAt").gt(startDate).lt(endDate))),
				Aggregation.project("addRow","removeRow").and("createdAt").substring(0, 4).as("year"),
				Aggregation.group(Fields.fields("year")).sum("addRow").as("totalAddRow").sum("removeRow").as("totalRemoveRow").count().as("totalCommit"),
				Aggregation.project("totalAddRow","totalRemoveRow","totalCommit").andExpression("$subtract","$totalAddRow","$totalRemoveRow")
		);
		System.out.println("******************aggregation="+agg.toString());
		AggregationResults aggregationResults=mongoTemplate.aggregate(agg, CommitStatsPo.class, Object.class);
		System.out.println("******************aggregationResults="+aggregationResults.toString());
		return aggregationResults.getMappedResults();
	}

	public List<Object> getMonthStatsByBranchAndTime(String branchId,Date startDate,Date endDate){

		return null;
	}

	public List<Object> getWeekStatsByBranchAndTime(String branchId,Date startDate,Date endDate){

		return null;
	}

	public List<Object> getDayStatsByBranchAndTime(String branchId,Date startDate,Date endDate){

		return null;
	}

	public List<CommitStatsVo> statsByUser(ProjectBranchStats pbs){
		List<CommitStatsVo> list=null;
		Aggregation agg= Aggregation.newAggregation(
				Aggregation.match(new Criteria("branchId").is(pbs.getId())),
				Aggregation.group(Fields.fields("$authorName")).sum("$addRow").as("addrow").sum("$removeRow").as("removerow").count().as("commit"),
				Aggregation.sort(Sort.Direction.DESC, "addrow")
		);
		AggregationResults<CommitStatsVo> ret=  mongoTemplate.aggregate(agg, CommitStatsPo.class, CommitStatsVo.class);
		list =ret.getMappedResults();
		return list;
	}

	public List<CommitStatsVo> statsByDay(ProjectBranchStats pbs){
		List<CommitStatsVo> list=null;
		Aggregation agg= Aggregation.newAggregation(
				Aggregation.match(new Criteria("branchId").is(pbs.getId())),
				Aggregation.project().and("createdAt").substring(0, 10).as("day").and("$addRow").as("addRow").and("$removeRow").as("removeRow"),
				Aggregation.group(Fields.fields("day")).sum("addRow").as("addrow").sum("removeRow").as("removerow").count().as("commit"),
				Aggregation.sort(Sort.Direction.ASC, "_id")
		);
		System.out.println(agg.toString());

		AggregationResults<CommitStatsVo> ret=  mongoTemplate.aggregate(agg, CommitStatsPo.class, CommitStatsVo.class);
		list =ret.getMappedResults();
		//补充 缺省日期数据  补充 到 当前
		list =complementDay(list,null);
		return list;
	}
	/**
	 * @method complementDay(缺省日期数据)
	 * @param beginDate 开始日期  null -> list第一个时间
	 * @return List<CommitStatsVo>
	 * @author liuqing
	 * @throws ParseException
	 * @date 2017年9月24日 上午10:37:09
	 */
	public List<CommitStatsVo> complementDay(List<CommitStatsVo> list,Date beginDate){
		LocalDate today = LocalDate.now();
		LocalDate loopday=null;
		// mongo查询出来的UnmodifiableRandomAccessList是一个 不可以修改
		System.out.println(list.getClass());

		if(beginDate==null){
			loopday = LocalDate.parse(list.get(0).get_id());
		}else{
			Instant instant = beginDate.toInstant();
			ZoneId zoneId = ZoneId.systemDefault();
			loopday = instant.atZone(zoneId).toLocalDate();
		}
		List<CommitStatsVo> tmp= new ArrayList<CommitStatsVo>();
		int index=0;
		while (loopday.toEpochDay()<=today.toEpochDay()) {
			if(list.size()-1<index){
				CommitStatsVo csv=new CommitStatsVo();
				csv.set_id(loopday.format(DateTimeFormatter.ISO_LOCAL_DATE));
				tmp.add(csv);
			}else{
				LocalDate cld=LocalDate.parse(list.get(index).get_id());
				if(cld.toEpochDay()!=loopday.toEpochDay()){
					CommitStatsVo csv=new CommitStatsVo();
					csv.set_id(loopday.format(DateTimeFormatter.ISO_LOCAL_DATE));
					tmp.add(csv);
				}else{
					tmp.add(list.get(index));
					index++;
				}
			}
			long nextday=loopday.toEpochDay()+1;
			loopday=LocalDate.ofEpochDay(nextday);
		}
		return tmp;
	}





}

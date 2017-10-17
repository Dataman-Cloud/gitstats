package com.dataman.gitstats;

import java.util.Date;
import java.util.List;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.GitLabApi.ApiVersion;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;

import com.alibaba.fastjson.JSON;

// import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


public class CodeTest {
	
	
	public static void main(String[] args) {
		int ret =122;
		
		int i=1;
		while (i!=ret) {
			if (i<ret){
				i=i*2;
			}else{
				break;
			}
			System.err.println(i);
		}
		
		
	}

	public static void mainxx(String[] args) {

//		db.getCollection('commitStatsPo').aggregate([  
//		{$match:{"proid" : "0670ed0736be4101aab4d679a997977e","branch" : "develop"}}
//		,{$project : { day : {$substr: ["$createdAt", 0, 10] },addRow : 1,removeRow:1 ,authorName:1}}
//		,{$group   : { _id : {authorName: "$authorName",day : "$day"},  addRow : { $sum : "$addRow" } ,removeRow : { $sum : "$removeRow" },commit:{$sum:1}}}   
//		,{$project : {_id :0, authorName : '$_id.authorName', dayinfo : {day : '$_id.day', addRow:'$addRow',removeRow:'$removeRow',commit:'$commit'}}}
//		,{$group:{_id:"$authorName", days:{$push:"$dayinfo"}, addrow: {$sum: "$dayinfo.addRow"},removeRow: {$sum: "$dayinfo.removeRow"},commit: {$sum: "$dayinfo.commit"}}}
//		,{$sort : { addrow : -1 }}
//		,{ $limit : 8 }
//		])
		
		
		Aggregation agg= Aggregation.newAggregation(
				Aggregation.match(new Criteria("branchId").is("888")),
				Aggregation.project().and("createdAt").dateAsFormattedString("%Y-%U").as("day").and("$addRow").as("addRow")
					.and("$removeRow").as("removeRow").and("$authorName").as("authorName"),
				Aggregation.group(Fields.fields("authorName","day")).sum("addRow").as("addRow").sum("removeRow").as("removeRow")
					.count().as("commit"),
				Aggregation.project().and("_id.authorName").as("authorName").and("dayinfo")
					.nested(Aggregation.bind("day","_id.day").and("addRow","addRow").and("removeRow","removeRow").and("commit","commit")),
				Aggregation.group(Fields.fields("authorName")).sum("dayinfo.addRow").as("addRow")
					.sum("dayinfo.removeRow").as("removeRow").sum("dayinfo.commit").as("commit")
					.push("dayinfo").as("days"),
				Aggregation.sort(Direction.DESC, "addRow"),
				Aggregation.limit(8l)
			);
		System.out.println(agg.toString());
		
		
		GitLabApi api;
		Project p=null;
		Pager<Commit> page=null;
		try {
			api = GitLabApi.login(ApiVersion.V3, "http://192.168.145.131:10080", "root", "12345678");
			//p= api.getProjectApi().getProject(1);
			//System.out.println(JSON.toJSONString(p));
			if(api.getApiVersion() == ApiVersion.V4){
				page= api.getCommitsApi().getCommits(3, "master", null, new Date(),100);
				System.out.println(JSON.toJSONString(page));
				List<Commit> list= page.first();
				System.out.println(JSON.toJSONString(list));
				int i=0;
				while (page.hasNext()) {
					i++;
					list=page.next();
					System.out.println("pagesize:"+i);
					System.out.println(JSON.toJSONString(list));
				}
			}else if(api.getApiVersion() == ApiVersion.V3){
				int i=0;
				int tatol=0;
				boolean hasNext=true;
				while (hasNext) {
					System.out.println("第"+i+"页数据");
					List<Commit> list= api.getCommitsApi().getCommits(3,"master",null,new Date(),i,100);
					System.out.println(JSON.toJSONString(list));
					if(list.isEmpty()){
						hasNext=false;
					}
					i++;
					tatol+=list.size();
				}
				System.out.println("一共"+tatol+"条数据");
				List<Commit> list= api.getCommitsApi().getCommits(1,"master",null,new Date(),0,100);
				System.out.println(JSON.toJSONString(list));
			}
			
			
			
		} catch (GitLabApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

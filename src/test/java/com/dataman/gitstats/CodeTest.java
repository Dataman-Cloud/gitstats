package com.dataman.gitstats;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;

// import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


public class CodeTest {

	public static void main(String[] args) {

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
				Aggregation.project().and("createdAt").substring(0, 10).as("day").and("$addRow").as("addRow")
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
		
	}
}

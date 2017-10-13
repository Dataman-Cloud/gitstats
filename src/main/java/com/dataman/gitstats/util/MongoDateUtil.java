package com.dataman.gitstats.util;

import java.util.Calendar;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;

/**
 * @ClassName: MongoDateUtil
 * @Description:记录一些mongo的时间操作 
 * @author liuqing 
 * @date 2017年10月12日 上午10:25:47 
 * @Copyright © 2017北京数人科技有限公司
 */
public class MongoDateUtil {
	
	public static String YYYYMMDD = "%Y-%m-%d";
	public static String YYYYMM = "%Y-%m";
	public static String YYYYW ="%Y-%U";
	/**
	 * @method getFormat(获取时间的格式 分组需要使用)
	 * @return String
	 * @author liuqing
	 * @date 2017年10月12日 下午2:53:43
	 */
	public static String getFormat(String DateCode){
		if(StringUtils.isEmpty(DateCode)){
			return YYYYMMDD;
		}
		String ret=YYYYMMDD;
		switch (DateCode) {
			case "YMD":
				ret=YYYYMMDD;
			break;
			case "YM":
				ret=YYYYMM;
			break;
			case "YW":
				ret=YYYYW;
			break;
			default:
				ret=YYYYMMDD;
			break;
		}
		return ret;
	}
	/**
	 * @method getOperation(组建管道查询的match)
	 * @return MatchOperation
	 * @author liuqing
	 * @date 2017年10月12日 下午2:53:37
	 */
	public static MatchOperation getOperation(String branchId,String DateCode,Integer lastDate){
		Criteria query=new Criteria();
		if (!StringUtils.isEmpty(branchId)) {
			query.and("branchId").is(branchId);
		}
		if(lastDate!=null){
			Calendar cal=Calendar.getInstance();
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 1);
			cal.set(Calendar.MILLISECOND, 0);
			if(StringUtils.isEmpty(DateCode)){
				cal.add(Calendar.DAY_OF_YEAR, -lastDate);
			}else{
				switch (DateCode) {
					case "YMD":
						cal.add(Calendar.DAY_OF_YEAR, -lastDate);
					break;
					case "YM":
						cal.add(Calendar.MONTH, -lastDate);
					break;
					case "YW":
						cal.add(Calendar.WEEK_OF_YEAR, -lastDate);
					break;
					default:
						cal.add(Calendar.DAY_OF_YEAR, -lastDate);
					break;
				}
			}
			query.and("createdAt").gte(cal.getTime());
		}
		return Aggregation.match(query);
	}
	
	
}

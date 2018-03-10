package com.dataman.gitstats.service;

import com.alibaba.fastjson.JSON;
import com.dataman.gitstats.exception.ApiResultCode;
import com.dataman.gitstats.exception.BusinessException;
import com.dataman.gitstats.param.AddGroupParam;
import com.dataman.gitstats.param.AddProjectParam;
import com.dataman.gitstats.param.ProjectWithBranches;
import com.dataman.gitstats.po.CommitStatsPo;
import com.dataman.gitstats.po.GroupStats;
import com.dataman.gitstats.po.ProjectBranchStats;
import com.dataman.gitstats.po.ProjectStats;
import com.dataman.gitstats.repository.CommitStatsRepository;
import com.dataman.gitstats.repository.GroupStatsRepository;
import com.dataman.gitstats.repository.ProjectBranchStatsRepository;
import com.dataman.gitstats.repository.ProjectRepository;
import com.dataman.gitstats.util.ClassUitl;
import com.dataman.gitstats.util.GitlabUtil;
import com.dataman.gitstats.vo.*;
import com.sun.xml.internal.bind.v2.TODO;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.ProjectHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	ProjectBranchStatsRepository projectBranchStatsRepository;

	@Autowired
	private GroupStatsRepository groupStatsRepository;
	@Autowired
	CommitStatsRepository commitStatsRepository;
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	AsyncTask asyncTask;

	@Autowired
	GitlabUtil gitlabUtil;

	@Autowired
	private WebHookService webHookService;
	
	SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-DD");
	
	//释放开启日期自动补充
	@Value("${gitstats.autofill}")
	private boolean autofill;
	
	/**
	 * @method addProject(添加需要统计的项目)
	 * @return int
	 * @author liuqing
	 * @throws Exception 
	 * @date 2017年9月19日 下午3:12:39
	 */
	public int addGroup(AddGroupParam param,String webHookUrl) throws Exception{
		int SUCCESS=0,EXISTED=1,NOTEXIST=2;
		Calendar cal=Calendar.getInstance();
		//验证是否 存在于 gitlab
		GitLabApi gitLabApi= gitlabUtil.getGitLabApi(param.getAccountid());
		Group group=gitLabApi.getGroupApi().getGroup(param.getGroupId());
		if(group==null){
			return NOTEXIST;
		}
		List<Project> projects=gitLabApi.getGroupApi().getProjects(param.getGroupId());
		//TODO(注意，这里没有考虑group下面有subgroup的情况，因为gitlab api的相关接口把subgroup和getprojects分开了，如果太平有这种情况需修复该处)
		//TODO	subgroups接口是只在gitlab10.3版本以后才有的新功能，一般公司都没用到这么高的版本，后期再升级
		if(param.getInclude()!=null){
			for(ProjectWithBranches includeProject:param.getInclude()){
				if(!projects.stream().anyMatch(project -> project.getName().equals(includeProject.getName()))){
					throw new BusinessException(ApiResultCode.ERR_PARAMETER.getCode(),ApiResultCode.ERR_PARAMETER.getMessage()+"the include's project "+includeProject.getName()+"不存在");
				}
				if(includeProject.getBranches()!=null){
					List<Branch> branches=gitLabApi.getRepositoryApi().getBranches(gitLabApi.getProjectApi().getProject(group.getFullPath(),includeProject.getName()).getId());
					for(String branchName:includeProject.getBranches()){
						if(!branches.stream().anyMatch(branch -> branch.getName().equals(branchName))){
							throw new BusinessException(ApiResultCode.ERR_PARAMETER.getCode(),ApiResultCode.ERR_PARAMETER.getMessage()+"the include's project "+includeProject.getName()+"  "+branchName+"不存在");
						}
					}
				}

			}
		}
		GroupStats groupStats=groupStatsRepository.findOne(group.getWebUrl()+"_"+param.getGroupId());
		if(groupStats==null){
			try{
				groupStats=new GroupStats();
				ClassUitl.copyProperties(param,groupStats);
				if(groupStats.getViewName()==null){
					groupStats.setViewName(group.getFullName());
				}
				groupStats.setName(group.getFullName());
				groupStats.setFullPath(group.getFullPath());
				groupStats.setPath(group.getPath());
				groupStats.setId(param.getAccountid()+"_"+param.getGroupId());
				groupStats.setWebUrl(group.getWebUrl());
				groupStats.setStatus(0);
				groupStats.setTotalAddRow(0);
				groupStats.setTotalDelRow(0);
				groupStats.setTotalRow(0);
				groupStats.setCreatedate(cal.getTime());
				groupStats.setLastupdate(cal.getTime());
				groupStats=groupStatsRepository.insert(groupStats);
				asyncTask.initGroupStats(groupStats);
				addWebHook(groupStats,gitLabApi,webHookUrl);
			}catch (Exception e){
				logger.error("初始化项目分支异常：",e);
			}

		}else{
			return EXISTED;
		}
		return SUCCESS;
	}

	private void addWebHook(GroupStats groupStats,GitLabApi gitLabApi,String webHookUrl) throws Exception {
		if(groupStats.getInclude()!=null){
			List<String> includeProjects=groupStats.getInclude().stream().map(projectWithBranches -> projectWithBranches.getName()).collect(Collectors.toList());
			List<Project> allProjects=gitLabApi.getGroupApi().getProjects(groupStats.getGroupId());
			for(Project project:allProjects){
				if(includeProjects.contains(project.getName())){
					if(!checkWebhookStats(groupStats.getAccountid(),project.getId(),webHookUrl)){
						webHookService.addGitlabPushEventWebHook(groupStats.getAccountid(),project.getId(),webHookUrl);
					}
				}
			}
		}else{
			if(groupStats.getExclude()!=null){
				List<Project> allProjects=gitLabApi.getGroupApi().getProjects(groupStats.getGroupId());
				List<String> excludeProjects=groupStats.getExclude().stream()
						.filter(projectWithBranches -> projectWithBranches.getBranches()!=null)
						.map(projectWithBranches -> projectWithBranches.getName()).collect(Collectors.toList());
				for(Project project:allProjects){
					if(!excludeProjects.contains(project.getName())){
						if(!checkWebhookStats(groupStats.getAccountid(),project.getId(),webHookUrl)){
							webHookService.addGitlabPushEventWebHook(groupStats.getAccountid(),project.getId(),webHookUrl);
						}
					}
				}
			}


		}
	}

	boolean checkWebhookStats(String aid,int pid,String webHookUrl) throws GitLabApiException{
		boolean flag=false;
		GitLabApi gitLabApi= gitlabUtil.getGitLabApi(aid);
		List<ProjectHook> hooks= gitLabApi.getProjectApi().getHooks(pid);
		if(!hooks.isEmpty()){
			flag=hooks.stream().anyMatch(hook->hook.getUrl().equals(webHookUrl));
		}
		return flag;
	}
	
	public ProjectBranchStats findProjectBranchStatsById(String id){
		return projectBranchStatsRepository.findOne(id);
	}

	public List<GroupStats> getAllGroupStats(String limit){
		if (StringUtils.isEmpty(limit)) {
			return groupStatsRepository.findAll();
		}else{
			return groupStatsRepository.findByStatus(1);
		}
		
	}

	public void deleteGroupStats(String id){
		groupStatsRepository.delete(id);
		projectRepository.deleteByGroupId(id);
		projectBranchStatsRepository.deleteByGroupId(id);
		commitStatsRepository.deleteByGroupId(id);
	}

	/**
	 * @method: resetProjectBranchStats
	 * @Description	重置项目，清空数据并重新初始化
	 * @author biancl
	 * @date 2017-10-15 10:17
	 * @param [id]
	 * @return void
	 */
	public void resetGroupStats(String id,String webHookUrl) throws Exception {
		GroupStats groupStats=groupStatsRepository.findOne(id);
		try{
			projectRepository.deleteByGroupId(id);
			projectBranchStatsRepository.deleteByGroupId(id);
			commitStatsRepository.deleteByGroupId(id);//删除已同步的commit
			groupStats.setStatus(0);
			groupStats.setTotalRow(0);
			groupStats.setTotalDelRow(0);
			groupStats.setTotalAddRow(0);
			groupStatsRepository.save(groupStats);
			asyncTask.initGroupStats(groupStats);
			addWebHook(groupStats,gitlabUtil.getGitLabApi(groupStats.getAccountid()),webHookUrl);
		}catch (Exception e){
			logger.error("重置项目分支异常：",e);
			groupStats.setStatus(-1);
			groupStatsRepository.save(groupStats);
		}

	}
	/**
	 * @method getProAllAuthorName(获取项目所有提交者)
	 * @return String[]
	 * @author liuqing
	 * @date 2017年10月18日 下午4:42:06
	 */
	public List<CommitStatsVo> getProAllAuthorName(String id){
		return proGroupByAuthorName(id);
	}

	public void modifyGroupStats(AddGroupParam param,String webHookUrl) throws Exception{
		GroupStats groupStats=groupStatsRepository.findOne(param.getId());
		if(param.getId()==null || groupStats==null){
			throw new Exception("参数错误");
		}
		if(groupStats.getAccountid().equals(param.getAccountid())&&
				param.getGroupId().equals(groupStats.getGroupId())&&
				JSON.toJSONString(param.getInclude()).equals(JSON.toJSONString(groupStats.getInclude()))&&
				JSON.toJSONString(param.getExclude()).equals(JSON.toJSONString(groupStats.getExclude()))){
			ClassUitl.copyProperties(param,groupStats);
			groupStats.setLastupdate(new Date());
			groupStatsRepository.save(groupStats);
		}else{
			deleteGroupStats(param.getId());
			addGroup(param, webHookUrl);
		}

	}

//	public ProjectBranchStats findProjectBranchStatsByProjectIdAndBranch(String projectId,String branch){
//		return projectBranchStatsRepository.findByProjectidAndBranch(projectId,branch);
//	}

//	public int delProject(String id){
//		int SUCCESS=0;
//		projectRepository.delete(id);
//		projectBranchStatsRepository.deleteByProjectid(id);
//		commitStatsRepository.deleteByProid(id);
//		return SUCCESS;
//	}
	/**
	 * @method showStatsByUser(根据用户查询统计)
	 * @return ProjectBranchStatsVo
	 * @author liuqing
	 * @date 2017年10月11日 下午4:53:27
	 */
	public GroupStatsVo showStatsByUser(String groupId,MatchOperation match) throws Exception{
		GroupStats groupStats=groupStatsRepository.findOne(groupId);
		GroupStatsVo groupStatsVo=ClassUitl.copyProperties(groupStats, new GroupStatsVo());
		groupStatsVo.setData(statsByUser(groupStats,match));
		return groupStatsVo;
	}
	/**
	 * @method showStatsByDay(根据天查询统计)
	 * @return ProjectBranchStatsVo
	 * @author liuqing
	 * @date 2017年10月11日 下午4:53:57
	 */
	public GroupStatsVo showStatsByDay(String groupId,String format,MatchOperation match) throws Exception{
		GroupStats groupStats=groupStatsRepository.findOne(groupId);
		GroupStatsVo pbsv=ClassUitl.copyProperties(groupStats, new GroupStatsVo());
		pbsv.setData(statsByDay(groupStats,format,match));
		return pbsv;
	}
	/**
	 * @method showStatsByUserAndDay(根据用户和天查询统计)
	 * @return Object
	 * @author liuqing
	 * @date 2017年10月11日 下午4:54:28
	 */
	public GroupStatsPlusVo showStatsByUserAndDay(String groupId,String format,MatchOperation match) throws Exception{
		GroupStats groupStats=groupStatsRepository.findOne(groupId);
		GroupStatsPlusVo groupStatsPlusVo=ClassUitl.copyProperties(groupStats, new GroupStatsPlusVo());
		groupStatsPlusVo.setData(statsByUserByDay(groupStats,format,match));
		return groupStatsPlusVo;
	}
	
	/**
	 * @method showStatsByDayAndUser(根据用户和天查询统计)
	 * @return Object
	 * @author liuqing
	 * @date 2017年10月11日 下午4:54:28
	 */
	public GroupStatsPlusVo showStatsByDayAndUser(String groupId,String format,MatchOperation match) throws Exception{
		GroupStats groupStats=groupStatsRepository.findOne(groupId);
		GroupStatsPlusVo groupStatsPlusVo=ClassUitl.copyProperties(groupStats, new GroupStatsPlusVo());
		groupStatsPlusVo.setData(statsByDayByUser(groupStats,format,match));
		return groupStatsPlusVo;
	}
	//	db.getCollection('commitStatsPo').aggregate([
	//	 {$match:{"proid" : "3ec34ce6b6f64d90afba8009a31a504e","branch" : "dev"}},
	//	 {$group:{_id:"$authorName",addrow:{$sum:"$addRow"},removerow:{$sum:"$removeRow"},commit:{$sum:1}}}
	//	 ,{$sort:{addrow:-1}}
	//	 ])
	public List<CommitStatsVo> statsByUser(GroupStats groupStats){
		List<CommitStatsVo> list=null;
		Aggregation agg= Aggregation.newAggregation(
			Aggregation.match(new Criteria("groupId").is(groupStats.getId())),
			Aggregation.group(Fields.fields("$authorName")).sum("$addRow").as("addrow").sum("$removeRow").as("removerow").count().as("commit"),
			Aggregation.sort(Direction.DESC, "addrow")
		);
		AggregationResults<CommitStatsVo> ret=  mongoTemplate.aggregate(agg, CommitStatsPo.class, CommitStatsVo.class);
		list =ret.getMappedResults();
		list=list.stream().filter(commitStatsVo->groupStats.getExcludeUser().indexOf(commitStatsVo.get_id())==-1).collect(Collectors.toList());
		return list;
	}
	public List<CommitStatsVo> statsByUser(GroupStats groupStats,MatchOperation match){
		List<CommitStatsVo> list=null;
		Aggregation agg= Aggregation.newAggregation(
				match,
				Aggregation.match(new Criteria("groupId").is(groupStats.getId())),
				Aggregation.group(Fields.fields("$authorName")).sum("$addRow").as("addrow").sum("$removeRow").as("removerow").count().as("commit"),
				Aggregation.sort(Direction.DESC, "addrow")
		);
		AggregationResults<CommitStatsVo> ret=  mongoTemplate.aggregate(agg, CommitStatsPo.class, CommitStatsVo.class);
		list =ret.getMappedResults();
		list=list.stream().filter(commitStatsVo->groupStats.getExcludeUser().indexOf(commitStatsVo.get_id())==-1).collect(Collectors.toList());
		return list;
	}
	
//	db.getCollection('commitStatsPo').aggregate([  
// 	    {$match:{"proid" : "3ec34ce6b6f64d90afba8009a31a504e","branch" : "dev"}}
//	    ,{$project : { day : {$substr: ["$createdAt", 0, 10] },addRow : 1,removeRow:1 }}
//	    ,{$group   : { _id : "$day",  addRow : { $sum : "$addRow" } ,removeRow : { $sum : "$removeRow" },commit:{$sum:1}}}   
//	    ,{$sort :{_id : -1}}
//	])
	public List<CommitStatsVo> statsByDay(GroupStats groupStats,String dataformat,MatchOperation match){
		List<CommitStatsVo> list=null;
		Aggregation agg= Aggregation.newAggregation(
			match,
			Aggregation.project().and("createdAt").dateAsFormattedString(dataformat).as("day").and("$addRow").as("addRow").and("$removeRow").as("removeRow"),
			Aggregation.group(Fields.fields("day")).sum("addRow").as("addrow").sum("removeRow").as("removerow").count().as("commit"),
			Aggregation.sort(Direction.ASC, "_id")
		);
		System.out.println(agg.toString());
		
		AggregationResults<CommitStatsVo> ret=  mongoTemplate.aggregate(agg, CommitStatsPo.class, CommitStatsVo.class);
		list =ret.getMappedResults();
		if(autofill){
			//补充 缺省日期数据  补充 到 当前
			list =complementDay(list,null);
		}
		return list;
	}
	
//	db.getCollection('commitStatsPo').aggregate([  
//	{$match:{"proid" : "0670ed0736be4101aab4d679a997977e","branch" : "develop"}}
//	,{$project : { day : {$substr: ["$createdAt", 0, 10] },addRow : 1,removeRow:1 ,authorName:1}}
//	,{$group   : { _id : {authorName: "$authorName",day : "$day"},  addRow : { $sum : "$addRow" } ,removeRow : { $sum : "$removeRow" },commit:{$sum:1}}}   
//	,{$project : {_id :0, authorName : '$_id.authorName', dayinfo : {day : '$_id.day', addRow:'$addRow',removeRow:'$removeRow',commit:'$commit'}}}
//	,{$group:{_id:"$authorName", days:{$push:"$dayinfo"}, addrow: {$sum: "$dayinfo.addRow"},removeRow: {$sum: "$dayinfo.removeRow"},commit: {$sum: "$dayinfo.commit"}}}
//	,{$sort : { addrow : -1 }}
//	])
	public List<StatsByUserByDayVo> statsByUserByDay(GroupStats groupStats,String dataformat,MatchOperation match){
		List<StatsByUserByDayVo> list=null;
		Aggregation agg= Aggregation.newAggregation(
				match,
				Aggregation.project().and("createdAt").dateAsFormattedString(dataformat).as("day").and("$addRow").as("addRow")
				.and("$removeRow").as("removeRow").and("$authorName").as("authorName"),
				Aggregation.sort(Direction.DESC, "day"),
				Aggregation.group(Fields.fields("authorName","day")).sum("addRow").as("addRow").sum("removeRow").as("removeRow")
					.count().as("commit"),
				Aggregation.project().and("_id.authorName").as("authorName").and("dayinfo")
					.nested(Aggregation.bind("_id","_id.day").and("addrow","addRow").and("removerow","removeRow").and("commit","commit")),
				Aggregation.group(Fields.fields("authorName")).sum("dayinfo.addrow").as("addrow")
					.sum("dayinfo.removerow").as("removerow").sum("dayinfo.commit").as("commit")
					.push("dayinfo").as("data"),
				Aggregation.sort(Direction.DESC, "addrow")
			);
		System.out.println(agg.toString());
		AggregationResults<StatsByUserByDayVo> ret=  mongoTemplate.aggregate(agg, CommitStatsPo.class, StatsByUserByDayVo.class);
		list =ret.getMappedResults();
		return list;
	}
	
	
//	db.getCollection('commitStatsPo').aggregate([  
//	{$match:{"branchId" : "59dad6c94cd6f7d2103db236"}}
//	,{$project : { day : {$substr: ["$createdAt", 0, 10] },addRow : 1,removeRow:1 ,authorName:1}}
//	,{$group   : { _id : {authorName: "$authorName",day : "$day"},  addRow : { $sum : "$addRow" } ,removeRow : { $sum : "$removeRow" },commit:{$sum:1}}}   
//	,{$project : { day : '$_id.day', usersinfo : {user : '$_id.authorName', addRow:'$addRow',removeRow:'$removeRow',commit:'$commit'}}}
//	,{$group:{_id:"$day", users:{$push:"$usersinfo"}, addrow: {$sum: "$usersinfo.addRow"},removeRow: {$sum: "$usersinfo.removeRow"},commit: {$sum: "$usersinfo.commit"}}}
//	,{$sort : { _id : -1 }}
//	])
	public List<StatsByUserByDayVo> statsByDayByUser(GroupStats groupStats,String dataformat,MatchOperation match){
		List<StatsByUserByDayVo> list=null;
		Aggregation agg= Aggregation.newAggregation(
				match,
				Aggregation.project().and("createdAt").dateAsFormattedString(dataformat).as("day").and("$addRow").as("addRow")
				.and("$removeRow").as("removeRow").and("$authorName").as("authorName"),
				Aggregation.group(Fields.fields("authorName","day")).sum("addRow").as("addRow").sum("removeRow").as("removeRow")
					.count().as("commit"),
				Aggregation.sort(Direction.DESC, "addRow"),
				Aggregation.project().and("_id.day").as("day").and("usersinfo")
					.nested(Aggregation.bind("_id","_id.authorName").and("addrow","addRow").and("removerow","removeRow").and("commit","commit")),
				Aggregation.group(Fields.fields("day")).sum("usersinfo.addrow").as("addrow")
					.sum("usersinfo.removerow").as("removerow").sum("usersinfo.commit").as("commit")
					.push("usersinfo").as("data"),
				Aggregation.sort(Direction.ASC, "_id")
			);
		System.out.println(agg.toString());
		AggregationResults<StatsByUserByDayVo> ret=  mongoTemplate.aggregate(agg, CommitStatsPo.class, StatsByUserByDayVo.class);
		list =ret.getMappedResults();
		return list;
	}
//	db.getCollection('commitStatsPo').aggregate([  
//	{$project : { day : {$substr: ["$createdAt", 0, 10] },addRow : 1,removeRow:1 ,branchId:1}}
//	,{$group   : { _id : {branchId: "$branchId",day : "$day"},  addRow : { $sum : "$addRow" } ,removeRow : { $sum : "$removeRow" },commit:{$sum:1}}}   
//	,{$sort : { addRow : -1 }}
//	     ,{$project : { day : '$_id.day', proinfo : {branchId : '$_id.branchId', addRow:'$addRow',removeRow:'$removeRow',commit:'$commit'}}}
//	     ,{$group:{_id:"$day", pros:{$push:"$proinfo"}, addrow: {$sum: "$proinfo.addRow"},removeRow: {$sum: "$proinfo.removeRow"},commit: {$sum: "$proinfo.commit"}}}
//	,{$sort : { _id : -1 }}
//	])
	public List<StatsByUserByDayVo> statsByDay(String dataformat,MatchOperation match){
		List<StatsByUserByDayVo> list=null;
		Aggregation agg= Aggregation.newAggregation(
				match,
				Aggregation.project().and("createdAt").dateAsFormattedString(dataformat).as("day").and("$addRow").as("addRow")
				.and("$removeRow").as("removeRow").and("$branchId").as("branchId"),
				Aggregation.group(Fields.fields("branchId","day")).sum("addRow").as("addRow").sum("removeRow").as("removeRow")
					.count().as("commit"),
				Aggregation.sort(Direction.DESC, "addRow"),
				Aggregation.project().and("_id.day").as("day").and("proinfo")
					.nested(Aggregation.bind("_id","_id.branchId").and("addrow","addRow").and("removerow","removeRow").and("commit","commit")),
				Aggregation.group(Fields.fields("day")).sum("proinfo.addrow").as("addrow")
					.sum("proinfo.removerow").as("removerow").sum("proinfo.commit").as("commit")
					.push("proinfo").as("data"),
				Aggregation.sort(Direction.ASC, "_id")
		);
		System.out.println(agg.toString());
		AggregationResults<StatsByUserByDayVo> ret=  mongoTemplate.aggregate(agg, CommitStatsPo.class, StatsByUserByDayVo.class);
		list =ret.getMappedResults();
		return list;
	}
	
//	db.getCollection('commitStatsPo').aggregate([
//	    {$group:{_id:'$authorName'}}
//	])
	public List<CommitStatsVo> proGroupByAuthorName(String groupId){
		List<CommitStatsVo> list=null;
		Aggregation agg= Aggregation.newAggregation(
				Aggregation.match(new Criteria("groupId").is(groupId)),
				Aggregation.group(Fields.fields("authorName"))
		);
		AggregationResults<CommitStatsVo> ret=  mongoTemplate.aggregate(agg, CommitStatsPo.class, CommitStatsVo.class);
		list =ret.getMappedResults();
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

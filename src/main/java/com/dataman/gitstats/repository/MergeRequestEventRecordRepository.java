package com.dataman.gitstats.repository;

import com.dataman.gitstats.po.MergeRequestEventRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MergeRequestEventRecordRepository extends MongoRepository<MergeRequestEventRecord,String>{

	
}

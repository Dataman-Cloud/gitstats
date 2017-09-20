package com.dataman.gitstats.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dataman.gitstats.po.PushEventRecord;

public interface PushEventRecordRepository extends MongoRepository<PushEventRecord,String>{

	
}

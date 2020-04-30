package org.tdds.service;

import java.util.List;

import org.tdds.entity.Activities;
import org.tdds.entity.Recipients;

public interface RecipientsService {

	int save(Recipients entity);

	List<Activities> findAllActivities();

	Recipients getOne(String currentSubsidiesTel);

	int update(Recipients entity);

}

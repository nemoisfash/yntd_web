package org.tdds.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdds.entity.Activities;
import org.tdds.entity.Recipients;
import org.tdds.mapper.RecipientsMapper;
import org.tdds.service.RecipientsService;

@Service
public class RecipientsServiceImpl implements RecipientsService{

	@Autowired
	RecipientsMapper daoRecipients;
	
	@Override
	public int save(Recipients entity) {
		return daoRecipients.insert(entity);
	}

	@Override
	public List<Activities> findAllActivities() {
		return daoRecipients.findAllActivities();
	}

	@Override
	public Recipients getOne(String currentSubsidiesTel) {
		Recipients entity = new Recipients();
		entity.setTel(currentSubsidiesTel);
		return daoRecipients.selectOne(entity);
	}

	@Override
	public int update(Recipients entity) {
		return daoRecipients.updateByPrimaryKey(entity);
	}

}

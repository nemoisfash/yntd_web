package org.tdds.mapper;

import java.util.List;

import org.tdds.entity.Activities;
import org.tdds.entity.Recipients;

import net.chenke.playweb.support.mybatis.DynaMapper;

public interface RecipientsMapper extends DynaMapper<Recipients>{

	List<Activities> findAllActivities();

}

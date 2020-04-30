package org.tdds.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author neo
 * 活动领取人
 */
@Table(name = "test_recipients")
public class Recipients {
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "tel")
	private String tel;
	
	@Column(name = "recom_tel")
	private String recomTel;
	
	@Column(name = "activities_id")
	private Long activitiesId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getRecomTel() {
		return recomTel;
	}

	public void setRecomTel(String recomTel) {
		this.recomTel = recomTel;
	}

	public Long getActivitiesId() {
		return activitiesId;
	}

	public void setActivitiesId(Long activitiesId) {
		this.activitiesId = activitiesId;
	}
}

package com.krishagni.catissueplus.core.administrative.domain;

import java.util.HashSet;
import java.util.Set;

import com.krishagni.catissueplus.core.administrative.domain.User;
import com.krishagni.catissueplus.core.biospecimen.domain.BaseEntity;
import com.krishagni.catissueplus.core.common.util.Status;
import com.krishagni.catissueplus.core.common.util.Utility;

public class UserGroup extends BaseEntity {
	private String name;

	private String description;

	private Institute institute;

	private String activityStatus;

	private Set<User> users = new HashSet<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Institute getInstitute() {
		return institute;
	}

	public void setInstitute(Institute institute) {
		this.institute = institute;
	}

	public String getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public void update(UserGroup other) {
		setName(other.getName());
		setDescription(other.getDescription());
		setInstitute(other.getInstitute());
		setUsers(other.getUsers());
		setActivityStatus(other.getActivityStatus());

		if (Status.isDisabledStatus(other.getActivityStatus())) {
			setName(Utility.getDisabledValue(getName(), 64));
		}
	}

	public void delete() {
		setActivityStatus(Status.ACTIVITY_STATUS_DISABLED.getStatus());
		setName(Utility.getDisabledValue(getName(), 64));
	}
}

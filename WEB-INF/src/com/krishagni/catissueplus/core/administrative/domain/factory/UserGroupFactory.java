package com.krishagni.catissueplus.core.administrative.domain.factory;

import com.krishagni.catissueplus.core.administrative.domain.UserGroup;
import com.krishagni.catissueplus.core.administrative.events.UserGroupDetail;

public interface UserGroupFactory {
	UserGroup createGroup(UserGroupDetail input);

	UserGroup createGroup(UserGroupDetail input, UserGroup existing);
}

package com.krishagni.catissueplus.core.common.service;

import com.krishagni.catissueplus.core.common.domain.MessageLog;

public interface MessageLogService {

	MessageLog getMessageLog(Long id);

	void registerHandler(String extAppName, MessageHandler handler);

	void retryPendingMessages();
}

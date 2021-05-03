package com.krishagni.catissueplus.core.common.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.context.MessageSource;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;
import com.krishagni.catissueplus.core.common.service.ConfigurationService;
import com.krishagni.catissueplus.core.common.service.TemplateService;

public class TemplateServiceImpl implements TemplateService {
	private VelocityEngine velocityEngine;
	
	private MessageSource messageSource;

	private ConfigurationService cfgSvc;
	
	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setCfgSvc(ConfigurationService cfgSvc) {
		this.cfgSvc = cfgSvc;
	}

	@Override
	public String render(String templateName, Map<String, Object> props) {
		try {
			props.put("locale", Locale.getDefault());
			props.put("messageSource", messageSource);
			props.put("dateFmt", new SimpleDateFormat(getDateFmt()));
			props.put("dateOnlyFmt", new SimpleDateFormat(getDateOnlyFormat()));
			return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateName, props);
		} catch (VelocityException ex) {
			throw OpenSpecimenException.serverError(ex);
		}
	}

	@Override
	public void render(String templateName, Map<String, Object> props, File output) {
		try (FileWriter writer = new FileWriter(output)) {
			props.put("locale", Locale.getDefault());
			props.put("messageSource", messageSource);
			props.put("dateFmt", new SimpleDateFormat(getDateFmt()));
			props.put("dateOnlyFmt", new SimpleDateFormat(getDateOnlyFormat()));
			VelocityEngineUtils.mergeTemplate(velocityEngine, templateName, props, writer);
		} catch (Exception e) {
			throw OpenSpecimenException.serverError(e);
		}
	}

	private String getDateFmt() {
		return cfgSvc.getDateFormat() + " " + cfgSvc.getTimeFormat();
	}

	private String getDateOnlyFormat() {
		return cfgSvc.getDateFormat();
	}
}

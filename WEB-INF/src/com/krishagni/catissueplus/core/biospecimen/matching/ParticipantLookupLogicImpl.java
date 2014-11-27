
package com.krishagni.catissueplus.core.biospecimen.matching;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.krishagni.catissueplus.core.biospecimen.domain.Participant;
import com.krishagni.catissueplus.core.biospecimen.events.MatchParticipantEvent;
import com.krishagni.catissueplus.core.biospecimen.events.ParticipantDetail;
import com.krishagni.catissueplus.core.biospecimen.events.ParticipantMatchedEvent;
import com.krishagni.catissueplus.core.biospecimen.repository.DaoFactory;
import com.krishagni.catissueplus.core.biospecimen.repository.ParticipantDao;

public class ParticipantLookupLogicImpl implements ParticipantLookupLogic {

	private DaoFactory daoFactory;

	public DaoFactory getDaoFactory() {
		return daoFactory;
	}

	public void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	@Override
	public ParticipantMatchedEvent getMatchingParticipants(MatchParticipantEvent req) {
		ParticipantDao dao = daoFactory.getParticipantDao();
		ParticipantDetail participant = req.getParticipantDetail();
		
		if (StringUtils.isNotBlank(participant.getEmpi())) {
			Participant matched = dao.getByEmpi(participant.getEmpi());
			if (matched != null) {
				List<ParticipantDetail> result = ParticipantDetail.from(Collections.singletonList(matched));
				return ParticipantMatchedEvent.ok("empi", result);
			}
		}
		
		if (StringUtils.isNotBlank(participant.getSsn())) {
			Participant matched = dao.getBySsn(participant.getSsn());
			if (matched != null) {
				List<ParticipantDetail> result = ParticipantDetail.from(Collections.singletonList(matched));
				return ParticipantMatchedEvent.ok("ssn", result);
			}
		}
		
		if (participant.getMedicalIdentifierList() != null) {
			List<Participant> matched = dao.getByPmis(participant.getMedicalIdentifierList());
			if (matched != null && !matched.isEmpty()) {
				return ParticipantMatchedEvent.ok("pmi", ParticipantDetail.from(matched));
			}
		}
		
		if (StringUtils.isNotBlank(participant.getLastName()) && participant.getBirthDate() != null) {
			List<Participant> matched = dao.getByLastNameAndBirthDate(participant.getLastName(), participant.getBirthDate());
			if (matched != null && !matched.isEmpty()) {
				return ParticipantMatchedEvent.ok("lnameAndDob", ParticipantDetail.from(matched));
			}
		}
		
		return ParticipantMatchedEvent.ok("none", Collections.<ParticipantDetail>emptyList());		
	}
}

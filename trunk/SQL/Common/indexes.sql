/*Indexes for Participant*/
CREATE INDEX INDX_PARTICIPANT_LNAME ON CATISSUE_PARTICIPANT (LAST_NAME);
CREATE INDEX INDX_PARTICIPANT_FNAME ON CATISSUE_PARTICIPANT (FIRST_NAME);
CREATE INDEX INDX_PARTICIPANT_MNAME ON CATISSUE_PARTICIPANT (MIDDLE_NAME);
CREATE INDEX INDX_PARTICIPANT_BDATE ON CATISSUE_PARTICIPANT (BIRTH_DATE);
CREATE INDEX INDX_PARTICIPANT_DDATE ON CATISSUE_PARTICIPANT (DEATH_DATE);
CREATE INDEX INDX_PARTICIPANT_VSTATUS ON CATISSUE_PARTICIPANT (VITAL_STATUS);
CREATE INDEX INDX_PARTICIPANT_GENDER ON CATISSUE_PARTICIPANT (GENDER);
CREATE INDEX INDX_PARTICIPANT_GENOTYPE ON CATISSUE_PARTICIPANT (GENOTYPE);
CREATE INDEX INDX_RACE_NAME ON CATISSUE_RACE (RACE_NAME);
CREATE INDEX INDX_PARTICIPANT_ETHNICITY ON CATISSUE_PARTICIPANT (ETHNICITY);
/*CREATE INDEX INDX_PARTICIPANT_SSN ON CATISSUE_PARTICIPANT (SOCIAL_SECURITY_NUMBER);*/
CREATE INDEX INDX_PART_MEDICAL_RECNO ON CATISSUE_PART_MEDICAL_ID (MEDICAL_RECORD_NUMBER);


/* Indexes for Collection Protocol*/
CREATE INDEX INDX_USER_LNAME ON CATISSUE_USER (LAST_NAME);
CREATE INDEX INDX_USER_FNAME ON CATISSUE_USER (FIRST_NAME);
/*CREATE INDEX INDX_SPECIMEN_PROTOCOL_TITLE ON CATISSUE_SPECIMEN_PROTOCOL (TITLE);*/
CREATE INDEX INDX_SP_PR_SHORT_TITLE ON CATISSUE_SPECIMEN_PROTOCOL (SHORT_TITLE);
CREATE INDEX INDX_SP_PR_IRB_ID ON CATISSUE_SPECIMEN_PROTOCOL (IRB_IDENTIFIER);
CREATE INDEX INDX_SP_PR_START_DATE ON CATISSUE_SPECIMEN_PROTOCOL (START_DATE);
CREATE INDEX INDX_SP_PR_END_DATE ON CATISSUE_SPECIMEN_PROTOCOL (END_DATE);

CREATE INDEX INDX_COLL_PROT_REGID ON CATISSUE_COLL_PROT_REG (PROTOCOL_PARTICIPANT_ID);
CREATE INDEX INDX_COLL_PROT_REG_DATE ON CATISSUE_COLL_PROT_REG (REGISTRATION_DATE);


/* Indexes for Specimen Collection Group*/
/*CREATE INDEX INDX_SP_COL_GR_NAME ON CATISSUE_SPECIMEN_COLL_GROUP (NAME);*/
CREATE INDEX INDX_SP_COL_GR_DIGNOSIS ON CATISSUE_SPECIMEN_COLL_GROUP (CLINICAL_DIAGNOSIS);
CREATE INDEX INDX_SP_COL_GR_CLIN_STATUS ON CATISSUE_SPECIMEN_COLL_GROUP (CLINICAL_STATUS);
CREATE INDEX INDX_CLIN_REPO_PATH_NO ON CATISSUE_CLINICAL_REPORT (SURGICAL_PATHOLOGICAL_NUMBER);
/*CREATE INDEX INDX_SITE_NAME ON CATISSUE_SITE (NAME);*/
CREATE INDEX INDX_COLPROTO_EVNT_CAL ON CATISSUE_COLL_PROT_EVENT (STUDY_CALENDAR_EVENT_POINT);

/* Indexes for Specimen*/
CREATE INDEX INDX_CATISSUE_SPECIMEN_CLASS ON CATISSUE_SPECIMEN (SPECIMEN_CLASS);
CREATE INDEX INDX_CATISSUE_SPECIMEN_TYPE ON CATISSUE_SPECIMEN (TYPE);
CREATE INDEX INDX_CATISSUE_SPECIMEN_PATH ON CATISSUE_SPECIMEN (PATHOLOGICAL_STATUS);
CREATE INDEX INDX_CATISSUE_SPECIMEN_CONC ON CATISSUE_SPECIMEN (CONCENTRATION);
CREATE INDEX INDX_CATISSUE_SPECIMEN_AVQTY ON CATISSUE_SPECIMEN (AVAILABLE_QUANTITY);
CREATE INDEX INDX_CATISSUE_SPECIMEN_QTY ON CATISSUE_SPECIMEN (QUANTITY);

/*CREATE INDEX INDX_CATISSUE_BIOHAZARD_NAME ON CATISSUE_BIOHAZARD (NAME);*/
CREATE INDEX INDX_CATISSUE_BIOHAZARD_TYPE ON CATISSUE_BIOHAZARD (TYPE);

CREATE INDEX INDX_CATISSUE_SP_CHAR_TSITE ON CATISSUE_SPECIMEN_CHAR (TISSUE_SITE);
CREATE INDEX INDX_CATISSUE_SP_CHAR_TSIDE ON CATISSUE_SPECIMEN_CHAR (TISSUE_SIDE);

/*Indexes for Event parameters*/
CREATE INDEX INDX_CATISSUE_CELLEVT_REVNCP ON CATISSUE_CELL_SPE_REVIEW_PARAM (NEOPLASTIC_CELLULARITY_PER);
CREATE INDEX INDX_CATISSUE_CELLEVT_REVVCP ON CATISSUE_CELL_SPE_REVIEW_PARAM (VIABLE_CELL_PERCENTAGE);
CREATE INDEX INDX_CATISSUE_SPEVT_TIMESTP ON CATISSUE_SPECIMEN_EVENT_PARAM (EVENT_TIMESTAMP);
CREATE INDEX INDX_CATISSUE_SPEVT_SPID ON CATISSUE_SPECIMEN_EVENT_PARAM (SPECIMEN_ID);
CREATE INDEX INDX_CATISSUE_IOEVT_STATUS ON CATISSUE_IN_OUT_EVENT_PARAM (STORAGE_STATUS);
CREATE INDEX INDX_CATISSUE_COLEVT_PROC ON CATISSUE_COLL_EVENT_PARAM (COLLECTION_PROCEDURE);
CREATE INDEX INDX_CATISSUE_COLEVT_CONT ON CATISSUE_COLL_EVENT_PARAM (CONTAINER);

CREATE INDEX INDX_CATISSUE_COLEVT_RSN ON CATISSUE_DISPOSAL_EVENT_PARAM (REASON);
CREATE INDEX INDX_CATISSUE_EMBEVT_MEDI ON CATISSUE_EMBEDDED_EVENT_PARAM (EMBEDDING_MEDIUM);
CREATE INDEX INDX_CATISSUE_FIXEVT_TYPE ON CATISSUE_FIXED_EVENT_PARAM (FIXATION_TYPE);
CREATE INDEX INDX_CATISSUE_FIXEVT_MIN ON CATISSUE_FIXED_EVENT_PARAM (DURATION_IN_MINUTES);
CREATE INDEX INDX_CATISSUE_FLDEVT_CELL ON CATISSUE_FLUID_SPE_EVENT_PARAM (CELL_COUNT);
CREATE INDEX INDX_CATISSUE_FROZEVT_METH ON CATISSUE_FROZEN_EVENT_PARAM (METHOD);

CREATE INDEX INDX_CATISSUE_MOLEVT_METH ON CATISSUE_MOL_SPE_REVIEW_PARAM (GEL_IMAGE_URL);
CREATE INDEX INDX_CATISSUE_MOLEVT_LANE ON CATISSUE_MOL_SPE_REVIEW_PARAM (LANE_NUMBER);
CREATE INDEX INDX_CATISSUE_MOLEVT_GEL ON CATISSUE_MOL_SPE_REVIEW_PARAM (GEL_NUMBER);
CREATE INDEX INDX_CATISSUE_MOLEVT_ABS1 ON CATISSUE_MOL_SPE_REVIEW_PARAM (ABSORBANCE_AT_260);
CREATE INDEX INDX_CATISSUE_MOLEVT_ABS2 ON CATISSUE_MOL_SPE_REVIEW_PARAM (ABSORBANCE_AT_280);
CREATE INDEX INDX_CATISSUE_MOLEVT_RAT ON CATISSUE_MOL_SPE_REVIEW_PARAM (RATIO_28S_TO_18S);
CREATE INDEX INDX_CATISSUE_PROCEVT_URL ON CATISSUE_PROCEDURE_EVENT_PARAM (URL);
CREATE INDEX INDX_CATISSUE_PROCEVT_NAME ON CATISSUE_PROCEDURE_EVENT_PARAM (NAME);

CREATE INDEX INDX_CATISSUE_RECEVT_QTY ON CATISSUE_RECEIVED_EVENT_PARAM (RECEIVED_QUALITY);
CREATE INDEX INDX_CATISSUE_TISEVT_NCEL ON CATISSUE_TIS_SPE_EVENT_PARAM (NEOPLASTIC_CELLULARITY_PER);
CREATE INDEX INDX_CATISSUE_TISEVT_NPER ON CATISSUE_TIS_SPE_EVENT_PARAM (NECROSIS_PERCENTAGE);
CREATE INDEX INDX_CATISSUE_TISEVT_LPER ON CATISSUE_TIS_SPE_EVENT_PARAM (LYMPHOCYTIC_PERCENTAGE);
CREATE INDEX INDX_CATISSUE_TISEVT_CPER ON CATISSUE_TIS_SPE_EVENT_PARAM (TOTAL_CELLULARITY_PERCENTAGE);
CREATE INDEX INDX_CATISSUE_TISEVT_HQTY ON CATISSUE_TIS_SPE_EVENT_PARAM (HISTOLOGICAL_QUALITY);

CREATE INDEX INDX_CATISSUE_TRANEVT_FDIM1 ON CATISSUE_TRANSFER_EVENT_PARAM (FROM_POSITION_DIMENSION_ONE);
CREATE INDEX INDX_CATISSUE_TRANEVT_FDIM2 ON CATISSUE_TRANSFER_EVENT_PARAM (FROM_POSITION_DIMENSION_TWO);
CREATE INDEX INDX_CATISSUE_TRANEVT_TDIM1 ON CATISSUE_TRANSFER_EVENT_PARAM (TO_POSITION_DIMENSION_ONE);
CREATE INDEX INDX_CATISSUE_TRANEVT_TDIM2 ON CATISSUE_TRANSFER_EVENT_PARAM (TO_POSITION_DIMENSION_TWO);
CREATE INDEX INDX_CATISSUE_TRANEVT_TSTOR ON CATISSUE_TRANSFER_EVENT_PARAM (TO_STORAGE_CONTAINER_ID);
CREATE INDEX INDX_CATISSUE_TRANEVT_FSTOR ON CATISSUE_TRANSFER_EVENT_PARAM (FROM_STORAGE_CONTAINER_ID);


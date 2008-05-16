/**
 * <p>Title: Specimen Class>
 * <p>Description:  A single unit of tissue, body fluid, or derivative 
 * biological macromolecule that is collected or created from a Participant </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.catissuecore.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.wustl.catissuecore.actionForm.AliquotForm;
import edu.wustl.catissuecore.actionForm.CollectionEventParametersForm;
import edu.wustl.catissuecore.actionForm.CreateSpecimenForm;
import edu.wustl.catissuecore.actionForm.NewSpecimenForm;
import edu.wustl.catissuecore.actionForm.ReceivedEventParametersForm;
import edu.wustl.catissuecore.actionForm.SpecimenForm;
import edu.wustl.catissuecore.bean.ConsentBean;
import edu.wustl.catissuecore.util.EventsUtil;
import edu.wustl.catissuecore.util.SearchUtil;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.catissuecore.util.global.Utility;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.bizlogic.IActivityStatus;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.util.MapDataParser;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * A single unit of tissue, body fluid, or derivative biological macromolecule 
 * that is collected or created from a Participant
 * @hibernate.class table="CATISSUE_SPECIMEN"
 * @hibernate.discriminator column="SPECIMEN_CLASS" 
 */
public class Specimen extends AbstractDomainObject implements Serializable, IActivityStatus
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -905954650055370532L;

	/**
	 * System generated unique id.
	 */
	protected Long id;

	/**
	 * Type of specimen. e.g. Serum, Plasma, Blood, Fresh Tissue etc.
	 */
	protected String type;

	/**
	 * Is this specimen still physically available in the tissue bank?
	 */
	protected Boolean available;

	/**
	 * Reference to dimensional position one of the specimen in Storage Container.
	 */
	protected Integer positionDimensionOne;

	/**
	 * Reference to dimensional position two of the specimen in Storage Container.
	 */
	protected Integer positionDimensionTwo;

	/**
	 * Barcode assigned to the specimen.
	 */
	protected String barcode;

	/**
	 * Comment on specimen.
	 */
	protected String comment;

	/**
	 * Defines whether this Specimen record can be queried (Active) 
	 * or not queried (Inactive) by any actor.
	 */
	protected String activityStatus;

	/**
	 * Parent specimen from which this specimen is derived. 
	 */
	protected Specimen parentSpecimen;

	/**
	 * Collection of attributes of a Specimen that renders it potentially harmful to a User.
	 */
	protected Collection<Biohazard> biohazardCollection = new HashSet<Biohazard>();

	//Change for API Search   --- Ashwin 04/10/2006
	/**
	 * A physically discreet container that is used to store a specimen  e.g. Box, Freezer etc.
	 */
	protected StorageContainer storageContainer;

	/**
	 * Collection of Specimen Event Parameters associated with this specimen. 
	 */
	protected Collection<SpecimenEventParameters> specimenEventCollection = new HashSet<SpecimenEventParameters>();

	/**
	 * Collection of children specimens derived from this specimen. 
	 */
	protected Collection<Specimen> childrenSpecimen = new HashSet<Specimen>();

	/**
	 * Collection of a pre-existing, externally defined id associated with a specimen.
	 */
	protected Collection<ExternalIdentifier> externalIdentifierCollection = new HashSet();

	//Change for API Search   --- Ashwin 04/10/2006
	/**
	 * An event that results in the collection of one or more specimen from a participant.
	 */
	protected AbstractSpecimenCollectionGroup specimenCollectionGroup;

	//Change for API Search   --- Ashwin 04/10/2006
	/**
	 * The combined anatomic state and pathological disease classification of a specimen.
	 */
	protected SpecimenCharacteristics specimenCharacteristics;

	/**
	 * A boolean variable which contains a true value if this specimen object is an aliquot 
	 * else it contains false value.
	 */
	//    protected Boolean isAliquot = Boolean.FALSE;
	/**
	 * Histoathological character of specimen.
	 * e.g. Non-Malignant, Malignant, Non-Malignant Diseased, Pre-Malignant.
	 */
	protected String pathologicalStatus;

	/**
	 * A historical information about the specimen i.e. whether the specimen is a new specimen
	 * or a derived specimen or an aliquot.
	 */
	protected String lineage;

	/**
	 * A label name of this specimen.
	 */
	protected String label;

	//Change for API Search   --- Ashwin 04/10/2006
	/**
	 * The quantity of a specimen.
	 */
	protected Quantity initialQuantity;

	//Change for API Search   --- Ashwin 04/10/2006
	/**
	 * The available quantity of a specimen.
	 */
	protected Quantity availableQuantity;

	protected transient boolean isParentChanged = false;

	private transient int noOfAliquots;

	private transient Map aliqoutMap = new HashMap();

	protected transient boolean disposeParentSpecimen = false;

	//-----For Consent Tracking. Ashish 21/11/06
	/**
	 * The consent tier status for multiple participants for a particular specimen.
	 */
	protected Collection<ConsentTierStatus> consentTierStatusCollection;

	//Mandar 15-jan-07 
	/*
	 * To perform operation based on withdraw button clicked.
	 * Default No Action to allow normal behaviour. 
	 */
	protected String consentWithdrawalOption = Constants.WITHDRAW_RESPONSE_NOACTION;

	//Mandar 23-jan-07 
	/*
	 * To apply changes to child specimen based on consent status changes.
	 * Default Apply none to allow normal behaviour. 
	 */
	protected String applyChangesTo = Constants.APPLY_NONE;

	protected String collectionStatus;
	protected Boolean isCollectionProtocolRequirement = null;

	public Boolean getIsCollectionProtocolRequirement()
	{
		return isCollectionProtocolRequirement;
	}

	public void setIsCollectionProtocolRequirement(Boolean collectionProtocolRequirement)
	{
		this.isCollectionProtocolRequirement = collectionProtocolRequirement;
	}

	/**
	 * @return the consentTierStatusCollection
	 * @hibernate.collection-one-to-many class="edu.wustl.catissuecore.domain.ConsentTierStatus" lazy="true" cascade="save-update"
	 * @hibernate.set name="consentTierStatusCollection" table="CATISSUE_CONSENT_TIER_STATUS"
	 * @hibernate.collection-key column="SPECIMEN_ID"
	 */
	public Collection<ConsentTierStatus> getConsentTierStatusCollection()
	{
		return consentTierStatusCollection;
	}

	/**
	 * @param consentTierStatusCollection the consentTierStatusCollection to set
	 */
	public void setConsentTierStatusCollection(Collection consentTierStatusCollection)
	{
		this.consentTierStatusCollection = consentTierStatusCollection;
	}

	//-----Consent Tracking end.

	/**
	 * Name: Sachin Lale 
	 * Bug ID: 3835
	 * Patch ID: 3835_1
	 * See also: 1-4 
	 * Description : Addeed createdOn field for derived and aliqut Specimen.
	 */
	protected Date createdOn;

	public Specimen()
	{
		
	}

	//Constructor
	public Specimen(AbstractActionForm form) throws AssignDataException
	{
		this();
		setAllValues(form);
	}

	/**
	 * Returns the system generated unique id.
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30" 
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_SPECIMEN_SEQ"
	 * @return the system generated unique id.
	 * @see #setId(Long)
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the system generated unique id.
	 * @param id the system generated unique id.
	 * @see #getId()
	 * */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Returns the type of specimen. e.g. Serum, Plasma, Blood, Fresh Tissue etc.
	 * @hibernate.property name="type" type="string" column="TYPE" length="50"
	 * @return The type of specimen. e.g. Serum, Plasma, Blood, Fresh Tissue etc.
	 * @see #setType(String)
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Sets the type of specimen. e.g. Serum, Plasma, Blood, Fresh Tissue etc.
	 * @param type The type of specimen. e.g. Serum, Plasma, Blood, Fresh Tissue etc.
	 * @see #getType()
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * Returns true if this specimen still physically available 
	 * in the tissue bank else returns false.
	 * @hibernate.property name="available" type="boolean" column="AVAILABLE"
	 * @return true if this specimen still physically available 
	 * in the tissue bank else returns false.
	 * @see #setAvailable(Boolean)
	 */
	public Boolean getAvailable()
	{
		return available;
	}

	/**
	 * Sets true if this specimen still physically available 
	 * in the tissue bank else returns false.
	 * @param available true if this specimen still physically available else false.
	 * @see #getAvailable()
	 */
	public void setAvailable(Boolean available)
	{
		this.available = available;
	}

	/**
	 * Returns the reference to dimensional position one of the specimen in Storage Container.
	 * @hibernate.property name="positionDimensionOne" type="int" column="POSITION_DIMENSION_ONE" length="30"  
	 * @return the reference to dimensional position one of the specimen in Storage Container.
	 * @see #setPositionDimensionOne(Integer)
	 */
	public Integer getPositionDimensionOne()
	{
		return positionDimensionOne;
	}

	/**
	 * Sets the reference to dimensional position one of the specimen in Storage Container.
	 * @param positionDimensionOne the reference to dimensional position one of the specimen 
	 * in Storage Container.
	 * @see #getPositionDimensionOne()
	 */
	public void setPositionDimensionOne(Integer positionDimensionOne)
	{
		this.positionDimensionOne = positionDimensionOne;
	}

	/**
	 * Returns the reference to dimensional position two of the specimen in Storage Container.
	 * @hibernate.property name="positionDimensionTwo" type="int" column="POSITION_DIMENSION_TWO" length="50"  
	 * @return the reference to dimensional position two of the specimen in Storage Container.
	 * @see #setPositionDimensionOne(Integer)
	 */
	public Integer getPositionDimensionTwo()
	{
		return positionDimensionTwo;
	}

	/**
	 * Sets the reference to dimensional position two of the specimen in Storage Container.
	 * @param positionDimensionTwo the reference to dimensional position two of the specimen 
	 * in Storage Container.
	 * @see #getPositionDimensionTwo()
	 */
	public void setPositionDimensionTwo(Integer positionDimensionTwo)
	{
		this.positionDimensionTwo = positionDimensionTwo;
	}

	/**
	 * Returns the barcode assigned to the specimen.
	 * @hibernate.property name="barcode" type="string" column="BARCODE" length="255" unique="true"
	 * @return the barcode assigned to the specimen.
	 * @see #setBarcode(String)
	 */
	public String getBarcode()
	{
		return barcode;
	}

	/**
	 * Sets the barcode assigned to the specimen.
	 * @param barCode the barcode assigned to the specimen.
	 * @see #getBarcode()
	 */
	public void setBarcode(String barcode)
	{
		this.barcode = barcode;
		if ("".equals(barcode))
		{
			this.barcode = null;
		}

	}

	/**
	 * Returns the comments on the specimen.
	 * @hibernate.property name="comment" type="string" column="COMMENTS" length="2000"
	 * @return the comments on the specimen.
	 * @see #setComment(String)
	 */
	public String getComment()
	{
		return comment;
	}

	/**
	 * Sets the comment on the specimen.
	 * @param comments The comments to set.
	 * @see #getComment()
	 */
	public void setComment(String comment)
	{
		this.comment = comment;
	}

	/**
	 * Returns whether this Specimen record can be queried (Active) or not queried (Inactive) by any actor.
	 * @hibernate.property name="activityStatus" type="string" column="ACTIVITY_STATUS" length="50"
	 * @return "Active" if this Specimen record can be queried or "Inactive" if cannot be queried.
	 * @see #setActivityStatus(String)
	 */
	public String getActivityStatus()
	{
		return activityStatus;
	}

	/**
	 * Sets whether this Specimen record can be queried (Active) or not queried (Inactive) by any actor.
	 * @param activityStatus "Active" if this Specimen record can be queried else "Inactive".
	 * @see #getActivityStatus()
	 */
	public void setActivityStatus(String activityStatus)
	{
		this.activityStatus = activityStatus;
	}

	/**
	 * Returns the parent specimen from which this specimen is derived.
	 * @hibernate.many-to-one column="PARENT_SPECIMEN_ID"
	 * class="edu.wustl.catissuecore.domain.Specimen" constrained="true"
	 * @return the parent specimen from which this specimen is derived.
	 * @see #setParentSpecimen(SpecimenNew)
	 */
	public Specimen getParentSpecimen()
	{
		return parentSpecimen;
	}

	/**
	 * Sets the parent specimen from which this specimen is derived.
	 * @param parentSpecimen the parent specimen from which this specimen is derived.
	 * @see #getParentSpecimen()
	 */
	public void setParentSpecimen(Specimen parentSpecimen)
	{
		this.parentSpecimen = parentSpecimen;
	}

	/**
	 * Returns the collection of attributes of a Specimen 
	 * that renders it potentially harmful to a User.
	 * @hibernate.set name="biohazardCollection" table="CATISSUE_SPECIMEN_BIOHZ_REL"
	 * cascade="none" inverse="false" lazy="false"
	 * @hibernate.collection-key column="SPECIMEN_ID"
	 * @hibernate.collection-many-to-many class="edu.wustl.catissuecore.domain.Biohazard" column="BIOHAZARD_ID"
	 * @return the collection of attributes of a Specimen 
	 * that renders it potentially harmful to a User.
	 * @see #setBiohazardCollection(Set)
	 */
	public Collection<Biohazard> getBiohazardCollection()
	{
		return biohazardCollection;
	}

	/**
	 * Sets the collection of attributes of a Specimen 
	 * that renders it potentially harmful to a User.
	 * @param biohazardCollection the collection of attributes of a Specimen 
	 * that renders it potentially harmful to a User.
	 * @see #getBiohazardCollection()
	 */
	public void setBiohazardCollection(Collection biohazardCollection)
	{
		this.biohazardCollection = biohazardCollection;
	}

	/**
	 * Returns the collection of Specimen Event Parameters associated with this specimen.  
	 * @hibernate.set name="specimenEventCollection" table="CATISSUE_SPECIMEN_EVENT"
	 * cascade="save-update" inverse="true" lazy="false"
	 * @hibernate.collection-key column="SPECIMEN_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.catissuecore.domain.SpecimenEventParameters"
	 * @return the collection of Specimen Event Parameters associated with this specimen.
	 * @see #setSpecimenEventCollection(Set)
	 */
	public Collection<SpecimenEventParameters> getSpecimenEventCollection()
	{
		return specimenEventCollection;
	}

	/**
	 * Sets the collection of Specimen Event Parameters associated with this specimen.
	 * @param specimenEventCollection the collection of Specimen Event Parameters 
	 * associated with this specimen.
	 * @see #getSpecimenEventCollection()
	 */
	public void setSpecimenEventCollection(Collection specimenEventCollection)
	{
		this.specimenEventCollection = specimenEventCollection;
	}

	/**
	 * Returns the collection of children specimens derived from this specimen.
	 * @hibernate.set name="childrenSpecimen" table="CATISSUE_SPECIMEN"
	 * cascade="save-update" inverse="true" lazy="false"
	 * @hibernate.collection-key column="PARENT_SPECIMEN_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.catissuecore.domain.Specimen"
	 * @return the collection of children specimens derived from this specimen.
	 * @see #setChildrenSpecimen(Set)
	 */
	public Collection<Specimen> getChildrenSpecimen()
	{
		return childrenSpecimen;
	}

	/**
	 * Sets the collection of children specimens derived from this specimen.
	 * @param childrenSpecimen the collection of children specimens 
	 * derived from this specimen.
	 * @see #getChildrenSpecimen()
	 */
	public void setChildrenSpecimen(Collection<Specimen> childrenSpecimen)
	{
		this.childrenSpecimen = childrenSpecimen;
	}

	/**
	 * Returns the physically discreet container that is used to store a specimen  e.g. Box, Freezer etc.
	 * @hibernate.many-to-one column="STORAGE_CONTAINER_IDENTIFIER" 
	 * class="edu.wustl.catissuecore.domain.StorageContainer" constrained="true"
	 * @return the physically discreet container that is used to store a specimen  e.g. Box, Freezer etc.
	 * @see #setStorageContainer(StorageContainer)
	 */
	public StorageContainer getStorageContainer()
	{
		return storageContainer;
	}

	/**
	 * Sets the physically discreet container that is used to store a specimen  e.g. Box, Freezer etc.
	 * @param storageContainer the physically discreet container that is used to store a specimen  
	 * e.g. Box, Freezer etc.
	 * @see #getStorageContainer()
	 */
	public void setStorageContainer(StorageContainer storageContainer)
	{
		this.storageContainer = storageContainer;
	}

	/**
	 * Returns the collection of a pre-existing, externally defined id associated with a specimen.
	 * @hibernate.set name="externalIdentifierCollection" table="CATISSUE_EXTERNAL_IDENTIFIER"
	 * cascade="save-update" inverse="true" lazy="false"
	 * @hibernate.collection-key column="SPECIMEN_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.catissuecore.domain.ExternalIdentifier"
	 * @return the collection of a pre-existing, externally defined id associated with a specimen.
	 * @see #setExternalIdentifierCollection(Set)
	 */
	public Collection<ExternalIdentifier> getExternalIdentifierCollection()
	{
		return externalIdentifierCollection;
	}

	/**
	 * Sets the collection of a pre-existing, externally defined id 
	 * associated with a specimen.
	 * @param externalIdentifierCollection the collection of a pre-existing, 
	 * externally defined id associated with a specimen.
	 * @see #getExternalIdentifierCollection()
	 */
	public void setExternalIdentifierCollection(Collection externalIdentifierCollection)
	{
		this.externalIdentifierCollection = externalIdentifierCollection;
	}

	/**
	 * Returns the event that results in the collection of one or more specimen from a participant.
	 * @hibernate.many-to-one column="SPECIMEN_COLLECTION_GROUP_ID"
	 * class="edu.wustl.catissuecore.domain.SpecimenCollectionGroup" constrained="true"
	 * @return the event that results in the collection of one or more specimen from a participant.
	 * @see #setSpecimenCollectionGroup(SpecimenCollectionGroup)
	 */
	public AbstractSpecimenCollectionGroup getSpecimenCollectionGroup()
	{
		return specimenCollectionGroup;
	}

	/**
	 * Sets the event that results in the collection of one or more specimen from a participant.
	 * @param specimenCollectionGroup the event that results in the collection of one or more 
	 * specimen from a participant.
	 * @see #getSpecimenCollectionGroup()
	 */
	public void setSpecimenCollectionGroup(AbstractSpecimenCollectionGroup specimenCollectionGroup)
	{
		this.specimenCollectionGroup = specimenCollectionGroup;
	}

	/**
	 * Returns the combined anatomic state and pathological disease classification of a specimen.
	 * @hibernate.many-to-one column="SPECIMEN_CHARACTERISTICS_ID" 
	 * class="edu.wustl.catissuecore.domain.SpecimenCharacteristics" constrained="true"
	 * @return the combined anatomic state and pathological disease classification of a specimen.
	 * @see #setSpecimenCharacteristics(SpecimenCharacteristics)
	 */
	public SpecimenCharacteristics getSpecimenCharacteristics()
	{
		return specimenCharacteristics;
	}

	/**
	 * Sets the combined anatomic state and pathological disease classification of a specimen.
	 * @param specimenCharacteristics the combined anatomic state and pathological disease 
	 * classification of a specimen.
	 * @see #getSpecimenCharacteristics()
	 */
	public void setSpecimenCharacteristics(SpecimenCharacteristics specimenCharacteristics)
	{
		this.specimenCharacteristics = specimenCharacteristics;
	}

	public boolean isParentChanged()
	{
		return isParentChanged;
	}

	public void setParentChanged(boolean isParentChanged)
	{
		this.isParentChanged = isParentChanged;
	}

	/**
	 * This function Copies the data from an NewSpecimenForm object to a Specimen object.
	 * @param specimenForm A formbean object containing the information about the Specimen.  
	 * */
	public void setAllValues(IValueObject valueObject) throws AssignDataException
	{
		//Change for API Search   --- Ashwin 04/10/2006
		AbstractActionForm abstractForm = (AbstractActionForm) valueObject;
		if (SearchUtil.isNullobject(storageContainer))
		{
			storageContainer = null;
		}

		//Change for API Search   --- Ashwin 04/10/2006
		if (SearchUtil.isNullobject(specimenCollectionGroup))
		{
			specimenCollectionGroup = new SpecimenCollectionGroup();
		}

		//Change for API Search   --- Ashwin 04/10/2006
		if (SearchUtil.isNullobject(specimenCharacteristics))
		{
			specimenCharacteristics = new SpecimenCharacteristics();
		}

		//Change for API Search   --- Ashwin 04/10/2006
		if (SearchUtil.isNullobject(initialQuantity))
		{
			initialQuantity = new Quantity();
		}

		//Change for API Search   --- Ashwin 04/10/2006
		if (SearchUtil.isNullobject(availableQuantity))
		{
			availableQuantity = new Quantity();
		}

		try
		{
			if (abstractForm.isAddOperation())
			{
				this.isCollectionProtocolRequirement = new Boolean(false);
			}

			if (abstractForm instanceof AliquotForm)
			{
				AliquotForm form = (AliquotForm) abstractForm;
				// Dispose parent specimen Bug 3773
				this.setDisposeParentSpecimen(form.getDisposeParentSpecimen());
				Validator validator = new Validator();

				this.aliqoutMap = form.getAliquotMap();
				this.noOfAliquots = Integer.parseInt(form.getNoOfAliquots());
				this.parentSpecimen = new Specimen();
				this.collectionStatus = Constants.COLLECTION_STATUS_COLLECTED;
				this.lineage = Constants.ALIQUOT;

				/**            
				 * Patch ID: 3835_1_2
				 * See also: 1_1 to 1_5
				 * Description : Set createdOn date for aliquot.  
				 */

				this.createdOn = Utility.parseDate(form.getCreatedDate(), Constants.DATE_PATTERN_MM_DD_YYYY);

				if (!validator.isEmpty(form.getSpecimenLabel())) // TODO
				{
					parentSpecimen.setLabel(form.getSpecimenLabel());
					parentSpecimen.setId(new Long(form.getSpecimenID()));
				}
				else if (!validator.isEmpty(form.getBarcode()))
				{
					parentSpecimen.setId(new Long(form.getSpecimenID()));
					parentSpecimen.setBarcode(form.getBarcode());
				}
			}
			else
			{
				String qty = ((SpecimenForm) abstractForm).getQuantity();
				if (qty != null && qty.trim().length() > 0)
					this.initialQuantity = new Quantity(((SpecimenForm) abstractForm).getQuantity());
				else
					this.initialQuantity = new Quantity("0");
				this.label = ((SpecimenForm) abstractForm).getLabel();

				if (abstractForm.isAddOperation())
				{
					this.availableQuantity = new Quantity(this.initialQuantity);
				}
				else
				{
					this.availableQuantity = new Quantity(((SpecimenForm) abstractForm).getAvailableQuantity());
				}

				Validator validator = new Validator();
				if (abstractForm instanceof NewSpecimenForm)
				{
					NewSpecimenForm form = (NewSpecimenForm) abstractForm;
					/**For Migration Start**/
					//					This case is handled in Biz logic.
					//					if(form.getSpecimenCollectionGroupName()!=null&&!form.getSpecimenCollectionGroupName().trim().equals(""))
					//					{
					//						form.setSpecimenCollectionGroupId(Utility.getSCGId(form.getSpecimenCollectionGroupName().trim()));
					//					}
					/**For Migration End**/
					this.activityStatus = form.getActivityStatus();
					if (form.isAddOperation())
					{
						this.collectionStatus = Constants.COLLECTION_STATUS_COLLECTED;
					}
					else
					{
						this.collectionStatus = form.getCollectionStatus();
					}
					
					if (!validator.isEmpty(form.getBarcode()))
						this.barcode = form.getBarcode();
					else
						this.barcode = null;

					this.comment = form.getComments();
					this.type = form.getType();

					if (form.isAddOperation())
					{
						this.available = new Boolean(true);
					}
					else
					{
						this.available = new Boolean(form.isAvailable());
					}

					//in case of edit
					if (!form.isAddOperation())
					{
						//specimen is a new specimen  
						if (parentSpecimen == null)
						{
							String parentSpecimenId = form.getParentSpecimenId();
							// specimen created from another specimen
							if (parentSpecimenId != null && !parentSpecimenId.trim().equals("") && Long.parseLong(parentSpecimenId) > 0)
							{
								isParentChanged = true;
							}
						}
						else
						//specimen created from another specimen
						{
							if (!parentSpecimen.getLabel().equalsIgnoreCase(form.getParentSpecimenName()))
							{
								isParentChanged = true;
							}
						}

						/**                         
						 * Patch ID: 3835_1_3
						 * See also: 1_1 to 1_5
						 * Description : Set createdOn date in edit mode for new specimen 
						 */
						this.createdOn = Utility.parseDate(form.getCreatedDate(), Constants.DATE_PATTERN_MM_DD_YYYY);
					}

					Logger.out.debug("isParentChanged " + isParentChanged);

					//Setting the SpecimenCharacteristics
					this.pathologicalStatus = form.getPathologicalStatus();
					specimenCharacteristics.tissueSide = form.getTissueSide();
					specimenCharacteristics.tissueSite = form.getTissueSite();

					//Getting the Map of External Identifiers
					Map extMap = form.getExternalIdentifier();

					MapDataParser parser = new MapDataParser("edu.wustl.catissuecore.domain");

					Collection extCollection = parser.generateData(extMap);
					this.externalIdentifierCollection = extCollection;

					Map bioMap = form.getBiohazard();
					Logger.out.debug("PRE FIX MAP " + bioMap);
					bioMap = fixMap(bioMap);
					Logger.out.debug("POST FIX MAP " + bioMap);

					//Getting the Map of Biohazards
					parser = new MapDataParser("edu.wustl.catissuecore.domain");
					Collection bioCollection = parser.generateData(bioMap);

					Logger.out.debug("BIO-COL : " + bioCollection);

					this.biohazardCollection = bioCollection;

					//Mandar : autoevents 14-july-06 start

					if (form.isAddOperation())
					{
						Logger.out.debug("Setting Collection event in specimen domain object");
						//seting collection event values
						CollectionEventParametersForm collectionEvent = new CollectionEventParametersForm();
						collectionEvent.setCollectionProcedure(form.getCollectionEventCollectionProcedure());
						collectionEvent.setComments(form.getCollectionEventComments());
						collectionEvent.setContainer(form.getCollectionEventContainer());
						collectionEvent.setTimeInHours(form.getCollectionEventTimeInHours());
						collectionEvent.setTimeInMinutes(form.getCollectionEventTimeInMinutes());
						collectionEvent.setDateOfEvent(form.getCollectionEventdateOfEvent());
						collectionEvent.setUserId(form.getCollectionEventUserId());
						collectionEvent.setOperation(form.getOperation());

						CollectionEventParameters collectionEventParameters = new CollectionEventParameters();
						collectionEventParameters.setAllValues(collectionEvent);

						collectionEventParameters.setSpecimen(this);
						Logger.out.debug("Before specimenEventCollection.size(): " + specimenEventCollection.size());
						specimenEventCollection.add(collectionEventParameters);
						Logger.out.debug("After specimenEventCollection.size(): " + specimenEventCollection.size());

						Logger.out.debug("...14-July-06... : CollectionEvent set");

						Logger.out.debug("Setting Received event in specimen domain object");
						//setting received event values
						ReceivedEventParametersForm receivedEvent = new ReceivedEventParametersForm();
						receivedEvent.setComments(form.getReceivedEventComments());
						receivedEvent.setDateOfEvent(form.getReceivedEventDateOfEvent());
						receivedEvent.setReceivedQuality(form.getReceivedEventReceivedQuality());
						receivedEvent.setUserId(form.getReceivedEventUserId());
						receivedEvent.setTimeInMinutes(form.getReceivedEventTimeInMinutes());
						receivedEvent.setTimeInHours(form.getReceivedEventTimeInHours());
						receivedEvent.setOperation(form.getOperation());

						ReceivedEventParameters receivedEventParameters = new ReceivedEventParameters();
						receivedEventParameters.setAllValues(receivedEvent);
						receivedEventParameters.setSpecimen(this);

						/**                         
						 * Patch ID: 3835_1_4
						 * See also: 1_1 to 1_5
						 * Description :createdOn should be collection event date for new specimen. 
						 */
						this.createdOn = Utility.parseDate(form.getCollectionEventdateOfEvent(), Constants.DATE_PATTERN_MM_DD_YYYY);

						Logger.out.debug("Before specimenEventCollection.size(): " + specimenEventCollection.size());
						specimenEventCollection.add(receivedEventParameters);
						Logger.out.debug("After specimenEventCollection.size(): " + specimenEventCollection.size());

						Logger.out.debug("...14-July-06... : ReceivedEvent set");

					}

					//Mandar : autoevents 14-july-06 end

					if (form.isAddOperation())
					{
						if (this.storageContainer == null)
						{
							this.storageContainer = new StorageContainer();
						}
						if (form.getStContSelection() == 1)
						{
							this.storageContainer = null;
							this.positionDimensionOne = null;
							this.positionDimensionTwo = null;
						}
						if (form.getStContSelection() == 2)
						{
							long stContainerId = Long.parseLong(form.getStorageContainer());
							this.storageContainer.setId(stContainerId);
							this.positionDimensionOne = new Integer(form.getPositionDimensionOne());
							this.positionDimensionTwo = new Integer(form.getPositionDimensionTwo());
						}
						else if (form.getStContSelection() == 3)
						{
							this.storageContainer.setName(form.getSelectedContainerName());
							if (form.getPos1() != null && !form.getPos1().trim().equals("") && form.getPos2() != null
									&& !form.getPos2().trim().equals(""))
							{
								this.positionDimensionOne = new Integer(form.getPos1());
								this.positionDimensionTwo = new Integer(form.getPos2());
							}

						}
					}
					else
					{
						if (!validator.isEmpty(form.getSelectedContainerName()))
						{
							//this.storageContainer = new StorageContainer();
							this.storageContainer.setName(form.getSelectedContainerName());
							this.positionDimensionOne = new Integer(form.getPositionDimensionOne());
							this.positionDimensionTwo = new Integer(form.getPositionDimensionTwo());
						}
						else
						{
							this.storageContainer = null;
							this.positionDimensionOne = null;
							this.positionDimensionTwo = null;
						}
					}

					if (form.isParentPresent())
					{
						/*lazy change parent Specimen link is set to false so not required to set*/
						/*
						parentSpecimen = new CellSpecimen();
						parentSpecimen.setId(new Long(form.getParentSpecimenId()));
						parentSpecimen.setLabel(form.getParentSpecimenName());
						*/
					}
					else
					{
						parentSpecimen = null;
						//specimenCollectionGroup = null;
						this.specimenCollectionGroup.setId(new Long(form.getSpecimenCollectionGroupId()));

						//this.specimenCollectionGroup.setGroupName(form.getSpecimenCollectionGroupName());
						/*lazy change */
						/*IBizLogic iBizLogic = BizLogicFactory.getInstance().getBizLogic(Constants.DEFAULT_BIZ_LOGIC);

						
						List scgList = iBizLogic.retrieve(SpecimenCollectionGroup.class.getName(), "name", form.getSpecimenCollectionGroupName());
						
						
						if (!scgList.isEmpty())
						{
							this.specimenCollectionGroup = (SpecimenCollectionGroup) scgList.get(0);
						}
						if(parentSpecimen.getSpecimenCollectionGroup()!=null)
						{
							this.specimenCollectionGroup = parentSpecimen.getSpecimenCollectionGroup();
						}*/

					}
				}
				else if (abstractForm instanceof CreateSpecimenForm)
				{
					CreateSpecimenForm form = (CreateSpecimenForm) abstractForm;

					this.activityStatus = form.getActivityStatus();
					this.collectionStatus = Constants.COLLECTION_STATUS_COLLECTED;

					if (!validator.isEmpty(form.getBarcode()))
						this.barcode = form.getBarcode();
					else
						this.barcode = null;

					this.comment = form.getComments();
					//this.positionDimensionOne = new Integer(form.getPositionDimensionOne());
					//this.positionDimensionTwo = new Integer(form.getPositionDimensionTwo());
					this.type = form.getType();

					/**                  
					 * Patch ID: 3835_1_5
					 * See also: 1_1 to 1_5
					 * Description : Set createdOn date for derived specimen . 
					 */
					this.createdOn = Utility.parseDate(form.getCreatedDate(), Constants.DATE_PATTERN_MM_DD_YYYY);

					if (form.isAddOperation())
					{
						this.available = new Boolean(true);
					}
					else
					{
						this.available = new Boolean(form.isAvailable());
					}

					//this.storageContainer.setId(new Long(form.getStorageContainer()));
					this.parentSpecimen = new CellSpecimen();

					//this.parentSpecimen.setId(new Long(form.getParentSpecimenId()));
					this.parentSpecimen.setLabel(form.getParentSpecimenLabel());
					this.parentSpecimen.setBarcode(form.getParentSpecimenBarcode());
					//Getting the Map of External Identifiers
					Map extMap = form.getExternalIdentifier();

					MapDataParser parser = new MapDataParser("edu.wustl.catissuecore.domain");

					Collection extCollection = parser.generateData(extMap);
					this.externalIdentifierCollection = extCollection;

					//					if (form.isVirtuallyLocated())
					//					{
					//						Logger.out.info("------------------Virtually located--------------");
					//						this.storageContainer = null;
					//						this.positionDimensionOne = null;
					//						this.positionDimensionTwo = null;
					//					}
					//					else
					//					{
					//						this.storageContainer.setId(new Long(form.getStorageContainer()));
					//						this.positionDimensionOne = new Integer(form.getPositionDimensionOne());
					//						this.positionDimensionTwo = new Integer(form.getPositionDimensionTwo());
					//
					//					}

					//setting the value of storage container
					if (form.isAddOperation())
					{
						if (this.storageContainer == null)
						{
							this.storageContainer = new StorageContainer();
						}
						if (form.getStContSelection() == 1)
						{
							this.storageContainer = null;
							this.positionDimensionOne = null;
							this.positionDimensionTwo = null;
						}
						if (form.getStContSelection() == 2)
						{
							this.storageContainer.setId(new Long(form.getStorageContainer()));
							this.positionDimensionOne = new Integer(form.getPositionDimensionOne());
							this.positionDimensionTwo = new Integer(form.getPositionDimensionTwo());
						}
						else if (form.getStContSelection() == 3)
						{
							this.storageContainer.setName(form.getSelectedContainerName());
							if (form.getPos1() != null && !form.getPos1().trim().equals("") && form.getPos2() != null
									&& !form.getPos2().trim().equals(""))
							{
								this.positionDimensionOne = new Integer(form.getPos1());
								this.positionDimensionTwo = new Integer(form.getPos2());
							}

						}
					}
				}
			}
		}
		catch (Exception excp)
		{
			Logger.out.error(excp.getMessage(), excp);
			throw new AssignDataException();

		}
		//Setting the consentTier responses. (Virender Mehta)
		if (abstractForm instanceof NewSpecimenForm)
		{
			NewSpecimenForm form = (NewSpecimenForm) abstractForm;
			this.consentTierStatusCollection = prepareParticipantResponseCollection(form);
			// ----------- Mandar --16-Jan-07
			this.consentWithdrawalOption = form.getWithdrawlButtonStatus();
			// ----- Mandar : ---23-jan-07 For bug 3464.
			this.applyChangesTo = form.getApplyChangesTo();
		}
	}

	/**
	 * For Consent Tracking
	 * Setting the Domain Object 
	 * @param  form CollectionProtocolRegistrationForm
	 * @return consentResponseColl
	 */
	private Collection prepareParticipantResponseCollection(NewSpecimenForm form)
	{
		MapDataParser mapdataParser = new MapDataParser("edu.wustl.catissuecore.bean");
		Collection beanObjColl = null;
		try
		{
			beanObjColl = mapdataParser.generateData(form.getConsentResponseForSpecimenValues());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		Iterator iter = beanObjColl.iterator();
		Collection consentResponseColl = new HashSet();
		while (iter.hasNext())
		{
			ConsentBean consentBean = (ConsentBean) iter.next();
			ConsentTierStatus consentTierstatus = new ConsentTierStatus();
			//Setting response
			consentTierstatus.setStatus(consentBean.getSpecimenLevelResponse());
			if (consentBean.getSpecimenLevelResponseID() != null && consentBean.getSpecimenLevelResponseID().trim().length() > 0)
			{
				consentTierstatus.setId(Long.parseLong(consentBean.getSpecimenLevelResponseID()));
			}
			//Setting consent tier
			ConsentTier consentTier = new ConsentTier();
			consentTier.setId(new Long(consentBean.getConsentTierID()));
			consentTierstatus.setConsentTier(consentTier);
			consentResponseColl.add(consentTierstatus);
		}
		return consentResponseColl;
	}

	protected Map fixMap(Map orgMap)
	{
		Map newMap = new HashMap();
		Iterator it = orgMap.keySet().iterator();
		while (it.hasNext())
		{
			String key = (String) it.next();
			//Logger.out.debug("key "+key);

			if (key.indexOf("persisted") == -1)
			{
				String value = String.valueOf(orgMap.get(key));
				newMap.put(key, value);
			}
		}
		return newMap;
	}

	/**
	 * This function returns the actual type of the specimen i.e Cell / Fluid / Molecular / Tissue.
	 */

	public final String getClassName()
	{
		String className = null;

		if (this instanceof CellSpecimen)
		{
			className = Constants.CELL;
		}
		else if (this instanceof MolecularSpecimen)
		{
			className = Constants.MOLECULAR;
		}
		else if (this instanceof FluidSpecimen)
		{
			className = Constants.FLUID;
		}
		else if (this instanceof TissueSpecimen)
		{
			className = Constants.TISSUE;
		}
		return className;
	}

	public String getObjectId()
	{
		Logger.out.debug(this.getClass().getName() + " is an instance of Specimen class");
		return Specimen.class.getName() + "_" + this.getId();
	}

	/**
	 * Returns the available quantity of a specimen.
	 * @return The available quantity of a specimen.
	 * @hibernate.component class="edu.wustl.catissuecore.domain.Quantity"
	 * @see #setAvailableQuantity(Quantity)
	 */
	public Quantity getAvailableQuantity()
	{
		return availableQuantity;
	}

	/**
	 * Sets the available quantity of a specimen.
	 * @param availableQuantity the available quantity of a specimen.
	 * @see #getAvailableQuantity()
	 */
	public void setAvailableQuantity(Quantity availableQuantity)
	{
		this.availableQuantity = availableQuantity;
	}

	/**
	 * Returns the quantity of a specimen.
	 * @return The quantity of a specimen.
	 * @hibernate.component class="edu.wustl.catissuecore.domain.Quantity"
	 * @see #setinitialQuantity(Quantity)
	 */
	public Quantity getInitialQuantity()
	{
		return initialQuantity;
	}

	/**
	 * Sets the quantity of a specimen.
	 * @param quantity The quantity of a specimen.
	 * @see #getInitialQuantity()
	 */
	public void setInitialQuantity(Quantity initialQuantity)
	{
		this.initialQuantity = initialQuantity;
	}

	/**
	 * Returns the Histoathological character of specimen.
	 * e.g. Non-Malignant, Malignant, Non-Malignant Diseased, Pre-Malignant.
	 * @hibernate.property name="pathologicalStatus" type="string" 
	 * column="PATHOLOGICAL_STATUS" length="50"
	 * @return the Histoathological character of specimen.
	 * @see #setPathologicalStatus(String)
	 */
	public String getPathologicalStatus()
	{
		return pathologicalStatus;
	}

	/**
	 * Sets the Histoathological character of specimen.
	 * e.g. Non-Malignant, Malignant, Non-Malignant Diseased, Pre-Malignant.
	 * @param pathologicalStatus the Histoathological character of specimen.
	 * @see #getPathologicalStatus()
	 */
	public void setPathologicalStatus(String pathologicalStatus)
	{
		this.pathologicalStatus = pathologicalStatus;
	}

	/*
	 * Returns true if this specimen object is an aliquot else false.
	 * @hibernate.property name="isAliquot" type="boolean" column="IS_ALIQUOT"
	 * @return the Histoathological character of specimen.
	 * @see #setIsAliquot(Boolean)
	 
	 public Boolean getIsAliquot()
	 {
	 return isAliquot;
	 }*/

	/*
	 * Sets true if this specimen object is an aliquot else false.
	 * @param isAliquot true if this specimen object is an aliquot else false.
	 * @see #getIsAliquot()
	 
	 public void setIsAliquot(Boolean isAliquot)
	 {
	 this.isAliquot = isAliquot;
	 }*/

	/**
	 * Returns the map that contains distinguished fields per aliquots.
	 * @return The map that contains distinguished fields per aliquots.
	 * @see #setAliquotMap(Map)
	 */
	public Map getAliqoutMap()
	{
		return aliqoutMap;
	}

	/**
	 * Sets the map of distinguished fields of aliquots.
	 * @param aliquotMap A map of distinguished fields of aliquots.
	 * @see #getAliquotMap()
	 */
	public void setAliqoutMap(Map aliqoutMap)
	{
		this.aliqoutMap = aliqoutMap;
	}

	/**
	 * Returns the no. of aliquots to be created.
	 * @return The no. of aliquots to be created.
	 * @see #setNoOfAliquots(int)
	 */
	public int getNoOfAliquots()
	{
		return noOfAliquots;
	}

	/**
	 * Sets the no. of aliquots to be created.
	 * @param noOfAliquots The no. of aliquots to be created.
	 * @see #getNoOfAliquots()
	 */
	public void setNoOfAliquots(int noOfAliquots)
	{
		this.noOfAliquots = noOfAliquots;
	}

	/**
	 * Returns the label name of specimen.
	 * @hibernate.property name="label" type="string" 
	 * column="LABEL" length="255"
	 * @return the label name of specimen.
	 * @see #setLabel(String)
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the label name of specimen.
	 * @param label The label name of specimen.
	 * @see #getLabel()
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Returns the historical information about the specimen.
	 * @hibernate.property name="lineage" type="string" 
	 * column="LINEAGE" length="50"
	 * @return The historical information about the specimen.
	 * @see #setLineage(String)
	 */
	public String getLineage()
	{
		return lineage;
	}

	/**
	 * Sets the historical information about the specimen.
	 * @param label The historical information about the specimen.
	 * @see #getLineage()
	 */
	public void setLineage(String lineage)
	{
		this.lineage = lineage;
	}

	/**
	 * Returns message label to display on success add or edit
	 * @return String
	 */
	public String getMessageLabel()
	{
		return this.label;
	}

	//----------------------------Mandar 16-jan-07
	public String getConsentWithdrawalOption()
	{
		return consentWithdrawalOption;
	}

	public void setConsentWithdrawalOption(String consentWithdrawalOption)
	{
		this.consentWithdrawalOption = consentWithdrawalOption;
	}

	public String getApplyChangesTo()
	{
		return applyChangesTo;
	}

	public void setApplyChangesTo(String applyChangesTo)
	{
		this.applyChangesTo = applyChangesTo;
	}

	/**
	 * @return Returns the disposeParentSpecimen.
	 */
	public boolean getDisposeParentSpecimen()
	{
		return disposeParentSpecimen;
	}

	/**
	 * @param disposeParentSpecimen The disposeParentSpecimen to set.
	 */
	public void setDisposeParentSpecimen(boolean disposeParentSpecimen)
	{
		this.disposeParentSpecimen = disposeParentSpecimen;
	}

	/**
	 * Name: Sachin Lale 
	 * Bug ID: 3835
	 * Patch ID: 3835_2
	 * See also: 1-4 
	 * Description : Addeed createdOn field for derived and aliqut Specimen.
	 * Returns the date on which the Participant is 
	 * registered to the Collection Protocol.
	 * @hibernate.property name="createdOn" column="CREATED_ON_DATE" type="date"
	 * @return the date on which the Dervive/aliqut Specimen is created 
	 * @see #setCreatedOn(Date)
	 */
	public Date getCreatedOn()
	{
		return createdOn;
	}

	/**
	 * Sets the date on which the Participant is 
	 * registered to the Collection Protocol.
	 * @param registrationDate the date on which the Participant is 
	 * registered to the Collection Protocol.
	 * @see #getRegistrationDate()
	 */
	public void setCreatedOn(Date createdOn)
	{
		this.createdOn = createdOn;
	}

	public String getCollectionStatus()
	{
		return collectionStatus;
	}

	public void setCollectionStatus(String collectionStatus)
	{
		this.collectionStatus = collectionStatus;
	}

	public Specimen(Specimen specimen)
	{
		this.activityStatus = specimen.getActivityStatus();
		this.applyChangesTo = specimen.getApplyChangesTo();
		this.available = specimen.getAvailable();
		if (specimen.getInitialQuantity() != null)
		{
			/**
			 * Name: Abhishek Mehta 
			 * Bug ID: 5558
			 * Patch ID: 5558_1
			 * See also: 1-3 
			 * Description : Earlier the available quantity for specimens that haven't been collected yet is greater than 0.
			 */
			this.availableQuantity = new Quantity("0");
			this.initialQuantity = new Quantity(specimen.getInitialQuantity());
		}
		this.biohazardCollection = setBiohazardCollection(specimen);
		this.createdOn = specimen.getCreatedOn();
		this.comment = specimen.getComment();
		this.lineage = specimen.getLineage();
		this.pathologicalStatus = specimen.getPathologicalStatus();
		this.collectionStatus = Constants.COLLECTION_STATUS_PENDING;
		if (specimen.getSpecimenCharacteristics() != null)
		{
			this.specimenCharacteristics = new SpecimenCharacteristics(specimen.getSpecimenCharacteristics());
		}
		this.type = specimen.getType();
		this.isCollectionProtocolRequirement = new Boolean(false);
		this.positionDimensionOne = specimen.positionDimensionOne;
	}

	private Collection setBiohazardCollection(Specimen specimen)
	{
		Collection biohazardCollectionN = new HashSet();
		Collection biohazardCollection = specimen.getBiohazardCollection();
		if (biohazardCollection != null && !biohazardCollection.isEmpty())
		{
			Iterator it = biohazardCollection.iterator();
			while (it.hasNext())
			{
				Biohazard bio = (Biohazard) it.next();
				Biohazard bioN = new Biohazard(bio);
				biohazardCollectionN.add(bioN);
			}
		}
		return biohazardCollectionN;
	}

	public Specimen createClone()
	{
		Specimen cloneSpecimen = new Specimen(this);
		return cloneSpecimen;
	}

	public void setDefaultSpecimenEventCollection(Long userID)
	{
		Collection specimenEventCollection = new HashSet();
		User user = new User();
		user.setId(userID);
		CollectionEventParameters collectionEventParameters = EventsUtil.populateCollectionEventParameters(user);
		collectionEventParameters.setSpecimen(this);
		specimenEventCollection.add(collectionEventParameters);

		ReceivedEventParameters receivedEventParameters = EventsUtil.populateReceivedEventParameters(user);
		receivedEventParameters.setSpecimen(this);
		specimenEventCollection.add(receivedEventParameters);
		setSpecimenEventCollection(specimenEventCollection);
	}

	public void setConsentTierStatusCollectionFromSCG(SpecimenCollectionGroup specimenCollectionGroup)
	{
    	Collection consentTierStatusCollectionN = this.getConsentTierStatusCollection();
    	if (consentTierStatusCollectionN ==null)
    	{
    		consentTierStatusCollectionN = new HashSet();
    	}

    	Collection consentTierStatusCollection = specimenCollectionGroup.getConsentTierStatusCollection();
		Collection specConsTierColl = this.getConsentTierStatusCollection();
		boolean hasMoreConsents =false;
		if (consentTierStatusCollection != null && !consentTierStatusCollection.isEmpty())
		{
			Iterator it = consentTierStatusCollection.iterator();
			Iterator specCoIterator = null;
			if (specConsTierColl !=null)
			{	
				specCoIterator = specConsTierColl.iterator();
				hasMoreConsents =specCoIterator.hasNext();
			}
			while (it.hasNext())
			{
				ConsentTierStatus consentTierStatus = (ConsentTierStatus) it.next();
				ConsentTierStatus consentTierstatusN = null;
				
				if (hasMoreConsents)
				{
					consentTierstatusN = (ConsentTierStatus) specCoIterator.next();
					consentTierstatusN.setConsentTier(consentTierStatus.getConsentTier());
					hasMoreConsents =specCoIterator.hasNext();
				}
				else
				{
					consentTierstatusN = new ConsentTierStatus(consentTierStatus);
					consentTierStatusCollectionN.add(consentTierstatusN);
				}

			}
		}
		setConsentTierStatusCollection(consentTierStatusCollectionN);
	}
}
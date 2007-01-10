/**
 * <p>Title: Order Class>
 * <p>Description:   Class for NewSpecimenOrderItem.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Ashish Gupta
 * @version 1.00
 * Created on October 16,2006
 */
package edu.wustl.catissuecore.domain;



/**
 * This is abstract class indicating the derived specimens from existing ones for the request order
 * * Represents  Pathology Order Item.
 * @hibernate.joined-subclass table="CATISSUE_DERIEVED_SP_ORD_ITEM" 
 * @hibernate.joined-subclass-key
 * column="IDENTIFIER"
 *
 * @author ashish_gupta
 */
public class DerivedSpecimenOrderItem extends NewSpecimenOrderItem
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6670417163722020858L;
	/**
	 * Specimen associated with the particular order item.
	 */
	protected Specimen specimen;
	/**
	 * Returns the specimen class of the requested new/derived specimen
	 * @hibernate.property  name="specimenClass" type="string" length="100" column="SPECIMEN_CLASS"
	 * @return Specimen Class of the new/derived specimen
	 */
	public String getSpecimenClass() 
	{
		return specimenClass;
	}
	
	/**
	 * Sets the specimen class of the requested new/derived specimen.
	 * @param specimenClass String
	 */
	public void setSpecimenClass(String specimenClass)
	{
		this.specimenClass = specimenClass;
	}

	/**
	 * Returns the specimen type of the requested new/derived specimen
	 * @hibernate.property name="specimenType" length="100" type="string" column="SPECIMEN_TYPE"
	 * @return Specimen Type of the new/derived specimen
	 */
	public String getSpecimenType()
	{
		return specimenType;
	}

	/**
	 * Sets the specimen type of the new/derived specimens
	 * @param specimenType String
	 */
	public void setSpecimenType(String specimenType)
	{
		this.specimenType = specimenType;
	}

	
	/**
	 * The specimen associated with the order item in SpecimenOrderItem.
	 * @hibernate.many-to-one column="SPECIMEN_ID" class="edu.wustl.catissuecore.domain.Specimen"
	 * constrained="true"
	 * @return the specimen
	 */
	public Specimen getSpecimen()
	{
		return specimen;
	}

	
	/**
	 * @param specimen the specimen to get.
	 * @see #getSpecimen()
	 */
	public void setSpecimen(Specimen specimen)
	{
		this.specimen = specimen;
	}
}


package com.krishagni.catissueplus.core.biospecimen.repository;

import java.util.List;

import com.krishagni.catissueplus.core.biospecimen.domain.Specimen;
import com.krishagni.catissueplus.core.biospecimen.domain.SpecimenCollectionGroup;
import com.krishagni.catissueplus.core.common.repository.Dao;

public interface SpecimenCollectionGroupDao extends Dao<SpecimenCollectionGroup> {

	public List<Specimen> getSpecimensList(Long scgId);

	public boolean isNameUnique(String name);

	public boolean isBarcodeUnique(String barcode);

	public SpecimenCollectionGroup getscg(Long scgId);

}

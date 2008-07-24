package edu.wustl.catissuecore.action;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.actionForm.ViewSpecimenSummaryForm;
import edu.wustl.catissuecore.bean.CollectionProtocolBean;
import edu.wustl.catissuecore.bean.CollectionProtocolEventBean;
import edu.wustl.catissuecore.bean.GenericSpecimen;
import edu.wustl.catissuecore.exception.CatissueException;
import edu.wustl.catissuecore.util.IdComparator;
import edu.wustl.catissuecore.util.SpecimenDetailsTagUtil;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.catissuecore.util.global.Utility;
import edu.wustl.common.util.dbManager.DAOException;

public class ViewSpecimenSummaryAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
			String target = Constants.SUCCESS;
			ViewSpecimenSummaryForm summaryForm = (ViewSpecimenSummaryForm) form;
		try {
			HttpSession session = request.getSession();
//			summaryForm.setLastSelectedSpecimenId(summaryForm.getSelectedSpecimenId());
			
			String eventId = summaryForm.getEventId();
			session.setAttribute(Constants.TREE_NODE_ID,(String)request.getParameter("nodeId"));
			String event_id=(String)request.getParameter("Event_Id");
			Object obj = request.getAttribute("SCGFORM");
			request.setAttribute("SCGFORM", obj);
			target =request.getParameter("target");
			String submitAction = request.getParameter("submitAction");
			
			
			if (target == null)
			{
				target = Constants.SUCCESS;
			}
			
			if (submitAction != null)
			{
				summaryForm.setSubmitAction(submitAction);
			}
			
			if(summaryForm.getTargetSuccess() == null)
			{
				summaryForm.setTargetSuccess(target);
			}
			target = summaryForm.getTargetSuccess();

			if (request.getAttribute("RequestType")!=null)
			{
				summaryForm.setRequestType(ViewSpecimenSummaryForm.REQUEST_TYPE_ANTICIPAT_SPECIMENS);
			}
				
			if (eventId == null) {
				eventId = (String) request
						.getParameter(Constants.COLLECTION_PROTOCOL_EVENT_ID);
			}
			
			if (summaryForm.getSpecimenList()!= null  )
			{
				updateSessionBean(summaryForm, session);
				
			}
		
			if(request.getParameter("save")!=null)
			{
				if (!isTokenValid(request))
				{
					summaryForm.setReadOnly(true);
					throw new CatissueException ("cannot submit duplicate request.");
				}

				resetToken(request);
				if((summaryForm.getSubmitAction().equals("bulkUpdateSpecimens") || summaryForm.getSubmitAction().equals("pageOfbulkUpdateSpecimens")) && (request.getParameter("printflag")!=null && request.getParameter("printflag").equals("1")))
				{
					request.setAttribute("printflag","1");
					return mapping.findForward(summaryForm.getSubmitAction() );
				}
				else
				{
					return mapping.findForward(summaryForm.getSubmitAction());
				}
			}
			else
			{
				saveToken(request);
			}
			

			CollectionProtocolBean collectionProtocolBean = 
				(CollectionProtocolBean)session
				.getAttribute(Constants.COLLECTION_PROTOCOL_SESSION_BEAN);
						
			//for disabling of CP set the collection protocol status: kalpana
	
			if(collectionProtocolBean!=null && collectionProtocolBean.getActivityStatus()!=null){
			
			//checked the associated specimens to the cp	
				boolean isSpecimenExist=(boolean)isSpecimenExists((Long)collectionProtocolBean.getIdentifier());
				if(isSpecimenExist)
				{
					ViewSpecimenSummaryForm.setSpecimenExist("true");
				}
				else
				{
					ViewSpecimenSummaryForm.setSpecimenExist("false");
				}
		
			
				ViewSpecimenSummaryForm.setCollectionProtocolStatus(collectionProtocolBean.getActivityStatus());
			}
			
			LinkedHashMap<String, GenericSpecimen> specimenMap = 
							getSpecimensFromSessoin(session, eventId, summaryForm);

			if (specimenMap != null) {
				populateSpecimenSummaryForm(summaryForm, specimenMap);
			} 

			if (ViewSpecimenSummaryForm.REQUEST_TYPE_COLLECTION_PROTOCOL.equals(summaryForm.getRequestType()))
			{
				summaryForm.setUserAction(ViewSpecimenSummaryForm.ADD_USER_ACTION);
				if("update".equals(collectionProtocolBean.getOperation()))
				{
					summaryForm.setUserAction(ViewSpecimenSummaryForm.UPDATE_USER_ACTION);
				}
			}
			String pageOf = request.getParameter(Constants.PAGEOF);
			//Mandar: 16May2008 : For specimenDetails customtag --- start ---
			if("anticipatory".equalsIgnoreCase(target) || "pageOfMultipleSpWithMenu".equalsIgnoreCase(target))
			{
				SpecimenDetailsTagUtil.setAnticipatorySpecimenDetails(request, summaryForm);
			}
			else 
			{
				SpecimenDetailsTagUtil.setSpecimenSummaryDetails(request, summaryForm);
			}
			
			//Mandar: 16May2008 : For specimenDetails customtag --- end ---
			summaryForm.setLastSelectedSpecimenId(summaryForm.getSelectedSpecimenId());
			if(pageOf != null && ViewSpecimenSummaryForm.REQUEST_TYPE_MULTI_SPECIMENS.equals(summaryForm.getRequestType()))
			{
				request.setAttribute(Constants.PAGEOF,pageOf);
				return mapping.findForward(target);
			}
			

			return mapping.findForward(target);
		} catch (Exception exception) {
			exception.printStackTrace();
			ActionErrors actionErrors = new ActionErrors();
			actionErrors.add(actionErrors.GLOBAL_MESSAGE, new ActionError(
					"errors.item",exception.getMessage()));
			saveErrors(request, actionErrors);		
			//Mandar: 17JULY2008 : For specimenDetails customtag --- start ---
			if("anticipatory".equalsIgnoreCase(target) || "pageOfMultipleSpWithMenu".equalsIgnoreCase(target))
			{
				SpecimenDetailsTagUtil.setAnticipatorySpecimenDetails(request, summaryForm);
			}
			else 
			{
				SpecimenDetailsTagUtil.setSpecimenSummaryDetails(request, summaryForm);
			}
			
			//Mandar: 17JULY2008 : For specimenDetails customtag --- end ---

			return mapping.findForward(target);
		}

	}

	/**
	 * @param summaryForm
	 */
	private void updateSessionBean(ViewSpecimenSummaryForm summaryForm, HttpSession session)
	{
		String eventId = summaryForm.getEventId();
		if (eventId == null || session
				.getAttribute(Constants.COLLECTION_PROTOCOL_EVENT_SESSION_MAP) == null)
		{
			return;
		}
		
		Map collectionProtocolEventMap = (Map) session
		.getAttribute(Constants.COLLECTION_PROTOCOL_EVENT_SESSION_MAP);		
		CollectionProtocolEventBean eventBean =(CollectionProtocolEventBean)
							collectionProtocolEventMap.get(eventId);	// get nullpointer sometimes
		LinkedHashMap specimenMap = (LinkedHashMap)eventBean.getSpecimenRequirementbeanMap();
		String selectedItem = summaryForm.getLastSelectedSpecimenId();
		GenericSpecimen selectedSpecimen=(GenericSpecimen) specimenMap.get(selectedItem);
		
		updateSpecimenToSession(summaryForm, specimenMap);
		if(selectedSpecimen != null)
		{
			updateAliquotToSession(summaryForm, selectedSpecimen);
			updateDerivedToSession(summaryForm, selectedSpecimen);
		}
	}

	/**
	 * @param summaryForm
	 * @param specimenMap
	 */
	private void updateSpecimenToSession(ViewSpecimenSummaryForm summaryForm,
			LinkedHashMap specimenMap) {
		Collection specimenCollection = specimenMap.values();
		Iterator iterator = summaryForm.getSpecimenList().iterator();

		
		while(iterator.hasNext())
		{
			GenericSpecimen specimenFormVO =(GenericSpecimen) iterator.next();

			GenericSpecimen specimenSessionVO =(GenericSpecimen)
			specimenMap.get(specimenFormVO.getUniqueIdentifier());

				if(specimenSessionVO!=null)
				{
					setFormValuesToSession(specimenFormVO, specimenSessionVO);
				}

		}
	}


	/**
	 * @param summaryForm
	 * @param selectedSpecimen
	 */
	private void updateAliquotToSession(ViewSpecimenSummaryForm summaryForm,
			GenericSpecimen selectedSpecimen) {
		Iterator aliquotIterator = summaryForm.getAliquotList().iterator();
		
		while(aliquotIterator.hasNext())
		{
			GenericSpecimen aliquotFormVO =(GenericSpecimen) aliquotIterator.next();
			String aliquotKey = aliquotFormVO.getUniqueIdentifier();
			GenericSpecimen  aliquotSessionVO = (GenericSpecimen) 
										getAliquotSessionObject(selectedSpecimen , aliquotKey);
			if(aliquotSessionVO != null)
			{
				setFormValuesToSession(aliquotFormVO, aliquotSessionVO);
			}
			
		}
	}
	
	
	private void updateDerivedToSession(ViewSpecimenSummaryForm summaryForm,
			GenericSpecimen selectedSpecimen) {
		Iterator derivedIterator = summaryForm.getDerivedList().iterator();
		
		while(derivedIterator.hasNext())
		{
			GenericSpecimen derivedFormVO =(GenericSpecimen) derivedIterator.next();
			String derivedKey = derivedFormVO.getUniqueIdentifier();
			GenericSpecimen  derivedSessionVO = (GenericSpecimen) 
										getDerivedSessionObject(selectedSpecimen , derivedKey);
			if(derivedSessionVO != null)
			{
				setFormValuesToSession(derivedFormVO, derivedSessionVO);
			}

		}
	}

	/**
	 * @param derivedFormVO
	 * @param derivedSessionVO
	 */
	private void setFormValuesToSession(GenericSpecimen derivedFormVO,
			GenericSpecimen derivedSessionVO) {
		derivedSessionVO.setCheckedSpecimen(derivedFormVO.getCheckedSpecimen());
		derivedSessionVO.setDisplayName(derivedFormVO.getDisplayName());
		derivedSessionVO.setBarCode(derivedFormVO.getBarCode());
		//derivedSessionVO.setContainerId(derivedFormVO.getContainerId());
		derivedSessionVO.setContainerId(null);
		derivedSessionVO.setSelectedContainerName(derivedFormVO.getSelectedContainerName());
		derivedSessionVO.setPositionDimensionOne(derivedFormVO.getPositionDimensionOne());
		derivedSessionVO.setPositionDimensionTwo(derivedFormVO.getPositionDimensionTwo());
		derivedSessionVO.setQuantity(derivedFormVO.getQuantity());
		derivedSessionVO.setConcentration(derivedFormVO.getConcentration());
		derivedSessionVO.setFormSpecimenVo(derivedFormVO);
	}
	
	private GenericSpecimen getDerivedSessionObject(GenericSpecimen parentSessionObject, String derivedKey)
	{
		LinkedHashMap deriveMap = parentSessionObject.getDeriveSpecimenCollection();
		Collection parentCollection;
		if(deriveMap != null && !deriveMap.isEmpty())
		{
		//	return null;
		
			GenericSpecimen derivedSessionObject =(GenericSpecimen) deriveMap.get(derivedKey);
			if (derivedSessionObject != null)
			{
				return derivedSessionObject;	
			}
			parentCollection = deriveMap.values();
			Iterator parentIterator = parentCollection.iterator();
			
			while(parentIterator.hasNext())
			{
				GenericSpecimen parentDerived = (GenericSpecimen) parentIterator.next();
				derivedSessionObject = getDerivedSessionObject(parentDerived, derivedKey);
				if (derivedSessionObject != null){
					return derivedSessionObject;
				}
			}
		}
		//Search Derived in derived specimen tree.
		LinkedHashMap aliquotMap = parentSessionObject.getAliquotSpecimenCollection();
		
		if(aliquotMap != null && !aliquotMap.isEmpty())
		{
			parentCollection = aliquotMap.values();
			Iterator parentIterator = parentCollection.iterator();
			
			while(parentIterator.hasNext())
			{
				GenericSpecimen derivedSpecimen = (GenericSpecimen) parentIterator.next();
				GenericSpecimen derivedSessionObject = getDerivedSessionObject(derivedSpecimen, derivedKey);
				if (derivedSessionObject != null){
					return derivedSessionObject;
				}
			}
		}
		return null;
		
	}
	private GenericSpecimen getAliquotSessionObject(GenericSpecimen parentSessionObject, String aliquotKey)
	{
		LinkedHashMap aliquotMap = parentSessionObject.getAliquotSpecimenCollection();
		if(aliquotMap != null && !aliquotMap.isEmpty())
		{
			GenericSpecimen aliquotSessionObject =(GenericSpecimen) aliquotMap.get(aliquotKey);
			if (aliquotSessionObject != null)
			{
				return aliquotSessionObject;	
			}
			Collection parentCollection = aliquotMap.values();
			Iterator parentIterator = parentCollection.iterator();
			
			while(parentIterator.hasNext())
			{
				GenericSpecimen parentAliquot = (GenericSpecimen) parentIterator.next();
				aliquotSessionObject = getAliquotSessionObject(parentAliquot, aliquotKey);
				if (aliquotSessionObject != null){
					return aliquotSessionObject;
				}
			}

		}
		//Search Aliquot in derived specimen tree.
		LinkedHashMap deriveMap = parentSessionObject.getDeriveSpecimenCollection();
		
		if(deriveMap != null && !deriveMap.isEmpty())
		{
			Collection parentCollection = deriveMap.values();
			Iterator parentIterator = parentCollection.iterator();
			
			while(parentIterator.hasNext())
			{
				GenericSpecimen derivedSpecimen = (GenericSpecimen) parentIterator.next();
				GenericSpecimen aliquotSessionObject = getAliquotSessionObject(derivedSpecimen, aliquotKey);
				if (aliquotSessionObject != null){
					return aliquotSessionObject;
				}
			}
		}
		return null;
		
	}
	/**
	 * This function retrieves the Map of specimens from session.
	 * @param session
	 * @param eventId
	 * @param specimenMap
	 * @return
	 */
	private LinkedHashMap<String, GenericSpecimen> getSpecimensFromSessoin(
			HttpSession session, String eventId, ViewSpecimenSummaryForm summaryForm) {

		LinkedHashMap<String, GenericSpecimen> specimenMap = null;
		
		if (eventId == null)
		{
			eventId = "dummy";
		}

		if (eventId != null ) 
		{
			if(summaryForm.getRequestType() == null)
			{
				summaryForm.setRequestType(ViewSpecimenSummaryForm.REQUEST_TYPE_COLLECTION_PROTOCOL);
			}
			StringTokenizer stringTokenizer =new StringTokenizer(eventId, "_");
			if(stringTokenizer!=null)
			{
				if (stringTokenizer.hasMoreTokens())
				{
					eventId = stringTokenizer.nextToken();
				}
			}
			
			Map collectionProtocolEventMap = (Map) session
					.getAttribute(Constants.COLLECTION_PROTOCOL_EVENT_SESSION_MAP);
			
			if (collectionProtocolEventMap != null && !collectionProtocolEventMap.isEmpty()) {
			
				CollectionProtocolEventBean collectionProtocolEventBean = 
					(CollectionProtocolEventBean) collectionProtocolEventMap.get(eventId);

				if (collectionProtocolEventBean == null  ) {
				
					eventId =(String) collectionProtocolEventMap.keySet().iterator().next();
					Collection cl =collectionProtocolEventMap.values();

					if (cl!=null && !cl.isEmpty())
					{
						
						collectionProtocolEventBean = 
							(CollectionProtocolEventBean) cl.iterator().next();
					}
					
				}				
				if (collectionProtocolEventBean != null) {
				
					specimenMap = (LinkedHashMap) collectionProtocolEventBean
							.getSpecimenRequirementbeanMap();
					
				}
			}
			summaryForm.setEventId(eventId);			
		} else {
			summaryForm.setRequestType(ViewSpecimenSummaryForm.REQUEST_TYPE_MULTI_SPECIMENS);
			specimenMap = (LinkedHashMap) session
					.getAttribute(Constants.SPECIMEN_LIST_SESSION_MAP);
		}
		
		return specimenMap;
	}

	/**
	 * Populates form object of the action with speimen, aliquot specimen
	 * and derived specimen object
	 * @param summaryForm
	 * @param specimenMap
	 */
	private void populateSpecimenSummaryForm(
			ViewSpecimenSummaryForm summaryForm,
			LinkedHashMap<String, GenericSpecimen> specimenMap) {
				
		LinkedList<GenericSpecimen> specimenList = getSpecimenList(specimenMap.values());
		summaryForm.setSpecimenList(specimenList);
		String selectedSpecimenId = summaryForm.getSelectedSpecimenId();

		if (selectedSpecimenId == null) 
		{
			if(specimenList!=null && !specimenList.isEmpty())
			{
				selectedSpecimenId =((GenericSpecimen) specimenList.get(0)).getUniqueIdentifier();
				summaryForm.setSelectedSpecimenId(selectedSpecimenId);
			}
		}
		GenericSpecimen selectedSpecimen = specimenMap
				.get(selectedSpecimenId);

		if (selectedSpecimen == null) 
		{
			return;
		}
		
		LinkedHashMap<String, GenericSpecimen> aliquotsList = selectedSpecimen
				.getAliquotSpecimenCollection();
		LinkedHashMap<String, GenericSpecimen> derivedList = selectedSpecimen
				.getDeriveSpecimenCollection();

		Collection nestedAliquots = new LinkedHashSet();
		Collection nestedDerives = new LinkedHashSet();
		if (aliquotsList != null && !aliquotsList.values().isEmpty()) 
		{
			nestedAliquots.addAll(aliquotsList.values());
			getNestedChildSpecimens(aliquotsList.values(), nestedAliquots, nestedDerives);
//			getNestedAliquotSpecimens(aliquotsList.values(), nestedAliquots);
//			getNestedDerivedSpecimens(aliquotsList.values(), nestedDerives);
			
		}

		if (derivedList != null && !derivedList.values().isEmpty()) 
		{
			nestedDerives.addAll(derivedList.values());
			getNestedChildSpecimens(derivedList.values(), nestedAliquots, nestedDerives);			
//			getNestedAliquotSpecimens(derivedList.values(), nestedAliquots);
//			getNestedDerivedSpecimens(derivedList.values(), nestedDerives);
		}
		
		summaryForm.setAliquotList(getSpecimenList(nestedAliquots));
		summaryForm.setDerivedList(getSpecimenList(nestedDerives));
		
	}

	private void getNestedChildSpecimens(Collection topChildCollection,
			Collection nestedAliquoteCollection, Collection nestedDerivedCollection) {


		Iterator iterator = topChildCollection.iterator();

		while (iterator.hasNext()) {
			GenericSpecimen specimen = (GenericSpecimen) iterator.next();

			if (specimen.getAliquotSpecimenCollection() != null) {
				Collection childSpecimen = specimen
						.getAliquotSpecimenCollection().values();

				if (!childSpecimen.isEmpty()) {
					nestedAliquoteCollection.addAll(childSpecimen);
					getNestedChildSpecimens(childSpecimen,
							nestedAliquoteCollection,nestedDerivedCollection);
				}				
			}

			if (specimen.getDeriveSpecimenCollection() != null) {
				Collection childSpecimen = specimen
						.getDeriveSpecimenCollection().values();

				if (!childSpecimen.isEmpty()) {
					nestedDerivedCollection.addAll(childSpecimen);
					getNestedChildSpecimens(childSpecimen,
							nestedAliquoteCollection,nestedDerivedCollection);
				}				
			}

			
		}
	}

	private void getNestedAliquotSpecimens(Collection topChildCollection,
			Collection nestedCollection) {


		Iterator iterator = topChildCollection.iterator();

		while (iterator.hasNext()) {
			GenericSpecimen specimen = (GenericSpecimen) iterator.next();

			if (specimen.getAliquotSpecimenCollection() != null) {
				Collection childSpecimen = specimen
						.getAliquotSpecimenCollection().values();
				if (!childSpecimen.isEmpty()) {
					nestedCollection.addAll(childSpecimen);
					getNestedAliquotSpecimens(childSpecimen, nestedCollection);
				}				
			}
		}
	}

	private void getNestedDerivedSpecimens(Collection topChildCollection,
			Collection nestedCollection) {

		Iterator iterator = topChildCollection.iterator();

		while (iterator.hasNext()) {
			GenericSpecimen specimen = (GenericSpecimen) iterator.next();

			if (specimen.getDeriveSpecimenCollection() != null) {
				Collection childSpecimen = specimen
						.getDeriveSpecimenCollection().values();

				if (!childSpecimen.isEmpty()) {
					nestedCollection.addAll(childSpecimen);
					getNestedDerivedSpecimens(childSpecimen, nestedCollection);
				}				
			}
		}
	}

	/**
	 * @param specimenMap
	 * @return
	 */
	private LinkedList<GenericSpecimen> getSpecimenList(
			Collection<GenericSpecimen> specimenColl) {
		LinkedList<GenericSpecimen> specimenList = new LinkedList<GenericSpecimen>();
		if (!specimenColl.isEmpty())
		{
			specimenList.addAll(specimenColl);
			
			IdComparator speciemnIdComp = new IdComparator();
			Collections.sort(specimenList,speciemnIdComp);

		}
		return specimenList;
	}	
	
	/**
	 * To check the associated specimens to the Collection protocol
	 * @param cpId
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	protected boolean isSpecimenExists(Long cpId) throws DAOException, ClassNotFoundException
	{
		
		String hql = " select" +
        " elements(scg.specimenCollection) " +
        "from " +
        " edu.wustl.catissuecore.domain.CollectionProtocol as cp" +
        ", edu.wustl.catissuecore.domain.CollectionProtocolRegistration as cpr" +
        ", edu.wustl.catissuecore.domain.SpecimenCollectionGroup as scg" +
        ", edu.wustl.catissuecore.domain.Specimen as s" +
        " where cp.id = "+cpId+"  and"+
        " cp.id = cpr.collectionProtocol.id and" +
        " cpr.id = scg.collectionProtocolRegistration.id and" +
        " scg.id = s.specimenCollectionGroup.id and " +
        " s.activityStatus = '"+Constants.ACTIVITY_STATUS_ACTIVE+"'";
		
		List specimenList=(List)Utility.executeQuery(hql);
		if((specimenList!=null) && (specimenList).size()>0)
		{
			return true;
		}	
		else
		{
			return false;
		}
		
	}
}

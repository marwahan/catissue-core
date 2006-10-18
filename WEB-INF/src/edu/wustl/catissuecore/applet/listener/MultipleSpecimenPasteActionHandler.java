
package edu.wustl.catissuecore.applet.listener;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import edu.wustl.catissuecore.applet.AppletConstants;
import edu.wustl.catissuecore.applet.AppletServerCommunicator;
import edu.wustl.catissuecore.applet.CopyPasteOperationValidatorModel;
import edu.wustl.catissuecore.applet.model.BaseAppletModel;
import edu.wustl.catissuecore.applet.model.SpecimenColumnModel;
import edu.wustl.catissuecore.applet.ui.MultipleSpecimenApplet;
import edu.wustl.catissuecore.applet.util.CommonAppletUtil;

/**
 * <p>This class initializes the fields of MultipleSpecimenPasteActionHandler.java</p>
 * @author Ashwin Gupta
 * @version 1.1
 */
public class MultipleSpecimenPasteActionHandler extends AbstractPasteActionHandler
{

	/**
	 * constructor with table to persist table
	 * @param table table used in applet
	 */
	public MultipleSpecimenPasteActionHandler(JTable table)
	{
		super(table);
	}

	/**
	 * @see edu.wustl.catissuecore.applet.listener.AbstractPasteActionHandler#doActionPerformed(java.awt.event.ActionEvent)
	 */
	protected void doActionPerformed(ActionEvent event)
	{
			super.doActionPerformed(event);
			//super.handleAction(event);
			/*
			 * Commented as code move to common abstractcopy action handler -- Ashwin
			System.out.println("\n<<<<<<<<<<<          PASTE         <<<<<<<<<<<<<<<<<<<<<<\n");
			System.out.println("Inside MultipleSpecimenPasteActionHandler");
			
			CopyPasteOperationValidatorModel validatorModel = CommonAppletUtil.getMultipleSpecimenTableModel(table).getCopyPasteOperationValidatorModel();
			
			int[] selectedColumns = table.getSelectedColumns();
			int[] selectedRows = table.getSelectedRows();
			System.out.println("selectedRows : ");
			CommonAppletUtil.printArray(selectedRows );
			System.out.println(" \n selectedColumns : ");
			CommonAppletUtil.printArray(selectedColumns );		  

			validatorModel.setSelectedPastedRows(CommonAppletUtil.createListFromArray(selectedRows));
			validatorModel.setSelectedPastedCols(CommonAppletUtil.createListFromArray(selectedColumns));
			//for validatior
			validatorModel.setOperation(AppletConstants.PASTE_OPERATION  );
			validatorModel.setColumnCount(CommonAppletUtil.getMultipleSpecimenTableModel(table).getTotalColumnCount());
			validatorModel.setRowCount(table.getRowCount());
			
			System.out.println(" Cell info set for Pasting ");
			
			//for validation
			MultipleSpecimenCopyPasteValidator copyPasteValidator = new MultipleSpecimenCopyPasteValidator(table,validatorModel);
			String errorMessage = copyPasteValidator.validateForPaste();
			System.out.println("Message from copyPasteValidator.validateForPaste() : "+ errorMessage);
			if(errorMessage.trim().length()>0)
			{
			    Object[] parameters = new Object[]{errorMessage }; 
			    CommonAppletUtil.callJavaScriptFunction((JButton) e.getSource(),
				getJSMethodName(), parameters);
			}	// validation end
			else if (CommonAppletUtil.validateCell())
			{
				updateUI(validatorModel);
			}
		System.out.println("\n >>>>>>>>>>>>>> PASTE DONE >>>>>>>>>>>>");
		*/
		CommonAppletUtil.getMultipleSpecimenTableModel(table).showMapData();
		System.out.println("\n >>>>>>>>>>>>>>MAP in PASTE  >>>>>>>>>>>>");
		
		CopyPasteOperationValidatorModel validatorModel = CommonAppletUtil.getMultipleSpecimenTableModel(table).getCopyPasteOperationValidatorModel();
		List selectedCopiedRows = validatorModel.getSelectedCopiedRows();
	
		/**
		 *  check if button(s) also copied
		 */
	      boolean isButtonCopied = false;
			for (int i = 0; i < selectedCopiedRows.size(); i++)
			{
				int copiedRow = ((Integer) selectedCopiedRows.get(i)).intValue();
				if(copiedRow >= AppletConstants.SPECIMEN_COMMENTS_ROW_NO)
				{
					isButtonCopied = true;
					break;
				}
			}

	if(isButtonCopied)
	{
		BaseAppletModel appletModel = new BaseAppletModel();

		Map validatorDataMap = new HashMap();
		validatorDataMap.put(AppletConstants.VALIDATOR_MODEL,validatorModel);
		appletModel.setData(validatorDataMap);
		try
		{
			MultipleSpecimenApplet applet = (MultipleSpecimenApplet) CommonAppletUtil
					.getBaseApplet(table);
			String url = applet.getServerURL()
					+ "/MultipleSpecimenCopyPasteAction.do?method=paste";

			appletModel = (BaseAppletModel) AppletServerCommunicator.doAppletServerCommunication(
					url, appletModel);
				
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception");
		}
	}
	}
	
	/*
	private void updateTableModel(int row, int col, Object value)
	{
		System.out.println("\n---X------X------X------X------X------X------X---\n");
		System.out.println("Inside MultipleSpecimenPasteActionHandler updateTableModel. >>>>>>>>>>>>>>>>>>>>>\n");
		System.out.println("Setting value at row : "+row+" , col : "+col+" , value : "+value);
		CommonAppletUtil.getMultipleSpecimenTableModel(table).setValueAt(value,row,col );
		System.out.println("Value Set");
		System.out.println("\n---X------X------X------X------X------X------X---\n");
	}
	*/
	/*
	private void updateUI(CopyPasteOperationValidatorModel validatorModel)
	{
		System.out.println("This method updates the table UI.");
		HashMap dataMap = validatorModel.getCopiedData();
		System.out.println("\n Received Copied Data Map >>>>>>>>>>> \n");
		System.out.println(dataMap);
		System.out.println("\n-------------------------\n");

		System.out.println("Rows : "+ validatorModel.getSelectedPastedRows());
		System.out.println("Cols : " + validatorModel.getSelectedPastedCols());
		
		System.out.println("\n----- Setting value --------------------\n");
		//commenting to test multiple cell pasting
//		int selectedRow = table.getSelectedRow();
//		int selectedCol = table.getSelectedColumn ();
//		
//		int r = ((Integer)(validatorModel.getSelectedPastedRows().get(0))).intValue() ;
//		int c = ((Integer)(validatorModel.getSelectedPastedCols().get(0))).intValue() ;
//		
//		String key = CommonAppletUtil.getDataKey(((Integer)(validatorModel.getSelectedCopiedRows().get(0))).intValue(),((Integer)(validatorModel.getSelectedCopiedCols().get(0))).intValue()); 
//			
//		System.out.println("Key : "+ key);
//		Object value = dataMap.get(key );
//		System.out.println("Value : "+ value);
//		TableColumnModel columnModel = table.getColumnModel();
//		SpecimenColumnModel scm = (SpecimenColumnModel)columnModel.getColumn(table.getSelectedColumn()).getCellEditor();
//		scm.updateComponentValue(selectedRow,value.toString() );
		System.out.println("Setting values to multiple cells\n-------------------------------\n");

		int selectedRow = table.getSelectedRow();
		int selectedCol = table.getSelectedColumn ();

		setUI(validatorModel, dataMap, selectedRow, selectedCol);
		System.out.println("\n>>>>>>>>>>>>>>>> UI data Set. <<<<<<<<<<<<<<<<<<<<<  \n");
	}
	
	private void setUI(CopyPasteOperationValidatorModel validatorModel, HashMap dataMap, int selectedRow, int selectedCol)
	{
		int tmpSelectedRow = selectedRow;
		
		// copeid rows, columns
		List copiedRows = validatorModel.getSelectedCopiedRows();
		List copiedCols = validatorModel.getSelectedCopiedCols();
		Collections.sort(copiedRows);
		Collections.sort(copiedCols);
		
//		//paste rows, columns
//		List pasteRows = validatorModel.getSelectedPastedRows();
//		List pasteCols = validatorModel.getSelectedPastedCols();
//		Collections.sort(pasteRows);
//		Collections.sort(pasteRows);
		
		System.out.println("CopiedRows Size: "+ copiedRows.size()+" | "+copiedRows);
		System.out.println("CopiedColumns Size: "+ copiedCols.size()+" | "+ copiedCols);
//		int selectedColumnIndex = table.getSelectedColumn();
		int selectedColumnIndex = selectedCol;

		for(int copiedColumnCount=0; copiedColumnCount<copiedCols.size(); copiedColumnCount++)
		{
			int copiedCol = ((Integer)(copiedCols.get(copiedColumnCount))).intValue();
			selectedRow = tmpSelectedRow;
			for(int count=0;count<copiedRows.size();count++ )
			{
				int copiedRow = ((Integer)(copiedRows.get(count))).intValue();
				//check for disabled rows
				if(!isDisabledRow(selectedRow))
				{
					System.out.println("Count : "+count+ " ,copiedRow: "+ copiedRow + " , copiedCol: "+ copiedCol);
					String key = CommonAppletUtil.getDataKey(copiedRow, copiedCol);
					System.out.println("Key : "+ key);
					Object value = dataMap.get(key );
					System.out.println("Value : "+ value);

					TableColumnModel columnModel = table.getColumnModel();
					SpecimenColumnModel scm = (SpecimenColumnModel)columnModel.getColumn(selectedColumnIndex).getCellEditor();
					scm.updateComponentValue(selectedRow,value.toString() );
					SpecimenColumnModel scmRenderer = (SpecimenColumnModel)columnModel.getColumn(selectedColumnIndex).getCellRenderer();
					scmRenderer.updateComponent(selectedRow );
					//To update the model.
					updateTableModel(selectedRow, selectedColumnIndex,  value);

					System.out.println("Row updated : " + selectedRow );
				}
				selectedRow = selectedRow + 1;
			}
			selectedColumnIndex++;
		}
		SwingUtilities.updateComponentTreeUI(table);
	}
	*/
	protected void doPasteData(int selectedRow, int selectedCol, List valueList)
	{
		if(selectedRow < AppletConstants.SPECIMEN_COMMENTS_ROW_NO)
		{
			TableColumnModel columnModel = table.getColumnModel();
			Object value = valueList.get(0);
			SpecimenColumnModel scm = (SpecimenColumnModel)columnModel.getColumn(selectedCol).getCellEditor();
			System.out.println("\n\n\nselectedRow : "+ selectedRow);
			if(scm.isCellEnabled(selectedRow))
			{
				scm.updateComponentValue(selectedRow,value.toString() );
				SpecimenColumnModel scmRenderer = (SpecimenColumnModel)columnModel.getColumn(selectedCol).getCellRenderer();
				scmRenderer.updateComponent(selectedRow );
				CommonAppletUtil.getMultipleSpecimenTableModel(table).setValueAt(value,selectedRow,selectedCol);
			}
		}
	}
	
	protected boolean isDisabledRow(int rowNo)
	{
		boolean result=false;
		//rowNo == AppletConstants.SPECIMEN_PARENT_ROW_NO  removed after discussing with Santosh to enable Parent Specimen.
		if(rowNo == AppletConstants.SPECIMEN_STORAGE_LOCATION_ROW_NO   ) // rowNo == AppletConstants.SPECIMEN_PARENT_ROW_NO 
		{
			result =  true;
		}
		System.out.println(rowNo + " Row is disabled : "+ result  );
		return result ;
	}
	
	/**
	* @return JS method name for this button.
	*/
	protected String getJSMethodName()
	{
	return "showErrorMessage";
	}
	
	/**
	 * @return total coumn count
	 */
	protected int getColumnCount()
	{
		return CommonAppletUtil.getMultipleSpecimenTableModel(table).getTotalColumnCount();		
	}

}
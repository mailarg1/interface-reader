package interfaceReaders;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.JFileChooser;
import Utils.FileValidations;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;


public class LauncherButtonClickListener implements ActionListener{
	
	private Launcher parent;
	private File feedFileName;
	private String savedFilename;
	private String interfaceName;
	private Hashtable<String, Record> layouts;
	private Hashtable<String, Integer> recType_currenRow;
	private int lineNumber = 0;
	boolean multiRecordType;

	    	
	
	public LauncherButtonClickListener(Launcher launcher){
		parent = launcher;
		layouts  = new Hashtable<String, Record>();
		recType_currenRow = new Hashtable<String, Integer>();
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		
        String command = e.getActionCommand(); 
        parent.getDoneLabel().setVisible(false);
        
        if( command.equals(PublicConstants.BROWSE_BUTTON))  {
       	
        	JFileChooser fc = new JFileChooser();
        	int returnVal = fc.showOpenDialog(this.parent);
        	
        	if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();                
                                           
                parent.getDataFileTxtField().setText(file.getPath());
                parent.getDataFileTxtField().setVisible(true);
                parent.pack();
                
                
        	}              	
        }        
        else if( command.equals(PublicConstants.EXPORTTOEXCEL_BUTTON) )  {
        	        	
        	feedFileName = new File(parent.getDataFileTxtField().getText());
        	interfaceName = parent.getFeedComboBox().getSelectedItem().toString();
        	multiRecordType = parent.getParameters().get(interfaceName).getRecordTypeId() == 0 ? false : true;
            
        	JFileChooser fc = new JFileChooser();
        	fc.setSelectedFile(new File(feedFileName.getPath() + ".xlsx"));
        	int returnVal = fc.showSaveDialog(this.parent);
        	
        	
        	if (returnVal == JFileChooser.APPROVE_OPTION) {
        		
    			parent.getLogger().log(Level.INFO, 	"Interface Name: "+ parent.getParameters().get(interfaceName).getInterfaceName() + "\n"
						+	"Interface Layout file name: " + parent.getParameters().get(interfaceName).getLayoutFileName() + "\n"
						+	"Record Type (field name seq): " + parent.getParameters().get(interfaceName).getRecordTypeId() + "\n"
						+	"File selected: " + feedFileName.getPath()+ "\n");   
	            try {
	            	
	        		getReadyBusyState(true);
	                File file = fc.getSelectedFile();   
	                savedFilename = file.getPath();	            	
					readLayout(); 					
					readFile();
		            getReadyBusyState(false);
		            parent.getDoneLabel().setVisible(true);	

				} catch (Exception e1){					
					parent.displayErrorMessage(e1, interfaceName);
					new File(savedFilename).delete();
					System.exit(1);
				}
        	}
        }
        else if( command.equals(PublicConstants.REFRESHINTERFACELIST_BUTTON) )  {  
        	
        	try{
        		parent.readLayoutParameters();
			} catch (Exception e1){
				parent.displayErrorMessage(e1, null);
				System.exit(1);
			}
        	parent.loadInterfaceList();
        	parent.getLogger().log(Level.INFO, PublicConstants.REFRESHINTERFACELIST_BUTTON + " button pressed - Interface list reloaded\n");
        }      		
		
	}



	private void getReadyBusyState(boolean wait) {
		if (wait){
			parent.getLabel().setEnabled(false);
			parent.getFeedComboBox().setEnabled(false);
			parent.getDataFileTxtField().setEnabled(false);
			parent.getBrowseButton().setEnabled(false);
			parent.getRefreshInterfaceListButton().setEnabled(false);
			parent.getExportToExcelButton().setEnabled(false);
		}
		else{
			parent.getLabel().setEnabled(true);
			parent.getFeedComboBox().setEnabled(true);
			parent.getDataFileTxtField().setEnabled(true);
			parent.getBrowseButton().setEnabled(true);
			parent.getRefreshInterfaceListButton().setEnabled(true);
			parent.getExportToExcelButton().setEnabled(true);			
		}
		
	}

	private void readFile() throws Exception  {
		
		FileReader fr = null;
		LineNumberReader lr = null;
		FileOutputStream excelFile = null;		
		String 	str = null, 
				recordType = null, 
				headers = "",
				filename = feedFileName.getPath();
		Sheet sheet_ml;
		Record record_RF = null;
		Integer index = new Integer(2);
		
	    try{
	        fr = new FileReader(filename);
	        lr = new LineNumberReader(fr);
	        excelFile = new FileOutputStream(savedFilename);	        
	        SXSSFWorkbook wb = new SXSSFWorkbook(PublicConstants.EXCEL_MAX_MEMORY_ROWS.intValue());
	    	
    		while((str = lr.readLine()) != null && str.length() > 0){
    			
    			lineNumber ++;    			
    			recordType = getRecordType(str);
    			
    			record_RF = getRecordLayout(str, recordType);   	    	
	    		
	    		sheet_ml = wb.getSheet(recordType);
	    		
	    		if (sheet_ml == null){
	    			
		    		sheet_ml = wb.createSheet(recordType);		    		
	    	        initializeSheet(sheet_ml, wb, headers, record_RF);
	    		}
	    			
	    		record_RF = layouts.get(recordType);	    		
	    		if(recType_currenRow.get(sheet_ml.getSheetName()) == PublicConstants.EXCEL_MAX_ROWS.intValue()){
	    			wb.write(excelFile);
	    			if (excelFile != null) 
	    				excelFile.close();
	    			excelFile = new FileOutputStream(feedFileName.getPath() + "_" + index + ".xlsx");
	    			wb = new SXSSFWorkbook(PublicConstants.EXCEL_MAX_MEMORY_ROWS.intValue());
	    			index++;
	    			sheet_ml = wb.createSheet(recordType);
	    			initializeSheet(sheet_ml, wb, headers, record_RF);
	    		}
    	        writeLine(getInterfaceRecord(str, record_RF.getArrayLV()), false, wb, sheet_ml);		    			

    		}
		        
		        wb.write(excelFile);
		        System.out.println("done");    	

	     }finally{
        	if (fr != null)
        		fr.close();
        	if (lr != null)
        		lr.close();	
			if (excelFile != null) 
				excelFile.close();
	     }
	}
	
	private void initializeSheet(Sheet sheet_IS, Workbook wb_IS, String headers_IS, Record record_IS) throws Exception {
		
		Iterator<LayoutVector> it = record_IS.getArrayLV().iterator();
        while (it.hasNext()){
        	LayoutVector lv = it.next();
        	headers_IS += lv.getFieldName() + ";";
        }
        
        recType_currenRow.put(sheet_IS.getSheetName(), new Integer(0));
        writeLine(headers_IS, true, wb_IS, sheet_IS);
        headers_IS = ""; 		
	}

	private Record getRecordLayout(String str_GRL, String recordType_GRL) throws Exception{
		
		Record record_GRL = null;
		String filename_GRL = feedFileName.getPath();
		InterfaceParameters lparam_GRL = parent.getParameters().get(interfaceName);
		
		record_GRL = layouts.get(recordType_GRL);
		
		
		//Validation
		if (record_GRL == null){
			throw new Exception("While reading file \""+ filename_GRL + "\" at line "+ lineNumber + ":\n"
					+ "Record type \"" + (recordType_GRL.equalsIgnoreCase("0")? null : recordType_GRL) + ("\" not found in layout \"" + lparam_GRL.getLayoutFileName() + "\". Record type start position " + lparam_GRL.getRecordTypeStartPosition() + ", Length "+ lparam_GRL.getRecordTypeLenght() +".\n")
					+ "Record: " + str_GRL + "\n");			
		}
		
		//Validation
		if (!recordIsVariableLenght(record_GRL)) //it means record length is fixed
		if (record_GRL.getSize() != str_GRL.length()){
			throw new Exception("While reading file \""+ filename_GRL + "\" at line "+ lineNumber + ":\n"
					+ "File Record length "+ str_GRL.length() + " does not match to the layout record length "+ record_GRL.getSize() + " for record type \"" + recordType_GRL + "\".\n"
					+ "Record: " + str_GRL + "\n"); 
		}
		
		return record_GRL;
	}

	private String getRecordType(String str) throws Exception{
		
		InterfaceParameters lparam_GRT = parent.getParameters().get(interfaceName);		
		String 	recordType_out,
				filename_GRT = feedFileName.getPath();
			
				
    	if (!multiRecordType) //only one record type
    		recordType_out = feedFileName.getName();
    	else{
    		try{
    			recordType_out = str.substring(lparam_GRT.getRecordTypeStartPosition(), lparam_GRT.getRecordTypeStartPosition() + lparam_GRT.getRecordTypeLenght()).trim();
    		}catch(StringIndexOutOfBoundsException e){
    			throw new StringIndexOutOfBoundsException("While reading file \""+ filename_GRT + "\" at line "+ lineNumber + ":\n"
    					+ "SubString is unable to get Record Type. Record type start position " + lparam_GRT.getRecordTypeStartPosition() + ", Length "+ lparam_GRT.getRecordTypeLenght() +". \n"
    					+ "Record: " + str + "\n");
    		}
    	}
    	
    	//Validation
    	if (recordType_out == null || recordType_out.equalsIgnoreCase(""))
		throw new Exception("While reading file \""+ filename_GRT + "\" at line "+ lineNumber + ":\n"
				+ "Record type = \"" + recordType_out + "\" is not expected for a Multi record types interface. . Record type start position " + lparam_GRT.getRecordTypeStartPosition() + ", Length "+ lparam_GRT.getRecordTypeLenght() +".\n"
				+ "Record: " + str + "\n"); 
    	
    	return recordType_out;
	}

	private boolean recordIsVariableLenght(Record record_RF) {
		boolean output_RIVL = false;
		ArrayList<LayoutVector> a_LV_RIVL = record_RF.getArrayLV();
		
		Iterator<LayoutVector> it = a_LV_RIVL.iterator();
		while (it.hasNext()){
			LayoutVector lv_RIVL = it.next();
			if (lv_RIVL.isVariableOcurrence()){
				output_RIVL = true;
				break;
			}				
		}
		return output_RIVL;
	}

	private String getInterfaceRecord(String str, ArrayList<LayoutVector> al_GTR) {
		String record = "";
		int size, decimals;
		FileValidations fv = new FileValidations();	
		Iterator<LayoutVector> it = al_GTR.iterator();
		String str_remaining = str;
		String value = null;

		while (it.hasNext()){
			LayoutVector lv = it.next();
			
			if(lv.getDelimiter() == null){ //Fixed position
				
				size 		= lv.getFieldSize(); 
				decimals 	= lv.getFieldDecimals();
				
				value 			= str_remaining.substring(0,size);
				str_remaining 	= str_remaining.substring(size,str_remaining.length()); 				
				       		        	
	        	
	        	if (decimals == 0)
	        		record += value + ";";
	        	else
	        		if (lv.isCobolFlag())
	        			record += fv.checkBigDec(value,decimals,true).toString() +";";        		
	        		else
	        			record += fv.checkBigDec(value,decimals,false).toString() +";";				
			}else{ //it uses delimiter
				
				if (String.valueOf(str_remaining.charAt(0)).equals(lv.getDelimiter())) {
					str_remaining = str_remaining.substring(1, str_remaining.length()); //eliminate delimiter symbol from the beginning
				}
				
				if(lv.isVariableOcurrence()){
					String[] fields = str_remaining.split(lv.getDelimiter().equals(PublicConstants.PIPE) ? PublicConstants.SCAPECHARS+PublicConstants.PIPE : lv.getDelimiter());
					for(int i=0; i < fields.length ; i++){
						record += fields[i] + ";";
					}					
				}else{
					String[] fields = str_remaining.split(lv.getDelimiter().equals(PublicConstants.PIPE) ? PublicConstants.SCAPECHARS+PublicConstants.PIPE : lv.getDelimiter(), 2);
					record += fields[0] + ";";
					str_remaining = fields[1];
					
				}
			}					
		}
		
		fv = null;	
		it = null;
		str_remaining = null;
		value = null;
		return record;
	}

	private void writeLine(String record, boolean isHeader, Workbook wb, Sheet sheet)throws Exception{
		

	    Row row = sheet.createRow((int)recType_currenRow.get(sheet.getSheetName()));	    
	    String fields[] = record.split(";");
	    CellStyle style = wb.getCellStyleAt((short) 0); //get style from first cell of the current sheet
	    int currentRow;
	    
	    if (isHeader){		   
	    	
	    	CellStyle style_hdr = wb.createCellStyle();	    	
		    Font font = wb.createFont();
		    font.setColor(IndexedColors.WHITE.getIndex());
		    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		    
		    style_hdr.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
		    style_hdr.setFillPattern(CellStyle.SOLID_FOREGROUND);
		    style_hdr.setFont(font);
		    style = style_hdr;
	    }
	    
	    for(int i=0; i < fields.length; i++){
	    	Cell cell = row.createCell(i);
	    	cell.setCellValue(fields[i]);
	    	cell.setCellStyle(style);
	    }
	    
	    currentRow = recType_currenRow.remove(sheet.getSheetName());
	    recType_currenRow.put(sheet.getSheetName(), (currentRow + 1));
		
	}	

	private void readLayout() throws IOException{
        
		InterfaceParameters lparam_RL = parent.getParameters().get(parent.getFeedComboBox().getSelectedItem());
		InputStream inp = null;
		boolean headerRow, isRecordTypeStartPositionSet = false;
		int recordTypeStartPosition = 0, fieldNameMaxLength = 0, fieldNameLength = 0;
		LayoutVector lv_RL = null;
		Record record_RL = null;		
		layouts.clear();
		
		try {			
				inp = new FileInputStream("./layouts/" + lparam_RL.getLayoutFileName());
				HSSFWorkbook wb;
				wb = new HSSFWorkbook(inp);
				Sheet sheet = wb.getSheetAt(0);
				headerRow = true;
				
				for (Row row : sheet) {
					
					if (headerRow)
						headerRow = false;
					else {
						
						lv_RL = getLayoutField(row);				
						record_RL = layouts.get(lv_RL.getRecordType());
						
						if (record_RL == null){ //key not found	
							
							record_RL = new Record();
							record_RL.setId(lv_RL.getRecordType());
						}
						
						record_RL.getArrayLV().add(lv_RL);
						record_RL.setSize(record_RL.getSize() + lv_RL.getFieldSize());
						layouts.put(lv_RL.getRecordType(), record_RL);						

						
						if (lv_RL.getRecordType() != null){ //it means there is more than one record type	
							
							//Calculate RecordType start position and RecordType length on sheet #1.					
							if(lv_RL.getFieldNameSeq() < lparam_RL.getRecordTypeId() && !isRecordTypeStartPositionSet)
								recordTypeStartPosition += lv_RL.getFieldSize();															
							
							if (lv_RL.getFieldNameSeq() == lparam_RL.getRecordTypeId() && !isRecordTypeStartPositionSet){
								parent.getParameters().get(interfaceName).setRecordTypeLenght(lv_RL.getFieldSize());
								parent.getParameters().get(interfaceName).setRecordTypeStartPosition(recordTypeStartPosition);
								isRecordTypeStartPositionSet = true;
							}							
						}
						
						//Get field name max length to print layout reading log
						fieldNameLength = lv_RL.getFieldName().length();
						if (fieldNameLength > fieldNameMaxLength)
							fieldNameMaxLength = fieldNameLength;
					}
					

				}
		
				Set<String> keys = layouts.keySet();
				Iterator<String> it = keys.iterator();
				String cobolFlag, variableOcurrence, log_var = "";
				
				
				while (it.hasNext()){
					String k = it.next();
					Record rec = layouts.get(k);
					log_var += "Record: " + rec.getId() + " | " + "Size: " + rec.getSize() + "\n";
//					System.out.println("Record: " + rec.getId() + " | " + "Size: " + rec.getSize());
					Iterator<LayoutVector> it2 = rec.getArrayLV().iterator();					
					while (it2.hasNext()){
						LayoutVector lv = it2.next();
						if (lv.isCobolFlag()) cobolFlag = "CF: TRUE"; else cobolFlag = "CF: FALSE";
						if (lv.isVariableOcurrence()) variableOcurrence = "VO: TRUE"; else variableOcurrence = "VO: FALSE";
						
						log_var += "RT: " + lv.getRecordType() + " | " + "FNS: " + lv.getFieldNameSeq() + " | " + "FN: " + String.format("%-"+fieldNameMaxLength+"s", lv.getFieldName()).replace(' ', ' ') + " | " + "FS: " + lv.getFieldSize() + " | " + "FD: " + lv.getFieldDecimals() + " | " + cobolFlag + " | " + "D: " + lv.getDelimiter() + " | " + variableOcurrence + "\n";
//						System.out.println("RT: " + lv.getRecordType() + " | " + "FNS: " + lv.getFieldNameSeq() + " | " + "FN: " + String.format("%-"+fieldNameMaxLength+"s", lv.getFieldName()).replace(' ', ' ') + " | " + "FS: " + lv.getFieldSize() + " | " + "FD: " + lv.getFieldDecimals() + " | " + cobolFlag + " | " + "D: " + lv.getDelimiter() + " | " + variableOcurrence);								       		
					}
					log_var += "\n";
//					System.out.print("\n");
					
				}
				log_var += "------------" + "\n";
				log_var += "RecordType Start position: "+parent.getParameters().get(interfaceName).getRecordTypeStartPosition() + ", Length: " + parent.getParameters().get(interfaceName).getRecordTypeLenght() + "\n";
				log_var += "Read Layout DONE" + "\n";
				parent.getLogger().log(Level.INFO, log_var);
//				System.out.println("------------");
//				System.out.println("RecordType Start position: "+parent.getParameters().get(interfaceName).getRecordTypeStartPosition() + ", Length: " + parent.getParameters().get(interfaceName).getRecordTypeLenght());
//				System.out.println("Read Layout DONE");
			
		} finally{
			if (inp != null) 
				inp.close();
		}
	}

	private LayoutVector getLayoutField(Row row) {
		LayoutVector lv_GFL = new LayoutVector();
				
		String recordType = row.getCell(0).getStringCellValue();
		lv_GFL.setRecordType(recordType.isEmpty() ? feedFileName.getName() : recordType);
		
		lv_GFL.setFieldNameSeq(Double.valueOf(row.getCell(1).getNumericCellValue()).intValue());
		lv_GFL.setFieldName(row.getCell(2).getStringCellValue());

		if (row.getCell(3) == null || row.getCell(3).getCellType() == Cell.CELL_TYPE_BLANK)
			lv_GFL.setFieldSize(0);
		else
			lv_GFL.setFieldSize(Double.valueOf(row.getCell(3).getNumericCellValue()).intValue());
		
		if (row.getCell(4) == null || row.getCell(4).getCellType() == Cell.CELL_TYPE_BLANK)
			lv_GFL.setFieldDecimals(0);
		else
			lv_GFL.setFieldDecimals(Double.valueOf(row.getCell(4).getNumericCellValue()).intValue());
		
		String isCobol = row.getCell(5).getStringCellValue();
		lv_GFL.setCobolFlag(isCobol.isEmpty() ? false : true);
		
		String delimiter = row.getCell(6).getStringCellValue();
		lv_GFL.setDelimiter(delimiter.isEmpty() ? null : delimiter);	
		
		String isVariable = row.getCell(7).getStringCellValue();
		lv_GFL.setVariableOcurrence(isVariable.isEmpty() ? false : true);		
		
		return lv_GFL;
	}
}

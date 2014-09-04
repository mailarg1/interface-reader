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
		Sheet sheet;
		Record record = null;
		Integer index = new Integer(2);
		
	    try{
	        fr = new FileReader(filename);
	        lr = new LineNumberReader(fr);
	        excelFile = new FileOutputStream(savedFilename);	        
	        SXSSFWorkbook wb = new SXSSFWorkbook(PublicConstants.EXCEL_MAX_MEMORY_ROWS.intValue());
	    	
    		while((str = lr.readLine()) != null && str.length() > 0){
    			
    			lineNumber ++;    			
    			recordType = getRecordType(str);
    			
    			record = getRecordLayout(str, recordType);   	    	
	    		
	    		sheet = wb.getSheet(recordType);
	    		
	    		if (sheet == null){
	    			
		    		sheet = wb.createSheet(recordType);		    		
	    	        initializeSheet(sheet, wb, headers, record);
	    		}
	    			
	    		record = layouts.get(recordType);	    		
	    		if(recType_currenRow.get(sheet.getSheetName()) == PublicConstants.EXCEL_MAX_ROWS.intValue()){
	    			wb.write(excelFile);
	    			if (excelFile != null) 
	    				excelFile.close();
	    			excelFile = new FileOutputStream(feedFileName.getPath() + "_" + index + ".xlsx");
	    			wb = new SXSSFWorkbook(PublicConstants.EXCEL_MAX_MEMORY_ROWS.intValue());
	    			index++;
	    			sheet = wb.createSheet(recordType);
	    			initializeSheet(sheet, wb, headers, record);
	    		}
    	        writeLine(getInterfaceRecord(str, record.getArrayLV()), false, wb, sheet);		    			

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
	
	private void initializeSheet(Sheet sheet, Workbook wb, String headers, Record record) throws Exception {
		
		Iterator<LayoutVector> it = record.getArrayLV().iterator();
        while (it.hasNext()){
        	LayoutVector lv = it.next();
        	headers += lv.getFieldName() + ";";
        }
        
        recType_currenRow.put(sheet.getSheetName(), new Integer(0));
        writeLine(headers, true, wb, sheet);
        headers = ""; 		
	}

	private Record getRecordLayout(String str, String recordType) throws Exception{
		
		Record record = null;
		String filename = feedFileName.getPath();
		InterfaceParameters lparam_GRL = parent.getParameters().get(interfaceName);
		
		record = layouts.get(recordType);
		
		
		//Validation
		if (record == null){
			throw new Exception("While reading file \""+ filename + "\" at line "+ lineNumber + ":\n"
					+ "Record type \"" + (recordType.equalsIgnoreCase("0")? null : recordType) + ("\" not found in layout \"" + lparam_GRL.getLayoutFileName() + "\". Record type start position " + lparam_GRL.getRecordTypeStartPosition() + ", Length "+ lparam_GRL.getRecordTypeLenght() +".\n")
					+ "Record: " + str + "\n");			
		}
		
		//Validation
		if (!recordIsVariableLenght(record)) //it means record length is fixed
		if (record.getSize() != str.length()){
			throw new Exception("While reading file \""+ filename + "\" at line "+ lineNumber + ":\n"
					+ "File Record length "+ str.length() + " does not match to the layout record length "+ record.getSize() + " for record type \"" + recordType + "\".\n"
					+ "Record: " + str + "\n"); 
		}
		
		return record;
	}

	private String getRecordType(String str) throws Exception{
		
		InterfaceParameters parameters = parent.getParameters().get(interfaceName);		
		String 	recordType_out,
				filename = feedFileName.getPath();
			
				
    	if (!multiRecordType) //only one record type
    		recordType_out = feedFileName.getName();
    	else{
    		try{
    			recordType_out = str.substring(parameters.getRecordTypeStartPosition(), parameters.getRecordTypeStartPosition() + parameters.getRecordTypeLenght()).trim();
    		}catch(StringIndexOutOfBoundsException e){
    			throw new StringIndexOutOfBoundsException("While reading file \""+ filename + "\" at line "+ lineNumber + ":\n"
    					+ "SubString is unable to get Record Type. Record type start position " + parameters.getRecordTypeStartPosition() + ", Length "+ parameters.getRecordTypeLenght() +". \n"
    					+ "Record: " + str + "\n");
    		}
    	}
    	
    	//Validation
    	if (recordType_out == null || recordType_out.equalsIgnoreCase(""))
		throw new Exception("While reading file \""+ filename + "\" at line "+ lineNumber + ":\n"
				+ "Record type = \"" + recordType_out + "\" is not expected for a Multi record types interface. . Record type start position " + parameters.getRecordTypeStartPosition() + ", Length "+ parameters.getRecordTypeLenght() +".\n"
				+ "Record: " + str + "\n"); 
    	
    	return recordType_out;
	}

	private boolean recordIsVariableLenght(Record record) {
		boolean output = false;
		ArrayList<LayoutVector> al_LV = record.getArrayLV();
		
		Iterator<LayoutVector> it = al_LV.iterator();
		while (it.hasNext()){
			LayoutVector lv = it.next();
			if (lv.isVariableOcurrence()){
				output = true;
				break;
			}				
		}
		return output;
	}

	private String getInterfaceRecord(String str, ArrayList<LayoutVector> al_LV) {
		String record = "";
		int size, decimals;
		FileValidations fv = new FileValidations();	
		Iterator<LayoutVector> it = al_LV.iterator();
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
        
		InterfaceParameters parameters = parent.getParameters().get(parent.getFeedComboBox().getSelectedItem());
		InputStream inp = null;
		boolean headerRow, isRecordTypeStartPositionSet = false;
		int recordTypeStartPosition = 0, fieldNameMaxLength = 0, fieldNameLength = 0;
		LayoutVector lv = null;
		Record record = null;		
		layouts.clear();
		
		try {			
				inp = new FileInputStream("./layouts/" + parameters.getLayoutFileName());
				HSSFWorkbook wb;
				wb = new HSSFWorkbook(inp);
				Sheet sheet = wb.getSheetAt(0);
				headerRow = true;
				
				for (Row row : sheet) {
					
					if (headerRow)
						headerRow = false;
					else {
						
						lv = getLayoutField(row);				
						record = layouts.get(lv.getRecordType());
						
						if (record == null){ //key not found	
							
							record = new Record();
							record.setId(lv.getRecordType());
						}
						
						record.getArrayLV().add(lv);
						record.setSize(record.getSize() + lv.getFieldSize());
						layouts.put(lv.getRecordType(), record);						

						
						if (lv.getRecordType() != null){ //it means there is more than one record type	
							
							//Calculate RecordType start position and RecordType length on sheet #1.					
							if(lv.getFieldNameSeq() < parameters.getRecordTypeId() && !isRecordTypeStartPositionSet)
								recordTypeStartPosition += lv.getFieldSize();															
							
							if (lv.getFieldNameSeq() == parameters.getRecordTypeId() && !isRecordTypeStartPositionSet){
								parent.getParameters().get(interfaceName).setRecordTypeLenght(lv.getFieldSize());
								parent.getParameters().get(interfaceName).setRecordTypeStartPosition(recordTypeStartPosition);
								isRecordTypeStartPositionSet = true;
							}							
						}
						
						//Get field name max length to print layout reading log
						fieldNameLength = lv.getFieldName().length();
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
						LayoutVector lv2 = it2.next();
						if (lv2.isCobolFlag()) cobolFlag = "CF: TRUE"; else cobolFlag = "CF: FALSE";
						if (lv2.isVariableOcurrence()) variableOcurrence = "VO: TRUE"; else variableOcurrence = "VO: FALSE";
						
						log_var += "RT: " + lv2.getRecordType() + " | " + "FNS: " + lv2.getFieldNameSeq() + " | " + "FN: " + String.format("%-"+fieldNameMaxLength+"s", lv2.getFieldName()).replace(' ', ' ') + " | " + "FS: " + lv2.getFieldSize() + " | " + "FD: " + lv2.getFieldDecimals() + " | " + cobolFlag + " | " + "D: " + lv2.getDelimiter() + " | " + variableOcurrence + "\n";
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
		LayoutVector lv = new LayoutVector();
				
		String recordType = row.getCell(0).getStringCellValue();
		lv.setRecordType(recordType.isEmpty() ? feedFileName.getName() : recordType);
		
		lv.setFieldNameSeq(Double.valueOf(row.getCell(1).getNumericCellValue()).intValue());
		lv.setFieldName(row.getCell(2).getStringCellValue());

		if (row.getCell(3) == null || row.getCell(3).getCellType() == Cell.CELL_TYPE_BLANK)
			lv.setFieldSize(0);
		else
			lv.setFieldSize(Double.valueOf(row.getCell(3).getNumericCellValue()).intValue());
		
		if (row.getCell(4) == null || row.getCell(4).getCellType() == Cell.CELL_TYPE_BLANK)
			lv.setFieldDecimals(0);
		else
			lv.setFieldDecimals(Double.valueOf(row.getCell(4).getNumericCellValue()).intValue());
		
		String isCobol = row.getCell(5).getStringCellValue();
		lv.setCobolFlag(isCobol.isEmpty() ? false : true);
		
		String delimiter = row.getCell(6).getStringCellValue();
		lv.setDelimiter(delimiter.isEmpty() ? null : delimiter);	
		
		String isVariable = row.getCell(7).getStringCellValue();
		lv.setVariableOcurrence(isVariable.isEmpty() ? false : true);		
		
		return lv;
	}
}

package interfaceReaders;


import java.awt.Color;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Launcher extends JFrame{

	private static final long serialVersionUID = 1L;
	private static final String version = "Version 1.0";
	private JLabel label;
	private JLabel doneLabel;
	private JComboBox feedComboBox;
	private JTextField dataFileTxtField;
	private JButton browseButton;		
	private JButton exportToExcelButton;
	private JButton refreshInterfaceListButton;
	private Hashtable<String, InterfaceParameters> parameters;
	private Handler handler;
	private Logger logger;
	
	
	public Launcher(){
        label 						= new JLabel(PublicConstants.INTERFACE_LABEL);
        doneLabel					= new JLabel(PublicConstants.DONE_LABEL);
        dataFileTxtField			= new JTextField("");
        feedComboBox 				= new JComboBox();        
        exportToExcelButton			= new JButton(PublicConstants.EXPORTTOEXCEL_BUTTON);
        browseButton 				= new JButton(PublicConstants.BROWSE_BUTTON);
        refreshInterfaceListButton 	= new JButton(PublicConstants.REFRESHINTERFACELIST_BUTTON);
        parameters					= new Hashtable<String, InterfaceParameters>();
        LauncherButtonClickListener launcherListener = new LauncherButtonClickListener(this);
        
 
        try{
    		handler = new FileHandler(PublicConstants.LOGFILE);
    		handler.setFormatter(new SimpleFormatter());
    		logger = Logger.getLogger(this.getClass().getName());
    		logger.addHandler(handler);
    		
    		readLayoutParameters();
    		
    		logger.log(Level.INFO, "Parameters file read\n");

        }catch (Exception e){
        	displayErrorMessage(e, null);
        	
        	if (handler != null)        		
        		handler.close();
        	System.exit(1);
        }
        loadInterfaceList();
        dataFileTxtField.setVisible(false);
        
        float[] hsbvals = {0,0,0}; //initialization
        Color.RGBtoHSB(0, 184, 0, hsbvals);
		doneLabel.setForeground(Color.getHSBColor(hsbvals[0],hsbvals[1],hsbvals[2]));
        doneLabel.setVisible(false);

        
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
              .addComponent(label)       		
              .addComponent(feedComboBox)
              .addComponent(dataFileTxtField)
              .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            		  .addComponent(browseButton)
            		  .addComponent(exportToExcelButton)) 
              .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            		  .addComponent(refreshInterfaceListButton)
            		  .addComponent(doneLabel))
        );
        
        layout.setVerticalGroup(layout.createSequentialGroup()
        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        	.addComponent(label)
        	.addComponent(feedComboBox)
        	.addComponent(dataFileTxtField)
        	.addComponent(browseButton)        	
        	.addComponent(refreshInterfaceListButton))
        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        	.addComponent(exportToExcelButton)
        	.addComponent(doneLabel))
        	
        );
        
        layout.linkSize(browseButton,exportToExcelButton); //it makes buttons the same size        
        browseButton.addActionListener(launcherListener); 
        exportToExcelButton.addActionListener(launcherListener);
        refreshInterfaceListButton.addActionListener(launcherListener);
                
        setTitle("Interface Reader" + " - " + version);
        pack();
        setLocationRelativeTo(null); //it centers the window
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);		
	}
	
	
	public void readLayoutParameters() throws Exception{
		
		InputStream inp = null;
		boolean headerRow = true;
		InterfaceParameters lp_RLP = null;
		parameters.clear();
		
		try {
			inp = new FileInputStream("./config/LayoutParameters.xls");
			HSSFWorkbook wb = new HSSFWorkbook(inp);
			Sheet sheet = wb.getSheetAt(0);
			
			for (Row row : sheet) {
								
				if (headerRow)
					headerRow = false;
				else {											
					lp_RLP  = new InterfaceParameters();
					lp_RLP.setInterfaceName(row.getCell(0).getStringCellValue());
					lp_RLP.setLayoutFileName(row.getCell(1).getStringCellValue());													
					
					parameters.put(lp_RLP.getInterfaceName(), lp_RLP);
				}
			}
						
		}finally{			
			if (inp != null)
				inp.close();
		}
	}	
	
	public void loadInterfaceList() {
		feedComboBox.removeAllItems();
        Set<String> keys = parameters.keySet(); 
        
        //Sort the Set converting it into List
        List<String> list = new ArrayList<String>(keys);
        Collections.sort(list);
        // End of sorting
        
        Iterator<String> it = list.iterator();
        while (it.hasNext()){
        	feedComboBox.addItem(it.next());
        }
        pack();
		
	}	
	
	protected void displayErrorMessage(Throwable e, String interfaceName) {
		
		String output_msg = e.getClass().toString() + " : " + e.getMessage() + "\n";
	    StackTraceElement elements[] = e.getStackTrace();
	    for (int i = 0, n = elements.length; i < n; i++) {       
	    	output_msg += elements[i].getFileName() + ":" + elements[i].getLineNumber() + " >> " + elements[i].getMethodName() + "()\n";
	    }
		    
		  // create a JTextArea
		  JTextArea textArea = new JTextArea(6, 50);
		  textArea.setText(output_msg);
		  textArea.setEditable(false);
		  logger.log(Level.SEVERE, output_msg);
			   
		  // wrap a scroll pane around it
		  JScrollPane scrollPane = new JScrollPane(textArea);

		JOptionPane.showMessageDialog(this, scrollPane, interfaceName == null? "Reading Parameters" : interfaceName, JOptionPane.ERROR_MESSAGE);
	}	
	
	/**
	 * @return the label
	 */
	public JLabel getLabel() {
		return label;
	}


	/**
	 * @param label the label to set
	 */
	public void setLabel(JLabel label) {
		this.label = label;
	}


	/**
	 * @return the feedComboBox
	 */
	public JComboBox getFeedComboBox() {
		return feedComboBox;
	}


	/**
	 * @param feedComboBox the feedComboBox to set
	 */
	public void setFeedComboBox(JComboBox feedComboBox) {
		this.feedComboBox = feedComboBox;
	}

	/**
	 * @return the dataFileTxtField
	 */
	public JTextField getDataFileTxtField() {
		return dataFileTxtField;
	}


	/**
	 * @param dataFileTxtField the dataFileTxtField to set
	 */
	public void setDataFileTxtField(JTextField dataFileTxtField) {
		this.dataFileTxtField = dataFileTxtField;
	}
	
	/**
	 * @return the browseButton
	 */
	public JButton getBrowseButton() {
		return browseButton;
	}


	/**
	 * @param browseButton the browseButton to set
	 */
	public void setBrowseButton(JButton browseButton) {
		this.browseButton = browseButton;
	}


	/**
	 * @return the exportToExcelButton
	 */
	public JButton getExportToExcelButton() {
		return exportToExcelButton;
	}


	/**
	 * @param exportToExcelButton the exportToExcelButton to set
	 */
	public void setExportToExcelButton(JButton exportToExcelButton) {
		this.exportToExcelButton = exportToExcelButton;
	}


	/**
	 * @return the seeLayoutButton
	 */
	public JButton getSeeLayoutButton() {
		return refreshInterfaceListButton;
	}


	/**
	 * @param seeLayoutButton the seeLayoutButton to set
	 */
	public void setSeeLayoutButton(JButton seeLayoutButton) {
		this.refreshInterfaceListButton = seeLayoutButton;
	}


	/**
	 * @return the refreshInterfaceListButton
	 */
	public JButton getRefreshInterfaceListButton() {
		return refreshInterfaceListButton;
	}


	/**
	 * @param refreshInterfaceListButton the refreshInterfaceListButton to set
	 */
	public void setRefreshInterfaceListButton(JButton refreshInterfaceListButton) {
		this.refreshInterfaceListButton = refreshInterfaceListButton;
	}


	/**
	 * @return the parameters
	 */
	public Hashtable<String, InterfaceParameters> getParameters() {
		return parameters;
	}


	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Hashtable<String, InterfaceParameters> parameters) {
		this.parameters = parameters;
	}


	/**
	 * @return the doneLabel
	 */
	public JLabel getDoneLabel() {
		return doneLabel;
	}


	/**
	 * @param doneLabel the doneLabel to set
	 */
	public void setDoneLabel(JLabel doneLabel) {
		this.doneLabel = doneLabel;
	}


	/**
	 * @return the handler
	 */
	public Handler getHandler() {
		return handler;
	}


	/**
	 * @param handler the handler to set
	 */
	public void setHandler(Handler handler) {
		this.handler = handler;
	}



	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return logger;
	}


	/**
	 * @param logger the logger to set
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Launcher().setVisible(true);

	}

}

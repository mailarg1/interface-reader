package interfaceReaders;

import java.util.ArrayList;

public class Record {
	private String id;
	private int size;
	private boolean variableLenght;
	private int recordTypeStartPosition;
	private int recordTypeLenght;		
	private ArrayList<LayoutVector> arrayLV;
	
	
	public Record(){
		id = null;
		size = 0;
		variableLenght = false;
		arrayLV = new ArrayList<LayoutVector>();
	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}


	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	
	/**
	 * @return the variableLenght
	 */
	public boolean isVariableLenght() {
		return variableLenght;
	}


	/**
	 * @param variableLenght the variableLenght to set
	 */
	public void setVariableLenght(boolean variableLenght) {
		this.variableLenght = variableLenght;
	}	

	/**
	 * @return the recordTypeStartPosition
	 */
	public int getRecordTypeStartPosition() {
		return recordTypeStartPosition;
	}


	/**
	 * @param recordTypeStartPosition the recordTypeStartPosition to set
	 */
	public void setRecordTypeStartPosition(int recordTypeStartPosition) {
		this.recordTypeStartPosition = recordTypeStartPosition;
	}


	/**
	 * @return the recordTypeLenght
	 */
	public int getRecordTypeLenght() {
		return recordTypeLenght;
	}


	/**
	 * @param recordTypeLenght the recordTypeLenght to set
	 */
	public void setRecordTypeLenght(int recordTypeLenght) {
		this.recordTypeLenght = recordTypeLenght;
	}


	/**
	 * @return the arrayLV
	 */
	public ArrayList<LayoutVector> getArrayLV() {
		return arrayLV;
	}


	/**
	 * @param arrayLV the arrayLV to set
	 */
	public void setArrayLV(ArrayList<LayoutVector> arrayLV) {
		this.arrayLV = arrayLV;
	}


	
}

package interfaceReaders;

import java.util.ArrayList;

public class Record {
	private String id;
	private int size;
	private ArrayList<LayoutVector> arrayLV;
	
	
	public Record(){
		id = null;
		size = 0;
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

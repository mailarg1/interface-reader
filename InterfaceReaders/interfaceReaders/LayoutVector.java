package interfaceReaders;

public class LayoutVector {
	
	private String recordType;
	private int fieldNameSeq;
	private String fieldName;
	private int fieldSize;
	private int fieldDecimals;
	private boolean cobolFlag;
	private String delimiter;
	private boolean variableOcurrence;
	private boolean isRecordType;
	

	public LayoutVector(){
		
	}
	
	/**
	 * @param recordType the recordType to set
	 */
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	/**
	 * @return the recordType
	 */
	public String getRecordType() {
		return recordType;
	}
	
	/**
	 * @return the fieldNameSeq
	 */
	public int getFieldNameSeq() {
		return fieldNameSeq;
	}
	
	/**
	 * @param fieldNameSeq the fieldNameSeq to set
	 */
	public void setFieldNameSeq(int fieldNameSeq) {
		this.fieldNameSeq = fieldNameSeq;
	}
	
	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the fieldSize
	 */
	public int getFieldSize() {
		return fieldSize;
	}

	/**
	 * @param fieldSize the fieldSize to set
	 */
	public void setFieldSize(int fieldSize) {
		this.fieldSize = fieldSize;
	}

	/**
	 * @return the fieldDecimals
	 */
	public int getFieldDecimals() {
		return fieldDecimals;
	}

	/**
	 * @param fieldDecimals the fieldDecimals to set
	 */
	public void setFieldDecimals(int fieldDecimals) {
		this.fieldDecimals = fieldDecimals;
	}

	/**
	 * @return the cobolFlag
	 */
	public boolean isCobolFlag() {
		return cobolFlag;
	}

	/**
	 * @param cobolFlag the cobolFlag to set
	 */
	public void setCobolFlag(boolean cobolFlag) {
		this.cobolFlag = cobolFlag;
	}


	/**
	 * @return the delimiter
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return the variableOcurrence
	 */
	public boolean isVariableOcurrence() {
		return variableOcurrence;
	}

	/**
	 * @param variableOcurrence the variableOcurrence to set
	 */
	public void setVariableOcurrence(boolean variableOcurrence) {
		this.variableOcurrence = variableOcurrence;
	}

	/**
	 * @param isRecordType the isRecordType to set
	 */
	public void setIsRecordType(boolean isRecordType) {
		this.isRecordType = isRecordType;
	}

	/**
	 * @return the isRecordType
	 */
	public boolean isRecordType() {
		return isRecordType;
	}

}

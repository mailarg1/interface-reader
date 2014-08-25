package interfaceReaders;

public final class InterfaceParameters {

	private String interfaceName;
	private String layoutFileName;
	private int recordTypeId;
	private int recordTypeStartPosition;
	private int recordTypeLenght;

	
	/**
	 * @return the interfaceName
	 */
	public String getInterfaceName() {
		return interfaceName;
	}
	/**
	 * @param interfaceName the interfaceName to set
	 */
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	/**
	 * @return the layoutFileName
	 */
	public String getLayoutFileName() {
		return layoutFileName;
	}
	/**
	 * @param layoutFileName the layoutFileName to set
	 */
	public void setLayoutFileName(String layoutFileName) {
		this.layoutFileName = layoutFileName;
	}

	/**
	 * @return the recordTypeId
	 */
	public int getRecordTypeId() {
		return recordTypeId;
	}
	/**
	 * @param recordTypeId the recordTypeId to set
	 */
	public void setRecordTypeId(int recordTypeId) {
		this.recordTypeId = recordTypeId;
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

}

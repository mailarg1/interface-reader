package Utils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class FileValidations {

	  public FileValidations(){
		  
	  }
	  
	  public BigDecimal checkBigDec (String str, int decimals, boolean isCobol){
		    COBOLSigns sign = new COBOLSigns();
		    String val = null;
		    
		    if (isCobol) 
		    	val = sign.signConverter(str);
		    else
		    	val = str;
		    
		    BigDecimal bd1;
		    try {
		      bd1 = new BigDecimal(val);
		      BigInteger bi = new BigInteger("10");
		      BigDecimal bd2 = new BigDecimal(bi.pow(decimals));
		      return (bd1.divide(bd2, decimals, BigDecimal.ROUND_HALF_UP));
		    }
		    catch(NumberFormatException e) {
		      if (decimals == 0){ return new BigDecimal("0"); }
		      String zero = "0.";
		      String pad = new String();
		      for (int i =0; i < decimals; i++){
		        pad = pad + "0";
		      }
		      zero = zero + pad;
		      return new BigDecimal(zero);
		    }
		  }
}

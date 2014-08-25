
// Copyright (c) 2000 Citicorp

package Utils;
import java.math.*;

public class COBOLFileWriter extends Object {

  public String createFields(long val, int len, String type){
      String s = createFields(Long.toString(val), len, type);
      return s;
  }

  public String createFields(double val, int len, String type){
      String s = createFields(Double.toString(val), len, type);
      return s;
  }

  public String createFields(BigDecimal val, int len, String type){
      String s = createFields(val.toString(), len, type);
      return s;
  }

  public String createFields(String val, int len, String type){
    String result = new String();
    String pad = new String();
    String sign = new String();
    String newVal = new String();

    char[] temp = val.toCharArray();
    for (int i = 0; i < temp.length; i++){
      if (temp[i] != '.') {
        newVal = newVal + temp[i];
        val = newVal;
      }
    }

    int fill = len - val.length();

    //alpha numeric
    if (type.toUpperCase().equals("C")) {
      for (int i = 0; i < fill; i++) {
        pad = pad + " ";
      }
      result = val + pad;
    }
    // numeric
    else if (type.toUpperCase().equals("N")) {
      for (int i = 0; i < fill; i++) {
        pad = pad + "0";
      }
      result = pad + val;
    }
    // signed numeric
    else if (type.toUpperCase().equals("S")) {
      if (val.charAt(0) == '-') {
        sign = "neg";
        val = val.replace('-','0');
      }
      else { sign = "pos"; }
      val = setSign(val, sign);
      for (int i = 0; i < fill; i++) {
        pad = pad + "0";
      }
      result = pad + val;
    }
    else { return "Invalid Type"; }

    return result;
  }

  private String setSign(String val, String sign){
    String lastChar = new String();
    int num = Integer.parseInt(val.substring(val.length()-1, val.length()));
    String[] positive = { "{", "A", "B", "C", "D", "E", "F", "G", "H", "I" };
    String[] negetive = { "}", "J", "K", "L", "M", "N", "O", "P", "Q", "R" };
    if (sign.equals("pos")){
      lastChar = positive[num];
    }
    else if (sign.equals("neg")){
      lastChar = negetive[num];
    }
    val = val.substring(0, val.length()-1) + lastChar;
    return val;
  }
} 
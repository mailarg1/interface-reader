

package Utils;
public class COBOLSigns {

  public COBOLSigns() {
  }

  public String signConverter(String val) {
    int size = val.length();
    String indicator = val.substring(size - 1, size);
    char old[] = indicator.toCharArray();
    char numbVal = old[0];
    String multiplier = "";

    if (indicator.equals("{")) {
      numbVal = '0';
    }
    else if (indicator.equals("A")) {
      numbVal = '1';
    }
    else if (indicator.equals("B")) {
      numbVal = '2';
    }
    else if (indicator.equals("C")) {
      numbVal = '3';
    }
    else if (indicator.equals("D")) {
      numbVal = '4';
    }
    else if (indicator.equals("E")) {
      numbVal = '5';
    }
    else if (indicator.equals("F")) {
      numbVal = '6';
    }
    else if (indicator.equals("G")) {
      numbVal = '7';
    }
    else if (indicator.equals("H")) {
      numbVal = '8';
    }
    else if (indicator.equals("I")) {
      numbVal = '9';
    }
    else if (indicator.equals("J")) {
      numbVal = '1';
      multiplier = "-";
    }
    else if (indicator.equals("K")) {
      numbVal = '2';
      multiplier = "-";
    }
    else if (indicator.equals("L")) {
      numbVal = '3';
      multiplier = "-";
    }
    else if (indicator.equals("M")) {
      numbVal = '4';
      multiplier = "-";
    }
    else if (indicator.equals("N")) {
      numbVal = '5';
      multiplier = "-";
    }
    else if (indicator.equals("O")) {
      numbVal = '6';
      multiplier = "-";
    }
    else if (indicator.equals("P")) {
      numbVal = '7';
      multiplier = "-";
    }
    else if (indicator.equals("Q")) {
      numbVal = '8';
      multiplier = "-";
    }
    else if (indicator.equals("R")) {
      numbVal = '9';
      multiplier = "-";
    }
    else if (indicator.equals("}")) {
      numbVal = '0';
      multiplier = "-";
    }
    val = multiplier + val.replace(old[0], numbVal);
    return val;
  }
} 
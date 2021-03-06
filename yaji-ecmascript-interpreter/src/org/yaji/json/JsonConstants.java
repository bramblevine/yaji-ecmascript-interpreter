/* Generated By:JavaCC: Do not edit this line. JsonConstants.java */
package org.yaji.json;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface JsonConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int BEGIN_ARRAY = 5;
  /** RegularExpression Id. */
  int BEGIN_OBJECT = 6;
  /** RegularExpression Id. */
  int END_ARRAY = 7;
  /** RegularExpression Id. */
  int END_OBJECT = 8;
  /** RegularExpression Id. */
  int NAME_SEPARATOR = 9;
  /** RegularExpression Id. */
  int VALUE_SEPARATOR = 10;
  /** RegularExpression Id. */
  int FLOATING_POINT_LITERAL = 11;
  /** RegularExpression Id. */
  int EXPONENT = 12;
  /** RegularExpression Id. */
  int STRING_LITERAL = 13;
  /** RegularExpression Id. */
  int HEX = 14;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\r\"",
    "\"\\n\"",
    "\"[\"",
    "\"{\"",
    "\"]\"",
    "\"}\"",
    "\":\"",
    "\",\"",
    "<FLOATING_POINT_LITERAL>",
    "<EXPONENT>",
    "<STRING_LITERAL>",
    "<HEX>",
    "\"false\"",
    "\"null\"",
    "\"true\"",
  };

}

package FESI.Data;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import FESI.Exceptions.RangeError;
import FESI.Interpreter.Evaluator;

public class NumberPrototypeTest {

    private Evaluator evaluator;

    @Before
    public void setUp() throws Exception {
        evaluator = new Evaluator();
    }

    @Test
    public void testToPrecisionNaNreturnsNan() throws Exception {
        ESObject number = ESNumber.valueOf(Double.NaN).toESObject(evaluator);
        ESValue value = number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESNumber.valueOf(5) });
        assertEquals(new ESString("NaN"), value);
    }

    @Test
    public void testToPrecisionInfinity() throws Exception {
        ESObject number = ESNumber.valueOf(Double.POSITIVE_INFINITY).toESObject(evaluator);
        ESValue value = number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESNumber.valueOf(5) });
        assertEquals(new ESString("Infinity"), value);
    }

    @Test
    public void testToPrecisionNegativeInfinity() throws Exception {
        ESObject number = ESNumber.valueOf(Double.NEGATIVE_INFINITY).toESObject(evaluator);
        ESValue value = number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESNumber.valueOf(5) });
        assertEquals(new ESString("-Infinity"), value);
    }

    @Test
    public void testToPrecisionInteger() throws Exception {
        ESObject number = ESNumber.valueOf(1).toESObject(evaluator);
        ESValue value = number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESNumber.valueOf(5) });
        assertEquals(new ESString("1.0000"), value);
    }

    @Test
    public void testToPrecisionZero() throws Exception {
        ESObject number = ESNumber.valueOf(0).toESObject(evaluator);
        ESValue value = number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESNumber.valueOf(4) });
        assertEquals(new ESString("0000"), value);
    }

    @Test
    public void testToPrecisionDouble() throws Exception {
        ESObject number = ESNumber.valueOf(123.456).toESObject(evaluator);
        ESValue value = number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESNumber.valueOf(2) });
        assertEquals(new ESString("1.2e+2"), value);
    }

//    @Test
//    public void testToPrecisionOneAsPerSpec() throws Exception {
//        ESObject number = ESNumber.valueOf(123.456).toESObject(evaluator);
//        ESValue value = number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESNumber.valueOf(1) });
//        assertEquals(new ESString("1.e+2"), value);
//    }
//
    @Test
    public void testToPrecisionOneAsPerWeb() throws Exception {
        ESObject number = ESNumber.valueOf(123.456).toESObject(evaluator);
        ESValue value = number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESNumber.valueOf(1) });
        assertEquals(new ESString("1e+2"), value);
    }

    @Test
    public void toPrecisionShouldRoundHalfUp() throws Exception {
        ESObject number = ESNumber.valueOf(123.450).toESObject(evaluator);
        ESValue value = number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESNumber.valueOf(4) });
        assertEquals(new ESString("123.5"), value);
    }
    
    @Test
    public void toPrecisonShouldReturnExactValue() throws Exception {
        ESObject number = ESNumber.valueOf(123).toESObject(evaluator);
        ESValue value = number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESNumber.valueOf(3) });
        assertEquals(new ESString("123"), value);
    }
    
    @Test
    public void toPrecisonShouldHandleLeadingZeroPadded() throws Exception {
        ESObject number = ESNumber.valueOf(123.3456e-4).toESObject(evaluator);
        ESValue value = number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESNumber.valueOf(4) });
        assertEquals(new ESString("0.01233"), value);
    }
    
    @Test
    public void toPrecisonShouldHandleVerySmall() throws Exception {
        ESObject number = ESNumber.valueOf(-123.3456e-21).toESObject(evaluator);
        ESValue value = number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESNumber.valueOf(4) });
        assertEquals(new ESString("-1.233e-19"), value);
    }
    
    @Test
    public void toPrecisonWithUndefinedShouldReturnSameAsToString() throws Exception {
        ESObject number = ESNumber.valueOf(-123.3456e-21).toESObject(evaluator);
        ESValue toPrecisionValue = number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESUndefined.theUndefined });
        ESValue toStringValue = number.doIndirectCall(evaluator, number, "toString", new ESValue[] { } );
        assertEquals(toStringValue,toPrecisionValue);
    }
    
    @Test(expected=RangeError.class)
    public void toPrecisonThrowsRangeErrorForPrecisionForGreaterThan21() throws Exception {
        ESObject number = ESNumber.valueOf(123.450).toESObject(evaluator);
        number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESNumber.valueOf(22) });
    }
    
    @Test(expected=RangeError.class)
    public void toPrecisonThrowsRangeErrorForPrecisionForLessThan1() throws Exception {
        ESObject number = ESNumber.valueOf(123.450).toESObject(evaluator);
        number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESNumber.valueOf(0) });
    }
    
//    @Test
//    public void performance() throws Exception {
//        Random r = new Random(1);
//        for( int i=0; i<1000000; i++) {
//            double d = (r.nextDouble()-0.5) * Double.MAX_VALUE;
//            int precision = r.nextInt(20)+1;
//            ESObject number = ESNumber.valueOf(d).toESObject(evaluator);
//            ESValue result = number.doIndirectCall(evaluator, number, "toPrecision", new ESValue[] { ESNumber.valueOf(precision) });
////            System.out.println(d + " ("+precision+") "+result);
//        }
//    }
    
//    @Test(expected=RangeError.class)
//    public void toFixedThrowsRangeErrorForFractionDigitsForGreaterThan20() throws Exception {
//        ESObject number = ESNumber.valueOf(123.450).toESObject(evaluator);
//        number.doIndirectCall(evaluator, number, "toFixed", new ESValue[] { ESNumber.valueOf(21) });
//    }
}

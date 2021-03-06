// ESNumber.java
// FESI Copyright (c) Jean-Marc Lugrin, 1999
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2 of the License, or (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package FESI.Data;

import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

import org.yaji.json.JsonState;
import org.yaji.log.ILog;
import org.yaji.log.Logs;

import FESI.Exceptions.EcmaScriptException;
import FESI.Interpreter.Evaluator;

/**
 * Implements the number primitive value as a double
 * 
 * GT Modification to optimise the handling of integer values. Put in primarily
 * for a Sun Fire T1000 as it only has one FPU shared between multiple cores.
 */

public final class ESNumber extends ESPrimitive {
    private static final long serialVersionUID = 3695852528633071467L;
    private static final ILog log = Logs.getLog(ESNumber.class);
    
    public static final ESNumber ZERO;
    public static final ESNumber NEGATIVE_ZERO;
    public static final Double DOUBLE_ZERO;
    public static final Double DOUBLE_NEGATIVE_ZERO;
    public static final ESNumber NaN;
    
    // The value
    private final double value;
    private long longValue;
    private boolean isLongValue;
    private static String[] toStringCache = new String[128];
    private static ESNumber[] cache;
    private static int maxESNumberCached;
    static {
        try {
            maxESNumberCached = Integer.getInteger(
                    "com.gtnet.fesi.esnumcachesize", 128).intValue();
        } catch (NumberFormatException e) {
            maxESNumberCached = 128;
            log.asError("Property com.gtnet.fesi.esnumcachesize set to invalid" +
            		" value : using 128", e);
        }
        final int low = -128;
        cache = new ESNumber[(maxESNumberCached - low) + 1];
        int j = low;
        for (int k = 0; k < cache.length; k++) {
            cache[k] = new ESNumber(j++);
        }
        
        DOUBLE_ZERO = Double.valueOf(0.0);
        DOUBLE_NEGATIVE_ZERO = Double.valueOf(-0.0);
        
        ZERO = ESNumber.valueOf(DOUBLE_ZERO.longValue());
        NEGATIVE_ZERO = new ESNumber(DOUBLE_NEGATIVE_ZERO.doubleValue());
        
        NaN = ESNumber.valueOf(Double.NaN);
    }

    /**
     * Create a new double with a specific value
     * 
     * @param value
     *            The (immutable) value
     */
    private ESNumber(double value) {
        long intValue = (long) value;
        if (intValue == value && !isNegativeZero(value)) {
            this.longValue = intValue;
            this.isLongValue = true;
        } else {
            this.isLongValue = false;
        }
        this.value = value;
    }

    private boolean isNegativeZero(double d) {
        return d == 0 && Double.doubleToLongBits(d) < 0;
    }

    public static ESNumber valueOf(long l) {
        if (l >= -128 && l <= maxESNumberCached) {
            return cache[(int) l + 128];
        }
        return new ESNumber(l);
    }

    public static ESNumber valueOf(double d) {
        Double dObject = Double.valueOf(d);
        if (DOUBLE_ZERO.equals(dObject)) {
            return ESNumber.ZERO;
        }
        else if (DOUBLE_NEGATIVE_ZERO.equals(dObject)) {
            return ESNumber.NEGATIVE_ZERO;
        } else if (((long) d) == d) {
            return ESNumber.valueOf((long) d);
        }
        return new ESNumber(d);
    }

    private ESNumber(long value) {
        this.longValue = value;
        this.isLongValue = true;
        this.value = value;
    }

    // overrides
    @Override
    public int getTypeOf() {
        return EStypeNumber;
    }

    // overrides
    @Override
    public String getTypeofString() {
        return "number";
    }

    // overrides
    @Override
    public boolean isNumberValue() {
        return true;
    }

    // overrides
    @Override
    public double doubleValue() {
        return value;
    }

    // overrides
    @Override
    public boolean booleanValue() {
        if (isLongValue) {
            return !(longValue == 0);
        }
        return !(Double.isNaN(value) || value == 0.0);
    }

    // overrides
    @Override
    public String toString() {
        if (isLongValue) {
            return toLongString();
        }
        double d = doubleValue();
        if (Double.isNaN(d)) {
            return "NaN";
        }
        if (d == 0.0) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        if (d < 0) {
            sb.append('-');
            d = -d;
        }
        if (Double.isInfinite(d)) {
            sb.append("Infinity");
        } else { 
            int n = (int)Math.floor(Math.log10(d));
            double sd = (d/Math.pow(10, n))/10.0d;
            String s = toString(sd,16);
            int distinctLength = s.length()-1; 
            while (s.charAt(distinctLength) == '0') {
                distinctLength --;
            }
            s = s.substring(2, distinctLength+1);
            int k = s.length();
            if (k <= n && n <= 20) {
                sb.append(s);
                pad(sb,n-k+1,'0');
            } else if (0 <= n && n <= 20) {
                sb.append(s.substring(0,n+1)).append('.').append(s.substring(n+1));
            } else if (-6 <= n && n < 0) {
                sb.append("0.");
                pad(sb,-n-1,'0');
                sb.append(s);
            } else {
                if (k == 1) {
                    sb.append(s);
                } else {
                    sb.append(s.charAt(0)).append('.').append(s.substring(1));
                }
                sb.append('e');
                if (n >= 0) {
                    sb.append('+');
                }
                sb.append(n);
            }
        }
        return sb.toString();
    }

    private String toLongString() {
        String string;
        if (longValue < 128 && longValue >= 0) {
            int intValue = (int) longValue;
            String stringValue = toStringCache[intValue];
            if (stringValue == null) {
                stringValue = Integer.toString(intValue);
                toStringCache[intValue] = stringValue;
            }
            string = stringValue;
        } else {
            string = Long.toString(longValue);
        }
        return string;
    }

    // overrides
    @Override
    public ESObject toESObject(Evaluator evaluator) throws EcmaScriptException {
        NumberPrototype theObject = null;
        ESObject np = evaluator.getNumberPrototype();
        theObject = new NumberPrototype(np, evaluator);
        theObject.value = this;
        return theObject;
    }

    // overrides
    @Override
    public ESValue toESNumber() {
        return this;
    }

    @Override
    public int toInt32() throws EcmaScriptException {
        if (isLongValue) {
            return (int) longValue;
        }
        return super.toInt32();
    }

    @Override
    public long toUInt32() throws EcmaScriptException {
        if (isLongValue) {
            return longValue & 0xffffffffL;
        }
        return super.toUInt32();
    }

    // overrides
    @Override
    public Object toJavaObject() {
        Object o = null;
        if (isLongValue) {
            if (((byte) longValue) == longValue) {
                o = Byte.valueOf((byte) longValue);
            } else if (((short) longValue) == longValue) {
                o = Short.valueOf((short) longValue);
            } else if (((int) longValue) == longValue) {
                o = Integer.valueOf((int) longValue);
            } else {
                o = Long.valueOf(longValue);
            }
        } else {
            o = Double.valueOf(doubleValue());
        }
        return o;
    }

    // overrides
    @Override
    public String toDetailString() {
        return "ES:#'" + toString() + "'";
    }

    /**
     * Advanced FESI GT Modified: 12/10/2004 Support for subtypes (storing
     * values in hashset)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Advanced FESI GT Modified: 12/10/2004 Support for subtypes (storing
     * values in hashset)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ESValue) {
            // handle the special cases of ESNull and ESUndefined
            if (o == ESNull.theNull || o == ESUndefined.theUndefined) {
                return false;
            }

            try {
                return doubleValue() == ((ESValue) o).doubleValue();
            } catch (EcmaScriptException e) {
                return false;
            }
        }

        return false;

    }

    @Override
    public long longValue() throws EcmaScriptException {
        if (isLongValue) {
            return longValue;
        }
        return super.longValue();
    }

    @Override
    public boolean isIntegerValue() {
        return isLongValue;
    }

    @Override
    public boolean isUInt32() {
        return isLongValue && longValue >= 0L && longValue < 0x100000000L;
    }
    
    @Override
    public boolean equalsSameType(ESValue v2) throws EcmaScriptException {
        if (isLongValue && v2.isIntegerValue()) {
            return longValue == v2.longValue();
        }
        double d1 = doubleValue();
        double d2 = v2.doubleValue();
        return (d1 == d2);
    }

    @Override
    public ESValue addValue(ESValue v2) throws EcmaScriptException {
        if (isLongValue && v2.isIntegerValue()) {
            return ESNumber.valueOf(longValue + v2.longValue());
        }
        return super.addValue(v2);
    }

    @Override
    public ESValue subtract(ESValue v2) throws EcmaScriptException {
        if (isLongValue && v2.isIntegerValue()) {
            return ESNumber.valueOf(longValue - v2.longValue());
        }
        return super.subtract(v2);
    }

    @Override
    public ESValue decrement() throws EcmaScriptException {
        if (isLongValue) {
            return ESNumber.valueOf(longValue - 1);
        }
        return super.decrement();
    }

    @Override
    public ESValue increment() throws EcmaScriptException {
        if (isLongValue) {
            return ESNumber.valueOf(longValue + 1);
        }
        return super.increment();
    }

    @Override
    public ESValue multiply(ESValue v2) throws EcmaScriptException {
        if (isLongValue && v2.isIntegerValue()) {
            return ESNumber.valueOf(longValue * v2.longValue());
        }
        return super.multiply(v2);
    }

    @Override
    public int compareNumbers(ESValue v2) throws EcmaScriptException {
        if (isLongValue && v2.isIntegerValue()) {
            return (longValue() < v2.longValue()) ? ESValue.COMPARE_TRUE
                    : ESValue.COMPARE_FALSE;
        }
        return super.compareNumbers(v2);
    }

    public static ESValue valueOf(String image) {
        return new ESNumber(new ESString(image).doubleValue());
    }

    @Override
    public void toJson(Appendable appendable, JsonState state, String parentPropertyName) throws IOException, EcmaScriptException {
        if (!isFinite()) {
            appendable.append("null");
        } else {
            super.toJson(appendable, state, parentPropertyName);
        }
    }
    
    @Override
    public boolean canJson() {
        return true;
    }
    
    static void pad(StringBuilder s, int count, char ch) {
        for(int i=0; i<count; i++) {
            s.append(ch);
        }
    }

    static String toString(double d,int p) {
        Appendable sb = new StringBuilder();
        Formatter formatter = new Formatter(sb,Locale.US);
        String format = "%."+p+"g";
        formatter.format(format, Double.valueOf(d));
        return sb.toString();
    
    }
    
    @Override
    public boolean isFinite() {
        return isLongValue || !(Double.isNaN(value) || Double.isInfinite(value));
    }
    
    @Override
    protected boolean sameValueTypeChecked(ESValue other) throws EcmaScriptException {
        double d = doubleValue();
        double otherD = other.doubleValue();
        
        return (Double.isNaN(d) && Double.isNaN(otherD))
                || ( d == 0.0 && otherD == 0.0 && Double.valueOf(d).compareTo(Double.valueOf(otherD)) == 0)
                || ( d != 0.0 && d == otherD );
    }
}

package edu.macalester.tagrelatedness;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.BigReal;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

public class KendallsCorrelation {
	
	public static void main(String args[]){
		long tiedx = new BigInteger("1853724906736").longValue();
		long tiedy = new BigInteger("5328254589").longValue();
		long numpairs = new BigInteger("4094164093575").longValue();
		BigSquareRoot sqrt = new BigSquareRoot();
		BigInteger first = new BigInteger(""+(numpairs-tiedx));
		BigInteger second = new BigInteger(""+(numpairs-tiedy));
		BigDecimal result = sqrt.get(first.multiply(second));
		System.out.println( result.doubleValue() );
		
	}

    /**
     * This code is taken from a patch submitted for the Apache Commons 3.3
     * library by The Apache Software Foundation 
     * at: https://issues.apache.org/jira/browse/MATH-814
     * 
     * 
     * Computes the Kendall's Tau rank correlation matrix for the columns of
     * the input matrix.
     *
     * @param matrix matrix with columns representing variables to correlate
     * @return correlation matrix
     * @author Matt Adereth
     */
    static public RealMatrix computeCorrelationMatrix(final RealMatrix matrix) {
        int nVars = matrix.getColumnDimension();
        RealMatrix outMatrix = new BlockRealMatrix(nVars, nVars);
        for (int i = 0; i < nVars; i++) {
            for (int j = 0; j < i; j++) {
                double corr = correlation(matrix.getColumn(i), matrix.getColumn(j));
                outMatrix.setEntry(i, j, corr);
                outMatrix.setEntry(j, i, corr);
            }
            outMatrix.setEntry(i, i, 1d);
        }
        return outMatrix;
    }
    
    /**
     * Computes the Kendall's Tau rank correlation matrix for the columns of
     * the input rectangular array.  The columns of the array represent values
     * of variables to be correlated.
     *
     * @param matrix matrix with columns representing variables to correlate
     * @return correlation matrix
     */
    static public RealMatrix computeCorrelationMatrix(final double[][] matrix) {
       return computeCorrelationMatrix(new BlockRealMatrix(matrix));
    }

    /**
     * Computes the Kendall's Tau rank correlation coefficient between the two
     * arrays.
     *
     * @param xArray first data array
     * @param yArray second data array
     * @return Returns Kendall's Tau rank correlation coefficient for the two
     * arrays
     * @throws DimensionMismatchException if the arrays lengths do not match
     */
    static public double correlation(final double[] xArray, final double[] yArray) {
        if (xArray.length != yArray.length) {
            throw new DimensionMismatchException(xArray.length, yArray.length);
        }
        List<Double> xList = doubleArrayToList(xArray);
        List<Double> yList = doubleArrayToList(yArray);
        return correlation(xList, yList);
    }

    static private List<Double> doubleArrayToList(final double[] a) {
        List<Double> list = new ArrayList<Double>(a.length);
        for (int i = 0; i < a.length; i++) {
            list.add(a[i]);
        }
        return list;
    }

    static private class ComparablePair
        implements Comparable<ComparablePair> {
        
        private final Comparable x;
        private final Comparable y;

        public ComparablePair(Comparable x, Comparable y) {
            this.x = x;
            this.y = y;
        }

        public int compareTo(ComparablePair t) {
            int compX = x.compareTo(t.x);
            return compX != 0 ? compX : y.compareTo(t.y);
        }
    }

    /**
     * Computes the Kendall's Tau rank correlation coefficient between the two
     * lists.
     *
     * @param xs first data list
     * @param ys second data list
     * @return Returns Kendall's Tau rank correlation coefficient for the two
     * lists
     * @throws DimensionMismatchException if the list lengths do not match
     */
    static public <E extends Comparable<E>, F extends Comparable<F>> double correlation (
            final List<E> xs,
            final List<F> ys) {

        final int n = xs.size();
        final long numPairs = (long) n * (n - 1) / 2;

        ComparablePair[] pairs = new ComparablePair[n];
        for (int i = 0; i < n; i++) {
            pairs[i] = new ComparablePair(xs.get(i), ys.get(i));
        }

        Arrays.sort(pairs);

        long tiedXPairs = 0;
        long tiedXYPairs = 0;
        long consecutiveXTies = 1;
        long consecutiveXYTies = 1;
        ComparablePair prev = pairs[0];
        for (int i = 1; i < n; i++) {
            final ComparablePair curr = pairs[i];
            if (curr.x.equals(prev.x)) {
                consecutiveXTies++;
                if (curr.y.equals(prev.y)) {
                    consecutiveXYTies++;
                } else {
                    tiedXYPairs += consecutiveXYTies * (consecutiveXYTies - 1) / 2;
                    consecutiveXYTies = 1;
                }
            } else {
                tiedXPairs += consecutiveXTies * (consecutiveXTies - 1) / 2;
                consecutiveXTies = 1;
                tiedXYPairs += consecutiveXYTies * (consecutiveXYTies - 1) / 2;
                consecutiveXYTies = 1;
            }
            prev = curr;
        }
        tiedXPairs += consecutiveXTies * (consecutiveXTies - 1) / 2;
        tiedXYPairs += consecutiveXYTies * (consecutiveXYTies - 1) / 2;

        long swaps = 0;
        ComparablePair[] pairsDestination = new ComparablePair[n];
        for (int segmentSize = 1; segmentSize < n; segmentSize <<= 1) {
            for (int offset = 0; offset < n; offset += 2 * segmentSize) {
                int i = offset;
                final int iEnd = Math.min(i + segmentSize, n);
                int j = iEnd;
                final int jEnd = Math.min(j + segmentSize, n);

                int copyLocation = offset;
                while (i < iEnd || j < jEnd) {
                    if (i < iEnd) {
                        if (j < jEnd) {
                            if (pairs[i].y.compareTo(pairs[j].y) <= 0) {
                                pairsDestination[copyLocation] = pairs[i];
                                i++;
                            } else {
                                pairsDestination[copyLocation] = pairs[j];
                                j++;
                                swaps += iEnd - i;
                            }
                        } else {
                            pairsDestination[copyLocation] = pairs[i];
                            i++;
                        }
                    } else {
                        pairsDestination[copyLocation] = pairs[j];
                        j++;
                    }
                    copyLocation++;
                }
            }
            final ComparablePair[] pairsTemp = pairs;
            pairs = pairsDestination;
            pairsDestination = pairsTemp;

        }

        long tiedYPairs = 0;
        long consecutiveYTies = 1;
        prev = pairs[0];
        for (int i = 1; i < n; i++) {
            final ComparablePair curr = pairs[ i];
            if (curr.y.equals(prev.y)) {
                consecutiveYTies++;
            } else {
                tiedYPairs += consecutiveYTies * (consecutiveYTies - 1) / 2;
                consecutiveYTies = 1;
            }
            prev = curr;
        }
        tiedYPairs += consecutiveYTies * (consecutiveYTies - 1) / 2;

        long concordantMinusDiscordant = (long) numPairs - tiedXPairs - tiedYPairs
                + tiedXYPairs - 2 * swaps;
        
//      The formula for tau is annotated below. Here, it is done with several objects to handle big numbers
//      concordantMinusDiscordant / Math.sqrt((numPairs - tiedXPairs) * (numPairs - tiedYPairs));
                
        BigSquareRoot bigSqrt = new BigSquareRoot();
        
        BigInteger numPairsMinusTiedX = new BigInteger("" + (numPairs - tiedXPairs));
        BigInteger numPairsMinusTiedY = new BigInteger("" + (numPairs - tiedYPairs));
        
        BigReal numerator = new BigReal(concordantMinusDiscordant);
        BigDecimal denominator = bigSqrt.get(numPairsMinusTiedX.multiply(numPairsMinusTiedY));
        
        BigReal result = numerator.divide(new BigReal(denominator));
        
        return result.doubleValue();
    }

}


/**
 * This is a utility class that is used to find the square root of BigInteger objects.
 * This was obtained from:
 * 
 * http://www.merriampark.com/bigsqrt.htm
 * 
 * The "license" for this code, as written on the web site, is:
 * 
 * "The following source code (BigSquareRoot.java) is free for you to use in whatever way you 
 * wish, with no restrictions and no guarantees."
 * 
 * @author Michael Gilleland
 */
class BigSquareRoot {

	  private static BigDecimal ZERO = new BigDecimal ("0");
	  private static BigDecimal ONE = new BigDecimal ("1");
	  private static BigDecimal TWO = new BigDecimal ("2");
	  public static final int DEFAULT_MAX_ITERATIONS = 50;
	  public static final int DEFAULT_SCALE = 10;

	  private BigDecimal error;
	  private int iterations;
	  private boolean traceFlag;
	  private int scale = DEFAULT_SCALE;
	  private int maxIterations = DEFAULT_MAX_ITERATIONS;

	  //---------------------------------------
	  // The error is the original number minus
	  // (sqrt * sqrt). If the original number
	  // was a perfect square, the error is 0.
	  //---------------------------------------

	  public BigDecimal getError () {
	    return error;
	  }

	  //-------------------------------------------------------------
	  // Number of iterations performed when square root was computed
	  //-------------------------------------------------------------

	  public int getIterations () {
	    return iterations;
	  }

	  //-----------
	  // Trace flag
	  //-----------

	  public boolean getTraceFlag () {
	    return traceFlag;
	  }

	  public void setTraceFlag (boolean flag) {
	    traceFlag = flag;
	  }

	  //------
	  // Scale
	  //------

	  public int getScale () {
	    return scale;
	  }

	  public void setScale (int scale) {
	    this.scale = scale;
	  }

	  //-------------------
	  // Maximum iterations
	  //-------------------

	  public int getMaxIterations () {
	    return maxIterations;
	  }

	  public void setMaxIterations (int maxIterations) {
	    this.maxIterations = maxIterations;
	  }

	  //--------------------------
	  // Get initial approximation
	  //--------------------------

	  private static BigDecimal getInitialApproximation (BigDecimal n) {
	    BigInteger integerPart = n.toBigInteger ();
	    int length = integerPart.toString ().length ();
	    if ((length % 2) == 0) {
	      length--;
	    }
	    length /= 2;
	    BigDecimal guess = ONE.movePointRight (length);
	    return guess;
	  }

	  //----------------
	  // Get square root
	  //----------------

	  public BigDecimal get (BigInteger n) {
	    return get (new BigDecimal (n));
	  }

	  public BigDecimal get (BigDecimal n) {

	    // Make sure n is a positive number

	    if (n.compareTo (ZERO) <= 0) {
	      throw new IllegalArgumentException ();
	    }

	    BigDecimal initialGuess = getInitialApproximation (n);
	    trace ("Initial guess " + initialGuess.toString ());
	    BigDecimal lastGuess = ZERO;
	    BigDecimal guess = new BigDecimal (initialGuess.toString ());

	    // Iterate

	    iterations = 0;
	    boolean more = true;
	    while (more) {
	      lastGuess = guess;
	      guess = n.divide(guess, scale, BigDecimal.ROUND_HALF_UP);
	      guess = guess.add(lastGuess);
	      guess = guess.divide (TWO, scale, BigDecimal.ROUND_HALF_UP);
	      trace ("Next guess " + guess.toString ());
	      error = n.subtract (guess.multiply (guess));
	      if (++iterations >= maxIterations) {
	        more = false;
	      }
	      else if (lastGuess.equals (guess)) {
	        more = error.abs ().compareTo (ONE) >= 0;
	      }
	    }
	    return guess;

	  }

	  //------
	  // Trace
	  //------

	  private void trace (String s) {
	    if (traceFlag) {
	      System.out.println (s);
	    }
	  }

	  //----------------------
	  // Get random BigInteger
	  //----------------------

	  public static BigInteger getRandomBigInteger (int nDigits) {
	    StringBuffer sb = new StringBuffer ();
	    java.util.Random r = new java.util.Random ();
	    for (int i = 0; i < nDigits; i++) {
	      sb.append (r.nextInt (10));
	    }
	    return new BigInteger (sb.toString ());
	  }

	}


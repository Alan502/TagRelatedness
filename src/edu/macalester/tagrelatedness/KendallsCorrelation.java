package edu.macalester.tagrelatedness;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class KendallsCorrelation {

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
        System.out.println("Swaps: "+swaps+" Numpairs: "+numPairs+" Concordant - Discordant: "+concordantMinusDiscordant+" Tied X pairs: "+tiedXPairs+" Tied Y pairs: "+tiedYPairs+" Tied XY Pairs: "+tiedXYPairs);
        return concordantMinusDiscordant /
                Math.sqrt((numPairs - tiedXPairs) * (numPairs - tiedYPairs));

    }

}
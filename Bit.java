import java.util.Iterator;

/**
 * Low-level bitwise utility functions used throughout the engine.
 *
 * The board is represented as a 64-bit long where bit i corresponds to
 * position i on the 4×4×4 board (positions 0–63). These helpers wrap
 * common bit-manipulation patterns in readable, named methods.
 */
public class Bit {

    /** Returns true if bit at position pos is set in x. */
    public static boolean isSet(long x, int pos) {
        return (x & (1L << pos)) != 0;
    }

    /** Returns x with bit at position pos set to 1. */
    public static long set(long x, int pos) {
        return x |= (1L << pos);
    }

    /** Returns x with bit at position pos cleared to 0. */
    public static long clear(long x, int pos) {
        return x &= ~(1L << pos);
    }

    /** Returns a long with only bit at position set (a single-bit mask). */
    public static long mask(int position) {
        return 1L << position;
    }

    /**
     * Returns the bit position of the single set bit in mask.
     * Inverse of {@link #mask(int)}. Assumes exactly one bit is set.
     */
    public static int position(long mask) {
        return 63 - countLeadingZeros(mask);
    }

    /**
     * Counts the number of 1-bits in x using Brian Kernighan's algorithm.
     * Each iteration clears the lowest set bit in O(number of set bits).
     */
    public static int countOnes(long x) {
        int count = 0;
        while (x != 0) {
            x &= (x - 1);   // clear the lowest set bit
            count++;
        }
        return count;
    }

    /** Counts leading zero bits in x using a binary-search approach. */
    public static int countLeadingZeros(long x) {
        int count = 63;
        int shift = 32;
        long y;

        while (shift > 0) {
            y = x >> shift;
            if (y != 0) {
                count -= shift;
                x = y;
            }
            shift >>= 1;
        }
        return x == 0 ? count + 1 : count;
    }


    // -----------------------------------------------------------------------
    // Iterators: iterate over bit positions containing a 1.
    //
    //   Allows loops such as:
    //
    //     for (Integer position : Bit.ones(x)) {
    //         System.out.println(position);
    //     }
    // -----------------------------------------------------------------------

    /** Returns an iterator over the positions of all set bits in bits. */
    public static Iterator<Integer> iterator(long bits) {
        return new BitIterator(bits);
    }

    /** Returns an Iterable over the positions of all set bits in bits. */
    public static Iterable<Integer> ones(long bits) {
        return new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return new BitIterator(bits);
            }
        };
    }

    private static class BitIterator implements Iterator<Integer> {

        private long bits;

        public BitIterator(long bits) {
            this.bits = bits;
        }

        public boolean hasNext() {
            return this.bits != 0;
        }

        public Integer next() {
            long temp = this.bits & (this.bits - 1);   // clear the least significant 1 bit
            long mask = this.bits - temp;              // isolate the bit just cleared
            this.bits = temp;                          // advance iterator state
            return position(mask);                     // convert mask back to position index
        }
    }
}

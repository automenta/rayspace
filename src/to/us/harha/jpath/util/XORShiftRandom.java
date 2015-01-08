/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package to.us.harha.jpath.util;
import java.util.Random;

/**
 *
 * @author me
 */
//Extremely fast, medium quality randomness: http://www.javamex.com/tutorials/random_numbers/java_util_random_subclassing.shtml
public class XORShiftRandom extends Random {

    private long seed = System.nanoTime();

    public static final XORShiftRandom the = new XORShiftRandom(System.nanoTime());
    

    public XORShiftRandom() {
        super();
        this.seed = System.nanoTime();
    }

    public XORShiftRandom(long seed) {
        super();
        this.seed = seed;
    }

    @Override
    protected int next(final int nbits) {
        // N.B. Not thread-safe!
        long x = this.seed;
        x ^= (x << 21);
        x ^= (x >>> 35);
        x ^= (x << 4);
        this.seed = x;
        x &= ((1L << nbits) - 1);
        return (int) x;
    }

    @Override
    public void setSeed(long newSeed) {
        this.seed = newSeed;
    }

}

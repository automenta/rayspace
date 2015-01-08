/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tracer.objects;

/**
 *
 * @author me
 */
public class FastMath {
    private static final int BIG_ENOUGH_INT = 16 * 1024;
    private static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    private static final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5;
    public final static double EPSILON = 0.0001;

    public static final int floor(double x) {
        return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    public static int round(double x) {
        return (int) (x + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
    }

    public static int ceil(float x) {
        return BIG_ENOUGH_INT - (int) (BIG_ENOUGH_FLOOR - x); // credit: roquen
    }
    
    public static float max(float a, float b) {
        if (a > b) return a;
        return b;
    }
    
}

package to.us.harha.jpath.util;

public class TimeUtils
{

	private static final long NANOSECONDS_PER_SEC = 1000000000L;
        private static final long MILLISECONDS_PER_SEC = 1000L;

	public static double getTime()
	{
		//return (double) System.nanoTime() / (double) NANOSECONDS_PER_SEC;
            return (double) System.currentTimeMillis() / (double) MILLISECONDS_PER_SEC;
	}

}

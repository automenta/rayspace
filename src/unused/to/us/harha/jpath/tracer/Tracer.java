package unused.to.us.harha.jpath.tracer;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerArray;

import unused.to.us.harha.jpath.Config;
import unused.to.us.harha.jpath.Display;
import unused.to.us.harha.jpath.Input;
import unused.to.us.harha.jpath.Main;
import unused.to.us.harha.jpath.tracer.object.Material;
import unused.to.us.harha.jpath.tracer.object.TracerObject;
import unused.to.us.harha.jpath.util.Logger;
import unused.to.us.harha.jpath.util.MathUtils;
import unused.to.us.harha.jpath.util.math.Intersection;
import unused.to.us.harha.jpath.util.math.Mat4f;
import unused.to.us.harha.jpath.util.math.Primitive;
import unused.to.us.harha.jpath.util.math.Ray;
import unused.to.us.harha.jpath.util.math.Vec3f;

public class Tracer
{
	// Tracer variables and objects
	private Vec3f[]            m_samples;
	private Scene              m_scene;
	private Camera             m_camera;
	private Logger             m_log;

	// Multithreading
	private int                m_thread_amount;
	private AtomicIntegerArray m_samples_taken;

	// Static constant objects to minimize object creation during tracing
	private static final Vec3f COLOR_BLACK = new Vec3f();
	private static final Vec3f COLOR_DEBUG = new Vec3f(1.0f, 0.0f, 1.0f);

	public Tracer(int thread_amount, int resolution)
	{
		m_log = new Logger(this.getClass().getName());
		m_thread_amount = thread_amount;
		m_samples = new Vec3f[resolution];

		if (m_thread_amount > 1)
			m_samples_taken = new AtomicIntegerArray((m_thread_amount / 2) * (m_thread_amount / 2));
		else
			m_samples_taken = new AtomicIntegerArray(1);

		clearSamples();

		m_scene = new Scene();
		m_camera = m_scene.getCameras().get(0);

		m_log.printMsg("Tracer instance has been initalized, using " + m_thread_amount + " threads!");
	}

	/*
	 * Update the scene
	 */
	public void update(float delta, Input input)
	{
		// Temporary way of handling input, this is just bad but whatever.. it kinda works
		if (input.getKey(Input.KEY_W))
		{
			m_camera.setPos(Vec3f.add(m_camera.getPos(), Vec3f.scale(m_camera.getForward(), delta)));
		} else if (input.getKey(Input.KEY_S))
		{
			m_camera.setPos(Vec3f.add(m_camera.getPos(), Vec3f.scale(m_camera.getBack(), delta)));
		}

		if (input.getKey(Input.KEY_D))
		{
			m_camera.setPos(Vec3f.add(m_camera.getPos(), Vec3f.scale(m_camera.getRight(), delta)));
		} else if (input.getKey(Input.KEY_A))
		{
			m_camera.setPos(Vec3f.add(m_camera.getPos(), Vec3f.scale(m_camera.getLeft(), delta)));
		}

		if (input.getKey(Input.KEY_R))
		{
			m_camera.setPos(Vec3f.add(m_camera.getPos(), Vec3f.scale(m_camera.getUp(), delta)));
		} else if (input.getKey(Input.KEY_F))
		{
			m_camera.setPos(Vec3f.add(m_camera.getPos(), Vec3f.scale(m_camera.getDown(), delta)));
		}

		if (input.getKey(Input.KEY_UP))
		{
			Mat4f rotation = new Mat4f();

			rotation.initRotation(delta * 45.0f, 0.0f, 0.0f);

			m_camera.setForward(Mat4f.mul(rotation, m_camera.getForward(), 1.0f));

			m_camera.calcDirections();
		} else if (input.getKey(Input.KEY_DOWN))
		{
			Mat4f rotation = new Mat4f();

			rotation.initRotation(-delta * 45.0f, 0.0f, 0.0f);

			m_camera.setForward(Mat4f.mul(rotation, m_camera.getForward(), 1.0f));

			m_camera.calcDirections();
		}

		if (input.getKey(Input.KEY_RIGHT))
		{
			Mat4f rotation = new Mat4f();

			rotation.initRotation(0.0f, delta * 45.0f, 0.0f);

			m_camera.setForward(Mat4f.mul(rotation, m_camera.getForward(), 1.0f));

			m_camera.calcDirections();
		} else if (input.getKey(Input.KEY_LEFT))
		{
			Mat4f rotation = new Mat4f();

			rotation.initRotation(0.0f, -delta * 45.0f, 0.0f);

			m_camera.setForward(Mat4f.mul(rotation, m_camera.getForward(), 1.0f));

			m_camera.calcDirections();
		}
	}

	/*
	 * Render the whole screen at once using simple raytracing
	 * For single-threaded rendering
	 */
	public void renderRayTraced(Display display)
	{
		for (int y = 0; y < display.getHeight(); y++)
		{
			for (int x = 0; x < display.getWidth(); x++)
			{
				// Calculate the primary ray
				Ray ray = Ray.calcCameraRay(m_camera, display.getWidth(), display.getHeight(), display.getAR(), x, y);

				// Do the ray tracing
				Vec3f color_raytraced = rayTrace(ray, 0);

				// Draw the pixel
				display.drawPixelVec3f(x, y, color_raytraced);
			}
		}
	}

	/*
	 * Render the whole screen at once
	 * For single-threaded rendering
	 */
	public void renderSingleThreaded(Display display)
	{
		incrementSampleCounter(0);

		for (int y = 0; y < display.getHeight(); y++)
		{
			for (int x = 0; x < display.getWidth(); x++)
			{
				int index = x + y * display.getWidth();

				// Calculate the primary ray
				Ray ray = Ray.calcCameraRay(m_camera, display.getWidth(), display.getHeight(), display.getAR(), x, y);

				// Do the path tracing
				m_samples[index] = Vec3f.add(m_samples[index], pathTrace(ray, 0));

				// Draw the pixel
				display.drawPixelVec3fAveraged(index, m_samples[index], m_samples_taken.get(0));
			}
		}
	}

	/*
	 * Render a chosen portion of the screen @ t1, t2
	 * The size of one portion is (m_cpu_cores)^2
	 * For multi-threaded rendering
	 */
	public void renderMultiThreaded(Display display, int t1, int t2)
	{
		int t = t1 + t2 * (m_thread_amount / 2);

		incrementSampleCounter(t);

		if (t1 >= (m_thread_amount / 2))
			t1 = (m_thread_amount / 2) - 1;
		if (t2 >= (m_thread_amount / 2))
			t2 = (m_thread_amount / 2) - 1;

		float width = display.getWidth();
		float height = display.getHeight();
		int width_portion = display.getWidth() / (m_thread_amount / 2);
		int height_portion = display.getHeight() / (m_thread_amount / 2);

                Vec3f sample = new Vec3f();
                        
		for (int y = height_portion * t2; y < (height_portion * t2) + height_portion; y++)
		{
                    
                    int yy = y - height_portion * t2;
                    
                    final int yw = y * display.getWidth();
                    //final int yyw = yy * width_portion;
                    
			for (int x = width_portion * t1; x < (width_portion * t1) + width_portion; x++)
			{
				int xx = x - width_portion * t1;
				
				int index_screen = x + yw;
				//int index_sample = xx + yyw;
                                



				// Supersample each pixel if demanded
				if (Config.ss_enabled)
				{
					sample.set( COLOR_BLACK );

					// Sample the pixels n times
					for (int i = 0; i < Config.ss_amount; i++)
					{
						// Calculate the randomized primary ray
						Ray ray = Ray.calcSupersampledCameraRay(m_camera, display.getWidth(), display.getHeight(), display.getAR(), x, y, Config.ss_jitter);

						// Do the path tracing
						sample = Vec3f.addSelf(sample, pathTrace(ray, 0));
					}

					// Get the average color of the sample
					Vec3f sample_averaged = Vec3f.divide(sample, Config.ss_amount);

					// Add the averaged sample to the samples
					m_samples[index_screen] = Vec3f.add(m_samples[index_screen], sample_averaged);

				} else
				{
					// Calculate the primary ray
					Ray ray = Ray.calcCameraRay(m_camera, display.getWidth(), display.getHeight(), display.getAR(), x, y);

					// Do the path tracing
					m_samples[index_screen] = Vec3f.add(m_samples[index_screen], pathTrace(ray, 0));
				}

				// Draw the pixel
				display.drawPixelVec3fAveraged(index_screen, m_samples[index_screen], m_samples_taken.get(t));

				// Draw lines to separate each section, for debugging purposes
				if (Config.debug_enabled)
				{
					if (xx == 0 || xx == width_portion || yy == 0 || yy == width_portion)
					{
						display.drawPixelVec3f(x, y, COLOR_DEBUG);
					}
				}
			}
		}
	}

	/*
	 * Path tracing
	 * n = recursion level
	 */
	public Vec3f pathTrace(Ray ray, int n)
	{
		// Return black if max recursion depth has been exceeded
		if (n > Config.max_recursion)
			return COLOR_BLACK;

		// Initialize some objects and variables
		Intersection iSection = null;
		Intersection iSectionFinal = null;
		TracerObject OBJECT = null;
		float t_init = Float.MAX_VALUE;

		// Intersect the initial ray against all scene objects and find the closest interestection to the ray origin
		for (TracerObject o : m_scene.getObjects())
		{
			for (Primitive p : o.getPrimitives())
			{
				iSection = p.intersect(ray);
				if (iSection != null)
				{
					if (iSection.getT() < t_init)
					{
						iSectionFinal = iSection;
						t_init = iSection.getT();
						OBJECT = o;
					}
				}
			}
		}

		// If no intersection happened at all, return black
		if (iSectionFinal == null)
			return COLOR_BLACK;

		// Get the object's surface material
		Material M = OBJECT.getMaterial();

		// If the object is a light source, return it's emittance
		if (Vec3f.lengthSq(M.getEmittance()) > 0.0f && iSectionFinal.getT() > Main.EPSILON)
			return M.getEmittance();

		// Get the intersection's info
		Vec3f P = iSectionFinal.getPos();
		Vec3f N = iSectionFinal.getNorm();

		// Get the info about the ray
		Vec3f RO = ray.getPos();
		Vec3f RD = ray.getDir();

		// Initialize the final color which will be returned in the end
		Vec3f color_final = new Vec3f();

		// If the object is reflective like a mirror, reflect a ray
		if (M.getReflectivity() > 0.0f)
		{
			Ray newRay;
			if (M.getGlossiness() > 0.0f)
				newRay = new Ray(P, Vec3f.normalize(Vec3f.add(Vec3f.reflect(RD, N), Vec3f.scale(Vec3f.randomHemisphere(N), M.getGlossiness()))));
			else
				newRay = new Ray(P, Vec3f.normalize(Vec3f.reflect(RD, N)));

			color_final = Vec3f.add(color_final, Vec3f.scale(pathTrace(newRay, n + 1), M.getReflectivity()));
		}

		// If the object is refractive like glass, refract the ray
		if (M.getRefractivity() > 0.0f)
		{
			Ray newRay;
			if (M.getGlossiness() > 0.0f)
				newRay = new Ray(P, Vec3f.normalize(Vec3f.add(Vec3f.refract(RD, N, 1.0f, M.getRefractivityIndex()), Vec3f.scale(Vec3f.randomHemisphere(N), M.getGlossiness()))));
			else
				newRay = new Ray(P, Vec3f.normalize(Vec3f.refract(RD, N, 1.0f, M.getRefractivityIndex())));

			color_final = Vec3f.add(color_final, Vec3f.scale(pathTrace(newRay, n + 1), M.getRefractivity()));
		}

		// Calculate the diffuse lighting if reflectance is greater than 0.0
		// NOTE: This could be improved / changed, it isn't physically correct at all atm and it's quite simple
		if (Vec3f.lengthSq(M.getReflectance()) > 0.0f)
		{
			Ray newRay = new Ray(P, Vec3f.randomHemisphere(N));

			float NdotD = Math.abs(Vec3f.dot(N, newRay.getDir()));
			Vec3f BRDF = Vec3f.scale(M.getReflectance(), 2.0f * NdotD);
			Vec3f REFLECTED = pathTrace(newRay, n + 1);

			color_final = Vec3f.add(color_final, Vec3f.add(M.getEmittance(), Vec3f.scale(BRDF, REFLECTED)));
		}

		// Simple radiance clamping to avoid fireflies
		return MathUtils.clamp(color_final, 0.0f, 10.0f);
	}

	/*
	 * Simple raytracing, no shading or anything
	 * Just for navigation, it's still slow but at least "real-time"
	 */
	public Vec3f rayTrace(Ray ray, int n)
	{
		// Return black if max recursion depth has been exceeded
		if (n > Config.max_recursion)
			return COLOR_BLACK;

		// Initialize some objects and variables
		Intersection iSection = null;
		Intersection iSectionFinal = null;
		TracerObject OBJECT = null;
		float t_init = Float.MAX_VALUE;

		// Intersect the initial ray against all scene objects and find the closest interestection to the ray origin
		for (TracerObject o : m_scene.getObjects())
		{
			for (Primitive p : o.getPrimitives())
			{
				iSection = p.intersect(ray);
				if (iSection != null)
				{
					if (iSection.getT() < t_init)
					{
						iSectionFinal = iSection;
						t_init = iSection.getT();
						OBJECT = o;
					}
				}
			}
		}

		// If no intersection happened at all, return black
		if (iSectionFinal == null)
			return COLOR_BLACK;

		// Get the object's surface material
		Material M = OBJECT.getMaterial();

		// If the object is a light source, return it's emittance
		if (Vec3f.lengthSq(M.getEmittance()) > 0.0f && iSectionFinal.getT() > Main.EPSILON)
			return MathUtils.clamp(M.getEmittance(), 0.0f, 1.0f);

		// Get the intersection's info
		Vec3f P = iSectionFinal.getPos();
		Vec3f N = iSectionFinal.getNorm();

		// Get the info about the ray
		Vec3f RO = ray.getPos();
		Vec3f RD = ray.getDir();

		// Initialize the final color which will be returned in the end
		Vec3f color_final = new Vec3f();

		// Reflect
		if (M.getReflectivity() > 0.0f)
		{
			color_final = Vec3f.add(color_final, Vec3f.scale(rayTrace(new Ray(iSectionFinal.getPos(), Vec3f.normalize(Vec3f.reflect(RD, N))), n + 1), M.getReflectivity()));
		}

		// Refract
		if (M.getRefractivity() > 0.0f)
		{
			color_final = Vec3f.add(color_final, Vec3f.scale(rayTrace(new Ray(iSectionFinal.getPos(), Vec3f.normalize(Vec3f.refract(RD, N, 1.0f, M.getRefractivityIndex()))), n + 1), M.getRefractivity()));
		}

		// Diffuse objects
		if (Vec3f.lengthSq(M.getReflectance()) > 0.0f)
			color_final = Vec3f.add(color_final, Vec3f.scale(M.getReflectance(), 0.50f));

		// Clamp the final color
		return MathUtils.clamp(color_final, 0.0f, 1.0f);
	}

	/*
	 * Clear all taken samples by setting their value to [0.0, 0.0, 0.0]
	 */
	public void clearSamples()
	{
		Arrays.fill(m_samples, new Vec3f());
		for (int i = 0; i < m_samples_taken.length(); i++)
			m_samples_taken.set(i, 0);
	}

	/*
	 * Increment a chosen sample counter by 1 @ index
	 */
	public void incrementSampleCounter(int index)
	{
		m_samples_taken.incrementAndGet(index);
	}

	/*
	 * Get the amount of samples taken per pixel of a chosen cell @ index
	 */
	public int getSamplesPerPixel(int index)
	{
		return m_samples_taken.get(index);
	}

	/*
	 * Get the samples per pixel as an array
	 */
	public AtomicIntegerArray getSamplesPerPixel()
	{
		return m_samples_taken;
	}

}

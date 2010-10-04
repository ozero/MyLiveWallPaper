package jp.ozero_currentdir_rgfx.colourLwp0;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.entity.particle.ParticleSystem;
import org.anddev.andengine.entity.particle.modifier.AccelerationInitializer;
import org.anddev.andengine.entity.particle.modifier.ExpireModifier;
import org.anddev.andengine.entity.particle.modifier.RotationInitializer;
import org.anddev.andengine.entity.particle.modifier.ScaleModifier;
import org.anddev.andengine.entity.particle.modifier.VelocityInitializer;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.view.RenderSurfaceView;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

public class LiveWallpaperTemplate 
	extends BaseLiveWallpaperService 
	implements SharedPreferences.OnSharedPreferenceChangeListener {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final String SHARED_PREFS_NAME = "livewallpapertemplatesettings";

	// Camera Constants
	private static int CAMERA_WIDTH = 720;
	private static int CAMERA_HEIGHT = 480;

	// ===========================================================
	// Fields
	// ===========================================================

	// Shared Preferences
	@SuppressWarnings("unused")
	private SharedPreferences mSharedPreferences;
	private IOffsetsChanged pOffsetsChangedListener;
	private Texture mTexture;
	private TextureRegion mFaceTextureRegion;
	private Camera mCamera;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public org.anddev.andengine.engine.Engine onLoadEngine() {
		// get screen size
		WindowManager windowManager = 
			(WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels;
		int width = displayMetrics.widthPixels;
//		int mini = (height < width)?height:width;
//		int dpi = displayMetrics.densityDpi;
		CAMERA_WIDTH = width;
		CAMERA_HEIGHT = height;
		
		// init cam
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new org.anddev.andengine.engine.Engine(
				new EngineOptions(true, ScreenOrientation.PORTRAIT,
						new FillResolutionPolicy(), mCamera));
	}

	
	@Override
	public void onLoadResources() {
		
		// <customize-here>
		
		// Set the Base Texture Path
		TextureRegionFactory.setAssetBasePath("gfx/");
		this.mTexture = new Texture(128, 128, TextureOptions.DEFAULT);

		this.mFaceTextureRegion = TextureRegionFactory.createFromAsset(
				this.mTexture, this, "tks128.png", 0, 0);
		
		// </customize-here>
		
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
	}

	
	@Override
	public Scene onLoadScene() {
		
		// <customize-here>
		
		final Scene scene = new Scene(1);
		
		scene.setBackground(new ColorBackground(
				254f/256f,
				236f/256f,
				255f/255f));
		final ParticleSystem particleSystem = new ParticleSystem(
				CAMERA_WIDTH/2*(-1),CAMERA_HEIGHT, 0, 0, 1, 4, 
				50, this.mFaceTextureRegion);

		particleSystem.addParticleInitializer(new VelocityInitializer(
				20, 50,-80, -200));
		particleSystem.addParticleInitializer(new AccelerationInitializer(
				10,20));
		particleSystem.addParticleInitializer(new RotationInitializer(
				0.0f,360.0f));

		particleSystem.addParticleModifier(new ScaleModifier(0.25f, 1.0f, 0, 5));
		particleSystem.addParticleModifier(new ExpireModifier(12, 12));

		scene.getTopLayer().addEntity(particleSystem);
		
		// </customize-here>
		
		return scene;
	}

	@Override
	public void onLoadComplete() {
	}
	

	@Override
	protected void onTap(final int pX, final int pY) {
	}

	@Override
	public void onSharedPreferenceChanged(
			SharedPreferences pSharedPrefs,
			String pKey) {
	}

	// fix from : by Kangee » Mon Aug 30, 2010 10:39 pm
	@Override
	public Engine onCreateEngine() {
		return new MyBaseWallpaperGLEngine(pOffsetsChangedListener);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// fix from : by Kangee » Mon Aug 30, 2010 10:39 pm
	/*
	 * I also create a Interface IOffsetsChanged (you see it in the constructor)
	 * in LiveWallpaperTemplate class (if you used Mimminito
	 * Live-Wallpaper-Template) and i override the onCreateEngine method in
	 * LiveWallpaperTemplate.
	 */
	public interface IOffsetsChanged {

		public void offsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset);

	}

	// fix from : by Kangee » Mon Aug 30, 2010 10:39 pm
	// http://www.andengine.org/forums/live-wallpaper
	// -extension/how-wallpaper-scroll-along-with-homescreen-t444.html

	/*
	 * the BaseWallpaperGLEngine class in the BaseLiveWallpaperService. To
	 * scroll along with the homescreen we need to implement the
	 * onOffsetsChanged() method. So i implement my own Version of
	 * BaseWallpaperGLEngine (copy the old stuff and extend it with the
	 * onOffsetsChanged() method):
	 */
	protected class MyBaseWallpaperGLEngine extends GLEngine {
		// ===========================================================
		// Fields
		// ===========================================================

		private org.anddev.andengine.opengl.view.GLSurfaceView.Renderer mRenderer;
		private IOffsetsChanged mOffsetsChangedListener = null;

		// ===========================================================
		// Constructors
		// ===========================================================

		public MyBaseWallpaperGLEngine(IOffsetsChanged pOffsetsChangedListener) {
			this.setEGLConfigChooser(false);
			this.mRenderer = new RenderSurfaceView.Renderer(
					LiveWallpaperTemplate.this.mEngine);
			this.setRenderer(this.mRenderer);
			this.setRenderMode(RENDERMODE_CONTINUOUSLY);
			this.mOffsetsChangedListener = pOffsetsChangedListener;
		}

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		@Override
		public Bundle onCommand(final String pAction, final int pX,
				final int pY, final int pZ, final Bundle pExtras,
				final boolean pResultRequested) {
			if (pAction.equals(WallpaperManager.COMMAND_TAP)) {
				LiveWallpaperTemplate.this.onTap(pX, pY);
			} else if (pAction.equals(WallpaperManager.COMMAND_DROP)) {
				LiveWallpaperTemplate.this.onDrop(pX, pY);
			}

			return super.onCommand(pAction, pX, pY, pZ, pExtras,
					pResultRequested);
		}

		@Override
		public void onResume() {
			super.onResume();
			LiveWallpaperTemplate.this.getEngine().onResume();
			LiveWallpaperTemplate.this.onResume();
		}

		@Override
		public void onPause() {
			super.onPause();
			LiveWallpaperTemplate.this.getEngine().onPause();
			LiveWallpaperTemplate.this.onPause();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			if (this.mRenderer != null) {
				// mRenderer.release();
			}
			this.mRenderer = null;
		}

		// fix from : by Kangee » Mon Aug 30, 2010 10:39 pm
		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
			float xOffsetStep, float yOffsetStep,
			int xPixelOffset,int yPixelOffset) {

			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);

			if (this.mOffsetsChangedListener != null)
				this.mOffsetsChangedListener.offsetsChanged(xOffset, yOffset,
						xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
			
			
			/*
			 * Now we get a callback if the user scroll. But we have to scroll
			 * our camera with and the solution for this problem is:
			 */
			if (mCamera != null) {
				mCamera.setCenter(
					mCamera.getWidth() * xOffset * 0.25f+
					mCamera.getWidth() * xOffset * 0.5f ,
					mCamera.getCenterY()
				);
			}
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			super.onTouchEvent(event);
		}

	}

}
package jp.ozero_currentdir_rgfx.colourLwp0;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.view.RenderSurfaceView;

import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.os.Bundle;

public class LiveWallpaperTemplate extends BaseLiveWallpaperService implements
		SharedPreferences.OnSharedPreferenceChangeListener {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final String SHARED_PREFS_NAME = "livewallpapertemplatesettings";

	// Camera Constants
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 720;

	// ===========================================================
	// Fields
	// ===========================================================

	// Shared Preferences
	@SuppressWarnings("unused")
	private SharedPreferences mSharedPreferences;
	private IOffsetsChanged pOffsetsChangedListener;

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
		Camera mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new org.anddev.andengine.engine.Engine(
				new EngineOptions(true, ScreenOrientation.PORTRAIT,
						new FillResolutionPolicy(), mCamera));
	}

	@Override
	public void onLoadResources() {
		// Set the Base Texture Path
		TextureRegionFactory.setAssetBasePath("gfx/");
	}

	@Override
	public Scene onLoadScene() {
		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0.0f, 0.0f, 0.0f));
		//ready to go :)

		return scene;
	}

	@Override
	public void onLoadComplete() {

	}

	@Override
	protected void onTap(final int pX, final int pY) {

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pSharedPrefs,
			String pKey) {

	}

	//fix from : by Kangee » Mon Aug 30, 2010 10:39 pm
	@Override
	public Engine onCreateEngine() {
		// TODO Auto-generated method stub
		return new MyBaseWallpaperGLEngine(pOffsetsChangedListener);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	//fix from : by Kangee » Mon Aug 30, 2010 10:39 pm
	/* I also create a Interface IOffsetsChanged (you see it in the constructor) 
	 * in LiveWallpaperTemplate class 
	 * (if you used Mimminito Live-Wallpaper-Template) 
	 * and i override the onCreateEngine method in LiveWallpaperTemplate. */	
	public interface IOffsetsChanged {

		public void offsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset);

	}

	//fix from : by Kangee » Mon Aug 30, 2010 10:39 pm
	//http://www.andengine.org/forums/live-wallpaper
	// -extension/how-wallpaper-scroll-along-with-homescreen-t444.html
	
	/*  the BaseWallpaperGLEngine class in the BaseLiveWallpaperService. 
	 * To scroll along with the homescreen we need to implement 
	 * the onOffsetsChanged() method. 
	 * So i implement my own Version of BaseWallpaperGLEngine 
	 * (copy the old stuff and extend it with the onOffsetsChanged() method): */
	protected class MyBaseWallpaperGLEngine extends GLEngine {
		// ===========================================================
		// Fields
		// ===========================================================

		private org.anddev.andengine.opengl.view.GLSurfaceView.Renderer mRenderer;

		private IOffsetsChanged mOffsetsChangedListener = null;
		//private Camera mCamera;


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

		//fix from : by Kangee » Mon Aug 30, 2010 10:39 pm
		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);

			if (this.mOffsetsChangedListener != null)
				this.mOffsetsChangedListener.offsetsChanged(xOffset, yOffset,
						xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);

			/* Now we get a callback if the user scroll. 
			 * But we have to scroll our camera with 
			 * and the solution for this problem is: */
//			if (mCamera != null) {
//				mCamera.setCenter(
//					((mCamera.getWidth() * (screensCount-1)) * xOffset )
//						- (mCamera.getWidth() / 2) ,
//					mCamera.getCenterY()
//				);
//			}		
			
		}



	}

}
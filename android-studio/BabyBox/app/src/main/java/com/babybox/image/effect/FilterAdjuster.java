package com.babybox.image.effect;

import android.opengl.Matrix;

import jp.co.cyberagent.android.gpuimage.GPUImage3x3TextureSamplingFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBilateralFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBulgeDistortionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorBalanceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageCrosshatchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDissolveBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGlassSphereFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHazeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLevelsFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageOpacityFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePixelationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageRGBFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSobelEdgeDetection;
import jp.co.cyberagent.android.gpuimage.GPUImageSphereRefractionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSwirlFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageWhiteBalanceFilter;

 public class FilterAdjuster {
	private final Adjuster<? extends GPUImageFilter> adjuster;

	public FilterAdjuster(final GPUImageFilter filter) {
		if (filter instanceof GPUImageSharpenFilter) {
			adjuster = new SharpnessAdjuster().filter(filter);
		} else if (filter instanceof GPUImageSepiaFilter) {
			adjuster = new SepiaAdjuster().filter(filter);
		} else if (filter instanceof GPUImageContrastFilter) {
			adjuster = new ContrastAdjuster().filter(filter);
		} else if (filter instanceof GPUImageGammaFilter) {
			adjuster = new GammaAdjuster().filter(filter);
		} else if (filter instanceof GPUImageBrightnessFilter) {
			adjuster = new BrightnessAdjuster().filter(filter);
		} else if (filter instanceof GPUImageSobelEdgeDetection) {
			adjuster = new SobelAdjuster().filter(filter);
		} else if (filter instanceof GPUImageEmbossFilter) {
			adjuster = new EmbossAdjuster().filter(filter);
		} else if (filter instanceof GPUImage3x3TextureSamplingFilter) {
			adjuster = new GPU3x3TextureAdjuster().filter(filter);
		} else if (filter instanceof GPUImageHueFilter) {
			adjuster = new HueAdjuster().filter(filter);
		} else if (filter instanceof GPUImagePosterizeFilter) {
			adjuster = new PosterizeAdjuster().filter(filter);
		} else if (filter instanceof GPUImagePixelationFilter) {
			adjuster = new PixelationAdjuster().filter(filter);
		} else if (filter instanceof GPUImageSaturationFilter) {
			adjuster = new SaturationAdjuster().filter(filter);
		} else if (filter instanceof GPUImageExposureFilter) {
			adjuster = new ExposureAdjuster().filter(filter);
		} else if (filter instanceof GPUImageHighlightShadowFilter) {
			adjuster = new HighlightShadowAdjuster().filter(filter);
		} else if (filter instanceof GPUImageMonochromeFilter) {
			adjuster = new MonochromeAdjuster().filter(filter);
		} else if (filter instanceof GPUImageOpacityFilter) {
			adjuster = new OpacityAdjuster().filter(filter);
		} else if (filter instanceof GPUImageRGBFilter) {
			adjuster = new RGBAdjuster().filter(filter);
		} else if (filter instanceof GPUImageWhiteBalanceFilter) {
			adjuster = new WhiteBalanceAdjuster().filter(filter);
		} else if (filter instanceof GPUImageVignetteFilter) {
			adjuster = new VignetteAdjuster().filter(filter);
		} else if (filter instanceof GPUImageDissolveBlendFilter) {
			adjuster = new DissolveBlendAdjuster().filter(filter);
		} else if (filter instanceof GPUImageGaussianBlurFilter) {
			adjuster = new GaussianBlurAdjuster().filter(filter);
		} else if (filter instanceof GPUImageCrosshatchFilter) {
			adjuster = new CrosshatchBlurAdjuster().filter(filter);
		} else if (filter instanceof GPUImageBulgeDistortionFilter) {
			adjuster = new BulgeDistortionAdjuster().filter(filter);
		} else if (filter instanceof GPUImageGlassSphereFilter) {
			adjuster = new GlassSphereAdjuster().filter(filter);
		} else if (filter instanceof GPUImageHazeFilter) {
			adjuster = new HazeAdjuster().filter(filter);
		} else if (filter instanceof GPUImageSphereRefractionFilter) {
			adjuster = new SphereRefractionAdjuster().filter(filter);
		} else if (filter instanceof GPUImageSwirlFilter) {
			adjuster = new SwirlAdjuster().filter(filter);
		} else if (filter instanceof GPUImageColorBalanceFilter) {
			adjuster = new ColorBalanceAdjuster().filter(filter);
		} else if (filter instanceof GPUImageLevelsFilter) {
			adjuster = new LevelsMinMidAdjuster().filter(filter);
		} else if (filter instanceof GPUImageBilateralFilter) {
			adjuster = new BilateralAdjuster().filter(filter);
		} else if (filter instanceof GPUImageTransformFilter) {
                adjuster = new RotateAdjuster().filter(filter);
            }
		else {

			adjuster = null;
		}
	}

	public boolean canAdjust() {
		return adjuster != null;
	}

	public void adjust(final int percentage) {

		if (adjuster != null) {
			adjuster.adjust(percentage);
		}

	}

	private abstract class Adjuster<T extends GPUImageFilter> {
		private T filter;

		@SuppressWarnings("unchecked")
		public Adjuster<T> filter(final GPUImageFilter filter) {
			this.filter = (T) filter;
			return this;
		}

		public T getFilter() {
			return filter;
		}

		public abstract void adjust(int percentage);

		protected float range(final int percentage, final float start, final float end) {
			return (end - start) * percentage / 100.0f + start;
		}

		protected int range(final int percentage, final int start, final int end) {
			return (end - start) * percentage / 100 + start;
		}
	}

	private class SharpnessAdjuster extends Adjuster<GPUImageSharpenFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setSharpness(range(percentage, -4.0f, 4.0f));
		}
	}

	private class PixelationAdjuster extends Adjuster<GPUImagePixelationFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setPixel(range(percentage, 1.0f, 100.0f));
		}
	}

	private class HueAdjuster extends Adjuster<GPUImageHueFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setHue(range(percentage, 0.0f, 360.0f));
		}
	}

	private class ContrastAdjuster extends Adjuster<GPUImageContrastFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setContrast(range(percentage, 0.0f, 2.0f));
		}
	}

	private class GammaAdjuster extends Adjuster<GPUImageGammaFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setGamma(range(percentage, 0.0f, 3.0f));
		}
	}

	private class BrightnessAdjuster extends Adjuster<GPUImageBrightnessFilter> {
		@Override
		public void adjust(final int percentage) {

			getFilter().setBrightness(range(percentage, -1.0f, 1.0f));
		}
	}

	private class SepiaAdjuster extends Adjuster<GPUImageSepiaFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setIntensity(range(percentage, 0.0f, 2.0f));
		}
	}

	private class SobelAdjuster extends Adjuster<GPUImageSobelEdgeDetection> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setLineSize(range(percentage, 0.0f, 5.0f));
		}
	}

	private class EmbossAdjuster extends Adjuster<GPUImageEmbossFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setIntensity(range(percentage, 0.0f, 4.0f));
		}
	}

	private class PosterizeAdjuster extends Adjuster<GPUImagePosterizeFilter> {
		@Override
		public void adjust(final int percentage) {
			// In theorie to 256, but only first 50 are interesting
			getFilter().setColorLevels(range(percentage, 1, 50));
		}
	}

	private class GPU3x3TextureAdjuster extends Adjuster<GPUImage3x3TextureSamplingFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setLineSize(range(percentage, 0.0f, 5.0f));
		}
	}

	private class SaturationAdjuster extends Adjuster<GPUImageSaturationFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setSaturation(range(percentage, 0.0f, 2.0f));
		}
	}

	private class ExposureAdjuster extends Adjuster<GPUImageExposureFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setExposure(range(percentage, -10.0f, 10.0f));
		}
	}

	private class HighlightShadowAdjuster extends Adjuster<GPUImageHighlightShadowFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setShadows(range(percentage, 0.0f, 1.0f));
			getFilter().setHighlights(range(percentage, 0.0f, 1.0f));
		}
	}

	private class MonochromeAdjuster extends Adjuster<GPUImageMonochromeFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setIntensity(range(percentage, 0.0f, 1.0f));
			//getFilter().setColor(new float[]{0.6f, 0.45f, 0.3f, 1.0f});
		}
	}

	private class OpacityAdjuster extends Adjuster<GPUImageOpacityFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setOpacity(range(percentage, 0.0f, 1.0f));
		}
	}

	private class RGBAdjuster extends Adjuster<GPUImageRGBFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setRed(range(percentage, 0.0f, 1.0f));
			//getFilter().setGreen(range(percentage, 0.0f, 1.0f));
			//getFilter().setBlue(range(percentage, 0.0f, 1.0f));
		}
	}

	private class WhiteBalanceAdjuster extends Adjuster<GPUImageWhiteBalanceFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setTemperature(range(percentage, 2000.0f, 8000.0f));
			//getFilter().setTint(range(percentage, -100.0f, 100.0f));
		}
	}

	private class VignetteAdjuster extends Adjuster<GPUImageVignetteFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setVignetteStart(range(percentage, 0.0f, 1.0f));
		}
	}

	private class DissolveBlendAdjuster extends Adjuster<GPUImageDissolveBlendFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setMix(range(percentage, 0.0f, 1.0f));
		}
	}

	private class GaussianBlurAdjuster extends Adjuster<GPUImageGaussianBlurFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setBlurSize(range(percentage, 0.0f, 1.0f));
		}
	}

	private class CrosshatchBlurAdjuster extends Adjuster<GPUImageCrosshatchFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setCrossHatchSpacing(range(percentage, 0.0f, 0.06f));
			getFilter().setLineWidth(range(percentage, 0.0f, 0.006f));
		}
	}

	private class BulgeDistortionAdjuster extends Adjuster<GPUImageBulgeDistortionFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setRadius(range(percentage, 0.0f, 1.0f));
			getFilter().setScale(range(percentage, -1.0f, 1.0f));
		}
	}

	private class GlassSphereAdjuster extends Adjuster<GPUImageGlassSphereFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setRadius(range(percentage, 0.0f, 1.0f));
		}
	}

	private class HazeAdjuster extends Adjuster<GPUImageHazeFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setDistance(range(percentage, -0.3f, 0.3f));
			getFilter().setSlope(range(percentage, -0.3f, 0.3f));
		}
	}

	private class SphereRefractionAdjuster extends Adjuster<GPUImageSphereRefractionFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setRadius(range(percentage, 0.0f, 1.0f));
		}
	}

	private class SwirlAdjuster extends Adjuster<GPUImageSwirlFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setAngle(range(percentage, 0.0f, 2.0f));
		}
	}

	private class ColorBalanceAdjuster extends Adjuster<GPUImageColorBalanceFilter> {

		@Override
		public void adjust(int percentage) {
			getFilter().setMidtones(new float[]{
					range(percentage, 0.0f, 1.0f),
					range(percentage / 2, 0.0f, 1.0f),
					range(percentage / 3, 0.0f, 1.0f)});
		}
	}

	private class LevelsMinMidAdjuster extends Adjuster<GPUImageLevelsFilter> {
		@Override
		public void adjust(int percentage) {
			getFilter().setMin(0.0f, range(percentage, 0.0f, 1.0f), 1.0f);
		}
	}

	private class BilateralAdjuster extends Adjuster<GPUImageBilateralFilter> {
		@Override
		public void adjust(final int percentage) {
			getFilter().setDistanceNormalizationFactor(range(percentage, 0.0f, 15.0f));
		}
	}

        private class RotateAdjuster extends Adjuster<GPUImageTransformFilter> {
            @Override
            public void adjust(final int percentage) {

                float[] transform = new float[16];
                Matrix.setRotateM(transform, 0, 360 * percentage / 100, 0, 0, 1.0f);

                getFilter().setTransform3D(transform);
            }
        }

}
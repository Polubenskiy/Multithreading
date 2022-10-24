package com.example.multithreading;

import android.graphics.Bitmap;
import android.graphics.Color;

public class GaussianWorker implements Runnable
{
	public int y0, y1;
	public int width, height;
	public int k;
	public Double[][] kernel;
	public Bitmap bitmap, result;

	@Override
	public void run()
	{
		for (int y = y0; y < y1; y++)
		{
			for (int x = 0; x < width; x++)
			{
				int sumRed = 0;
				int sumGreen = 0;
				int sumBlue = 0;

				for (int kernelX = -k; kernelX < k; kernelX++)
				{
					for (int kernelY = -k; kernelY < k; kernelY++)
					{
						double kernelValue = kernel[kernelX + k][kernelY + k];

						int c = bitmap.getPixel(x - kernelX, y - kernelY);
						sumRed += Color.red(c) * kernelValue;
						sumGreen += Color.green(c) * kernelValue;
						sumBlue += Color.blue(c) * kernelValue;
					}
				}

				result.setPixel(x, y, Color.rgb(sumRed, sumGreen, sumBlue));
			}
		}
	}
}

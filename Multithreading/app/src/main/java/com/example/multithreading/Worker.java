package com.example.multithreading;

import android.graphics.Bitmap;
import android.graphics.Color;

public class Worker implements Runnable
{
	public int y0, y1;
	public int width, height;
	public int core;
	public Bitmap bitmap, result;

	@Override
	public void run()
	{
		for (int y = y0; y < y1; y++)
		{
			for (int x = 0; x < width; x++)
			{
				int red = 0;
				int green = 0;
				int blue = 0;

				for (int v = 0; v < core; v++)
				{
					for (int u = 0; u < core; u++)
					{
						int px = u + x - core / 2;
						int py = v + y - core / 2;

						if (px < 0) px = 0;
						if (py < 0) py = 0;
						if (px >= width) px = width - 1;
						if (py >= height) py = height - 1;

						int c = bitmap.getPixel( px, py );
						red += Color.red(c);
						green += Color.green(c);
						blue += Color.blue(c);
					}
				}

				red /= core * core;
				green /= core * core;
				blue /= core * core;

				result.setPixel(x, y, Color.rgb(red, green, blue));
			}
		}
	}
}

package com.example.multithreading;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
	ImageView imageView;
	SeekBar seekBarCore, seekBarThreads;
	TextView textViewValueCore, textViewValueThread, textViewProgress;
	Switch switchDefault, switchGaussian;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		imageView = findViewById(R.id.imageView);
		seekBarCore = findViewById(R.id.seekBarCore);
		seekBarThreads = findViewById(R.id.seekBarThreads);

		seekBarCore.setProgress(3);
		seekBarThreads.setProgress(1);

		textViewValueCore = findViewById(R.id.textViewValueCore);
		textViewValueThread = findViewById(R.id.textViewValueThreads);
		textViewProgress = findViewById(R.id.textViewProgress);

		switchDefault = findViewById(R.id.switchBlur);
		switchGaussian = findViewById(R.id.switchGaussian);

		Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.cat);
		imageView.setImageBitmap(bitmap);


		seekBarCore.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				if (seekBarCore.getProgress() < 3)
					seekBarCore.setProgress(3);
				textViewValueCore.setText(String.valueOf(seekBarCore.getProgress()));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
		});

		seekBarThreads.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				if (seekBarThreads.getProgress() < 1)
					seekBarThreads.setProgress(1);
				textViewValueThread.setText(String.valueOf(seekBarThreads.getProgress()));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
		});
	}

	public void ProcessImages(View view)
	{
		Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.cat);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		int threadCount = seekBarThreads.getProgress();
		int core = seekBarCore.getProgress(); // convolution core
		int s = height / threadCount;

		Thread[] threads = new Thread[threadCount];
		long startTime = System.currentTimeMillis();

		if (switchGaussian.isActivated())
		{
			double sigma = core / 2;
			int kernelWidth = (2 * core) + 1;

			Double[][] kernel = new Double [kernelWidth][kernelWidth];
			double sum = 0;
			for (int x = -core; x < core; x++)
			{
				for (int  y = -core; y < core; y++)
				{
					double exponentNumberator = (double)(-(x * x + y * y));
					double exponentDenominator = (2 * sigma * sigma);

					double Expression = Math.pow(Math.E, exponentNumberator / exponentDenominator);
					double kernelValue = (Expression / (2 * Math.PI * sigma * sigma));

					kernel[x + core] [y + core] = kernelValue;
					sum += kernelValue;
				}
			}

			// Normalize the kernel
			// This ensures that all of the values in the kernel together add up to 1
			for (int x = 0; x < kernelWidth; x++)
			{
				for (int y = 0; y < kernelWidth; y++)
				{
					kernel[x][y] /= sum;
				}
			}

			GaussianWorker[] workers = new GaussianWorker[threadCount];
			for (int i = 0; i < threads.length; i++)
			{
				workers[i] = new GaussianWorker();
				workers[i].bitmap = bitmap;
				workers[i].result = result;
				workers[i].kernel = kernel;
				workers[i].width = width;
				workers[i].height = height;
				workers[i].k = core;
				workers[i].y0 = s * i;
				workers[i].y1 = workers[i].y0 + s;

				threads[i] = new Thread(workers[i]);
				threads[i].start();
			}
		}
		else
		{
			Worker[] worker = new Worker[threadCount];
			for (int i = 0; i < threads.length; i++)
			{
				worker[i] = new Worker();
				worker[i].bitmap = bitmap;
				worker[i].result = result;
				worker[i].width = width;
				worker[i].height = height;
				worker[i].core = seekBarCore.getProgress();
				worker[i].y0 = s * i;
				worker[i].y1 = worker[i].y0 + s;

				threads[i] = new Thread(worker[i]);
				threads[i].start();
			}
		}

		for (int i = 0; i < threadCount; i++)
		{
			try
			{
				threads[i].join();
			}
			catch (InterruptedException exception)
			{
				exception.printStackTrace();
			}
		}

		imageView.setImageBitmap(result);
		long endTime = System.currentTimeMillis();
		long progressTime = endTime - startTime;
		textViewProgress.setText("Progressing complete \n It took " + ((double) progressTime / 1000.0) + "seconds");
	}
}

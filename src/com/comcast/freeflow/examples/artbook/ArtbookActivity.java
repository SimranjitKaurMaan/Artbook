/*******************************************************************************
 * Copyright 2013 Comcast Cable Communications Management, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package com.comcast.freeflow.examples.artbook;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;

import com.comcast.freeflow.core.AbsLayoutContainer;
import com.comcast.freeflow.core.AbsLayoutContainer.OnItemClickListener;
import com.comcast.freeflow.core.FreeFlowContainer;
import com.comcast.freeflow.core.FreeFlowContainer.OnScrollListener;
import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.examples.artbook.data.DribbbleDataAdapter;
import com.comcast.freeflow.examples.artbook.models.DribbbleFeed;
import com.comcast.freeflow.examples.artbook.models.DribbbleFetch;
import com.comcast.freeflow.layouts.FreeFlowLayout;
import com.comcast.freeflow.layouts.HGridLayout;
import com.comcast.freeflow.layouts.VGridLayout;

public class ArtbookActivity extends Activity implements OnClickListener
{

	public static final String TAG = "ArtbookActivity";

	private FreeFlowContainer Hcontainer;

	private VGridLayout vgrid;

	private HGridLayout hgrid;

	private DribbbleFetch fetch;

	private int itemsPerPage = 25;

	private int pageIndex = 1;

	DribbbleDataAdapter adapter;

	FreeFlowLayout[] layouts;

	int currLayoutIndex = 0;

	private Point size;

	private FrameLayout parentContainer;

	private int screenHt;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artbook);

		Hcontainer = (FreeFlowContainer) findViewById(R.id.Hcontainer);
		parentContainer = (FrameLayout) findViewById(R.id.parentContainer);
		Display display = getWindowManager().getDefaultDisplay();
		getActionBar().hide();
		size = new Point();
		display.getSize(size);
		screenHt = getScreenHeight();
		parentContainer.setLayoutParams(new FrameLayout.LayoutParams(size.x, screenHt));

		// Grid Layout
		vgrid = new VGridLayout();
		VGridLayout.LayoutParams params = new VGridLayout.LayoutParams(size.x / 5, screenHt / 2);
		vgrid.setLayoutParams(params);

		hgrid = new HGridLayout();

		HGridLayout.LayoutParams params2 = new HGridLayout.LayoutParams(size.x / 5, screenHt / 2);
		hgrid.setLayoutParams(params2);

		layouts = new FreeFlowLayout[] { hgrid, vgrid };

		adapter = new DribbbleDataAdapter(this);

		Hcontainer.setLayout(layouts[currLayoutIndex]);
		Hcontainer.setAdapter(adapter);

		fetch = new DribbbleFetch();

		fetch.load(this, itemsPerPage, pageIndex);

	}

	private int getScreenHeight()
	{
		Rect rectangle = new Rect();
		Window window = getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
		int statusBarHeight = rectangle.top;
		/*
		 * int contentViewTop= // window.findViewById(Window.ID_ANDROID_CONTENT).getTop(); // int titleBarHeight=
		 * contentViewTop - statusBarHeight; // Log.i("screenht",""+ (size.y-(statusBarHeight+titleBarHeight)));
		 */

		return (size.y - statusBarHeight);
	}

	public void onDataLoaded(DribbbleFeed feed)
	{
		Log.d(TAG, "photo: " + feed.getShots().get(0).getImage_teaser_url());
		adapter.update(feed);
		Hcontainer.dataInvalidated();
		Hcontainer.setOnItemClickListener(new OnItemClickListener()
		{

			private int leftFrameWidth;

			@Override
			public void onItemClick(AbsLayoutContainer parent, FreeFlowItem proxy)
			{
				currLayoutIndex++;
				if (currLayoutIndex == layouts.length)
				{
					currLayoutIndex = 0;

				}
				if (currLayoutIndex == 1)
				{
					Log.d("inside", "onItemClick" + currLayoutIndex);
					parentContainer.removeAllViews();
					parentContainer.setX(-0);
					leftFrameWidth = (size.x / 2);
					parentContainer.setLayoutParams(new FrameLayout.LayoutParams(leftFrameWidth, screenHt));
					parentContainer.addView(Hcontainer);

				}
				if (currLayoutIndex == 0)
				{
					Log.d("inside", "onItemClick" + currLayoutIndex);
					parentContainer.removeAllViews();
					parentContainer.setLayoutParams(new FrameLayout.LayoutParams(size.x, screenHt));
					parentContainer.setX(0);
					parentContainer.addView(Hcontainer);

				}

				Hcontainer.setLayout(layouts[currLayoutIndex]);

			}
		});

		Hcontainer.addScrollListener(new OnScrollListener()
		{

			@Override
			public void onScroll(FreeFlowContainer container)
			{
				Log.d(TAG, "scroll percent " + container.getScrollPercentX());
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.artbook, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return super.onOptionsItemSelected(item);

	}

	@Override
	public void onClick(View v)
	{
		Log.d(TAG, "Loading data");
		pageIndex++;
		fetch.load(this, itemsPerPage, pageIndex);
	}
}

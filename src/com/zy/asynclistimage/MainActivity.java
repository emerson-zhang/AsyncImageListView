package com.zy.asynclistimage;

import java.util.ArrayList;
import java.util.List;

import com.zy.asynclistimage.doman.Artical;

import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.app.ListActivity;

public class MainActivity extends ListActivity {
	private ListViewAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		List<Artical> articals = prepareDatas();
		adapter = new ListViewAdapter(this, R.layout.row, articals,
				getListView());

		getListView().setAdapter(adapter);
		getListView().setOnScrollListener(new ListScrollListener());

	}

	private List<Artical> prepareDatas() {
		List<Artical> articals = new ArrayList<Artical>();

		for (int i = 0; i < 20; i++) {
			articals.add(new Artical(
					"http://www.baidu.com/img/baidu_sylogo1.gif", "text" + i));
		}
		return articals;
	}

	private class ListScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				// System.out.println("++++++++ idle+++++++++++");
				adapter.unlockloadingImageThread();
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				// System.out.println("........... scroll ........");
				adapter.lockLoadingImageThreadWhenScrolling();
				break;

			default:
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}
	}

}

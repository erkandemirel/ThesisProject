package viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class NavigationViewPager extends ViewPager {

	public NavigationViewPager(Context context) {
		super(context);
	}

	public NavigationViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {

		if (v.getClass().getPackage().getName().startsWith("maps.")) {
			return true;
		}
		return super.canScroll(v, checkV, dx, x, y);
	}

}
package com.bclymer.pokecontacts;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.Window;

public class ContactsListActivity extends SherlockFragmentActivity {

	private ViewPager mViewPager;
	private ActionBar mActionBar;
	private TabsAdapter mTabsAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.pager);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(mViewPager);
		setSupportProgressBarIndeterminateVisibility(false);
		mActionBar = getSupportActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mViewPager.setOffscreenPageLimit(2);
		mTabsAdapter = new TabsAdapter(this, mViewPager);

		mTabsAdapter.addTab(mActionBar.newTab().setText("PokeContacts"), PokeContactsFragment.class, null);
		mTabsAdapter.addTab(mActionBar.newTab().setText("Favorites"), FavoritesFragment.class, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
	
	public static class TabsAdapter extends FragmentPagerAdapter implements TabListener, OnPageChangeListener {

		private final ActionBar mActionBar;
		private final FragmentManager mFragmentManager;
		private final Activity mActContext;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
		public int mCurrentTab;

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;
			private String tag;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public TabsAdapter(ContactsListActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mActContext = activity;
			mActionBar = activity.getSupportActionBar();
			mFragmentManager = activity.getSupportFragmentManager();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			final Fragment fragment = (Fragment) super.instantiateItem(container, position);
			final TabInfo info = mTabs.get(position);
			info.tag = fragment.getTag();
			return fragment;
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
				mCurrentTab = i;
			}
		}

		@Override
		public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
			mCurrentTab = position;
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			Fragment frag = Fragment.instantiate(mActContext, info.clss.getName(), info.args);
			return frag;
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {

		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {

		}

		@Override
		public void onPageScrollStateChanged(int position) {

		}

		@Override
		public void onPageScrolled(int position, float arg1, int arg2) {

		}
	}

}

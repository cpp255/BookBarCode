package com.cpp255.bookbarcode;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;

public class AllBookListActivity extends Activity {
	private static final String TAG = AllBookListActivity.class.getSimpleName();

	private boolean mInSearchUi;
	private SearchView mSearchView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_bookinfo_list);
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE,
					ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE
					| ActionBar.DISPLAY_SHOW_HOME);
			actionBar.setTitle(R.string.all_book_info_title);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.all_book_info, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add_book:
			addBookInfo();
			break;
		case R.id.action_search_book:
			enterSearchUi();
			break;
		case R.id.action_about:
			Intent intent = new Intent(AllBookListActivity.this,
					AboutBookBarCodeActivity.class);
			startActivity(intent);
			break;
		case android.R.id.home:
			exitSearchUi();
			break;
		default:
			super.onOptionsItemSelected(item);
		}
		return true;
	}

	/**
	 * Hides every tab and shows search UI for phone lookup.
	 */
	private void enterSearchUi() {
//		if (mSearchFragment == null) {
//			// We add the search fragment dynamically in the first
//			// onLayoutChange() and
//			// mSearchFragment is set sometime later when the fragment
//			// transaction is actually
//			// executed, which means there's a window when users are able to hit
//			// the (physical)
//			// search key but mSearchFragment is still null.
//			// It's quite hard to handle this case right, so let's just ignore
//			// the search key
//			// in this case. Users can just hit it again and it will work this
//			// time.
//			return;
//		}
		if (mSearchView == null) {
			prepareSearchView();
		}

		final ActionBar actionBar = getActionBar();

		final Tab tab = actionBar.getSelectedTab();

		mSearchView.setQuery(null, true);

		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Show the search fragment and hide everything else.
//		mSearchFragment.setUserVisibleHint(true);
//		final FragmentTransaction transaction = getFragmentManager()
//				.beginTransaction();
//		transaction.show(mSearchFragment);
//		transaction.commitAllowingStateLoss();

		// We need to call this and onActionViewCollapsed() manually, since we
		// are using a custom
		// layout instead of asking the search menu item to take care of
		// SearchView.
		mSearchView.onActionViewExpanded();
		mInSearchUi = true;
	}

    /**
     * Goes back to usual all bookinfo UI with tags. Previously selected Tag and associated Fragment
     * should be automatically focused again.
     */
    private void exitSearchUi() {
        final ActionBar actionBar = getActionBar();

        // Hide the search fragment, if exists.
//        if (mSearchFragment != null) {
//            mSearchFragment.setUserVisibleHint(false);
//
//            final FragmentTransaction transaction = getFragmentManager().beginTransaction();
//            transaction.hide(mSearchFragment);
//            transaction.commitAllowingStateLoss();
//        }

        // We want to hide SearchView and show Tabs. Also focus on previously selected one.
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        
        hideInputMethod(getCurrentFocus());

        // Request to update option menu.
        invalidateOptionsMenu();

        // See comments in onActionViewExpanded()
        mSearchView.onActionViewCollapsed();
        mInSearchUi = false;
    }
    
	/**
	 * Listener used to send search queries to the book search fragment.
	 */
	private final OnQueryTextListener mBookSearchQueryTextListener = new OnQueryTextListener() {
		@Override
		public boolean onQueryTextSubmit(String query) {
			View view = getCurrentFocus();
			if (view != null) {
				hideInputMethod(view);
				view.clearFocus();
			}
			return true;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			// Show search result with non-empty text. Show a bare list
			// otherwise.
//			if (mSearchFragment != null) {
//				mSearchFragment.setQueryString(newText, true);
//			}
			return true;
		}
	};

    /**
     * Listener used to handle the "close" button on the right side of {@link SearchView}.
     * If some text is in the search view, this will clean it up. Otherwise this will exit
     * the search UI and let users go back to usual all bookinfo UI.
     *
     * This does _not_ handle back button.
     */
    private final OnCloseListener mBookSearchCloseListener =
            new OnCloseListener() {
                @Override
                public boolean onClose() {
                    if (!TextUtils.isEmpty(mSearchView.getQuery())) {
                        mSearchView.setQuery(null, true);
                    }
                    return true;
                }
    };
    
	private void prepareSearchView() {
		final View searchViewLayout = getLayoutInflater().inflate(
				R.layout.all_bookinfo_custom_actionbar, null);
		mSearchView = (SearchView) searchViewLayout
				.findViewById(R.id.search_view);
		mSearchView.setOnQueryTextListener(mBookSearchQueryTextListener);
		mSearchView.setOnCloseListener(mBookSearchCloseListener);
		// Since we're using a custom layout for showing SearchView instead of
		// letting the
		// search menu icon do that job, we need to manually configure the View
		// so it looks
		// "shown via search menu".
		// - it should be iconified by default
		// - it should not be iconified at this time
		// See also comments for onActionViewExpanded()/onActionViewCollapsed()
		mSearchView.setIconifiedByDefault(true);
		mSearchView.setQueryHint(getString(R.string.hint_findBooks));
		mSearchView.setIconified(false);
		mSearchView
				.setOnQueryTextFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View view, boolean hasFocus) {
						if (hasFocus) {
							showInputMethod(view.findFocus());
						}
					}
				});

		getActionBar().setCustomView(
				searchViewLayout,
				new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));
	}

	private void showInputMethod(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			if (!imm.showSoftInput(view, 0)) {
				Log.w(TAG, "Failed to show soft input method.");
			}
		}
	}

    private void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    
	private void addBookInfo() {
		Intent intent = new Intent(AllBookListActivity.this, CaptureActivity.class);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		if(mInSearchUi) {
			exitSearchUi();
		} else {
			super.onBackPressed();
		}
	}
}

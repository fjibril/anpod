package com.blork.anpod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.blork.anpod.R;
import com.blork.anpod.adapters.PictureThumbnailAdapter;
import com.commonsware.cwac.cache.SimpleWebImageCache;
import com.commonsware.cwac.endless.EndlessAdapter;
import com.commonsware.cwac.thumbnail.ThumbnailAdapter;
import com.commonsware.cwac.thumbnail.ThumbnailBus;
import com.commonsware.cwac.thumbnail.ThumbnailMessage;

// TODO: Auto-generated Javadoc
/**
 * This is the "top-level" fragment, showing a list of items that the
 * user can pick.  Upon picking an item, it takes care of displaying the
 * data to the user as appropriate based on the currrent UI layout.
 */

public abstract class ResultsFragment extends ListFragment {    	
	
	/** The Constant IMAGE_IDS. */
	protected static final int[] IMAGE_IDS={R.id.item_image};
	
	/** The bus. */
	private ThumbnailBus bus=new ThumbnailBus();
	
	/** The cache. */
	public SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache=
							new SimpleWebImageCache<ThumbnailBus, ThumbnailMessage>(null, null, 101, bus);
	
	/** The thumbs. */
	protected PictureThumbnailAdapter thumbs;
	
    /** The m dual pane. */
    boolean mDualPane;
    
    /** The m cur check position. */
    int mCurCheckPosition = 0;
	
	/** The query. */
	private String query = null;

	protected ThumbnailAdapter tAdapter;
	
	protected EndlessAdapter etAdapter;
		
    
    /* (non-Javadoc)
     * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        	
        return inflater.inflate(R.layout.titles_fragment, container, false);
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.ListFragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        

    	Log.e("", "onActivityCreated "+query);
    
		
		listSetup();
    	
        
        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showDetails(mCurCheckPosition);
        }
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     *
     * @param index the index
     */
    void showDetails(int index) {
        mCurCheckPosition = index;

        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(index, true);

            // Check what fragment is currently shown, replace if needed.
            TitlesDetailsFragment details = (TitlesDetailsFragment) getFragmentManager().findFragmentById(R.id.details);
            
            if (details == null || details.getShownIndex() != index) {
                // Make new fragment to show this selection.
                details = TitlesDetailsFragment.newInstance(index);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.details, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
        	Intent intent = new Intent();
            //intent.setClass(getActivity(), DetailsActivity.class);
        	intent.setClass(getActivity(), DetailsFragmentPagerActivity.class);
            intent.putExtra("index", index);
            startActivity(intent);           
       }
        
    }
    
    public abstract void listSetup();
     
    
}
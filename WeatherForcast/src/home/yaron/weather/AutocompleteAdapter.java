package home.yaron.weather;

import home.yaron.weather_forcast.R;

import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class AutocompleteAdapter extends ArrayAdapter<String>
{
	private static final String TAG = AutocompleteAdapter.class.getSimpleName();

	private CitiesFilter citiesFilter = null;
	private final Object mLock = new Object(); // a copy form super	
	private SortedSet<String> mOriginalValues = null;
	private LayoutInflater inflater;	

	public AutocompleteAdapter(Context context, int textViewResourceId, SortedSet<String> data)
	{		
		super(context, textViewResourceId);
		Log.d(TAG, "AutocompleteAdapter(..)");
		mOriginalValues = data; // Cities list.
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}	

	@Override
	public Filter getFilter()
	{
		if(citiesFilter == null)		
			citiesFilter = new CitiesFilter();

		return citiesFilter;
	}	

	/**
	 * <p>An array filter constrains the content of the array adapter with
	 * a prefix. Each item that does not start with the supplied prefix
	 * is removed from the list.</p>
	 */
	private class CitiesFilter extends Filter
	{
		@Override
		protected FilterResults performFiltering(CharSequence prefix)
		{			
			final FilterResults results = new FilterResults();			

			if(prefix == null || prefix.length() == 0) // No filter
			{ 
				final TreeSet<String> unfilterSet;
				synchronized(mLock) { // Lock for reading original values.
					unfilterSet = new TreeSet<String>(mOriginalValues.subSet("Aba","Abazzzzzzzz"));
				}
				results.values = unfilterSet;
				results.count = unfilterSet.size();
			}
			else // Filter
			{	
				// Convert every word first letter to upper case.
				final String prefixString = prefixToCapitalLetters(prefix.toString());

				final TreeSet<String> valuesSet;
				synchronized (mLock) { // Lock for reading original values.
					valuesSet = new TreeSet<String>(mOriginalValues.subSet(prefixString, prefixString+"zzzzzzzz"));
				}				

				results.values = valuesSet;
				results.count = valuesSet.size();
			}

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results)
		{
			AutocompleteAdapter.this.clear();				
			AutocompleteAdapter.this.addAll((TreeSet<String>)results.values);			
		}
	}

	private String prefixToCapitalLetters(String prefix)
	{
		final StringBuilder upperPrefix = new StringBuilder();
		final String[] wordList = prefix.trim().split(" ");
		for( String aWord : wordList)
		{	
			if( aWord.length() > 0 )
			{				
				final String firstLetter = aWord.substring(0,1).toUpperCase(Locale.US);
				upperPrefix.append(firstLetter + aWord.substring(1) + " ");
			}
		}

		return upperPrefix.toString().trim();
	}	

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{	
		if( convertView == null )
		{			
			convertView = inflater.inflate(R.layout.autocomplete_item, null);
		}	

		final TextView textView = (TextView)convertView.findViewById(R.id.item_autocomplete_text);		
		textView.setText(this.getItem(position));

		return convertView;	
	}
}

package com.manditrades.fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.manditrades.R;
import com.manditrades.activities.SellerInfoActivity;
import com.manditrades.adapters.SellersListAdapter;
import com.manditrades.jsonwrapper.MTSeller;
import com.manditrades.jsonwrapper.MTSellerList;
import com.manditrades.util.JsonDataCallback;
import com.manditrades.util.LanguageScripts;
import com.manditrades.util.MTURLHelper;

public class SellersListFragment extends Fragment implements OnScrollListener {

	private int from;
	private int to;
	private Context context;
	private ListView sellersList;
	private SellersListAdapter sellerListAdapter;

	private EditText search_text;
	private ListView searchResultLV;

	private String query = null;
	private boolean ifItemClicked;

	private ArrayList<MTSeller> mtSellerList;
	private boolean hasInitialized;
	private SharedPreferences preferences;
	int i = 0;
	boolean userScrolled;
	boolean isMoreData = true;
	boolean isLoading;
	private ProgressBar footer;

	public SellersListFragment() {

	}

	@Override
	public void onPause() {
		super.onPause();
		Editor editor = preferences.edit();
		editor.putString("SOURCE", "SellersListFragment");
		editor.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (preferences.getBoolean("UPDATE", false)) {
			from = 1;
			to = 10;
			mtSellerList.clear();
			makeSellersListRequest();
			preferences.edit().putBoolean("UPDATE", false).commit();
		}
		sellerListAdapter.notifyDataSetChanged();
	}

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	//
	// if (requestCode == SellersListAdapter.CALL) {
	// MTSeller seller = (MTSeller) data.getExtras().getSerializable(
	// "SELLER_INFO");
	// Intent intent = new Intent(context, RateUserActivity.class);
	// intent.putExtra("SELLER_INFO", seller);
	// context.startActivity(intent);
	// }
	//
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_seller_list,
				container, false);
		context = container.getContext();

		initComponents(rootView);

		from = 1;
		to = 10;
		makeSellersListRequest();
		setSearchBar();
		setListeners();

		mtSellerList = new ArrayList<MTSeller>();
		sellerListAdapter = new SellersListAdapter(context, mtSellerList,
				"SellersListFragment");
		sellersList.setAdapter(sellerListAdapter);
		sellersList.setOnScrollListener(this);
		return rootView;
	}

	private void initComponents(View rootView) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		sellersList = (ListView) rootView.findViewById(R.id.sellers_list);
		search_text = (EditText) rootView.findViewById(R.id.search_box);
		searchResultLV = (ListView) rootView
				.findViewById(R.id.search_result_lv);
		hasInitialized = true;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footer = (ProgressBar) inflater.inflate(R.layout.footer, null);
		sellersList.addFooterView(footer);

	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (menuVisible && hasInitialized) {
			setValues();
		}
	}

	private void setValues() {
		int language = preferences.getInt("LANGUAGE", 0);
		LanguageScripts scripts = new LanguageScripts();

		((Activity) context).getActionBar().setTitle(
				scripts.sellerList[language]);
	}

	private void setListeners() {
		sellersList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {

				Intent intent = new Intent(context, SellerInfoActivity.class);
				intent.putExtra("SELLER_INFO", mtSellerList.get(position));
				intent.putExtra("SOURCE", "SellersListFragment");

				startActivity(intent);

			}
		});
	}

	private void setSearchBar() {
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_dropdown_item_1line);

		search_text.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				query = search_text.getText().toString();

				if (!ifItemClicked && query != null && query.length() > 0) {

					String url = MTURLHelper
							.getAPIEndpointURL(MTURLHelper.searchAutoCompleteURL);
					if (query != null || query != "")
						url += "?commodity=" + query;
					else
						return;

					String method = "GET";

					Object[] objects = { url, method, null };

					JsonDataCallback callback = new JsonDataCallback(context) {

						@SuppressWarnings("unchecked")
						@Override
						public void receiveData(JSONObject responseJson) {
							try {
								List<String> suggestionList = new Gson()
										.fromJson(
												responseJson
														.getJSONObject("root")
														.getJSONArray(
																"searchList")
														.toString(), List.class);
								adapter.clear();
								for (String s : suggestionList) {
									adapter.add(s);
								}

								searchResultLV.setAdapter(adapter);
								searchResultLV.setVisibility(View.VISIBLE);

							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					};

					callback.execute(objects);
				} else {
					searchResultLV.setVisibility(View.GONE);
				}
			}
		});

		searchResultLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long l) {

				ifItemClicked = true;

				searchResultLV.setVisibility(View.GONE);

				search_text.setText(((TextView) view).getText());

				String searchString = search_text.getText().toString();

				String url = MTURLHelper
						.getAPIEndpointURL(MTURLHelper.searchItemURL);
				if (searchString != null || searchString != "")
					url += "?key=" + searchString + "&from=1&to&100";
				String method = "GET";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("From", "1"));
				params.add(new BasicNameValuePair("To", "30"));

				Object[] objects = { url, method, params };

				JsonDataCallback callback = new JsonDataCallback(context) {

					@Override
					public void receiveData(JSONObject responseJson) {

						MTSellerList mtSellers;
						try {
							mtSellers = new Gson().fromJson(
									responseJson.toString(), MTSellerList.class);
							sellersList.setAdapter(new SellersListAdapter(
									context, mtSellers.getMTSellerList(),
									"SellersListFragment"));
							ifItemClicked = false;
						} catch (JsonSyntaxException e) {
							e.printStackTrace();
						}

					}
				};

				callback.execute(objects);
			}
		});

	}

	private void makeSellersListRequest() {

		isLoading = true;

		String url = MTURLHelper.getAPIEndpointURL(MTURLHelper.sellersListURL);
		// String url="http://192.168.1.25/apimandi/v1/index.php/sellers";

		String method = "POST";

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("from", from + ""));
		params.add(new BasicNameValuePair("to", to + ""));

		Object[] object = { url, method, params, null };

		JsonDataCallback callback = new JsonDataCallback(context) {

			@Override
			public void receiveData(JSONObject responseJson) {
				isLoading = false;

				from = from + 10;
				to = to + 10;

				Gson gson = new Gson();

				MTSellerList sellerList = gson.fromJson(
						responseJson.toString(), MTSellerList.class);
				ArrayList<MTSeller> tempSellerList = sellerList
						.getMTSellerList();

				if (tempSellerList.size() == 0) {
					isMoreData = false;
					sellersList.removeFooterView(footer);
					sellersList.setOnScrollListener(null);
				}

				mtSellerList.addAll(tempSellerList);
				sellerListAdapter.notifyDataSetChanged();
				System.out.println("Loading data from : " + from + " to : "
						+ to + "turn " + i);

			}
		};
		callback.execute(object);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		int lastVisibleItem = visibleItemCount + firstVisibleItem;

		if (userScrolled && isMoreData && !isLoading
				&& lastVisibleItem == totalItemCount) {
			makeSellersListRequest();
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
			userScrolled = true;
		}
	}
}
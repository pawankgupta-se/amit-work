package com.manditrades.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.manditrades.R;
import com.manditrades.adapters.ImagePagerAdapter;
import com.manditrades.jsonwrapper.MTSeller;
import com.manditrades.jsonwrapper.MTSellerList;
import com.manditrades.util.GPSTracker;
import com.manditrades.util.JsonDataCallback;
import com.manditrades.util.MTAlertUtil;
import com.manditrades.util.MTFormatter;
import com.manditrades.util.MTURLHelper;
import com.manditrades.util.MTUtil;
import com.viewpagerindicator.CirclePageIndicator;

@SuppressLint("DefaultLocale")
public class SellerInfoActivity extends Activity implements
		OnSeekBarChangeListener {

	private String source;
	private ViewPager viewPager;
	private CirclePageIndicator indicator;
	private ToggleButton playPause;
	private SeekBar seekBar;
	private TextView sellerName;
	private ImageView verified;
	private RatingBar ratingBar;
	private TextView memberSince;
	private ImageView defaultImage;
	private TextView quantity;
	private TextView pricePerKg;
	private TextView variety;
	private TextView sellerAddress;
	private TextView distance;
	private TextView noOfCalls;
	private Context context;
	private ArrayList<String> imageUrls;
	private MediaPlayer mediaPlayer;
	private VideoView videoView;
	private MediaController mediaController;
	private List<String> mImages = new ArrayList<String>();
	private Button tabBtnImage;
	private Button tabBtnVideo;
	private Button tabBtnAudio;
	private LinearLayout mediaLL;
	private LinearLayout toggleLL;
	private LinearLayout videoLL;
	private LinearLayout imageLL;
	private LinearLayout audioLL;
	private RelativeLayout directionRL;
	private RelativeLayout updateWishlistRL;
	private RelativeLayout callRL;
	private RelativeLayout interestedUserRL;
	private ImageView updateWishlistIV;
	private TextView postedOnTV;
	private MTSeller seller;

	private int CALL_REQUEST_CODE = 500;

	String videoUrl;
	String audioUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_seller_info);
		context = this;
		source = getIntent().getStringExtra("SOURCE");
		initComponents();
		setSellerInfo();

		setActionBar();

		String url = MTURLHelper.getAPIEndpointURL(MTURLHelper.sellerInfoURL);
		String method = "POST";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("seller_mobile", seller
				.getSellerMobileNo()));
		params.add(new BasicNameValuePair("imagedate", seller.getImageDate()));

		Object[] objects = { url, method, params };

		JsonDataCallback callback = new JsonDataCallback(this) {

			@Override
			public void receiveData(JSONObject responseJson) {
				try {
					JSONObject root = responseJson.getJSONObject("root");
					JSONArray imagesArray = root.getJSONArray("images");
					audioUrl = root.getString("audio");
					videoUrl = root.getString("video");
					if (imagesArray != null) {
						imageUrls = new ArrayList<String>();
						for (int i = 0; i < imagesArray.length(); i++) {
							System.out.println("!!!!!! "
									+ imagesArray.getString(i));
							imageUrls.add(imagesArray.getString(i));
						}
						viewPager.setAdapter(new ImagePagerAdapter(context,
								imageUrls));
						indicator.setViewPager(viewPager);

						if (videoUrl != null) {
							videoView
									.setVideoURI(Uri.parse(String.format(
											"%s/v1/%s",
											MTURLHelper.getAPIEndpointURL(""),
											videoUrl)));
							videoView.setMediaController(mediaController);
							videoView.seekTo(1);
							videoView.setFitsSystemWindows(true);
						}

						if (audioUrl != null) {

						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};

		callback.execute(objects);

		setListeners();

	}

	private void setActionBar() {
		MTUtil.setActionBar(context, "Seller's Information", true);
	}

	private void setListeners() {

		playPause.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					startPlayback();
				} else {
					stopPlayback();
				}
			}

		});

		seekBar.setOnSeekBarChangeListener(this);

		tabBtnImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				audioLL.setVisibility(View.GONE);
				videoLL.setVisibility(View.GONE);
				imageLL.setVisibility(View.VISIBLE);
				tabBtnImage.setBackgroundColor(Color.BLACK);
				tabBtnVideo.setBackgroundColor(Color.LTGRAY);
				tabBtnImage.setTextColor(Color.LTGRAY);
				tabBtnVideo.setTextColor(Color.BLACK);
				tabBtnAudio.setBackgroundColor(Color.LTGRAY);
				tabBtnAudio.setTextColor(Color.BLACK);
			}
		});

		tabBtnVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				videoLL.setVisibility(View.VISIBLE);
				imageLL.setVisibility(View.GONE);
				audioLL.setVisibility(View.GONE);
				tabBtnImage.setBackgroundColor(Color.LTGRAY);
				tabBtnVideo.setBackgroundColor(Color.BLACK);
				tabBtnVideo.setTextColor(Color.LTGRAY);
				tabBtnImage.setTextColor(Color.BLACK);
				tabBtnAudio.setBackgroundColor(Color.LTGRAY);
				tabBtnAudio.setTextColor(Color.BLACK);
			}
		});

		tabBtnAudio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				audioLL.setVisibility(View.VISIBLE);
				imageLL.setVisibility(View.GONE);
				videoLL.setVisibility(View.GONE);
				tabBtnImage.setBackgroundColor(Color.LTGRAY);
				tabBtnVideo.setBackgroundColor(Color.LTGRAY);
				tabBtnVideo.setTextColor(Color.BLACK);
				tabBtnImage.setTextColor(Color.BLACK);
				tabBtnAudio.setBackgroundColor(Color.BLACK);
				tabBtnAudio.setTextColor(Color.LTGRAY);

			}
		});

		// down bar actions

		directionRL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				double latitude = 0.0;
				double longitude = 0.0;
				latitude = Double.valueOf(seller.getLatLang()[0]);
				longitude = Double.valueOf(seller.getLatLang()[1]);
				GPSTracker gpsTracker = new GPSTracker(context);
				final Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://maps.google.com/maps?" + "saddr="
								+ gpsTracker.getLatitude() + ","
								+ gpsTracker.getLongitude() + "&daddr="
								+ latitude + "," + longitude));
				intent.setClassName("com.google.android.apps.maps",
						"com.google.android.maps.MapsActivity");
				context.startActivity(intent);
			}
		});

		interestedUserRL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context,
						InterestedUserActivity.class);
				intent.putExtra("INTERESTED_USERS", seller.getNoOfCalls());
				context.startActivity(intent);
			}
		});

		updateWishlistRL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(context);
				Gson gson = new Gson();
				String json = null;
				Editor prefsEditor = preferences.edit();
				MTSellerList sellerList = null;
				ArrayList<MTSeller> wishList;

				if (source.equalsIgnoreCase("WishlistFragment")) {

					json = preferences.getString("WISH_LIST", null);
					sellerList = gson.fromJson(json, MTSellerList.class);
					wishList = sellerList.getMTSellerList();
					int index = Collections.binarySearch(wishList, seller,
							new MTSellerComparator());

					wishList.remove(index);

					sellerList = new MTSellerList();
					sellerList.setMTSellerList(wishList);

					MTAlertUtil.showMessesBox(context, "Mandi Trades",
							"Removed from Wishlist.",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});

				} else {

					json = preferences.getString("WISH_LIST", null);

					if (json != null) {
						sellerList = gson.fromJson(json, MTSellerList.class);

						wishList = sellerList.getMTSellerList();

						int index = Collections.binarySearch(wishList, seller,
								new MTSellerComparator());

						if (index < 0) {
							wishList.add(seller);
							sellerList.setMTSellerList(wishList);

							MTAlertUtil.showMessesBox(context, "Mandi Trades",
									"Successfully Added To Wishlist",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									});

						} else {
							MTAlertUtil.showMessesBox(context, "Mandi Trades",
									"Already in Wishlist.",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									});

						}

					} else {
						wishList = new ArrayList<MTSeller>();
						wishList.add(seller);

						sellerList = new MTSellerList();
						sellerList.setMTSellerList(wishList);

						MTAlertUtil.showMessesBox(context, "Mandi Trades",
								"Successfully Added To Wishlist",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								});
					}
					if (sellerList != null) {
						json = gson.toJson(sellerList);
						prefsEditor.putString("WISH_LIST", json);
						prefsEditor.commit();
					}
				}

			}
		});
		callRL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MTAlertUtil.showMessesBox(context, "Phone",
						"Call " + seller.getSellerMobileNo() + "?",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent callIntent = new Intent(
										Intent.ACTION_CALL);
								callIntent.setData(Uri.parse("tel:91"
										+ seller.getSellerMobileNo()));
								startActivityForResult(callIntent,
										CALL_REQUEST_CODE);
							}
						}, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(context,
										RateUserActivity.class);
								intent.putExtra("SELLER_INFO", seller);
								startActivity(intent);
							}
						}, "Call", "Don't call");

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 500 && resultCode == RESULT_OK) {
			Intent intent = new Intent(context, RateUserActivity.class);
			intent.putExtra("SELLER_INFO", seller);
			startActivity(intent);
		}
	}

	protected void stopPlayback() {
		if (seekBar != null)
			seekBar.setProgress(0);
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			// mediaPlayer.reset();
			// mediaPlayer.release();
			// mediaPlayer = null;
		}
	}

	protected void startPlayback() {
		seekBar.setIndeterminate(true);
		seekBar.setProgress(0);
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mediaPlayer.setDataSource(String.format("%s/v1/%s",
					MTURLHelper.getAPIEndpointURL(""), audioUrl));
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(final MediaPlayer mediaPlayer) {
					seekBar.setIndeterminate(false);
					mediaPlayer.start();
					seekBar.setMax(mediaPlayer.getDuration());

					new Thread(new Runnable() {

						@Override
						public void run() {
							while (mediaPlayer != null
									&& mediaPlayer.getCurrentPosition() < mediaPlayer
											.getDuration()) {
								Message msg = new Message();
								int millis = mediaPlayer.getCurrentPosition();

								msg.obj = millis;
								// mHandler.sendMessage(msg);
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								if (mediaPlayer != null
										&& mediaPlayer.isPlaying())
									seekBar.setProgress(mediaPlayer
											.getCurrentPosition());
							}
						}

						// Handler mHandler = new Handler();

					}).start();

				}
			});
			mediaPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mediaPlayer, int what,
						int extra) {
					System.out.println("@#@#@#@@#@#@# " + what);
					return false;
				}
			});
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					playPause.setChecked(false);
				}
			});
			mediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initComponents() {
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		indicator = (CirclePageIndicator) findViewById(R.id.indicator);
		playPause = (ToggleButton) findViewById(R.id.play_pause);
		seekBar = (SeekBar) findViewById(R.id.seek_bar);
		sellerName = (TextView) findViewById(R.id.seller_name);
		verified = (ImageView) findViewById(R.id.verified);
		ratingBar = (RatingBar) findViewById(R.id.rating_bar);
		memberSince = (TextView) findViewById(R.id.member_since);
		defaultImage = (ImageView) findViewById(R.id.default_image);
		quantity = (TextView) findViewById(R.id.quantity);
		pricePerKg = (TextView) findViewById(R.id.price_per_kg);
		variety = (TextView) findViewById(R.id.variety);
		videoView = (VideoView) findViewById(R.id.video_view);
		sellerAddress = (TextView) findViewById(R.id.seller_address);
		distance = (TextView) findViewById(R.id.distance);
		noOfCalls = (TextView) findViewById(R.id.no_of_calls);
		tabBtnImage = (Button) findViewById(R.id.tab_button_image);
		tabBtnVideo = (Button) findViewById(R.id.tab_button_video);
		tabBtnAudio = (Button) findViewById(R.id.tab_button_audio);
		mediaLL = (LinearLayout) findViewById(R.id.media_ll);
		toggleLL = (LinearLayout) findViewById(R.id.toggle_ll);
		videoLL = (LinearLayout) findViewById(R.id.video_ll);
		imageLL = (LinearLayout) findViewById(R.id.images_ll);
		audioLL = (LinearLayout) findViewById(R.id.audio_bar_ll);
		postedOnTV = (TextView) findViewById(R.id.posted_on);
		mediaController = new MediaController(context);

		directionRL = (RelativeLayout) findViewById(R.id.directionRL);
		updateWishlistRL = (RelativeLayout) findViewById(R.id.update_wishlist_rl);
		callRL = (RelativeLayout) findViewById(R.id.call_rl);
		updateWishlistIV = (ImageView) findViewById(R.id.update_wishlist_iv);
		interestedUserRL = (RelativeLayout) findViewById(R.id.interested_user_rl);

		if (source.equals("WishlistFragment")) {
			updateWishlistIV.setImageResource(R.drawable.remove_icon);
		}
	}

	private void setSellerInfo() {

		seller = (MTSeller) getIntent().getExtras().getSerializable(
				"SELLER_INFO");

		String postedOn = MTFormatter
				.formatDateString(MTFormatter.getDateFromString(seller.getDOC()
						.getDate()), false);

		postedOnTV.setText("Posted on " + postedOn);

		sellerName.setText(seller.getSellerName());
		// if (seller.getTag().equalsIgnoreCase("certified"))
		verified.setVisibility(View.VISIBLE);
		// else
		// verified.setVisibility(View.GONE);
		ratingBar.setRating(Float.parseFloat(seller.getRating()));
		memberSince.setText(seller.getMemberSince());
		try {
			defaultImage.setImageBitmap(BitmapFactory.decodeStream(getAssets()
					.open("default_jpg/"
							+ seller.getSellerCommodity().toLowerCase()
							+ ".jpg")));
		} catch (IOException e) {
			try {
				defaultImage.setImageBitmap(BitmapFactory
						.decodeStream(getAssets().open(
								"default_png/"
										+ seller.getSellerCommodity()
												.toLowerCase() + ".png")));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		quantity.setText(seller.getSellerQuantity());
		pricePerKg.setText(seller.getSellerPrice());
		variety.setText(seller.getSellerVariety());
		sellerAddress.setText(seller.getSellerAddress());

		distance.setText(MTUtil.computeDistance(seller.getLatLang()[0],
				seller.getLatLang()[1]));

		int callNumber = seller.getNoOfCalls() != null ? seller.getNoOfCalls()
				.size() : 0;
		noOfCalls.setText("(" + callNumber + ")");

		viewPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				view.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				viewPager.getParent().requestDisallowInterceptTouchEvent(true);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	public Bitmap getStringToBitMap(String encodedString) {
		try {
			byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
					encodeByte.length);
			return bitmap;
		} catch (Exception e) {
			e.getMessage();
			return null;
		}
	}

	@Override
	public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
		// mediaPlayer.seekTo(progress);
		// seekBar.setProgress(progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar bar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar bar) {

	}

}

class MTSellerComparator implements Comparator<MTSeller> {

	@Override
	public int compare(MTSeller s1, MTSeller s2) {
		if (s1.getSellerId().getId().equals(s2.getSellerId().getId())) {
			return 0;
		} else {
			return -1;
		}
	}

}
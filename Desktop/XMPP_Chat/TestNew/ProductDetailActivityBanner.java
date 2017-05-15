package com.ealpha.homeclick;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ealpha.R;
import com.ealpha.main.MainActivity;
import com.ealpha.support.ImageGalleryProductAdapter;
import com.ps.DTO.CartsDTO;
import com.ps.DTO.SliderDTO;
import com.ps.utility.JSONParser;
import com.ps.utility.SessionManager;

public class ProductDetailActivityBanner extends Activity {
	private TextView btn_back, txt_headline, txt_price, txt_description,
			txt_color, txt_size;
	private ViewPager viewpager;
	private ProgressDialog progressDialog;
	private ArrayList<SliderDTO> sliderDTOs;
	private SliderDTO sliderDTO;
	private String product_link_banner = "";
	private SessionManager sessionManager;
	private Button btn_to_cart_list;
	private String vProduct_ID = "";
	private LinearLayout show_size_view, show_color_view;
	private ArrayList<String> color_codes;
	private ArrayList<String> sizes;
	private int id_size = -1;
	private boolean is_same_size;
	String default_color_code = "#000000";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popular_product_deatail_activity);

		sessionManager = new SessionManager(this);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		try {
			product_link_banner = getIntent().getStringExtra("link");
		} catch (Exception e) {
			// TODO: handle exception
		}

		initialize();

	}

	public void initialize() {
		// TODO Auto-generated method stub
		viewpager = (ViewPager) findViewById(R.id.viewpager_pp);
		btn_back = (TextView) findViewById(R.id.btn_back);
		txt_headline = (TextView) findViewById(R.id.txt_headline);
		txt_price = (TextView) findViewById(R.id.txt_rs);
		txt_color = (TextView) findViewById(R.id.tv_color1);
		txt_size = (TextView) findViewById(R.id.tv_size11);

		btn_to_cart_list = (Button) findViewById(R.id.btn_to_cart_list);
		txt_description = (TextView) findViewById(R.id.txt_product_description);
		show_size_view = (LinearLayout) findViewById(R.id.show_size_view);
		show_color_view = (LinearLayout) findViewById(R.id.show_color_view);
		sizes = new ArrayList<String>();
		color_codes = new ArrayList<String>();
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		try {
			System.out.println("product_link_banner.." + product_link_banner);
			vProduct_ID = product_link_banner
					.replace(
							"http://ealpha.com//mob/customers.php?get_data=product_data&product_id=",
							"");
			if (isProductAddedIntoCart(vProduct_ID)) {
				btn_to_cart_list.setText("Remove from Cart");
			} else {
				btn_to_cart_list.setText("Add to Cart");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		btn_to_cart_list.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!sessionManager.isLogin()) {
					Toast.makeText(ProductDetailActivityBanner.this,
							"User not logged in.", Toast.LENGTH_SHORT).show();
					return;
				}
				vProduct_ID = product_link_banner
						.replace(
								"http://ealpha.com//mob/customers.php?get_data=product_data&product_id=",
								"");
				if (btn_to_cart_list.getText().toString().trim()
						.equals("Add to Cart")) {
					btn_to_cart_list.setText("Remove from Cart");
					new addToCartAsyncTask()
							.execute("http://www.ealpha.com/mob/customers.php?customers=add_to_cart");
				} else if (btn_to_cart_list.getText().toString().trim()
						.equals("Remove from Cart")) {
					btn_to_cart_list.setText("Add to Cart");
					new addToCartAsyncTask()
							.execute("http://www.ealpha.com/mob/customers.php?customers=remove_add_to_cart");
				}
			}
		});
		progressDialog = new ProgressDialog(this);
		progressDialog.setIndeterminate(true);
		progressDialog.setTitle("Please wait");
		progressDialog.setCancelable(true);
		progressDialog.setMessage("Please wait");
		progressDialog.show();
		new popular_p_AsynchTask().execute(product_link_banner);
	}

	class popular_p_AsynchTask extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected JSONObject doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JSONObject json = new JSONParser().makeHttpRequest2(args[0],
					"POST", params);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			super.onPostExecute(jsonObject);
			progressDialog.dismiss();
			// ----------for slider--------------
			try {
				System.out.println("product_data..." + jsonObject.toString());

				JSONObject productDataObject1 = jsonObject
						.getJSONObject("product_data");
				try {
					txt_headline.setText(productDataObject1.getString("name"));
					// tum is try catch me jo bhi value nikalna hai set kr sakte
					// ho okay
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					txt_price.setText(productDataObject1
							.getString("final_price").replaceAll("\\.0*$", ""));
					// tum is try catch me jo bhi value nikalna hai set kr sakte
					// ho okay
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					txt_description.setText(productDataObject1
							.getString("description"));
					// tum is try catch me jo bhi value nikalna hai set kr sakte
					// ho okay
				} catch (Exception e) {
					// TODO: handle exception
					txt_color.setText(productDataObject1.getString("color"));
				}
				try {
					txt_color.setText(productDataObject1
							.getString("product_attribute"));
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					txt_size.setText(productDataObject1.getString("size"));

				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					JSONArray size = productDataObject1.getJSONArray("size");
					for (int i = 0; i < size.length(); i++) {
						sizes.add(size.getString(i));
					}

				} catch (Exception e) {

				}
				try {
					JSONArray color_code = productDataObject1
							.getJSONArray("color");
					for (int i = 0; i < color_code.length(); i++) {
						color_codes.add(color_code.getString(i));
					}
				} catch (Exception e) {

				}

				// for slider on top
				JSONObject sliderImageObject = productDataObject1.getJSONArray(
						"product_image").getJSONObject(0);
				sliderDTOs = new ArrayList<SliderDTO>();
				try {
					JSONArray product_img_default = sliderImageObject
							.getJSONArray("product_img_default");
					for (int i = 0; i < product_img_default.length(); i++) {
						sliderDTO = new SliderDTO();
						sliderDTO.setSlider_image(product_img_default
								.getString(i));
						sliderDTOs.add(sliderDTO);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				setSlider();
			} catch (Exception e) {
				// TODO: handle exception
			}

			try {

				setSize();
				setColors();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	public boolean isProductAddedIntoCart(String vProduct_ID) {
		boolean isAddedInoCart = false;
		ArrayList<CartsDTO> cartsDTOs = new ArrayList<CartsDTO>();
		try {
			cartsDTOs = sessionManager.getCartsDTOs();
		} catch (Exception e) {
			// TODO: handle exception
		}
		for (int i = 0; i < cartsDTOs.size(); i++) {
			if (cartsDTOs.get(i).getId_product().equals(vProduct_ID)) {
				isAddedInoCart = true;
				break;
			}
		}
		return isAddedInoCart;
	}

	private void removeProductFromCart(String vProduct_id) {
		// TODO Auto-generated method stub
		ArrayList<CartsDTO> cartsDTOs = new ArrayList<CartsDTO>();
		try {
			cartsDTOs = sessionManager.getCartsDTOs();
		} catch (Exception e) {
			// TODO: handle exception
		}
		for (int i = 0; i < cartsDTOs.size(); i++) {
			if (cartsDTOs.get(i).getId_product().equals(vProduct_id)) {
				cartsDTOs.remove(i);
				break;
			}
		}
		sessionManager.setCartsDTOs(cartsDTOs);
	}

	public void setSlider() {
		viewpager.setAdapter(new ImageGalleryProductAdapter(this, sliderDTOs));
		viewpager.postDelayed(new Runnable() {
			int i = 0;

			@Override
			public void run() {
				if (i == sliderDTOs.size()) {
					i = 0;
				}
				viewpager.setCurrentItem(i);
				i++;
				// viewpager.postDelayed(this, 2000);
			}
		}, 5000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.product_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class addToCartAsyncTask extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected JSONObject doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_customer", sessionManager
					.getUserDetail().getCustomer_id()));
			params.add(new BasicNameValuePair("id_product", vProduct_ID));
			params.add(new BasicNameValuePair("quantity", "1"));
			System.out.println("params..." + params.toString());
			// [id_customer=8057, id_product=9572, quantity=1]
			JSONObject json = new JSONParser().makeHttpRequest2(args[0],
					"POST", params);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			super.onPostExecute(jsonObject);
			String vStatus = "";
			String vMessage = "";
			try {
				// {"add_to_cart":{"message":"Product successfully added to your shopping cart","status":"Success"}}
				// {"customer_remove_add_to_cart":{"status":"Success","message":"Product Remove successfully"}}
				System.out.println("add_to_cart..." + jsonObject.toString());
				JSONObject add_to_cart = null;
				if (jsonObject.has("add_to_cart")) {
					add_to_cart = jsonObject.getJSONObject("add_to_cart");
					vStatus = add_to_cart.getString("status");
					String vCartId = add_to_cart
							.getString("message")
							.replace(
									"Product successfully added to your shopping cart, Your Cart Id Is : ",
									"");
					if (vCartId != null) {
						if (vCartId.trim().length() > 0) {
							MainActivity.cartsDTO.setId_cart(vCartId);
							ArrayList<CartsDTO> cartsDTOs = new ArrayList<>();
							try {
								cartsDTOs = sessionManager.getCartsDTOs();
							} catch (Exception e) {

							}
							if (cartsDTOs == null) {
								cartsDTOs = new ArrayList<>();
							}
							if (cartsDTOs.size() > 0) {
								cartsDTOs = sessionManager.getCartsDTOs();
								cartsDTOs.add(MainActivity.cartsDTO);
								sessionManager.setCartsDTOs(cartsDTOs);
							} else {
								cartsDTOs.add(MainActivity.cartsDTO);
								sessionManager.setCartsDTOs(cartsDTOs);
							}
						} else {
							MainActivity.cartsDTO.setId_cart(vCartId);
							ArrayList<CartsDTO> cartsDTOs = new ArrayList<>();
							try {
								cartsDTOs = sessionManager.getCartsDTOs();
							} catch (Exception e) {

							}
							if (cartsDTOs == null) {
								cartsDTOs = new ArrayList<>();
							}
							if (cartsDTOs.size() > 0) {
								cartsDTOs = sessionManager.getCartsDTOs();
								cartsDTOs.add(MainActivity.cartsDTO);
								sessionManager.setCartsDTOs(cartsDTOs);
							} else {
								cartsDTOs.add(MainActivity.cartsDTO);
								sessionManager.setCartsDTOs(cartsDTOs);
							}
						}
					}
					vMessage = "item added into cart.";
				} else if (jsonObject.has("customer_remove_add_to_cart")) {
					add_to_cart = jsonObject
							.getJSONObject("customer_remove_add_to_cart");
					vMessage = "item removed from cart.";
					vStatus = add_to_cart.getString("status");
					removeProductFromCart(vProduct_ID);
				}
				if (vStatus.trim().equals("Success")) {
					Toast.makeText(ProductDetailActivityBanner.this, vMessage,
							Toast.LENGTH_SHORT).show();
				} else if (vStatus.trim().equals("Error")) {
					Toast.makeText(ProductDetailActivityBanner.this, vMessage,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ProductDetailActivityBanner.this,
							"item not added into cart.", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (Exception e) {

			}
		}
	}

	public void setSize() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
		params.setMargins(10, 10, 10, 10);
		for (int i = 0; i < sizes.size(); i++) {

			final TextView size_text = new TextView(this);
			size_text.setLayoutParams(params);
			size_text.setPadding(10, 10, 10, 10);
			try {
				size_text.setText(Integer.parseInt(sizes.get(i)) + "");
			} catch (Exception e) {
				if (sizes.get(i).contains("Short")) {
					size_text.setText("S");
				} else if (sizes.get(i).contains("Medium")) {
					size_text.setText("M");
				} else if (sizes.get(i).contains("Long")) {
					size_text.setText("L");
				} else if (sizes.get(i).contains("XL : Extra large")) {
					size_text.setText("XL");
				} else if (sizes.get(i).contains("XXL : Extra Extra Large")) {
					size_text.setText("XXL");
				} else if (sizes.get(i).contains(
						"XXXL : Extra Extra Extra Large")) {
					size_text.setText("XXXL");
				} else {
					size_text.setText(sizes.get(i) + "");
				}
			}
			size_text.setAllCaps(true);
			size_text.setGravity(Gravity.CENTER);
			size_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			size_text.setBackgroundColor(Color.parseColor("#D3D3D3"));
			size_text.setId(i);
			size_text.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (id_size == -1) {
						id_size = v.getId();
						size_text.setBackgroundColor(Color
								.parseColor("#E3E3E3"));
					} else {
						TextView textView = (TextView) show_size_view
								.findViewById(id_size);
						textView.setBackgroundColor(Color.parseColor("#D3D3D3"));
						if (id_size == v.getId()) {
							if (is_same_size) {
								is_same_size = false;
							} else {
								is_same_size = true;
								return;
							}
						}
						id_size = v.getId();
						size_text.setBackgroundColor(Color
								.parseColor("#E3E3E3"));
					}
				}
			});
			show_size_view.addView(size_text);
		}
	}

	public void setColors() {
		final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				new ViewGroup.LayoutParams(50, 50));
		params.setMargins(10, 10, 10, 10);
		for (int i = 0; i < color_codes.size(); i++) {
			final TextView size_text = new TextView(this);
			size_text.setLayoutParams(params);
			size_text.setPadding(10, 10, 10, 10);
			size_text.setGravity(Gravity.CENTER);
			size_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			try {
				default_color_code = color_codes.get(i);
			} catch (Exception e) {

			}
			size_text.setBackgroundColor(Color.parseColor(default_color_code));
			size_text.setId(i);
			size_text.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					viewpager.setCurrentItem(v.getId());
					Toast.makeText(ProductDetailActivityBanner.this,
							"Color Selected.", Toast.LENGTH_SHORT).show();
				}
			});
			show_color_view.addView(size_text);
		}
	}

}

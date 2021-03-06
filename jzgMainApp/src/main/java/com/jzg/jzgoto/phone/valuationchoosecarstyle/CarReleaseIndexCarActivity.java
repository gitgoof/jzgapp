package com.jzg.jzgoto.phone.valuationchoosecarstyle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.SlidingDrawer.OnDrawerScrollListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jzg.jzgoto.phone.R;
import com.jzg.jzgoto.phone.event.CarChooseEvent;
import com.jzg.jzgoto.phone.global.AppContext;
import com.jzg.jzgoto.phone.models.business.car.CarMakeBean;
import com.jzg.jzgoto.phone.models.business.car.CarMakeResultModels;
import com.jzg.jzgoto.phone.models.business.car.CarModelBean;
import com.jzg.jzgoto.phone.models.business.car.CarModelResultModels;
import com.jzg.jzgoto.phone.models.business.car.CarStyleBean;
import com.jzg.jzgoto.phone.models.business.car.CarStyleResultModels;
import com.jzg.jzgoto.phone.services.business.cartype.GetCarListInterface;
import com.jzg.jzgoto.phone.services.business.cartype.GetCarListService;
import com.jzg.jzgoto.phone.tools.DialogManager;
import com.jzg.jzgoto.phone.tools.ShowDialogTool;
import com.jzg.pricechange.phone.JzgCarChooseChineseUtil;
import com.jzg.pricechange.phone.JzgCarChooseConstant;
import com.jzg.pricechange.phone.JzgCarChooseCustomArrayAdapter;
import com.jzg.pricechange.phone.JzgCarChooseCustomArrayAdapter.OnImgClickListener;
import com.jzg.pricechange.phone.JzgCarChooseCustomData;
import com.jzg.pricechange.phone.JzgCarChooseHorizontalListView;
import com.jzg.pricechange.phone.JzgCarChooseMake;
import com.jzg.pricechange.phone.JzgCarChooseMakeList;
import com.jzg.pricechange.phone.JzgCarChooseModel;
import com.jzg.pricechange.phone.JzgCarChooseModelCategory;
import com.jzg.pricechange.phone.JzgCarChooseModelList;
import com.jzg.pricechange.phone.JzgCarChooseMyLetterListView;
import com.jzg.pricechange.phone.JzgCarChooseMyLetterListView.OnTouchingLetterChangedListener;
import com.jzg.pricechange.phone.JzgCarChooseStyle;
import com.jzg.pricechange.phone.JzgCarChooseStyleCategory;
import com.jzg.pricechange.phone.JzgCarChooseStyleCategoryAdapter;
import com.jzg.pricechange.phone.JzgCarChooseStyleList;
import com.jzg.pricechange.phone.PhoneJzgApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.greenrobot.event.EventBus;

/**
 * 发车、筛选车型索引共用界面
 * 新车1，旧车0
 * @author 作者 E-mail:
 * @version 创建时间：2015-1-5 下午5:30:02 类说明
 */
@SuppressLint("NewApi")
public class CarReleaseIndexCarActivity extends Activity implements
		OnClickListener, OnItemClickListener, OnDrawerOpenListener,
		OnDrawerCloseListener, OnScrollListener, OnImgClickListener {

	public static final String PARAMS_KEY_SOURCEFROM = "key_source_from_id";
	private static final int SUCCESS = 100;
	private int mSourceFrom = 0;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	private JzgCarChooseMyLetterListView mIndexListView = null;
	private ListView mIndexCarListView = null;
	private Button mReturnBtn = null;

	// 存放存在的汉语拼音首字母
	private String[] sections;

	// 存放存在的汉语拼音首字母和与之对应的列表位置
	private HashMap<String, Integer> index;

	/**
	 * 汽车系列drawer
	 */
	private SlidingDrawer mCarTypeDrawer;
	private ImageView mCarTypeHandle;
	private ListView mCarTypeContent;

	/**
	 * 汽车类型drawer
	 */
	private SlidingDrawer mCarYearstyleDrawer;
	private ImageView mCarYearstyleHandle;
	private ListView mCarYearstyleContent;

	/**
	 * 热门推荐
	 * 
	 * private HotCarList mHotCarList;
	 */

	/**
	 * 品牌列表
	 */
	private JzgCarChooseMakeList mMakeList;

	/**
	 * 品牌、车系、车型/热门车控制
	 */
	private Handler mHandler;

	/**
	 * 汽车品牌排序集合
	 */
	private ArrayList<Map<String, Object>> items;

	/**
	 * 当前选择的汽车品牌
	 */
	JzgCarChooseMake make = null;
	
	/**
	 * 当前选择的汽车品牌名称
	 */
	private String mCurMake;

	/**
	 * 当前选择的品牌id
	 */
	private int mCurMakeid;

	/**
	 * 当前选择的汽车车系
	 */
	private String mCurModel;

	/**
	 * 当前选择的车系id
	 */
	private int mCurModelid;

	/**
	 * 解析后车系json数据封装
	 */
	private List<JzgCarChooseModelCategory> mModelCategorys;

	/**
	 * 品牌列表所有数据
	 */
	private ArrayList<JzgCarChooseMake> makes;

	/**
	 * 车系列表所有数据包括标题
	 */
	private List<JzgCarChooseModel> mModels;

	/**
	 * 所有车系标题
	 */
	private List<String> mModelsGroupkey;

	/**
	 * 车型列表所有数据包括标题
	 */
	private List<JzgCarChooseStyle> mStyles;

	/**
	 * 所有车型标题
	 */
	private List<String> mStylessGroupkey;

	/**
	 * 车型json解析数据封装
	 */
	private List<JzgCarChooseStyleCategory> carStyles;

	/**
	 * 品牌列表适配器
	 */
	private ListAdapter mIndexCarListAdapter;

	/**
	 * 车系列表适配器
	 */
	private JzgCarChooseModelCategoryAdapter1 mModelCategoryAdapter;

	/**
	 * 车型列表适配器
	 */
	private JzgCarChooseStyleCategoryAdapter mStyleCategoryAdapter;

	/**
	 * 品牌列表的上一次点击位置
	 */
	private int mMakeListOldPosition = -1;

	/**
	 * 车系列表的上一次点击位置
	 */
	private int mModelListOldPosition = -1;

	/**
	 * 车型列表的上一次点击位置
	 */
	private int mStyleListOldPosition = -1;

	/**
	 * 图片加载配置选项
	 */
	private DisplayImageOptions mOptions;

	/**
	 * 弹出窗口管理器
	 */
	private DialogManager mDialogManager;

	/**
	 * 加载中提示框
	 */
	private Dialog mDialog;

	private AppContext mAppContext;

	
	public static final int QUERYCAR_MSG = 1;// 车辆信息

	private String carFilterModleFlag = "";

	/**
	 * 判断是否有历史记录的标记
	 */
	private boolean queryEditIsNull;

	/**
	 * 筛选过程中选择的车型项
	 */
	private List<JzgCarChooseModel> models;

	private JzgCarChooseHorizontalListView mHlvCustomListWithDividerAndFadingEdge;
	private JzgCarChooseCustomArrayAdapter adapter = null;
	private static List<JzgCarChooseCustomData> mCustomData = new ArrayList<JzgCarChooseCustomData>();
	private LinearLayout chooseLayout;

	private boolean isFirst;
	
	/**
	 * 是查询新车还是旧车，新车1，旧车0
	 */
	private int newOrOld = 0;
	
	private TextView tv_title_center_text;
	
	private String curApp = "1";	//当前应用精真估1，姑爷2,现代首选交易系统4,一汽大众3

	private ImageView make_logo_img;
	private TextView make_name_text;
	
	private int curYear,curMonth,curDay;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_valuation_choose_carstyle_layout);
		mHandler = getHandler();
		mAppContext = (AppContext) getApplicationContext();
		mOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.jzgcarchoose_jingzhengu)
				.showImageForEmptyUri(R.drawable.jzgcarchoose_jingzhengu)
				.showImageOnFail(R.drawable.jzgcarchoose_jingzhengu).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(20)).build();
		
		carFilterModleFlag = getIntent().getStringExtra("carfilter_modle");
		queryEditIsNull = getIntent().getBooleanExtra("queryEditIsNull", true);
		newOrOld = getIntent().getIntExtra("newOrOld", 0);
		init();
		
		final Calendar end = Calendar.getInstance();
		curYear = end.get(Calendar.YEAR);
		curMonth = end.get(Calendar.MONTH);
		curDay = end.get(Calendar.DAY_OF_MONTH);
		mSourceFrom = getIntent().getIntExtra(PARAMS_KEY_SOURCEFROM, 0);
	}

	public void query_car_left_back(View v) {
		if(mCarYearstyleDrawer.isOpened()){
			mCarYearstyleDrawer.animateClose();
			tv_title_center_text.setText("选择车系");
		}else{
			this.finish();
		}
		
	}

	private Handler getHandler() {
		return new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (mDialog != null && mDialog.isShowing())
					mDialog.dismiss();
				int id = msg.what;
				switch (id) {
				case JzgCarChooseConstant.hotcarlist:
					// 组装热门品牌数据
					assemblyHotCarGrid(msg);
					break;
				case JzgCarChooseConstant.makelist:
					// 组装汽车品牌数据
					assemblyMakeList(msg);
					break;
				case JzgCarChooseConstant.modellist:
					colseDrawer(mCarYearstyleDrawer);
					if (checkModelist(msg)) {
						// 组装汽车系列数据
						assemblyModelList(msg);
					} else {
						colseDrawer(mCarTypeDrawer);
					}
					break;
				case JzgCarChooseConstant.stylelist:
					// 组装汽车类型数据
					if (checkResult(msg)) {
						assemblyStyleList(msg);
					}
					break;

				case JzgCarChooseConstant.car_detail:
					completeCarDetail(msg);
					break;
				case JzgCarChooseConstant.net_error:
					// showError(getResources().getString(R.string.net_error));
					break;
				case JzgCarChooseConstant.carDetailSuc:
					break;
				case JzgCarChooseConstant.net_error_back:
					ShowDialogTool.showCenterToast(CarReleaseIndexCarActivity.this,getResources().getString(R.string.error_net));
				//	Toast.makeText(CarReleaseIndexCarActivity.this, "数据返回失败，请重试", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}

			}

			private void assemblyHotCarGrid(Message msg) {
			}

			/**
			 * 查车详情完成 completeCarDetail: <br/>
			 * @param msg
			 */
			private void completeCarDetail(Message msg) {
			}

			private void assemblyStyleList(Message msg) {
				JzgCarChooseStyleList styleList = (JzgCarChooseStyleList) msg.obj;
				carStyles = styleList.getCarStyles();

				// 车型数据加载完毕后添加数据到车型历史记录
				PhoneJzgApplication.mHistoryCarStyles.clear();
				if(carStyles!=null){
					PhoneJzgApplication.mHistoryCarStyles.addAll(carStyles);
					showStyleList(queryEditIsNull ? carStyles
							: PhoneJzgApplication.mHistoryCarStyles);
				}else{
					Toast.makeText(mAppContext, styleList.getMsg(), Toast.LENGTH_SHORT).show();
					if(mCarYearstyleDrawer.isOpened()){
						mCarYearstyleDrawer.close();
					}
				}

			}

			private void assemblyModelList(Message msg) {
				JzgCarChooseModelList modelList = (JzgCarChooseModelList) msg.obj;
				mModelCategorys = modelList.getModels();
//				mModelsList = modelList.getmModelsList();

				// 车系数据加载完毕后添加数据到车系历史记录列表
				PhoneJzgApplication.mHistoryModelCategorys.clear();
				PhoneJzgApplication.mHistoryModelCategorys
						.addAll(mModelCategorys);

//				 组装数据的时候如果历史记录标记是null位true则用原有列表，否则使用历史记录列表
				 showModelList(queryEditIsNull ? mModelCategorys
				 : PhoneJzgApplication.mHistoryModelCategorys);

//				showModelListNew(mModelsList);
			}

			private void assemblyMakeList(Message msg) {
				mMakeList = (JzgCarChooseMakeList) msg.obj;

					makes = mMakeList.getMakes();

					// 加载品牌数据完成后添加数据到品牌历史记录
					PhoneJzgApplication.mHistoryMakes.clear();
					PhoneJzgApplication.mHistoryMakes.addAll(makes);

					showMakeList(makes);


			}
		};
	}

	/**
	 * 显示品牌列表数据
	 * 
	 * @param makes
	 */
	protected void showMakeList(ArrayList<JzgCarChooseMake> makes) {
		setMakeColor();

		String contactSort = null;
		items = new ArrayList<Map<String, Object>>();
		if (makes != null) {
			Map<String, Object> map = null;
			for (int i = 0; i < makes.size(); i++) {
				map = new HashMap<String, Object>();
				map.put("name", makes.get(i).getMakeName());
				map.put("fontColor", makes.get(i).getFontColor());
				map.put("logo", makes.get(i).getMakeLogo());
				contactSort = JzgCarChooseChineseUtil
						.getFullSpell(map.get("name").toString()).toUpperCase()
						.substring(0, 1);
				map.put("Sort", contactSort);
				map.put("itemColor", makes.get(i).getItemColor());
				items.add(map);
			}
			Comparator comp = new Mycomparator();
			Collections.sort(items, comp);
			mIndexCarListAdapter = new ListAdapter(getApplicationContext(),
					items);
			mIndexCarListView.setAdapter(mIndexCarListAdapter);
			if (mMakeListOldPosition != -1) {
				mIndexCarListView.setSelection(mMakeListOldPosition);
			}
		}
	}

	private void setMakeColor() {
		// 设置所有颜色为原色
		for (JzgCarChooseMake make : makes) {
			make.setFontColor(Color.BLACK);
			make.setItemColor(Color.WHITE);
		}

		// 设置点击item的点击颜色
		if (mMakeListOldPosition != -1) {
			// makes.get(mMakeListOldPosition).setFontColor(
			// getResources().getColor(R.color.list_click));
			makes.get(mMakeListOldPosition).setItemColor(
					getResources().getColor(R.color.grey2));
		}

	}

	/**
	 * 显示热门车系列表
	 * 
	 * @param hotCars
	 * 
	 *            protected void showHotCarList(List<HotCar> hotCars) {
	 *            mHotRecommended.setAdapter(new
	 *            GridAdapter(getApplicationContext(), hotCars)); }
	 */

	/**
	 * 显示车系列表
	 * @param mModelCategorys
	 */
	protected void showModelList(List<JzgCarChooseModelCategory> mModelCategorys) {
		openDrawer(mCarTypeDrawer);
		tv_title_center_text.setText("选择车系");
		if(make != null){
			ImageLoader.getInstance().displayImage(make.getMakeLogo(),make_logo_img,mOptions);
			make_name_text.setText(make.getMakeName());
		}
		
		mModels = new ArrayList<JzgCarChooseModel>();
		mModelsGroupkey = new ArrayList<String>();
		for (JzgCarChooseModelCategory category : mModelCategorys) {
			JzgCarChooseModel model = new JzgCarChooseModel();
			String groupName = category.getmCategoryName();
			mModelsGroupkey.add(groupName);
			model.setName(groupName);
			model.setManufacturerName(JzgCarChooseConstant.IS_TITLE);
			model.setFontColor(getResources().getColor(R.color.categroy_title));
			mModels.add(model);
			mModels.addAll(category.getmCategoryItem());
		}

		setModelColor();

		mModelCategoryAdapter = new JzgCarChooseModelCategoryAdapter1(
				getApplicationContext(), mModels, mModelsGroupkey);
		mCarTypeContent.setAdapter(mModelCategoryAdapter);

		// 只有为历史记录的时候才跳转历史选择的项
		if (!queryEditIsNull) {
			if (PhoneJzgApplication.mModelHistoryPosition != -1) {
				mCarTypeContent
						.setSelection(PhoneJzgApplication.mModelHistoryPosition);
			}
		}
	}

	private void setModelColor() {
		// 设置所有颜色为原色
		for (JzgCarChooseModel model : mModels)
			if (JzgCarChooseConstant.IS_TITLE.equals(model.getManufacturerName())) {
				model.setFontColor(getResources().getColor(
						R.color.categroy_title));
			} else {
				model.setFontColor(Color.BLACK);
				model.setItemColor(Color.WHITE);
			}
		// 设置点击item的点击颜色
		if (mModelListOldPosition != -1)
			mModels.get(mModelListOldPosition).setItemColor(
					getResources().getColor(R.color.grey2));
		// mModels.get(mModelListOldPosition).setFontColor(
		// getResources().getColor(R.color.list_click));
	}

	/**
	 * 显示车型列表
	 * 
	 * @param carStyles
	 */
	protected void showStyleList(List<JzgCarChooseStyleCategory> carStyles) {
		openDrawer(mCarYearstyleDrawer);
		mStyles = new ArrayList<JzgCarChooseStyle>();
		mStylessGroupkey = new ArrayList<String>();
		for (JzgCarChooseStyleCategory category : carStyles) {
			JzgCarChooseStyle style = new JzgCarChooseStyle();
			String groupName = category.getYearTitle();
			mStylessGroupkey.add(groupName);
			style.setName(groupName);
			style.setManufacturerName(JzgCarChooseConstant.IS_TITLE);
			style.setFontColor(getResources().getColor(R.color.categroy_title));
			style.setItemColor(Color.WHITE);
			// 添加标题到列表
			mStyles.add(style);
			// 添加所有选项到列表
			mStyles.addAll(category.getCategoryItem());
		}

		setStyleColor();

		mStyleCategoryAdapter = new JzgCarChooseStyleCategoryAdapter(
				getApplicationContext(), mStyles, mStylessGroupkey);
		mCarYearstyleContent.setAdapter(mStyleCategoryAdapter);

		// 只有为历史记录的时候才跳转历史选择的项
		if (!queryEditIsNull) {
			if (PhoneJzgApplication.mStyleHistoryPosition != -1) {
				mCarYearstyleContent
						.setSelection(PhoneJzgApplication.mStyleHistoryPosition);
			}
		}

	}

	private void setStyleColor() {
		// 设置所有颜色为原色
		for (JzgCarChooseStyle style : mStyles)
			if (mStylessGroupkey.contains(style.getName())) {
				style.setFontColor(getResources().getColor(
						R.color.categroy_title));
			} else {
				style.setFontColor(Color.BLACK);
				style.setItemColor(Color.WHITE);
			}
		// 设置点击item的点击颜色
		if (mStyleListOldPosition != -1)
			mStyles.get(mStyleListOldPosition).setItemColor(
					getResources().getColor(R.color.grey2));
		// 原有修改点击item字体颜色代码
		// mStyles.get(mStyleListOldPosition).setFontColor(
		// getResources().getColor(R.color.list_click));
	}

	protected void openDrawer(SlidingDrawer mDrawer) {
		if (!mDrawer.isOpened()) {
			mDrawer.animateOpen();
		}
	}

	protected void colseDrawer(SlidingDrawer mDrawer) {
		if (mDrawer.isOpened()) {
			mDrawer.close();
		}
	}

	/**
	 * 校验Modellist返回结果 checkModelist: <br/>
	 * @param msg
	 * @return
	 */
	protected boolean checkModelist(Message msg) {
		JzgCarChooseModelList modelList = (JzgCarChooseModelList) msg.obj;
		if (modelList.isSuccess())
			return true;
		return false;
	}

	protected boolean checkResult(Message msg) {
		JzgCarChooseStyleList styleList = (JzgCarChooseStyleList) msg.obj;
		if (styleList.isSuccess()) {
			return true;
		}else{
			
		}
		return false;
	}

	// 按中文拼音排序
	public class Mycomparator implements Comparator {
		@Override
		public int compare(Object o1, Object o2) {
			Map<String, Object> c1 = (Map<String, Object>) o1;
			Map<String, Object> c2 = (Map<String, Object>) o2;
			Comparator cmp = Collator.getInstance(java.util.Locale.ENGLISH);
			return cmp.compare(c1.get("Sort"), c2.get("Sort"));
		}
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// MobclickAgent.onPause(this);
		// 活动界面暂停的时候对开启的滑动界面进行关闭操作
		if (mCarTypeDrawer != null && mCarTypeDrawer.isOpened()) {
			mCarTypeDrawer.close();
		}
		if (mCarYearstyleDrawer != null && mCarYearstyleDrawer.isOpened()) {
			mCarYearstyleDrawer.close();
		}
	}

	public void init() {
		models = new ArrayList<JzgCarChooseModel>();
		models.clear();
		mCustomData.clear();
		isFirst = true;
		chooseLayout = (LinearLayout) findViewById(R.id.chooseLayout);
		tv_title_center_text = (TextView) findViewById(R.id.tv_title_center_text);
		make_logo_img = (ImageView) findViewById(R.id.make_logo_img);
		make_name_text = (TextView) findViewById(R.id.make_name_text);
		

		mDialogManager = new DialogManager();

		// 初始化汽车品牌
		mIndexListView = (JzgCarChooseMyLetterListView) findViewById(R.id.index_car_index_list);
		mIndexListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());
		mIndexCarListView = (ListView) findViewById(R.id.index_car_list);
		mIndexCarListView.setOnItemClickListener(this);
		// 开启汽车品牌查询线程
//		mDialog = mDialogManager.show(this, this, R.drawable.jzgcarchoose_bg_loading);

		// 初始化汽车类型
		mCarTypeDrawer = (SlidingDrawer) findViewById(R.id.index_car_type_drawer);
		mCarTypeHandle = (ImageView) findViewById(R.id.index_car_type_handle);
		mCarTypeContent = (ListView) findViewById(R.id.index_car_type_list_content);
		mCarTypeContent.setOnItemClickListener(this);
		// 设置SlidingDrawer打开或者关闭时的监听器，设置失去
		mCarTypeDrawer.setOnDrawerOpenListener(this);
		mCarTypeDrawer.setOnDrawerCloseListener(this);

		// 设置mCarTypeDrawer滑动时监听，如果mCarYearstyleDrawer是打开状态则先关闭
		mCarTypeDrawer.setOnDrawerScrollListener(new OnDrawerScrollListener() {
			@Override
			public void onScrollStarted() {
				colseDrawer(mCarYearstyleDrawer);
			}

			@Override
			public void onScrollEnded() {

			}
		});

		// 初始化汽车年度款式
		mCarYearstyleDrawer = (SlidingDrawer) findViewById(R.id.index_car_yearstyle_drawer);
		mCarYearstyleHandle = (ImageView) findViewById(R.id.index_car_yearstyle_handle);

		mCarYearstyleContent = (ListView) findViewById(R.id.index_car_yearstyle_content);
		mCarYearstyleContent.setOnItemClickListener(this);

		// 设置SlidingDrawer打开或者关闭时的监听器
		mCarYearstyleDrawer.setOnDrawerOpenListener(this);
		mCarYearstyleDrawer.setOnDrawerCloseListener(this);

		mIndexCarListView.setOnScrollListener(this);
		mCarTypeContent.setOnScrollListener(this);
		
//		startMakeListThread(curApp,newOrOld);
		
		if (makes == null) {
			makes = new ArrayList<JzgCarChooseMake>();
		} else {
			makes.clear();
		}
		
		getCarListInterface = GetCarListService.getInstance(this);
		CarMakeResultModels carMakeResultModels = getCarListInterface.queryCarBrand(String.valueOf(newOrOld));
		List<CarMakeBean> ds = carMakeResultModels.getMakeList();
		
		for(int i = 0;i<ds.size();i++){
			JzgCarChooseMake JzgCarChooseMake = new JzgCarChooseMake();
			JzgCarChooseMake.setMakeName(ds.get(i).getMakeName());
			JzgCarChooseMake.setGroupName(ds.get(i).getGroupName());
			JzgCarChooseMake.setMakeId(Integer.parseInt(ds.get(i).getMakeId()));
			JzgCarChooseMake.setMakeLogo(ds.get(i).getMakeLogo());
			
			makes.add(JzgCarChooseMake);
		}
		//显示品牌
		showMakeList(makes);
		
	}
	private GetCarListInterface getCarListInterface = null;

	@SuppressLint("ResourceAsColor")
	private void showAllHistoryList() {
		if (mDialog != null)
			mDialog.dismiss();

		mModelListOldPosition = PhoneJzgApplication.mModelHistoryPosition;
		mStyleListOldPosition = PhoneJzgApplication.mStyleHistoryPosition;
/*
		if (makes == null) {
			makes = new ArrayList<JzgCarChooseMake>();
		} else {
			makes.clear();
		}
*/
		makes.addAll(PhoneJzgApplication.mHistoryMakes);
		showMakeList(makes);

		if (mModelCategorys == null) {
			mModelCategorys = new ArrayList<JzgCarChooseModelCategory>();
		} else {
			mModelCategorys.clear();
		}
		mModelCategorys.addAll(PhoneJzgApplication.mHistoryModelCategorys);
		showModelList(mModelCategorys);

		if (carStyles == null) {
			carStyles = new ArrayList<JzgCarChooseStyleCategory>();
		} else {
			carStyles.clear();
		}
		carStyles.addAll(PhoneJzgApplication.mHistoryCarStyles);
		showStyleList(carStyles);
	}

	/**
	 * 汽车品牌查询线程 runCarListThread: <br/>
	 */
	private void startMakeListThread(String tokenid,int newOrOldId) {
		HttpServiceCar.getMakeList(this,mHandler,
				AppContext.getRequestQueue(), JzgCarChooseConstant.makelist,tokenid
				,newOrOldId+"");
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int viewid = parent.getId();
		switch (viewid) {
		// 品牌列表点击
		case R.id.index_car_list:
			makeItemClick(view, position);
			break;
		// 车系列表点击
		case R.id.index_car_type_list_content:
			tv_title_center_text.setText("选择车型");
			modelItemClick(view, position);
			break;
		// 车型列表点击
		case R.id.index_car_yearstyle_content:
			styleItemClick(position);
			break;
		// 热门推荐
		/*
		 * case R.id.hot_recommended: hotRecommendedItemClick(position); break;
		 */
		default:
			// showError("出错了请仔细检查！");
			break;
		}
	}

	/*
	 * private void hotRecommendedItemClick(int position) { //
	 * 热门品牌被点击的时候记录历史被点击的位置 AppContext.mHotCarsHistoryPosition =
	 * mHotcarsOldPosition = position;
	 * 
	 * // 判断是否为历史记录如果不是历史记录走之前的流程 HotCar hotCar = null; if (queryEditIsNull) {
	 * hotCar = mHotCarList.getHotCars().get(position); } // 如果有历史记录则从历史记录中拿取数据
	 * else { hotCar = AppContext.mHistoryHotCars.get(position); }
	 * 
	 * final String makeid = String.valueOf(hotCar.getMakeId());
	 * startModelListThread(makeid); mDialog = mDialogManager.show(this, this,
	 * R.drawable.bg_loading); // 每次点击选热门品牌列表后，对车系上一次点击位置进行初始化
	 * AppContext.mModelHistoryPosition = mModelListOldPosition = -1; }
	 */

	private void styleItemClick(int position) {
		// 当车型列表被点击时，存储当前点击的位置
		PhoneJzgApplication.mStyleHistoryPosition = mStyleListOldPosition = position;

		JzgCarChooseStyle style = mStyles.get(position);

//		mCurStyleid = style.getId();
		style.setBrandId(mCurMakeid);
		style.setBrandName(mCurMake);
		style.setModeId(mCurModelid);
		style.setModeName(mCurModel);
		style.setModelimgpath(mHasCheckModel.getModelimgpath());
		
		PhoneJzgApplication.mQueryCarYear = style.getYear();
		Intent in = new Intent();
		in.putExtra("mQueryCarStyle", style);
		setResult(JzgCarChooseConstant.Valuation_QUERYCAR_MSG, in);
		EventBus.getDefault().post(CarChooseEvent.build(style, mSourceFrom));
		finish();

	}

	private JzgCarChooseModel mHasCheckModel;

	private void modelItemClick(View view, int position) {
		// 当车系列表被点击时，存储当前点击的位置
		PhoneJzgApplication.mModelHistoryPosition = mModelListOldPosition = position;

		modifyModelFontColor(view, R.id.addexam_list_item_text,
				R.color.list_click, position);
		int modelid = 0;
		// 走不是历史记录流程
		if (queryEditIsNull) {
			modelid = mModels.get(position).getId();
			mCurModel = mModels.get(position).getName();
			mHasCheckModel = mModels.get(position);
			// 走历史记录流程
		} else {
			ArrayList<JzgCarChooseModel> models = new ArrayList<JzgCarChooseModel>();
			for (JzgCarChooseModelCategory category : PhoneJzgApplication.mHistoryModelCategorys) {
				JzgCarChooseModel model = new JzgCarChooseModel();
				String groupName = category.getmCategoryName();
				model.setName(groupName);
				model.setManufacturerName(JzgCarChooseConstant.IS_TITLE);
				model.setFontColor(getResources().getColor(
						R.color.categroy_title));
				models.add(model);
				models.addAll(category.getmCategoryItem());
			}
			modelid = models.get(position).getId();
			mCurModel = models.get(position).getName();
			mHasCheckModel = models.get(position);
		}

		mCurModelid = modelid;
		if (mStylessGroupkey != null) {
			mStylessGroupkey.clear();
			mStyleCategoryAdapter.notifyDataSetChanged();
		}
		// 走筛选流程
		if ("carfilter_modle".equals(carFilterModleFlag)) {
			JzgCarChooseModel model = mModels.get(position);
			// 如果地区列表中不包含当前城市则添加到列表中，然后进行业务操作
			if (!models.contains(model)) {
				if (models.size() != 3) {
					models.add(model);
					mCustomData
							.add(new JzgCarChooseCustomData(Color.WHITE, model.getName()));
					chooseLayout.setVisibility(View.VISIBLE);
					if (isFirst) {
						// 继续组装list的数据
						isFirst = false;
						adapter = new JzgCarChooseCustomArrayAdapter(this, mCustomData,
								this);
						mHlvCustomListWithDividerAndFadingEdge
								.setAdapter(adapter);
					} else {
						if (adapter != null)
							adapter.notifyDataSetChanged();
					}
				} else {
					Toast.makeText(this, "品牌车系选项已达上限", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, model.getName() + "已经存在", Toast.LENGTH_SHORT).show();
			}
			// 估价、筛选流程
		} else {
//			mDialog = mDialogManager.show(this, this, R.drawable.jzgcarchoose_bg_loading);
//			startStyleListThread(String.valueOf(modelid),curApp,newOrOld+"");
			
			CarStyleResultModels carStyleResultModels  =
					getCarListInterface.queryCarStyle(String.valueOf(modelid), String.valueOf(newOrOld));
			
			List<CarStyleBean> Lists = carStyleResultModels.getStyleList();
			
			showStyleListLochost(Lists);
		}
		// 每次点击车系列表后，对车型列表上次点击的位置进行初始化
		mStyleListOldPosition = PhoneJzgApplication.mStyleHistoryPosition = -1;
	}
	
	/**
	 * 显示车型列表
	 * 
	 */
	protected void showStyleListLochost(List<CarStyleBean> styleLists) {
		openDrawer(mCarYearstyleDrawer);
		mStyles = new ArrayList<JzgCarChooseStyle>();
		mStylessGroupkey = new ArrayList<String>();
		
		TreeSet<String> set = new TreeSet<String>();
		for (CarStyleBean carStyleBean : styleLists) {
			set.add(carStyleBean.getYear());
		}
		
		Iterator<String> it = set.descendingIterator();
		while (it.hasNext()) {
			String title = it.next();
			System.out.println(title);
			ArrayList<JzgCarChooseStyle> list = new ArrayList<JzgCarChooseStyle>();
			for (CarStyleBean carStyleBean : styleLists) {
				if (title.equals(carStyleBean.getYear())) {
					JzgCarChooseStyle style = new JzgCarChooseStyle();
					style.setId(Integer.parseInt(carStyleBean.getId()));
					style.setName(carStyleBean.getName());
					style.setYear(Integer.parseInt(carStyleBean.getYear()));
					style.setFullName(carStyleBean.getFullName());
					// 最小上牌时间是当前年款的前一年。月份为6月
					style.setMinYEAR((Integer.parseInt(carStyleBean.getYear()) - 1)
							+ "-06");
					// 最大上牌时间是下一年款的加一年，月份默认到12月，如果下一款加一年，大于当前年就按当前年算。
					if ((Integer.parseInt(carStyleBean.getNextYear()) + 1) >= curYear) {
						style.setMaxYEAR(curYear + "-" + curMonth);
					} else {
						style.setMaxYEAR((Integer.parseInt(carStyleBean
								.getNextYear()) + 1) + "-12");

					}

					list.add(style);
				}
			}

			JzgCarChooseStyle style = new JzgCarChooseStyle();
			mStylessGroupkey.add(title);
			style.setName(title);
			style.setManufacturerName(JzgCarChooseConstant.IS_TITLE);
			style.setFontColor(getResources().getColor(R.color.categroy_title));
			style.setItemColor(Color.WHITE);
			// 添加标题到列表
			mStyles.add(style);
			// 添加所有选项到列表
			mStyles.addAll(list);

		}

		setStyleColor();

		mStyleCategoryAdapter = new JzgCarChooseStyleCategoryAdapter(
				getApplicationContext(), mStyles, mStylessGroupkey);
		mCarYearstyleContent.setAdapter(mStyleCategoryAdapter);

		// 只有为历史记录的时候才跳转历史选择的项
		if (!queryEditIsNull) {
			if (PhoneJzgApplication.mStyleHistoryPosition != -1) {
				mCarYearstyleContent
						.setSelection(PhoneJzgApplication.mStyleHistoryPosition);
			}
		}

	}

	private void makeItemClick(View view, int position) {
		// 当品牌列表被点击的时候存储当前点击的位置
		PhoneJzgApplication.mMakeHistoryPosition = mMakeListOldPosition = position;

		
		// 如果当前点击是在历史记录状态下进行的则从历史列表中拿取相应的数据
		if (!queryEditIsNull) {
			make = PhoneJzgApplication.mHistoryMakes.get(position);
		}
		// 否则从查询出来的数据中拿取数据
		else {
			make = makes.get(position);
		}
		final String makeid = String.valueOf(make.getMakeId());
		// 设置当前选择的品牌、id
		mCurMake = make.getMakeName();
		mCurMakeid = make.getMakeId();
		// TODO 颜色变化稍后添加，目前先把数据的逻辑理清
		modifyMakeFontColor(view, R.id.car_name, R.color.list_click, position);
//		mDialog = mDialogManager.show(this, this, R.drawable.jzgcarchoose_bg_loading);
		
		//请求车系
//		startModelListThread(makeid,curApp,newOrOld+"");
		// 每次点击选品牌列表后，对车系上一次点击位置进行初始化
		mModelListOldPosition = -1;
		PhoneJzgApplication.mModelHistoryPosition = -1;
		
		CarModelResultModels carModelResultModels  = getCarListInterface.queryCarModel(makeid, String.valueOf(newOrOld));
		List<CarModelBean> modelLists = carModelResultModels.getModelList();
		showModelListLochost(modelLists);
	}

	/**
	 * 显示车系列表
	 * 
	 */
	protected void showModelListLochost(List<CarModelBean> modelLists) {
		openDrawer(mCarTypeDrawer);
		tv_title_center_text.setText("选择车系");
		if(make != null){
			ImageLoader.getInstance().displayImage(make.getMakeLogo(),make_logo_img);
			make_name_text.setText(make.getMakeName());
		}
		
		mModels = new ArrayList<JzgCarChooseModel>();
		mModelsGroupkey = new ArrayList<String>();
		Set<String> set = new LinkedHashSet<String>();
//		Collections.reverse(modelLists);
		for (CarModelBean carModelBean : modelLists) {
			set.add(carModelBean.getManufacturerName());
		}
		
		Iterator<String> it = set.iterator();
		   while (it.hasNext()) {
			   String title = it.next();
		    System.out.println(title);
		    ArrayList<JzgCarChooseModel>  list = new ArrayList<JzgCarChooseModel>();
		    for (CarModelBean carModelBean : modelLists) {
		    	if(title.equals(carModelBean.getManufacturerName())){
		    		JzgCarChooseModel JzgCarChooseModel = new JzgCarChooseModel();
		    		JzgCarChooseModel.setId(Integer.parseInt(carModelBean.getId()));
		    		JzgCarChooseModel.setName(carModelBean.getName());
		    		JzgCarChooseModel.setManufacturerName(carModelBean.getManufacturerName());
		    		JzgCarChooseModel.setModelimgpath(carModelBean.getModelimgpath());
		    		list.add(JzgCarChooseModel);
		    	}
		    }
		    
		    JzgCarChooseModel model = new JzgCarChooseModel();
			mModelsGroupkey.add(title);
			model.setName(title);
			model.setManufacturerName(JzgCarChooseConstant.IS_TITLE);
			model.setFontColor(getResources().getColor(R.color.categroy_title));
			mModels.add(model);
			mModels.addAll(list);
			
		   }
		
		setModelColor();

		mModelCategoryAdapter = new JzgCarChooseModelCategoryAdapter1(
				getApplicationContext(), mModels, mModelsGroupkey);
		mCarTypeContent.setAdapter(mModelCategoryAdapter);
	}
	/**
	 * 修改品牌列表字体点击颜色 modifyMakeFontColor: <br/>
	 * 
	 * @author wang
	 * @param view
	 * @param position
	 * @since JDK 1.6
	 */
	private void modifyMakeFontColor(View view, int resid, int listClickColor,
			int position) {

		// 设置所有颜色为原色
		for (int i = 0; i < items.size(); i++) {
			items.get(i).put("fontColor", Color.BLACK);
			items.get(i).put("itemColor", Color.WHITE);
		}

		TextView textView = (TextView) view.findViewById(resid);
		textView.setTextColor(getResources().getColor(listClickColor));

		// items.get(position).put("fontColor",
		// getResources().getColor(listClickColor));
		// 原有版本改变为黄色字体的代码，用下面代码替换，不改变字体颜色
		items.get(position).put("fontColor", Color.BLACK);
		items.get(position).put("itemColor",
				getResources().getColor(R.color.grey2));

		mIndexCarListAdapter.notifyDataSetChanged();
	}

	/**
	 * 修改车系列表字体点击颜色 modifyFontColor: <br/>
	 * 
	 * @author wang
	 * @param view
	 * @param resid
	 * @param listClickColor
	 * @param position
	 * @since JDK 1.6
	 */

	private void modifyModelFontColor(View view, int resid, int listClickColor,
			int position) {
		for (JzgCarChooseModel model : mModels) {
			if (TextUtils.isEmpty(model.getManufacturerName()) || !JzgCarChooseConstant.IS_TITLE.equals(model.getManufacturerName())) {
				model.setFontColor(Color.BLACK);
			} else {
				model.setFontColor(getResources().getColor(
						R.color.categroy_title));
			}
			model.setItemColor(Color.WHITE);
		}

		TextView textView = (TextView) view.findViewById(resid);
		textView.setTextColor(getResources().getColor(listClickColor));
		// mModels.get(position).setFontColor(
		// getResources().getColor(listClickColor));
		// 原有版本改变为黄色字体的代码，用下面代码替换，不改变字体颜色
		mModels.get(position).setFontColor(Color.BLACK);
		mModels.get(position).setItemColor(
				getResources().getColor(R.color.grey2));
		mModelCategoryAdapter.notifyDataSetChanged();

	}

	/**
	 * 查询车辆价格详情 startQueryCarDetailThread: <br/>
	 * 
	 * @author wang
	 * @since JDK 1.6
	 */
	private void startQueryCarDetailThread() {
		/*
		 * HttpService.getCarDetail(mHandler, AppContext.getRequestQueue(),
		 * R.id.car_detail, mCurStyleid);
		 */
	}

	/**
	 * 车型查询线程 startStyleListThread: <br/>
	 * 
	 * @author wang
	 * @param modelid
	 * @since JDK 1.6
	 */
	private void startStyleListThread(final String modelid,final String tokenid,final String InSale) {
		HttpServiceCar.getStyleList(this,mHandler,
				AppContext.getRequestQueue(), JzgCarChooseConstant.stylelist, modelid,tokenid,InSale);
	}

	/**
	 * 车系查询线程 startModelListThread: <br/>
	 * 
	 * @author wang
	 * @param makeid
	 * @since JDK 1.6
	 */
	private void startModelListThread(final String makeid,final String Token,final String InSale) {
		HttpServiceCar.getModelList(this,mHandler,
				AppContext.getRequestQueue(), JzgCarChooseConstant.modellist, makeid,Token,InSale);
	}

	/**
	 * a-z索引监听 ClassName: LetterListViewListener <br/>
	 * Function: TODO ADD FUNCTION. <br/>
	 * Reason: TODO ADD REASON. <br/>
	 * date: 2014-6-2 下午3:44:10 <br/>
	 * 
	 * @author wang
	 * @version IndexCarActivity
	 * @since JDK 1.6
	 */
	class LetterListViewListener implements OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (index != null && index.get(s) != null) {
				int position = index.get(s);
				mIndexCarListView.setSelection(position);
				// overlay.setText(sections[position]);
				// overlay.setVisibility(View.VISIBLE);
				// handler.removeCallbacks(overlayThread);
				// 延迟一秒后执行，让overlay为不可见
				// handler.postDelayed(overlayThread, 1500);
			}
		}

	}

	/**
	 * 品牌列表适配器 ClassName: ListAdapter <br/>
	 * Function: TODO ADD FUNCTION. <br/>
	 * Reason: TODO ADD REASON. <br/>
	 * date: 2014-6-26 下午1:17:09 <br/>
	 * 
	 * @author wang
	 * @version IndexCarActivity
	 * @since JDK 1.6
	 */
	public class ListAdapter extends BaseAdapter {
		private ImageLoadingListener mAnimateFirstListener = new AnimateFirstDisplayListener();
		private LayoutInflater inflater;
		private List<Map<String, Object>> list;

		public ListAdapter(Context context, List<Map<String, Object>> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			Map<String, Integer> alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				// 当前汉语拼音首字母
				String currentStr = list.get(i).get("Sort").toString();
				// 上一个汉语拼音首字母，如果不存在为“ ”
				String previewStr = (i - 1) >= 0 ? list.get(i - 1).get("Sort")
						.toString() : " ";
				if (!previewStr.equals(currentStr)) {
					String name = list.get(i).get("Sort").toString();
					alphaIndexer.put(name, i);
					System.out
							.println("name is " + name + ", position is " + i);
					sections[i] = name;
				}
			}
			index = (HashMap<String, Integer>) alphaIndexer;

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.jzgcarchoose_car_list_content, null);
				holder = new ViewHolder();
				holder.iamge = (ImageView) convertView
						.findViewById(R.id.car_image);
				holder.name = (TextView) convertView
						.findViewById(R.id.car_name);
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			convertView.setBackgroundColor((Integer) list.get(position).get(
					"itemColor"));
			holder.name.setText(list.get(position).get("name").toString());
			holder.name.setTextColor((Integer) list.get(position).get(
					"fontColor"));

			String imgUrl = (String) list.get(position).get("logo");
			// 品牌logo异步加载
			imageLoader.displayImage(imgUrl, holder.iamge, mOptions,
					mAnimateFirstListener);
			String currentStr = list.get(position).get("Sort").toString();
			String previewStr = (position - 1) >= 0 ? list.get(position - 1)
					.get("Sort").toString() : " ";
			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder {
			ImageView iamge;
			TextView name;
			TextView alpha;
		}

	}

	@Override
	public void onDrawerClosed() {
		// Toast.makeText(this, "onDrawerClosed", Toast.LENGTH_SHORT).show();

		// 如果mCarTypeDrawer关闭，但是mCarYearstyleDrawer还开启着，则mCarYearstyleDrawer也关联关闭
		if (!mCarTypeDrawer.isOpened() && mCarYearstyleDrawer.isOpened()) {
			mCarYearstyleDrawer.close();
		}
		// 如果CarYearstyleDrawer关闭，则调整handle的初始宽度为0
		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(0,
				LayoutParams.MATCH_PARENT);
		if (!mCarYearstyleDrawer.isOpened()) {
			mCarYearstyleHandle.setLayoutParams(layout);
		}

		// 如果mCarTypeDrawer关闭，则调整两个Drawer的handle的初始宽度为0
		if (!mCarTypeDrawer.isOpened()) {
			mCarTypeHandle.setLayoutParams(layout);
		}
	}

	@Override
	public void onDrawerOpened() {
		if (mCarTypeDrawer.isOpened()) {
			mCarTypeHandle.setLayoutParams(new LayoutParams(40,
					LayoutParams.MATCH_PARENT));
		}
		if (mCarYearstyleDrawer.isOpened()) {
			mCarYearstyleHandle.setLayoutParams(new LayoutParams(0,
					LayoutParams.MATCH_PARENT));
		}
		// Toast.makeText(this, "onDrawerOpened", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// ListView开始滚动和结束滚动时候会调用
		switch(view.getId()){
		case R.id.index_car_type_list_content:
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				// 滚动结束调用
				System.out.println("1");
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				// System.out.println("2");滚动时调用
				if (mCarYearstyleDrawer.isOpened())
					mCarYearstyleDrawer.animateClose();

				break;
			case OnScrollListener.SCROLL_STATE_FLING:
				System.out.println("3");
				break;
			}
			break;
		case R.id.index_car_list:
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				// 滚动结束调用
				System.out.println("1");
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				// System.out.println("2");滚动时调用
				if (mCarTypeDrawer.isOpened())
					mCarTypeDrawer.animateClose();
				if (mCarYearstyleDrawer.isOpened())
					mCarYearstyleDrawer.animateClose();
				tv_title_center_text.setText("选择品牌");
				break;
			case OnScrollListener.SCROLL_STATE_FLING:
				System.out.println("3");
				break;
			}
			
			break;
		}
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// ListView滚动过程中会一直调用
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*
		 * case R.id.sure_btn: if (models.size() > 0) { Intent intent = new
		 * Intent(); StringBuffer modelStr = new StringBuffer(); StringBuffer
		 * modelId = new StringBuffer(); for (Model model : models) {
		 * modelStr.append(model.getName() + " "); modelId.append(model.getId()
		 * + ","); } intent.putExtra("modelStr", modelStr.substring(0,
		 * modelStr.length() - 1)); intent.putExtra("modelId",
		 * modelId.substring(0, modelId.length() - 1));
		 * setResult(Activity.RESULT_OK, intent); finish(); } else {
		 * Toast.makeText(this, "请选择品牌车系", 2000).show(); } break; case
		 * R.id.return_btn: finish(); break;
		 */
		default:
			break;
		}

	}

	@Override
	public void onImgClick(int position) {
		models.remove(position);
		mCustomData.remove(position);
		adapter.notifyDataSetChanged();
		if (mCustomData.size() == 0) {
			chooseLayout.setVisibility(View.GONE);
		}
		System.out.println("models result is " + models.toString());
	}

}

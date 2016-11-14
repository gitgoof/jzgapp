package com.jzg.jzgoto.phone.updateapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jzg.jzgoto.phone.R;
import com.jzg.jzgoto.phone.tools.MD5Utils;
import com.jzg.jzgoto.phone.tools.ShowDialogTool;

/**
 * 应用程序更新工具包
 * @author liux (http://my.oschina.net/liux)
 * @version 1.1
 * @created 2012-6-29
 */
public class UpdateManager {

	private static final int DOWN_NOSDCARD = 0;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int DOWN_FAIL = 3;
	
    private static final int DIALOG_TYPE_LATEST = 0;
    private static final int DIALOG_TYPE_FAIL   = 1;
    
	private static UpdateManager updateManager;
	
	private Context mContext;
	//通知对话框
	private Dialog noticeDialog;
	//下载对话框
	private Dialog downloadDialog;
	//'已经是最新' 或者 '无法获取最新版本' 的对话框
	private Dialog latestOrFailDialog;
    //进度条
    private ProgressBar mProgress;
    //显示下载数值
    private TextView mProgressText;
    //查询动画
    private ProgressDialog mProDialog;
    //进度值
    private int progress;
    //下载线程
    private Thread downLoadThread;
    //终止标记
    private boolean interceptFlag;
	//提示语
	private String updateMsg = "";
	//返回的安装包url
	private String apkUrl = "";
	//下载包保存路径
    private String savePath = "";
	//apk保存完整路径
	private String apkFilePath = "";
	//临时下载文件路径
	private String tmpFilePath = "";
	//下载文件大小
	private String apkFileSize;
	//已下载文件大小
	private String tmpFileSize;
	
	private String curVersionName = "";
	private int curVersionCode;
	private UpdateApp mUpdate;
	
	//是否强制更新
	boolean isUpdate = false;
    
    private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				mProgressText.setText(tmpFileSize + "/" + apkFileSize);
				break;
			case DOWN_OVER:
				downloadDialog.dismiss();
				installApk();
				break;
			case DOWN_NOSDCARD:
				downloadDialog.dismiss();
				ShowDialogTool.showCenterToast(mContext,"无法下载安装文件，请检查SD卡是否挂载");
				break;
			case DOWN_FAIL:
				downloadDialog.dismiss();
				ShowDialogTool.showCenterToast(mContext,"获取下载安装文件失败，请检查网络");
				break;
			}
    	};
    };
    
	public static UpdateManager getUpdateManager() {
		if(updateManager == null){
			updateManager = new UpdateManager();
		}
		updateManager.interceptFlag = false;
		return updateManager;
	}
	
	/**
	 * 检查App更新
	 * @param context
	 * @param isShowMsg 是否显示提示消息
	 */
	public void checkAppUpdate(Context context, final boolean isShowMsg){
		this.mContext = context;
		getCurrentVersion();
		if(isShowMsg){
			if(mProDialog == null)
				mProDialog = ProgressDialog.show(mContext, null, "正在检测，请稍后...", true, true);
			else if(mProDialog.isShowing() || (latestOrFailDialog!=null && latestOrFailDialog.isShowing()))
				return;
		}
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				//进度条对话框不显示 - 检测结果也不显示
				if(mProDialog != null && !mProDialog.isShowing()){
					return;
				}
				//关闭并释放释放进度条对话框
				if(isShowMsg && mProDialog != null){
					mProDialog.dismiss();
					mProDialog = null;
				}
				//显示检测结果
				if(msg.what == HttpUpdateAppService.SUCCESS){
					JsonObjectImpl jsonObject = new JsonObjectImpl();
					String json = (String)msg.obj;
					System.out.println("返回json" + json);
					
					if(jsonObject.isSuccess(mContext, json)){
						mUpdate = jsonObject.parserUpdateApp(json);
						
						if(mUpdate != null){
							if(curVersionCode < mUpdate.getVersionNewCode()){
								apkUrl = mUpdate.getDownloadUrl();
								updateMsg = mUpdate.getUpdateLog();
								String Isupdate =  mUpdate.getIsupdate();
								if("true".equals(Isupdate)){
									isUpdate = true;
								}
								showNoticeDialog();
							}else if(isShowMsg){
								showLatestOrFailDialog(DIALOG_TYPE_LATEST);
							}
						}
					}
					
				}else if(isShowMsg){
					showLatestOrFailDialog(DIALOG_TYPE_FAIL);
				}
			}
		};
		/*new Thread(){
			public void run() {
				Message msg = new Message();
				try {					
					
					//UpdateApp update = ApiClient.checkVersion((AppContext)mContext.getApplicationContext());
					String json = "{\"msg\":\"成功！\",\"status\":100,\"versionCode\":42,\"versionName\":\"V2.1.1\",\"downloadUrl\":\"http://www.jingzhengu.com/app/jzg.apk\",\"updateLog\":\"更新日志：修复动弹点赞问题修复图片保存图库未刷新BUG安装包大小：5.50MB\"}";
					
					msg.what = HttpUpdateAppService.SUCCESS;
					msg.obj = json;
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}			
		}.start();*/	
		
		//-----2015-4-23---------后续有接口后修改，现在用上面的测试
		
		HttpUpdateAppService.updateApp(context,handler,getParams());
	}	
	
	/**
	 * 提交的参数
	 */
	public Map<String, String> getParams() {

		// 在这里设置需要post的参数
		Map<String, String> map = new HashMap<String, String>();
		Map<String, Object> maps = new HashMap<String, Object>();
		map.put("sign", MD5Utils.getMD5Sign(maps));
		return map;
	}
	
	
	/**
	 * 显示'已经是最新'或者'无法获取版本信息'对话框
	 */
	private void showLatestOrFailDialog(int dialogType) {
		if (latestOrFailDialog != null) {
			//关闭并释放之前的对话框
			latestOrFailDialog.dismiss();
			latestOrFailDialog = null;
		}
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("系统提示");
		if (dialogType == DIALOG_TYPE_LATEST) {
			builder.setMessage("您当前已经是最新版本");
		} else if (dialogType == DIALOG_TYPE_FAIL) {
			builder.setMessage("无法获取版本更新信息");
		}
		builder.setPositiveButton("确定", null);
		latestOrFailDialog = builder.create();
		latestOrFailDialog.show();
	}

	/**
	 * 获取当前客户端版本信息
	 */
	private void getCurrentVersion(){
        try { 
        	PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        	curVersionName = info.versionName;
        	curVersionCode = info.versionCode;
        } catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
	}
	
	/**
	 * 显示版本更新通知对话框
	 */
	private void showNoticeDialog(){
		Intent intent = new Intent(mContext,UpdateShowActivity.class);
		intent.putExtra("app_download_version",mUpdate);
		intent.putExtra("update_msg",updateMsg);
		intent.putExtra("is_force_update",isUpdate);
		mContext.startActivity(intent);
		/*
		final AlertDialog dlg = new AlertDialog.Builder(mContext).create();
		dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.update_dialog_layout);
		TextView title = (TextView) window.findViewById(R.id.tv_dialog_hint_view_title);
		title.setText("软件版本更新");
		TextView msg = (TextView) window.findViewById(R.id.tv_dialog_hint_view_msg);
		msg.setText(updateMsg);
		
		
		Button ok = (Button) window.findViewById(R.id.btn_dialog_hint_ok);
		ok.setText("立刻更新");
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.cancel();
				showDownloadDialog();	
			}
		});

		// 关闭alert对话框架
		Button cancel = (Button) window
				.findViewById(R.id.btn_dialog_hint_cancel);
		
		//是否强制更新
		if(isUpdate){
			cancel.setText("退出");
			cancel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dlg.cancel();
					System.exit(0);
				}
			});
			
			
		}else{
			cancel.setText("以后再说");
			cancel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dlg.cancel();
				}
			});
		}
		
		if(isUpdate){
			dlg.setCancelable(false);
		}
		*/
//		AlertDialog.Builder builder = new Builder(mContext);
//		builder.setTitle("软件版本更新");
//		builder.setMessage(updateMsg);
//		builder.setPositiveButton("立即更新", new OnClickListener() {			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				showDownloadDialog();			
//			}
//		});
//		
//		//是否强制更新
//		if(isUpdate){
//			builder.setNegativeButton("退出", new OnClickListener() {			
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();	
//					System.exit(0);
//				}
//			});
//			
//		}else{
//		
//			builder.setNegativeButton("以后再说", new OnClickListener() {			
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					
//						dialog.dismiss();	
//					
//								
//				}
//			});
//		}
//		noticeDialog = builder.create();
//		noticeDialog.show();
//		if(isUpdate){
//			noticeDialog.setCancelable(false);
//		}
		
	}
	
	/**
	 * 显示下载对话框
	 */
	private void showDownloadDialog(){
		AlertDialog.Builder builder = new Builder(mContext,AlertDialog.THEME_HOLO_DARK);
		builder.setTitle("正在下载新版本");
		
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.update_progress, null);
		mProgress = (ProgressBar)v.findViewById(R.id.update_progress);
		mProgressText = (TextView) v.findViewById(R.id.update_progress_text);
		
		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.setCanceledOnTouchOutside(false);
		downloadDialog.show();
		
		downloadApk();
	}
	
	/**
	* 下载apk
	*/
	private void downloadApk(){
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}
	
	private Runnable mdownApkRunnable = new Runnable() {	
		@Override
		public void run() {
			try {
				String apkName = "JZG_"+mUpdate.getVersionName()+".apk";
				String tmpApk = "JZG_"+mUpdate.getVersionName()+".tmp";
				//判断是否挂载了SD卡
				String storageState = Environment.getExternalStorageState();		
				if(storageState.equals(Environment.MEDIA_MOUNTED)){
					savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/jzg/Update/";
					File file = new File(savePath);
					if(!file.exists()){
						file.mkdirs();
					}
					apkFilePath = savePath + apkName;
					tmpFilePath = savePath + tmpApk;
				}
				
				//没有挂载SD卡，无法下载文件
				if(apkFilePath == null || apkFilePath == ""){
					mHandler.sendEmptyMessage(DOWN_NOSDCARD);
					return;
				}
				
				System.out.println("安装文件apkFilePath"+apkFilePath);
				File ApkFile = new File(apkFilePath);
				
				//是否已下载更新文件
				if(ApkFile.exists()){
					downloadDialog.dismiss();
					installApk();
					return;
				}
				
				//输出临时下载文件
				File tmpFile = new File(tmpFilePath);
				FileOutputStream fos = new FileOutputStream(tmpFile);
				
				System.out.println("apk下载地址" + apkUrl);
				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				
				//显示文件大小格式：2个小数点显示
		    	DecimalFormat df = new DecimalFormat("0.00");
		    	//进度条下面显示的总文件大小
		    	apkFileSize = df.format((float) length / 1024 / 1024) + "MB";
				
				int count = 0;
				byte buf[] = new byte[1024];
				
				do{   		   		
		    		int numread = is.read(buf);
		    		count += numread;
		    		//进度条下面显示的当前下载文件大小
		    		tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
		    		//当前进度值
		    	    progress =(int)(((float)count / length) * 100);
		    	    //更新进度
		    	    mHandler.sendEmptyMessage(DOWN_UPDATE);
		    		if(numread <= 0){	
		    			//下载完成 - 将临时下载文件转成APK文件
						if(tmpFile.renameTo(ApkFile)){
							//通知安装
							mHandler.sendEmptyMessage(DOWN_OVER);
						}
		    			break;
		    		}
		    		fos.write(buf,0,numread);
		    	}while(!interceptFlag);//点击取消就停止下载
				
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				mHandler.sendEmptyMessage(DOWN_FAIL);
				e.printStackTrace();
			} catch(IOException e){
				mHandler.sendEmptyMessage(DOWN_FAIL);
				e.printStackTrace();
			}
			
		}
	};
	
	
	
	/**
    * 安装apk
    */
	private void installApk(){
		File apkfile = new File(apkFilePath);
        if (!apkfile.exists()) {
            return;
        }    
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
	}
}

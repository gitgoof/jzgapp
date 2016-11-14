package com.jzg.jzgoto.phone.models.business.buy;

import java.util.HashMap;
import java.util.Map;

import com.jzg.jzgoto.phone.global.HttpServiceHelper;
import com.jzg.jzgoto.phone.models.common.BaseParamsModels;
import com.jzg.jzgoto.phone.tools.MD5Utils;

public class MyRecomandListPrams extends BaseParamsModels {

	private String mUrl = HttpServiceHelper.BASE_URL+"/APPNew/CarSourceV4.ashx";
	public int PageIndex = 1;
	public int PageSize = 10;
	@Override
	public Map<String, String> getParams() {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("op", "GetCarSourceList");
		map.put("PageIndex", String.valueOf(PageIndex));
		map.put("PageSize", String.valueOf(PageSize));
		Map<String,String> params = new HashMap<String, String>();
		params.put("op", "GetCarSourceList");
		params.put("PageIndex", String.valueOf(PageIndex));
		params.put("PageSize", String.valueOf(PageSize));
		params.put("sign", MD5Utils.getMD5Sign(map));
		return params;
	}

	@Override
	public String toParamsString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return mUrl;
	}

}

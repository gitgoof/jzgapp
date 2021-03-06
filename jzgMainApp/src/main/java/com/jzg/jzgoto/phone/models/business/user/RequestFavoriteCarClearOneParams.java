package com.jzg.jzgoto.phone.models.business.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.jzg.jzgoto.phone.global.HttpServiceHelper;
import com.jzg.jzgoto.phone.models.common.BaseParamsModels;
import com.jzg.jzgoto.phone.models.common.CommonModelSettings;
import com.jzg.jzgoto.phone.tools.MD5Utils;

public class RequestFavoriteCarClearOneParams extends BaseParamsModels {

	private String mUrl = HttpServiceHelper.BASE_URL + "/appv5/MyCollection.ashx";
	public String uid = "0";
	public int id = 0;
	public String operateStr = "DelMyCollection";


	@Override
	public Map<String, String> getParams() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("op", operateStr);
		map.put("id", id);

		Map<String, String> params = new HashMap<String, String>();
		params.put("op", operateStr);
		params.put("id", String.valueOf(id));
		params.put("sign", MD5Utils.getMD5Sign(map) );
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

package com.yunpai.tms.net.callback;

import java.util.Map;

import rx.Observer;


/**
 * 自己重新封装的回调
 */
public abstract class CallbackListener implements Observer<Map<Object,Object>> {

	@Override
	public void onCompleted() {
		onFinsh();
	}
	
	@Override
	public void onError(Throwable err) {
		onErron(5001,err.toString());
	}
	
	@Override
	public void onNext(Map<Object, Object> result) {
		if (result != null && result.containsKey("ret") && result.containsKey("msg")) {
			if ("0".equals(String.valueOf(result.get("ret")))) {
				onSuccess(result);
			}else{
				String err = String.valueOf(result.get("msg"));
				int code = Integer.valueOf(result.get("ret").toString());
				onErron(code,err);
			}
		}else {
			onErron(5001,"服务器错误！");
		}
	}
	
	
	public abstract void onSuccess(Map<Object,Object> result);
	public abstract void onErron(int code,String erro);
	public abstract void onFinsh();
}

package com.yunpai.tms.net.service;


import com.yunpai.tms.net.resultbean.HttpResult;
import com.yunpai.tms.net.resultbean.Subject;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface NetService {
    @GET("top250")
    Observable<HttpResult<List<Subject>>> top250(@Query("start") int start, @Query("count") int count);

   /* @GET("CRUD/CRUD-U-OSM-WaybillLog-queryWaybill.do")
    Observable<HttpResult<Object>> getWyBillByNo(@Query("hwaybillNo") String billNo,@Query("page") int start, @Query("rows") int count);

    @GET( "CRUD/CRUD-CQ-Auth-login.do")
    Observable<HttpResult<Object>> postLogin(@Query("companyCode")String companyCode, @Query("loginName")String loginName, @Query("loginPwd")String loginPwd);*/
}

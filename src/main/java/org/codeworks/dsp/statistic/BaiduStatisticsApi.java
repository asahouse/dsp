package org.codeworks.dsp.statistic;

import com.alibaba.fastjson.JSON;
import com.baidu.statistics.common.StatisticConstant;
import com.baidu.statistics.dataapi.core.ResHeader;
import com.baidu.statistics.dataapi.core.TongjiResponse;
import com.baidu.statistics.dataapi.om.profile.*;
import com.baidu.statistics.dataapi.svc.Report;
import com.baidu.statistics.exception.StaticsException;
import com.baidu.statistics.login.core.LoginResponse;
import com.baidu.statistics.login.core.LoginReturnCode;
import com.baidu.statistics.login.om.*;
import com.baidu.statistics.login.svc.Login;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codeworks.dsp.controller.advice.ExceptionController;
import org.codeworks.dsp.exception.BaiduStatisticsException;
import org.codeworks.dsp.exception.ErrorCodes;
import org.codeworks.dsp.utils.EhcacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by benjaminkc on 16/12/11.
 *
 * 百度统计API的连接实现类
 */
@Component
public class BaiduStatisticsApi {

    private static final Logger log = LogManager.getLogger(BaiduStatisticsApi.class);

    private static String CACHE_NAME_SITE = "baidu-statistics-sites";
    private static String CACHE_NAME_DATAS = "baidu-statistics-datas";
    private static String CACHE_NAME_USERINFO = "baidu-statistics-userinfo";

    @Autowired
    private EhcacheUtil ehcacheUtil;

    private static Login login = new Login();

    private Report initReport(){
        Optional<DoLoginResponse> retData = this.doLogin();
        if (!retData.isPresent())
            throw new BaiduStatisticsException(ErrorCodes.baidu_statistics_request_error, new Object[]{"initReport"});

        ehcacheUtil.put(CACHE_NAME_USERINFO, CACHE_NAME_USERINFO, retData.get());

        Report report = new Report(retData.get().getUcid(), retData.get().getSt());
        return report;
    }

    private Report getCacheReport(){
        Optional<DoLoginResponse> retData =
                Optional.ofNullable((DoLoginResponse)ehcacheUtil.get(CACHE_NAME_USERINFO, CACHE_NAME_USERINFO));

        Report report;

        if (retData.isPresent()) {
            report = new Report(retData.get().getUcid(), retData.get().getSt());
        }else
            report = this.initReport();

        return report;
    }

    private boolean preLogin(){
        PreLoginRequest request = new PreLoginRequest(
                StatisticConstant.OS_VERSION_LINUX, StatisticConstant.DEVICE_TYPE_PC, "1.0");

        Optional<LoginResponse<PreLoginResponse>> resOpt;
        try {
            resOpt = Optional.ofNullable(login.preLogin(request));
        }catch (StaticsException se){
            throw new BaiduStatisticsException(ErrorCodes.baidu_statistics_request_error, new Object[]{"preLogin"});
        }

        if (!resOpt.isPresent()) throw new BaiduStatisticsException(ErrorCodes.baidu_statistics_request_error, new Object[]{"preLogin"});

        LoginResponse<PreLoginResponse> response = resOpt.get();


        if (LoginReturnCode.OK != response.getReturnCode()) {
            System.out.println("[error] preLogin unsuccessfully with return code: " + response.getReturnCode());
            throw new BaiduStatisticsException(
                    ErrorCodes.baidu_statistics_prelogin_error, new Object[]{response.getReturnCode()});
        }

        PreLoginResponse retData = response.getRealData();

        if (retData.getNeedAuthCode() == null) {
            System.out.println("[error] unexpected preLogin return data: " + JSON.toJSONString(response.getRealData()));
            throw new BaiduStatisticsException(
                    ErrorCodes.baidu_statistics_prelogin_auth, new Object[]{JSON.toJSONString(response.getRealData())});
        }
        if (retData.getNeedAuthCode() == true) {
            System.out.println("[error] preLogin return data format error: " + JSON.toJSONString(response.getRealData()));
            throw new BaiduStatisticsException(
                    ErrorCodes.baidu_statistics_prelogin_format, new Object[]{JSON.toJSONString(response.getRealData())});
        }

        System.out.println("[notice] preLogin successfully!");
        return true;
    }

    private Optional<DoLoginResponse> doLogin(){

        if(!this.preLogin()) return Optional.empty();

        Optional<LoginResponse<DoLoginResponse>> resOpt;
        try{
            resOpt = Optional.ofNullable(login.doLogin());
        }catch (StaticsException se){
            throw new BaiduStatisticsException(ErrorCodes.baidu_statistics_request_error, new Object[]{"doLogin"});
        }

        if (!resOpt.isPresent()) throw new BaiduStatisticsException(ErrorCodes.baidu_statistics_request_error);

        LoginResponse<DoLoginResponse> response = resOpt.get();

        if (LoginReturnCode.OK != response.getReturnCode()) {
            System.out.println("[error] doLogin unsuccessfully with return code: " + response.getReturnCode());
            throw new BaiduStatisticsException(
                    ErrorCodes.baidu_statistics_dologin_error, new Object[]{response.getReturnCode()});
        }

        DoLoginResponse retData = response.getRealData();

        if (retData.getRetcode() == null || retData.getUcid() == null
                || retData.getSt() == null) {
            System.out.println("[error] doLogin return data format error: " + retData);
            throw new BaiduStatisticsException(
                    ErrorCodes.baidu_statistics_dologin_format, new Object[]{response.getReturnCode()});
        }
        if (retData.getRetcode() != DoLoginResponse.RETCODE_OK) {
            System.out.println("[error] doLogin unsuccessfully with retcode: " + retData);
            throw new BaiduStatisticsException(
                    ErrorCodes.baidu_statistics_dologin_return_error, new Object[]{response.getReturnCode()});
        }

        System.out.println("[notice] doLogin successfully!");
        return Optional.ofNullable(retData);
    }

    public boolean doLogout(Integer ucid, String st){
        DoLogoutRequest request = new DoLogoutRequest(ucid, st);

        Optional<LoginResponse<DoLogoutResponse>> resOpt;

        try{
            resOpt = Optional.ofNullable(login.doLogout(request));
        }catch (StaticsException se){
            throw new BaiduStatisticsException(ErrorCodes.baidu_statistics_request_error, new Object[]{"doLogin"});
        }

        if (!resOpt.isPresent()) throw new BaiduStatisticsException(ErrorCodes.baidu_statistics_request_error);

        LoginResponse<DoLogoutResponse> response = resOpt.get();

        if (LoginReturnCode.OK != response.getReturnCode()) {
            System.out.println("[error] doLogout unsuccessfully with return code: " + response.getReturnCode());
            throw new BaiduStatisticsException(
                    ErrorCodes.baidu_statistics_dologout_error, new Object[]{response.getReturnCode()});
        }
        DoLogoutResponse retData = response.getRealData();
        if (retData.getRetcode() == null) {
            System.out.println("[error] doLogout return data format error: " + retData);
            throw new BaiduStatisticsException(
                    ErrorCodes.baidu_statistics_dologout_format, new Object[]{response.getReturnCode()});
        }
        System.out.println("[notice] doLogout successfully!");
        return true;
    }



    public GetSiteListResponse requestSiteList(){

        String Key = "sites";

        Object cache = ehcacheUtil.get(CACHE_NAME_SITE, Key);

        if (Optional.ofNullable(cache).isPresent()){
            GetSiteListResponse rq = (GetSiteListResponse)cache;
            return rq;
        }

        Report report = this.getCacheReport();

        try{
            TongjiResponse<GetSiteListResponse> sitesInfo = report.getSiteList();
            Optional<ResHeader> resHeader = Optional.ofNullable(sitesInfo.getHeader());
            if (!resHeader.get().getFailures().isEmpty()
                    && resHeader.get().getFailures().get(0).getCode()==8202) {
                this.initReport();
                return requestSiteList();
            }

            GetSiteListResponse sites = new GetSiteListResponse();
            for (int i = 0; i < 3; i++) {
                GetSiteListResponse body = sitesInfo.getBody();
                if (body != null || body.getList().size() > 0) {
                    sites = body;
                    ehcacheUtil.put(CACHE_NAME_SITE, Key, body);
                    break;
                }
            }
            return sites;
        }catch(StaticsException se) {
            log.error("BaiduStatisticsApi :: requestSiteList");
            log.error(se);
            throw new BaiduStatisticsException(ErrorCodes.baidu_statistics_request_error, new Object[]{"requestSiteList"});
        }finally {
            ehcacheUtil.remove(CACHE_NAME_USERINFO, CACHE_NAME_USERINFO);
        }
    }

    public GetDataResponse requestData(GetDataRequest request){

        String reqJsonStr = JSON.toJSON(request).toString();
        String reqMD5 = DigestUtils.md5Hex(reqJsonStr);

        Object cache = ehcacheUtil.get(CACHE_NAME_DATAS, reqMD5);
        if (Optional.ofNullable(cache).isPresent()){
            GetDataResponse rq = (GetDataResponse)cache;
            return rq;
        }

        Report report = this.getCacheReport();

        try{
            TongjiResponse<GetDataResponse> sitesInfo = report.getData(request);
            Optional<ResHeader> resHeader = Optional.ofNullable(sitesInfo.getHeader());
            if (!resHeader.get().getFailures().isEmpty()
                    && resHeader.get().getFailures().get(0).getCode()==8202) {
                this.initReport();
                return requestData(request);
            }

            GetDataResponse result = new GetDataResponse();
            for (int i = 0; i < 3; i++) {
                GetDataResponse body = sitesInfo.getBody();
                if (Optional.ofNullable(body).isPresent()
                        && Optional.ofNullable(body.getResult()).isPresent()){

                    ehcacheUtil.put(CACHE_NAME_DATAS, reqMD5, body);
                    result = body;
                    break;
                }
            }
            result.getResult().put("metrics", request.getMetrics());
            return result;


        }catch(Exception se){
            log.error("BaiduStatisticsApi :: requestData");
            log.error(se);
            throw new BaiduStatisticsException(ErrorCodes.baidu_statistics_request_error, new Object[]{"requestData"});
        }
    }

    public List<GetDataResponse> requestCollectionData(GetDataCollectionRequest rqCol){

        List<GetDataResponse> resResutl = new ArrayList<>();
        Optional.ofNullable(rqCol.getRequests()).ifPresent(rqs -> {
            rqs.forEach(rq ->{
                GetDataResponse rs = this.requestData(rq);
                if (rs!=null) {
                    resResutl.add(rs);
                }
            });
        });
        System.out.println("requestCollectionData Coollection successfully!");

        ehcacheUtil.remove(CACHE_NAME_USERINFO, CACHE_NAME_USERINFO);
        return resResutl;
    }
}

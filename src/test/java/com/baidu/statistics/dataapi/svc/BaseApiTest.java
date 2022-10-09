package com.baidu.statistics.dataapi.svc;

import com.baidu.statistics.dataapi.core.TongjiResponse;
import com.baidu.statistics.dataapi.om.profile.GetDataRequest;
import com.baidu.statistics.dataapi.om.profile.GetDataCollectionRequest;
import com.baidu.statistics.dataapi.om.profile.GetDataResponse;
import com.baidu.statistics.dataapi.om.profile.GetSiteListResponse;
import com.baidu.statistics.login.om.DoLoginResponse;
import com.baidu.statistics.login.svc.BaseLoginTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.List;
import java.util.stream.Collectors;

public class BaseApiTest {

	private static Integer ucid;
	private static String st;
	
	private static BaseLoginTest login;

	protected static Report report;
	
	@BeforeClass
	public static void login() throws Exception {
		login = new BaseLoginTest();
		
		DoLoginResponse retData = login.doLogin();
		Assert.assertNotNull(retData);
		
		ucid = retData.getUcid();
		st = retData.getSt();
		report = new Report(ucid, st);
	}
	
	@AfterClass
	public static void logout() throws Exception {
		boolean ret = login.doLogout(ucid, st);
		Assert.assertSame(ret, true);
	}


	public GetSiteListResponse getSiteList() throws Exception {
		TongjiResponse<GetSiteListResponse> sitesInfo = report.getSiteList();
		GetSiteListResponse sites = sitesInfo.getBody();
		if (sites == null || sites.getList().size() <= 0) {
			System.out.println("getSiteList failed!");
			return null;
		}
		System.out.println("getSiteList successfully!");
		return sites;
	}

	public GetDataResponse getData(GetDataRequest rq) throws Exception {

		TongjiResponse<GetDataResponse> sitesInfo = report.getData(rq);
		GetDataResponse result = sitesInfo.getBody();
		if (result == null) {
			System.out.println("getSiteList failed!");
			return null;
		}
		System.out.println("getSiteList successfully!");
		return result;
	}

	public List<GetDataResponse> getCollectionData(GetDataCollectionRequest rq) throws Exception {

		List<TongjiResponse<GetDataResponse>> sitesInfo = report.getCollectionData(rq);
		if (sitesInfo == null && !sitesInfo.isEmpty()) {
			System.out.println("getSiteList Coollection failed!");
			return null;
		}
		System.out.println("getSiteList Coollection successfully!");

		return sitesInfo.stream().map(TongjiResponse::getBody).collect(Collectors.toList());
	}

}

package com.axis2;

/**
* Description 服务器端
* @Author junwei
* @Date 16:20 2019/9/5
**/
public class TestService {

	  public String test(String param) {
	        System.out.println("服务端被请求了一次....");
			return "axis2 response"+param;
	    }
	  
	  
}

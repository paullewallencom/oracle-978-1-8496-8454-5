package com.oracle.cep.sample.spatial;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.http.HttpService;

import com.bea.wlevs.ede.api.ApplicationIdentityAware;
import com.bea.wlevs.ede.api.DisposableBean;
import com.bea.wlevs.ede.api.InitializingBean;
import com.bea.wlevs.ede.api.StageIdentityAware;
import com.bea.wlevs.util.Service;

public class WebContentRegistration implements InitializingBean, ApplicationIdentityAware, StageIdentityAware, DisposableBean {

	private Log log_ = LogFactory.getLog(WebContentRegistration.class);

	private HttpService httpService_;
	private String serverPath_;
	private String resourcePath_;

	private String appId_;

	private String stageId_;

	@Service
	public void setHttpService(HttpService httpService) {
		httpService_ = httpService;
	}

	public void setServerPath(String serverPath) {
		serverPath_ = serverPath;
	}
	
	public void setResourcePath(String resourcePath) {
		resourcePath_ = resourcePath;
	}
	
	public void afterPropertiesSet() throws Exception {
		if(httpService_ == null) {
			throw new Exception("httpService not set");
		}
		if(serverPath_ == null) {
			throw new Exception("serverPath not set");
		}
		if(resourcePath_ == null) {
			throw new Exception("resourcePath not set");
		}
		httpService_.registerResources(serverPath_, resourcePath_, null);
		log_.info(myId()+"registeredResources with:[serverPath:"+serverPath_+"][resourcePath:"+resourcePath_+"]");
	}

	public void setApplicationIdentity(String id) {
		appId_ = id;
	}

	public void setStageIdentity(String id) {
		stageId_ = id;
	}	

	private String myId() {
		return "[app:"+appId_+"][stage:"+stageId_+"]";
	}

	public void destroy() throws Exception {
		httpService_.unregister(serverPath_);
		log_.info(myId()+"unRegisteredResources:[serverPath:"+serverPath_+"][resourcePath:"+resourcePath_+"]");		
	}
}

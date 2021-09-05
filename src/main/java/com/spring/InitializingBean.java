package com.spring;

/**
 * @author qinjian
 */
public interface InitializingBean {


	/**
	 *
	 *
	 * @throws Exception
	 */
	void afterPropertiesSet() throws Exception;

}
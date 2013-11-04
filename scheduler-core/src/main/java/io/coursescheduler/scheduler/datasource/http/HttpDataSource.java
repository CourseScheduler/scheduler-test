/**
  * @(#)HttpDataSource.java
  *
  * Provide access to HTTP resources as a HataSource
  *
  * @author Mike Reinhold
  * 
  * @license GNU General Public License version 3 (GPLv3)
  *
  * This file is part of Course Scheduler, an open source, cross platform
  * course scheduling tool, configurable for most universities.
  *
  * Copyright (C) 2010-2013 Mike Reinhold
  *
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
  * 
  */
package io.coursescheduler.scheduler.datasource.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.prefs.Preferences;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.coursescheduler.scheduler.datasource.DataSource;
import io.coursescheduler.util.variable.StrSubstitutorFactory;
import io.coursescheduler.util.variable.SubstitutionVariableSource;
import io.coursescheduler.util.variable.preferences.PreferencesBasedVariableFactory;

/**
 * Provide access to HTTP resources as a HataSource
 *
 * @author Mike Reinhold
 *
 */
public class HttpDataSource extends DataSource {
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Preferences node property for specifying the HTTP method to use in the request
	 * 
	 * Value: {@value}
	 */
	public static final String HTTP_METHOD_PROPERTY = "method";
	
	/**
	 * Preferences node property for retrieving the URI that will be used in the HTTP request
	 * 
	 * Value: {@value}
	 */
	public static final String HTTP_URI_PROPERTY = "uri";
	
	/**
	 * Preferences node property prefix for retrieving the parameters that will be used in the HTTP request
	 * 
	 * Varue: {@value}
	 */
	public static final String HTTP_PARAM_PROPERTY_PREFIX = "param.";
	
	/**
	 * Preferences node property prefix for retrieving the headers that will be used in the HTTP request
	 * 
	 * Value: {@value}
	 */
	public static final String HTTP_HEADER_PROPERTY_PREFIX = "header.";
	
	/**
	 * Preferences node property postfix for retrieving the parameter or header name
	 * 
	 * Value: {@value}
	 */
	public static final String HTTP_NAME_POSTFIX = ".name";
	
	/**
	 * Preferences node property postfix for retrieving the parameter or header value
	 * 
	 * Value: {@value}
	 */
	public static final String HTTP_VALUE_POSTFIX = ".value";
	
	/**
	 * Component based logger
	 */
	private transient Logger log = LoggerFactory.getLogger(getClass().getName());

	/**
	 * Create a new HTTP Data source for retrieving data using HTTP to access resources
	 *
	 * @param substitutionFactory the substitution factory that contains the global variable replacements
	 * @param prefSourceFactory the preferences node factory for accessing configuration nodes
	 * @param settings the Preferences node containing the configuration for this instance
	 * @param replacements the local substitution variable source 
	 */
	@AssistedInject
	public HttpDataSource(StrSubstitutorFactory substitutionFactory, PreferencesBasedVariableFactory prefSourceFactory, @Assisted("config") Preferences settings, @Assisted("localVars") SubstitutionVariableSource replacements) {
		super(substitutionFactory, prefSourceFactory, settings, replacements);
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute() {
		log.info("Preparing the HTTP Client and request");
		
		long startCreate = System.currentTimeMillis();
		try(CloseableHttpClient httpClient = HttpClients.createDefault()){
			HttpUriRequest httpRequest = buildHttpRequest();
			long endCreate = System.currentTimeMillis();
			log.info("Built HTTP Client request {} in {} ms", httpRequest, endCreate - startCreate);
	
			log.info("Executing the HTTP Client request");
			long startExecute = System.currentTimeMillis();
			try(CloseableHttpResponse response = httpClient.execute(httpRequest)) {
				long endExecute = System.currentTimeMillis();
				log.info("Executed HTTP Client Request {} in {} ms.", httpRequest, endExecute - startExecute);
				
				StatusLine status = response.getStatusLine();
				log.info("HTTP Client Response: {}", status);

				Header[] headers = response.getAllHeaders();
				log.debug("HTTP Response contains {} headers", headers.length);
				if(log.isDebugEnabled()) {
					for(Header header: headers) {
						log.debug("{}", header);
					}
				}
				
				HttpEntity entity = response.getEntity();
				log.info("HTTP Response contains {} bytes, {}", entity.getContentLength(), entity.getContentType());

				//Tee the input if configured
				InputStream teedSource = tee(entity.getContent());
				
				//Prepare the cross thread pipe
				PipedOutputStream bufferSink = createPipeSource();
				PipedInputStream bufferedStream = createPipeSink(bufferSink);
				
				//Set the data source stream
				setDataSource(bufferedStream);

				log.info("Preparing to pipe data source");
				long start = System.currentTimeMillis();
				transferDataToPipe(teedSource, bufferSink);
				long end = System.currentTimeMillis();
				log.info("Finished piping data source in {} ms", end - start);
			} catch (IOException e) {
				log.error("Exception occurred during execution of HTTP request", e);
			}
		} catch (IOException e) {
			log.error("Exception occurred during close of HTTP client", e);
		}
		
		
	}
	
	/**
	 * Retrieve the request URI from the configuration for the data source
	 *
	 * @return the URI for the HTTP request
	 */
	protected String getRequestURI() {
		String uri = performReplacements(getSettings().get(HTTP_URI_PROPERTY, null));
		log.debug("Using {} as the URI for the HTTP Request", uri);
		
		return uri;
	}
	
	/**
	 * Retrieve the HTTP request  method for the data source
	 *
	 * @return the method for the HTTP request
	 */
	protected String getRequestMethod() {
		String method = performReplacements(getSettings().get(HTTP_METHOD_PROPERTY, null));
		log.debug("Using {} as the HTTP method for the request", method);
		
		return method;
	}
	
	/**
	 * Build the HTTP Request as required by the data source configuration
	 *
	 * @return the HTTP Request 
	 */
	protected HttpUriRequest buildHttpRequest() {		
		RequestBuilder builder = RequestBuilder.create(getRequestMethod()).setUri(getRequestURI());
		
		//TODO other HTTP configurations / settings?
		
		builder = buildHeaders(builder);
		builder = buildParameters(builder);
		
		return builder.build();
	}
	
	/**
	 * Build the HTTP request headers into the HTTP request per the data source configuration
	 *
	 * @param builder the Request Builder that contains the rest of the request configuration
	 * @return the updated request builder 
	 */
	protected RequestBuilder buildHeaders(RequestBuilder builder) {
		log.debug("Preparing to build request headers");
		
		for(int index = 0; true; index++) {
			String header = performReplacements(getSettings().get(HTTP_HEADER_PROPERTY_PREFIX + index + HTTP_NAME_POSTFIX, null));
			String value = performReplacements(getSettings().get(HTTP_HEADER_PROPERTY_PREFIX + index + HTTP_VALUE_POSTFIX, null));
			
			if(header != null) {
				log.debug("Found HTTP header index {}: {} = {}", new Object[] {index, header, value});
				builder = builder.addHeader(header, value);
			} else {
				log.debug("No header entry found for index {}", index);
				break;
			}
		}
		log.debug("Finished building request headers");
		
		return builder;
	}
	
	/**
	 * Build the HTTP request parameters into the HTTP request per the data source configuration
	 *
	 * @param builder the Request Builder that contains the rest of the request configuration
	 * @return the updated request builder
	 */
	protected RequestBuilder buildParameters(RequestBuilder builder) {
		log.debug("Preparing to build request parameters");
		
		for(int index = 0; true; index++) {
			String header = performReplacements(getSettings().get(HTTP_PARAM_PROPERTY_PREFIX + index + HTTP_NAME_POSTFIX, null));
			String value = performReplacements(getSettings().get(HTTP_PARAM_PROPERTY_PREFIX + index + HTTP_VALUE_POSTFIX, null));
						
			if(header != null) {
				log.debug("Found HTTP parameter index {}: {} = {}", new Object[] {index, header, value});
				builder = builder.addParameter(header, value);
			} else {
				log.debug("No parameter entry found for index {}", index);
				break;
			}
		}
		log.debug("Finished building request parameters");
		
		return builder;
	}
	
}

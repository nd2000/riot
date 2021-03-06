/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.runtime;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.mapping.HandlerUrlResolver;
import org.riotfamily.common.web.mapping.UrlResolverContext;
import org.riotfamily.common.web.servlet.PathCompleter;
import org.riotfamily.common.web.servlet.PathCompleterSupport;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.riot.RiotVersion;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;

/**
 * Bean that exposes the riot-servlet prefix, the resource path and the
 * riot version.
 * <p>
 * By default, Riot assumes that the riot-servlet is mapped to 
 * <code>/riot/*</code>. In order to use a different mapping, you have to set 
 * the context attribute <code>riotServletPrefix</code> in your 
 * <code>web.xml</code>.
 * </p>
 */
public class RiotRuntime implements ServletContextAware {

	public static final String SERVLET_PREFIX_ATTRIBUTE = "riotServletPrefix";
	
	public static final String DEFAULT_SERVLET_PREFIX = "/riot";
	
	private String servletPrefix;
	
	private String resourceMapping;

	private String resourcePath;
	
	private HandlerUrlResolver handlerUrlResolver;
	
	private PathCompleter pathCompleter;
	
	private UrlResolverContext context = new UrlResolverContext();

	
	public void setResourceMapping(String resourceMapping) {
		this.resourceMapping = resourceMapping;
	}
	
	public void setServletContext(ServletContext context) {
		Assert.notNull(resourceMapping, "A resourceMapping must be specified.");
		servletPrefix = (String) context.getInitParameter(SERVLET_PREFIX_ATTRIBUTE);
		if (servletPrefix == null) {
			servletPrefix = DEFAULT_SERVLET_PREFIX;
		}
		resourcePath = servletPrefix + resourceMapping + '/' + getVersionString() + '/';
		pathCompleter = new PathCompleterSupport(servletPrefix, null);
	}
	
	/**
	 * @see RiotRuntimeInitializer
	 */
	public void setHandlerUrlResolver(HandlerUrlResolver handlerUrlResolver) {
		this.handlerUrlResolver = handlerUrlResolver;
	}
	
	public String getServletPrefix() {
		return servletPrefix;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}
    
	public String getVersionString() {
		return RiotVersion.getVersionString();
	}
	
	public String getUrlForHandler(String handlerName, Object attributes) {
		return pathCompleter.addMapping(handlerUrlResolver.getUrlForHandler(context, handlerName, attributes));
	}
	
	public String getUrlForHandler(String handlerName, Object... attributes) {
		return pathCompleter.addMapping(handlerUrlResolver.getUrlForHandler(context, handlerName, attributes));
	}
	
	public String getDeeplinkForHandler(HttpServletRequest request, String handlerName, Object attribute) {
		return ServletUtils.getAbsoluteUrlPrefix(request)
				.append(request.getContextPath())
				.append(servletPrefix)
				.append("?url=")
				.append(handlerUrlResolver.getUrlForHandler(context, handlerName, attribute))
				.toString();
	}
	
	public String getDeeplinkForHandler(HttpServletRequest request, String handlerName, Object... attributes) {
		return ServletUtils.getAbsoluteUrlPrefix(request)
				.append(request.getContextPath())
				.append(servletPrefix)
				.append("?url=")
				.append(handlerUrlResolver.getUrlForHandler(context, handlerName, attributes))
				.toString();
	}

	public static RiotRuntime getRuntime(ApplicationContext context) {
		return (RiotRuntime) BeanFactoryUtils.beanOfTypeIncludingAncestors(
				context, RiotRuntime.class);
	}
}

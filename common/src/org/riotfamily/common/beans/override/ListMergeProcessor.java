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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.beans.override;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ListMergeProcessor implements BeanFactoryPostProcessor {

	private static Log log = LogFactory.getLog(ListMergeProcessor.class);
			
	private String ref;
	
	private String property;
	
	private List values;
	
	public void setRef(String ref) {
		this.ref = ref;
	}
	
	public void setProperty(String property) {
		this.property = property;
	}

	public void setValues(List values) {
		this.values = values;
	}

	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) 
			throws BeansException {

		BeanDefinition bd = beanFactory.getBeanDefinition(ref);
		if (property == null) {
			Assert.state(ListFactoryBean.class.getName().equals(bd.getBeanClassName()),
					"Bean must be a ListFactoryBean");
			
			property = "sourceList";
		}
		log.info("Adding " + values.size() + " items to " + property);
		PropertyValue pv = bd.getPropertyValues().getPropertyValue(property);
		if (pv == null) {
			bd.getPropertyValues().addPropertyValue(property, values);
		}
		else {
			Object value = pv.getValue();
			if (value instanceof RuntimeBeanReference) {
				RuntimeBeanReference ref = (RuntimeBeanReference) value;
				value = beanFactory.getBean(ref.getBeanName());
			}
			else if (value instanceof TypedStringValue) {
				TypedStringValue tsv = (TypedStringValue) value;
				bd.getPropertyValues().addPropertyValue(property, values);
				values.add(tsv.getValue());
				return;
			}
			Assert.isInstanceOf(List.class, value);
			List list = (List) value;
			list.addAll(values);
		}
	}
}
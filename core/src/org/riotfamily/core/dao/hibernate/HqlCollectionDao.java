/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.core.dao.hibernate;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.common.beans.property.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.riotfamily.core.dao.CutAndPaste;
import org.riotfamily.core.dao.Hierarchy;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.Order;
import org.riotfamily.core.dao.Sortable;
import org.springframework.util.Assert;

/**
 * RiotDao implementation that loads a bean and returns one of the bean's
 * properties as (filtered) collection.
 */
public class HqlCollectionDao extends AbstractHibernateRiotDao implements
		Sortable, Hierarchy, CutAndPaste {

	private Logger log = LoggerFactory.getLogger(HqlCollectionDao.class);

	private boolean polymorph = true;

	private String where;

	private Class<?> entityClass;

	private Class<?> parentClass;

	private String parentProperty;

	private String collectionProperty;

	public HqlCollectionDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setPolymorph(boolean polymorph) {
		this.polymorph = polymorph;
	}

	public void setWhere(String string) {
		where = string;
	}

	public void setParentClass(Class<?> parentClass) {
		this.parentClass = parentClass;
	}

	public void setCollectionProperty(String property) {
		this.collectionProperty = property;
	}

	public void setParentProperty(String parentProperty) {
		this.parentProperty = parentProperty;
	}
	
	@Override
	protected void initDao() throws Exception {
		Assert.notNull(collectionProperty, "The collectionProperty must be set");
		Assert.isTrue(parentProperty != null || parentClass != null,
				"Either parentProperty or parentClass must be set");
		
		if (entityClass == null && parentClass != null) {
			entityClass = PropertyUtils.getCollectionPropertyType(parentClass, collectionProperty);
		}
		Assert.notNull(entityClass, "The entityClass must be set");
	}

	public Object getParent(Object entity) {
		if (parentProperty != null) {
			return PropertyUtils.getProperty(entity, parentProperty);
		}
		StringBuffer hql = new StringBuffer();
		hql.append("select parent from ").append(parentClass.getName());
		hql.append(" parent join parent.").append(collectionProperty);
		hql.append(" child where child = :child");

		Query query = getSession().createQuery(hql.toString());
		query.setMaxResults(1);
		query.setParameter("child", entity);
		return query.uniqueResult();
	}

	@Override
	public void save(Object entity, Object parent) {
		if (parentProperty != null) {
			PropertyUtils.setProperty(entity, parentProperty, parent);
		}
		getCollection(parent).add(entity);
		super.save(entity, parent);
	}

	@Override
	public void delete(Object entity, Object parent) {
		getCollection(parent).remove(entity);
		super.delete(entity, parent);
	}

	@SuppressWarnings("unchecked")
	protected Collection<Object> getCollection(Object parent) {
		return (Collection<Object>) PropertyUtils.getProperty(parent,
				collectionProperty);
	}

	protected void buildQueryString(StringBuffer hql, ListParams params) {
		boolean hasWhere = false;
		if (!polymorph) {
			hql.append(" where this.class = ");
			hql.append(getEntityClass().getName());
			hasWhere = true;
		}
		if (where != null) {
			hql.append(hasWhere ? " and " : " where ");
			hasWhere = true;
			hql.append(where);
		}
		hql.append(getOrderBy(params));
	}

	@Override
	protected List<?> listInternal(Object parent, ListParams params) {
		StringBuffer hql = new StringBuffer("select this ");
		buildQueryString(hql, params);

		Collection<?> c = getCollection(parent);
		if (c != null) {
			Query query = getSession().createFilter(c, hql.toString());

			if (params.getPageSize() > 0) {
				query.setFirstResult(params.getOffset());
				query.setMaxResults(params.getPageSize());
			}
			if (params.getFilter() != null) {
				query.setProperties(params.getFilter());
			}
			if (log.isDebugEnabled()) {
				log.debug("HQL query: " + query.getQueryString());
			}
			return query.list();
		}
		return Collections.emptyList();
	}

	public int getListSize(Object parent, ListParams params) {
		Collection<?> c = getCollection(parent);
		if (c != null) {
			StringBuffer hql = new StringBuffer("select count(*) ");
			if (!polymorph) {
				hql.append(" where this.class = ");
				hql.append(getEntityClass().getName());
			}
			if (where != null) {
				hql.append(polymorph ? " where " : " and ");
				hql.append(where);
			}
			
			Query query = getSession().createFilter(c, hql.toString());
	
			if (params.getFilter() != null) {
				query.setProperties(params.getFilter());
			}
			Number size = (Number) query.uniqueResult();
			return size.intValue();
		}
		return 0;
	}

	protected String getOrderBy(ListParams params) {
		StringBuffer sb = new StringBuffer();
		if (params.hasOrder()) {
			sb.append(" order by");
			Iterator<?> it = params.getOrder().iterator();
			while (it.hasNext()) {
				Order order = (Order) it.next();
				sb.append(" this.");
				sb.append(order.getProperty());
				sb.append(' ');
				sb.append(order.isAscending() ? "asc" : "desc");
				if (it.hasNext()) {
					sb.append(',');
				}
			}
		}
		return sb.toString();
	}

	public boolean canCut(Object entity) {
		return true;
	}

	public void cut(Object entity, Object parent) {
		getCollection(parent).remove(entity);
		if (parentProperty != null) {
			PropertyUtils.setProperty(entity, parentProperty, null);
		}
	}

	public boolean canPasteCut(Object entity, Object target) {
		return true;
	}

	public void pasteCut(Object entity, Object parent) {
		getCollection(parent).add(entity);
		if (parentProperty != null) {
			PropertyUtils.setProperty(entity, parentProperty, parent);
		}
	}

}

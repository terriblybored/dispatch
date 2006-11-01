/*
 * Databinder: a simple bridge from Wicket to Hibernate
 * Copyright (C) 2006  Nathan Hamblen nathan@technically.us

 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.databinder.models;

import net.databinder.DataRequestCycle;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;

import wicket.model.LoadableDetachableModel;

/**
 * Model for a List generated by a Hibernate query. This read-only model can be used
 * to fill ListModel and PropertyListModel components with rows from a database.
 * @author Nathan Hamblen
 */
public class HibernateListModel extends LoadableDetachableModel {
	private String queryString;
	private IQueryBinder queryBinder;
	private IQueryBuilder queryBuilder;
	private Class objectClass;
	private ICriteriaBuilder criteriaBuilder;
	
	/**
	 * Contructor for a simple query.
	 * @param queryString query with no parameters
	 */
	public HibernateListModel(String queryString) {
		this.queryString = queryString;
	}
	
	/**
	 * Constructor for a parameterized query.
	 * @param queryString Query with parameters
	 * @param queryBinder object that binds the query parameters
	 */
	public HibernateListModel(String queryString, IQueryBinder queryBinder) {
		this(queryString);
		this.queryBinder = queryBinder;
	}

	/**
	 * Constructor for a list of all results in class. While this query will be too open for 
	 * most applications, it can useful in early development.
	 * @param objectClass class objects to return
	 */
	public HibernateListModel(Class objectClass) {
		this.objectClass = objectClass;
	}

	/**
	 * Constructor for a list of results in class matching a built criteria.
	 * @param objectClass class for root criteria
	 * @param criteriaBuilder builder to apply criteria restrictions
	 */
	public HibernateListModel(Class objectClass, ICriteriaBuilder criteriaBuilder) {
		this.objectClass = objectClass;
		this.criteriaBuilder = criteriaBuilder;
	}
	
	/**
	 * Constructor for a custom query that is built by the calling application.
	 * @param queryBuilder builder to create and bind query object
	 */
	public HibernateListModel(IQueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
	}

	/**
	 * Load the object List through Hibernate, binding query parameters if available.
	 */
	@Override
	protected Object load() {
		Session session = DataRequestCycle.getHibernateSession();
		if (queryString != null) {
			Query query = session.createQuery(queryString);
			if (queryBinder != null)
				queryBinder.bind(query);
			return query.list();
		}
		if (queryBuilder != null) {
			return queryBuilder.build(session).list();
		}
		Criteria criteria = session.createCriteria(objectClass);
		if (criteriaBuilder != null)
			criteriaBuilder.build(criteria);
		return criteria.list();
	}
}

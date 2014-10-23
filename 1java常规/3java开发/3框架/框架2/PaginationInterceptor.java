package com.interceptor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.common.page.Page;
import com.common.page.PageCnd;
import com.dialect.DatabaseDialects;
import com.dialect.Dialect;
import com.dialect.MySQLDialect;
import com.dialect.OracleDialect;

/**
 * mybatis 分页拦截器
 * <p>
 * Description:mybatis 分页拦截器<br />
 * </p>
 * 
 * @title PaginationInterceptor.java
 * @package com.interceptor
 * @author qiongbiao.zhang
 * @version 0.1 2014年4月18日
 */
@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class PaginationInterceptor implements Interceptor {
	
	private String dialect;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		// final Executor executor = (Executor) invocation.getTarget();
		final Object[] args = invocation.getArgs();
		final MappedStatement mappedStatement = (MappedStatement) args[0];
		final Object parameter = args[1];
		final RowBounds rowBounds = (RowBounds) args[2];
		
		if (rowBounds == null || rowBounds == RowBounds.DEFAULT || rowBounds == Page.DEFAULT) {
			return invocation.proceed();
		}
		
		DatabaseDialects databaseType = DatabaseDialects.getDatabaseDialect(dialect);
		if (databaseType == null) {
			throw new RuntimeException("the value of the dialect property is not defined : " + dialect);
		}
		Dialect dialect = null;
		switch (databaseType) {
		case MYSQL:
			dialect = new MySQLDialect();
			break;
		case ORACLE:
			dialect = new OracleDialect();
			break;
		default:
			break;
		}
		
		final Configuration configuration = mappedStatement.getConfiguration();
		final BoundSql boundSql = mappedStatement.getBoundSql(parameter);
		int offset, limit;
		if (rowBounds instanceof Page) {
			Page page = (Page) rowBounds;
			offset = page.getOffset();
			limit = page.getLimit();
		} else {
			offset = rowBounds.getOffset();
			limit = rowBounds.getLimit();
		}

		args[0] = buildNewMappedStatement(mappedStatement, buildNewBoundSql(configuration, boundSql, dialect.getLimitString(boundSql.getSql()), offset, limit));
		// args[1] = newBoundSql.getParameterObject();
		args[2] = RowBounds.DEFAULT;
		return invocation.proceed();
	}
	
	private BoundSql buildNewBoundSql(Configuration configuration, BoundSql boundSql, String newSql, int offset, int limit) {

		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		int size = parameterMappings != null ? parameterMappings.size() : 0;
		List<ParameterMapping> newParameterMappings = new ArrayList<ParameterMapping>(size + 2);
		if (size > 0) {
			newParameterMappings.addAll(parameterMappings);
		}
		newParameterMappings.add(new ParameterMapping.Builder(configuration, "_offset", Integer.class).build());
		newParameterMappings.add(new ParameterMapping.Builder(configuration, "_limit", Integer.class).build());
		
		BoundSql newBoundSql = null;
		Object parameterObject = boundSql.getParameterObject();
		if (parameterObject == null) {
			boundSql.setAdditionalParameter("_offset", offset);
			boundSql.setAdditionalParameter("_limit", limit);
			
			Map<String, Object> parameterObjectMap = new HashMap<String, Object>(2);
			parameterObjectMap.put("_offset", offset);
			parameterObjectMap.put("_limit", limit);
			newBoundSql = new BoundSql(configuration, newSql, newParameterMappings, parameterObjectMap);
			
		} else if (parameterObject instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> parameterObjectMap = (HashMap<String, Object>) parameterObject;
			parameterObjectMap.put("_offset", offset);
			parameterObjectMap.put("_limit", limit);
			newBoundSql = new BoundSql(configuration, newSql, newParameterMappings, parameterObjectMap);

		} else if (parameterObject instanceof PageCnd) {
			PageCnd pageCnd = (PageCnd) parameterObject;
			pageCnd.set_offset(offset);
			pageCnd.set_limit(limit);
			newBoundSql = new BoundSql(configuration, newSql, newParameterMappings, pageCnd);
			
		} else if (parameterObject instanceof String
				|| parameterObject instanceof Integer
				|| parameterObject instanceof Long
				|| parameterObject instanceof Double
				|| parameterObject instanceof Float
				|| parameterObject instanceof Short
				|| parameterObject instanceof Byte
				|| parameterObject instanceof Date
				|| parameterObject instanceof java.sql.Date
				|| parameterObject instanceof Timestamp
				|| parameterObject instanceof Boolean) {
			boundSql.setAdditionalParameter(newParameterMappings.get(0).getProperty(), parameterObject);
			boundSql.setAdditionalParameter("_offset", offset);
			boundSql.setAdditionalParameter("_limit", limit);
			
			Map<String, Object> parameterObjectMap = new HashMap<String, Object>(3);
			parameterObjectMap.put(newParameterMappings.get(0).getProperty(), parameterObject);
			parameterObjectMap.put("_offset", offset);
			parameterObjectMap.put("_limit", limit);
			newBoundSql = new BoundSql(configuration, newSql, newParameterMappings, parameterObjectMap);
			
		} else {
			throw new RuntimeException("unknown type exception: instance of the parameter object is not a common type of java or not extends BaseCnd(implements PageCnd) - " + parameterObject.getClass());
		}
		
		String property = null;
		for (ParameterMapping mapping : newParameterMappings) {
			property = mapping.getProperty();
			if (boundSql.hasAdditionalParameter(property)) {
				newBoundSql.setAdditionalParameter(property, boundSql.getAdditionalParameter(property));
			}
		}
		return newBoundSql;
	}
	
	private MappedStatement buildNewMappedStatement(MappedStatement mappedStatement, BoundSql newBoundSql) {
		Builder builder = new Builder(mappedStatement.getConfiguration(), mappedStatement.getId(), new MySqlSource(newBoundSql), mappedStatement.getSqlCommandType());
		
		builder.resource(mappedStatement.getResource());
		builder.fetchSize(mappedStatement.getFetchSize());
		builder.statementType(mappedStatement.getStatementType());
		builder.keyGenerator(mappedStatement.getKeyGenerator());
		
		if (mappedStatement.getKeyProperties() != null && mappedStatement.getKeyProperties().length != 0) {
			StringBuilder keyProperties = new StringBuilder(mappedStatement.getKeyProperties().length * 16);
			for (String keyProperty : mappedStatement.getKeyProperties()) {
				keyProperties.append(keyProperty).append(",");
			}
			keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
			builder.keyProperty(keyProperties.toString());
		}

		builder.timeout(mappedStatement.getTimeout());
		builder.parameterMap(mappedStatement.getParameterMap());
		builder.resultMaps(mappedStatement.getResultMaps());
		builder.resultSetType(mappedStatement.getResultSetType());
		builder.cache(mappedStatement.getCache());
		builder.flushCacheRequired(mappedStatement.isFlushCacheRequired());
		builder.useCache(mappedStatement.isUseCache());

		return builder.build();
	}
	
	public static class MySqlSource implements SqlSource {
		private BoundSql boundSql;

		public MySqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}
	
	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}
}

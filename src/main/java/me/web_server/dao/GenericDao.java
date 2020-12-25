package me.web_server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import me.web_server.ServiceRequestException;

public abstract class GenericDao extends JdbcDaoSupport {
	@Autowired
	private DataSource dataSource;
	private Connection connection;

	protected interface DaoTypeMappingCallable {
		Object call(String value);
	}

	protected final static HashMap<Pattern, DaoTypeMappingCallable> typeMapping = new HashMap<>();

	protected final static String NAME_PREFIX = "\"application\".";

	@PostConstruct
	private void initialize() throws CannotGetJdbcConnectionException, SQLException {
		setDataSource(dataSource);

		connection = getConnection();
	}

	protected Connection getDbConnection() {
		return connection;
	}

	protected abstract boolean isInternalError(SQLException exception);

	protected abstract String getSqlExceptionMessage(SQLException exception);

	public interface DaoCallable<T> {
		public T call() throws SQLException;
	}

	public <T> T handleSqlQuery(DaoCallable<T> callable) throws ServiceRequestException {
		try {
			return callable.call();
		} catch (SQLException exception) {
			if (isInternalError(exception)) {
				logger.error("Error occured while executing query! [" + exception.getMessage() + "]");

				throw new ServiceRequestException("Internal error occured!");
			} else {
				throw new ServiceRequestException(getSqlExceptionMessage(exception));
			}
		}
	}

	private static void setAuthUser(PreparedStatement statement, String username, int offset) throws SQLException {
		statement.setString(offset, username);
	}

	private static void setAuthPassword(PreparedStatement statement, byte[] passwordHash, int offset) throws SQLException {
		statement.setBytes(offset, passwordHash);
	}

	protected static void setAuthParameters(PreparedStatement statement, String username, byte[] passwordHash, int offset) throws SQLException {
		setAuthUser(statement, username, offset);
		setAuthPassword(statement, passwordHash, offset + 1);
	}

	protected ArrayList<HashMap<String, Object>> mapResultSet(ResultSet set) throws SQLException {
		ArrayList<HashMap<String, Object>> list = new ArrayList<>(set.getMetaData().getColumnCount());

		ResultSetMetaData metadata = set.getMetaData();

		int columns = metadata.getColumnCount();

		while (set.next()) {
			HashMap<String, Object> map = new HashMap<>();

			for (int z = 0; z < columns; ++z) {
				int columnType = metadata.getColumnType(z + 1);
				if (columnType == Types.ARRAY || columnType == Types.OTHER) {
					try {
						Object[] objects = Object[].class.cast(set.getArray(z + 1).getArray());
						ArrayList<Object> objectsList = new ArrayList<>(objects.length);

						object_loop: for (Object object : objects) {
							String value = object.toString();
							
							for (Pattern pattern : typeMapping.keySet()) {
								if (pattern.matcher(value).matches()) {
									objectsList.add(typeMapping.get(pattern).call(value));

									continue object_loop;
								}
							}

							objectsList.add(object);
						}

						map.put(metadata.getColumnName(z + 1), objectsList);

						continue;
					} catch (ClassCastException | SQLException exception) {}
				}

				map.put(
					metadata.getColumnName(z + 1),
					set.getObject(z + 1)
				);
			}

			list.add(map);
		}
		
		return list;
	}
}
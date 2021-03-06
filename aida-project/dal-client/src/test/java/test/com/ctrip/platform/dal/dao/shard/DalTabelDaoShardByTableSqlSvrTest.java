package test.com.ctrip.platform.dal.dao.shard;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import test.com.ctrip.platform.dal.dao.unitbase.SqlServerDatabaseInitializer;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

/**
 * Only test shard by table case.
 * TODO fix or find root cause of why keyholder not work for insert
 * @author jhhe
 *
 */
public class DalTabelDaoShardByTableSqlSvrTest extends BaseDalTabelDaoShardByTableTest {
	public DalTabelDaoShardByTableSqlSvrTest() {
		super(DATABASE_NAME_SQLSVR, SqlServerDatabaseInitializer.diff);
	}
	
	private final static String DATABASE_NAME_SQLSVR = "dao_test_sqlsvr_tableShard";
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 4;
	
	private final static String DROP_TABLE_SQL_SQLSVR_TPL = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "_%d') "
			+ "DROP TABLE  "+ TABLE_NAME + "_%d";

	//Create the the table
	private final static String DROP_TABLE_SQL_SQLSVR_TPL_1 = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "') "
			+ "DROP TABLE  "+ TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_SQLSVR_TPL = "CREATE TABLE " + TABLE_NAME +"_%d("
			+ "Id int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
			+ "quantity int,tableIndex int,type smallint, "
			+ "address varchar(64) not null,"
			+ "last_changed datetime default getdate())";
	
	private static DalClient clientSqlSvr;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalClientFactory.initClientFactory();
		clientSqlSvr = DalClientFactory.getClient(DATABASE_NAME_SQLSVR);
		DalHints hints = new DalHints();
		String[] sqls = null;
		// For SQL server
		hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		for(int i = 0; i < mod; i++) {
			sqls = new String[] { 
					String.format(DROP_TABLE_SQL_SQLSVR_TPL, i, i), 
					String.format(CREATE_TABLE_SQL_SQLSVR_TPL, i)};
			for (int j = 0; j < sqls.length; j++) {
				clientSqlSvr.update(sqls[j], parameters, hints);
			}
		}
		clientSqlSvr.update(DROP_TABLE_SQL_SQLSVR_TPL_1, parameters, hints);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		//For Sql Server
		hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		for(int i = 0; i < mod; i++) {
			sqls = new String[] { String.format(DROP_TABLE_SQL_SQLSVR_TPL, i, i)};
			for (int j = 0; j < sqls.length; j++) {
				clientSqlSvr.update(sqls[j], parameters, hints);
			}
		}
	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		//For Sql Server
		hints = new DalHints();
		for(int i = 0; i < mod; i++) {
			insertSqls = new String[i + 3];
			insertSqls[0] = "SET IDENTITY_INSERT "+ TABLE_NAME + "_" + i + " ON";
			for(int j = 0; j < i + 1; j ++) {
				int id = j + 1;
				int quantity = 10 + j;
				insertSqls[j + 1] = "INSERT INTO " + TABLE_NAME + "_" + i + "(Id, quantity,tableIndex,type,address)"
							+ " VALUES(" + id + ", " + quantity + ", " + i + ",1, 'SH INFO')";
			}
					
			insertSqls[i+2] = "SET IDENTITY_INSERT "+ TABLE_NAME + "_" + i +" OFF";
			clientSqlSvr.batchUpdate(insertSqls, hints);
		}
	}

	@After
	public void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		sql = "DELETE FROM " + TABLE_NAME;
		parameters = new StatementParameters();
		hints = new DalHints();
		try {
			for(int i = 0; i < mod; i++) {
				clientSqlSvr.update(sql + "_" + i, parameters, hints);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
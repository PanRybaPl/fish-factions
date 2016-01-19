/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds;

import com.avaje.ebean.AdminAutofetch;
import com.avaje.ebean.AdminLogging;
import com.avaje.ebean.BackgroundExecutor;
import com.avaje.ebean.BeanState;
import com.avaje.ebean.CallableSql;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Expression;
import com.avaje.ebean.ExpressionFactory;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Filter;
import com.avaje.ebean.FutureIds;
import com.avaje.ebean.FutureList;
import com.avaje.ebean.FutureRowCount;
import com.avaje.ebean.InvalidValue;
import com.avaje.ebean.JoinConfig;
import com.avaje.ebean.OrderBy;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.Query;
import com.avaje.ebean.Query.Type;
import com.avaje.ebean.Query.UseIndex;
import com.avaje.ebean.QueryIterator;
import com.avaje.ebean.QueryListener;
import com.avaje.ebean.QueryResultVisitor;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.SqlFutureList;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.TxCallable;
import com.avaje.ebean.TxIsolation;
import com.avaje.ebean.TxRunnable;
import com.avaje.ebean.TxScope;
import com.avaje.ebean.Update;
import com.avaje.ebean.ValuePair;
import com.avaje.ebean.cache.ServerCacheManager;
import com.avaje.ebean.config.lucene.LuceneIndex;
import com.avaje.ebean.text.csv.CsvReader;
import com.avaje.ebean.text.json.JsonContext;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.OptimisticLockException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.panryba.mc.guilds.entities.Guild;

/**
 *
 * @author PanRyba.pl
 */
public class GuildManagerTest {
    
    public GuildManagerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void testGettingGuildByTag() {
        Guild guild = new Guild();
        guild.setId(1l);
        guild.setTag("Test");
        
        EbeanServer testDb = new TestDatabase();
        GuildManager manager = new GuildManager(testDb);
        
        manager.addGuild(guild);
        
        manager.getGuildByTag("test").isSameGuild(guild);
        manager.getGuildByTag("Test").isSameGuild(guild);
        manager.getGuildByTag("TEST").isSameGuild(guild);
    }
    
    @Test
    public void testChangeGuildTag() {
        Guild guild = new Guild();
        guild.setTag("Test");
        
        EbeanServer testDb = new TestDatabase();
        GuildManager manager = new GuildManager(testDb);
        
        manager.setGuildTag(guild, "TEST");
        
        assertEquals("TEST", guild.getTag());
    }
    
    private class TestDatabase implements EbeanServer {

        @Override
        public AdminLogging getAdminLogging() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public AdminAutofetch getAdminAutofetch() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public LuceneIndex getLuceneIndex(Class<?> type) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ExpressionFactory getExpressionFactory() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public BeanState getBeanState(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Object getBeanId(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Map<String, ValuePair> diff(Object o, Object o1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public InvalidValue validate(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public InvalidValue[] validate(Object o, String string, Object o1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> T createEntityBean(Class<T> type) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ObjectInputStream createProxyObjectInputStream(InputStream in) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> CsvReader<T> createCsvReader(Class<T> type) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> Query<T> createNamedQuery(Class<T> type, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> Query<T> createQuery(Class<T> type, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> Query<T> createQuery(Class<T> type) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> Query<T> find(Class<T> type) {
            return new Query<T>() {

                @Override
                public Query<T> setUseIndex(UseIndex ui) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public UseIndex getUseIndex() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Type getType() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public RawSql getRawSql() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setRawSql(RawSql rawsql) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void cancel() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> copy() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public ExpressionFactory getExpressionFactory() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public boolean isAutofetchTuned() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setAutofetch(boolean bln) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setQuery(String string) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> select(String string) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> fetch(String string, String string1) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> join(String string, String string1) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> fetch(String string, String string1, FetchConfig fc) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> join(String string, String string1, JoinConfig jc) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> fetch(String string) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> join(String string) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> fetch(String string, FetchConfig fc) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> join(String string, JoinConfig jc) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public List<Object> findIds() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public QueryIterator<T> findIterate() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void findVisit(QueryResultVisitor<T> qrv) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public List<T> findList() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Set<T> findSet() {
                    return new HashSet<>();
                }

                @Override
                public Map<?, T> findMap() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public <K> Map<K, T> findMap(String string, Class<K> type) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public T findUnique() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public int findRowCount() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public FutureRowCount<T> findFutureRowCount() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public FutureIds<T> findFutureIds() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public FutureList<T> findFutureList() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public PagingList<T> findPagingList(int i) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setParameter(String string, Object o) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setParameter(int i, Object o) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setListener(QueryListener<T> ql) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setId(Object o) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> where(String string) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> where(Expression exprsn) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public ExpressionList<T> where() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public ExpressionList<T> filterMany(String string) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public ExpressionList<T> having() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> having(String string) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> having(Expression exprsn) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> orderBy(String string) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> order(String string) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public OrderBy<T> order() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public OrderBy<T> orderBy() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setOrder(OrderBy<T> ob) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setOrderBy(OrderBy<T> ob) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setDistinct(boolean bln) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setVanillaMode(boolean bln) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public int getFirstRow() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setFirstRow(int i) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public int getMaxRows() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setMaxRows(int i) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setBackgroundFetchAfter(int i) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setMapKey(String string) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setUseCache(boolean bln) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setUseQueryCache(boolean bln) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setReadOnly(boolean bln) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setLoadBeanCache(boolean bln) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setTimeout(int i) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Query<T> setBufferFetchSizeHint(int i) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public String getGeneratedSql() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }

        @Override
        public Object nextId(Class<?> type) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> Filter<T> filter(Class<T> type) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> void sort(List<T> list, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> Update<T> createNamedUpdate(Class<T> type, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> Update<T> createUpdate(Class<T> type, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public SqlQuery createSqlQuery(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public SqlQuery createNamedSqlQuery(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public SqlUpdate createSqlUpdate(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public CallableSql createCallableSql(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public SqlUpdate createNamedSqlUpdate(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Transaction createTransaction() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Transaction createTransaction(TxIsolation ti) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Transaction beginTransaction() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Transaction beginTransaction(TxIsolation ti) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Transaction currentTransaction() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void commitTransaction() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void rollbackTransaction() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void endTransaction() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void logComment(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void refresh(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void refreshMany(Object o, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> T find(Class<T> type, Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> T getReference(Class<T> type, Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> int findRowCount(Query<T> query, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> List<Object> findIds(Query<T> query, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> QueryIterator<T> findIterate(Query<T> query, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> void findVisit(Query<T> query, QueryResultVisitor<T> qrv, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> List<T> findList(Query<T> query, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> FutureRowCount<T> findFutureRowCount(Query<T> query, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> FutureIds<T> findFutureIds(Query<T> query, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> FutureList<T> findFutureList(Query<T> query, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public SqlFutureList findFutureList(SqlQuery sq, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> PagingList<T> findPagingList(Query<T> query, Transaction t, int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> Set<T> findSet(Query<T> query, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> Map<?, T> findMap(Query<T> query, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> T findUnique(Query<T> query, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<SqlRow> findList(SqlQuery sq, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<SqlRow> findSet(SqlQuery sq, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Map<?, SqlRow> findMap(SqlQuery sq, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public SqlRow findUnique(SqlQuery sq, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void save(Object o) throws OptimisticLockException {
        }

        @Override
        public int save(Iterator<?> itrtr) throws OptimisticLockException {
            return 0;
        }

        @Override
        public int save(Collection<?> clctn) throws OptimisticLockException {
            return 0;
        }

        @Override
        public void delete(Object o) throws OptimisticLockException {
            
        }

        @Override
        public int delete(Iterator<?> itrtr) throws OptimisticLockException {
            return 0;
        }

        @Override
        public int delete(Collection<?> clctn) throws OptimisticLockException {
            return 0;
        }

        @Override
        public int delete(Class<?> type, Object o) {
            return 0;
        }

        @Override
        public int delete(Class<?> type, Object o, Transaction t) {
            return 0;
        }

        @Override
        public void delete(Class<?> type, Collection<?> clctn) {
        }

        @Override
        public void delete(Class<?> type, Collection<?> clctn, Transaction t) {
        }

        @Override
        public int execute(SqlUpdate su) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int execute(Update<?> update) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int execute(Update<?> update, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int execute(CallableSql cs) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void externalModification(String string, boolean bln, boolean bln1, boolean bln2) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> T find(Class<T> type, Object o, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void save(Object o, Transaction t) throws OptimisticLockException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int save(Iterator<?> itrtr, Transaction t) throws OptimisticLockException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void update(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void update(Object o, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void update(Object o, Set<String> set) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void update(Object o, Set<String> set, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void update(Object o, Set<String> set, Transaction t, boolean bln, boolean bln1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void insert(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void insert(Object o, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int deleteManyToManyAssociations(Object o, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int deleteManyToManyAssociations(Object o, String string, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void saveManyToManyAssociations(Object o, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void saveManyToManyAssociations(Object o, String string, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void saveAssociation(Object o, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void saveAssociation(Object o, String string, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void delete(Object o, Transaction t) throws OptimisticLockException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int delete(Iterator<?> itrtr, Transaction t) throws OptimisticLockException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int execute(SqlUpdate su, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int execute(CallableSql cs, Transaction t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void execute(TxScope ts, TxRunnable tr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void execute(TxRunnable tr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> T execute(TxScope ts, TxCallable<T> tc) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> T execute(TxCallable<T> tc) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ServerCacheManager getServerCacheManager() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public BackgroundExecutor getBackgroundExecutor() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void runCacheWarming() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void runCacheWarming(Class<?> type) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public JsonContext createJsonContext() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}

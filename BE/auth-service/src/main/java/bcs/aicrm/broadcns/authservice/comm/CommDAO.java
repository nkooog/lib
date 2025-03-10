package bcs.aicrm.broadcns.authservice.comm;

import java.util.List;

interface CommDAO<T> {

	int selectByCount(String queryId, T clazz);
	<T> T selectByOne(String queryId, T clazz);
	<T> List<T> selectByList(String queryId, T clazz);
	int sqlInsert(String queryId, T clazz);
	int sqlDelete(String queryId, T clazz);
	int sqlUpdate(String queryId, T clazz);

}

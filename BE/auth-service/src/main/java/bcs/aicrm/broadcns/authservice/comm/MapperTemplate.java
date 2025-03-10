package bcs.aicrm.broadcns.authservice.comm;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

public abstract class MapperTemplate<T> implements CommDAO<T> {

	protected SqlSession session;

	public MapperTemplate(SqlSession session) {
		this.session = session;
	}

	@Override
	public int selectByCount(String queryId, T clazz) {
		return this.session.selectOne(queryId, clazz);
	}
	@Override
	public <T> T selectByOne(String queryId, T clazz) {
		return this.session.selectOne(queryId, clazz);
	};
	@Override
	public <T> List<T> selectByList(String queryId, T clazz) {
		return this.session.selectList(queryId, clazz);
	};
	@Override
	public int sqlInsert(String queryId, T clazz) {
		return this.session.insert(queryId, clazz);
	};

	@Override
	public int sqlDelete(String queryId, T clazz) {
		return this.session.delete(queryId, clazz);
	};

	@Override
	public int sqlUpdate(String queryId, T clazz) {
		return this.session.update(queryId, clazz);
	};
}

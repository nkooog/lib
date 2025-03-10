package bcs.aicrm.broadcns.authservice.lgin.service.dao;

import bcs.aicrm.broadcns.authservice.comm.MapperTemplate;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("LGIN000DAO")
public class LGIN000DAO<T> extends MapperTemplate<T> {

	public LGIN000DAO(SqlSession session) {
		super(session);
	}

	@Override
	public int selectByCount(String queryId, T clazz) {
		return super.selectByCount(queryId, clazz);
	}

	@Override
	public <T1> T1 selectByOne(String queryId, T1 clazz) {
		return session.selectOne(queryId, clazz);
	}

	@Override
	public <T1> List<T1> selectByList(String queryId, T1 clazz) {
		return session.selectList(queryId, clazz);
	}
}

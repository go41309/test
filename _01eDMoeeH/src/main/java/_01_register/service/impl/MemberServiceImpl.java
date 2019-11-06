package _01_register.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import _00_init.util.HibernateUtils;
import _01_register.dao.MemberDao;
import _01_register.dao.impl.MemberDaoImpl_Hibernate;
import _01_register.model.MemberBean;
import _01_register.service.MemberService;
import _04_ShoppingCart.model.OrderBean;

public class MemberServiceImpl implements MemberService {

	MemberDao dao;

	SessionFactory factory;

	public MemberServiceImpl() {
		this.dao = new MemberDaoImpl_Hibernate();
		factory = HibernateUtils.getSessionFactory();
	}

	@Override
	public int saveMember(MemberBean mb) {
		int count = 0;

		Session session = factory.getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			dao.saveMember(mb);
			count++;
			tx.commit();
		} catch (Exception ex) {
			if (tx != null)  tx.rollback();
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		return count;
	}

	@Override
	public boolean idExists(String id) {
		boolean exist = false;
		Session session = factory.getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			exist = dao.idExists(id);
			tx.commit();
		} catch (Exception ex) {
			if (tx != null)  tx.rollback();
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		return exist;
	}

	@Override
	public MemberBean queryMember(String id) {
		MemberBean mb = null;
		Session session = factory.getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			mb = dao.queryMember(id);
			tx.commit();
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		return mb;
	}

	@Override
	public void updateUnpaidOrderAmount(OrderBean ob) {
		Session session = factory.getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			dao.updateUnpaidOrderAmount(ob);
			tx.commit();
		} catch (Exception ex) {
			if (tx != null)  tx.rollback();
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	public MemberBean checkIDPassword(String userId, String password) {
		MemberBean mb = null;

		Session session = factory.getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			mb = dao.checkIDPassword(userId, password);
			tx.commit();
		} catch (Exception ex) {
			if (tx != null)  tx.rollback();
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		return mb;
	}
}

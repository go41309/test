package _01_register.dao.impl;

import java.sql.Connection;

import javax.persistence.NoResultException;

import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import _00_init.ude.MemberNotFoundException;
import _00_init.ude.OrderAmountLimitException;
import _00_init.util.GlobalService;
import _00_init.util.HibernateUtils;
import _01_register.dao.MemberDao;
import _01_register.model.MemberBean;
import _04_ShoppingCart.model.OrderBean;

// 本類別使用為標準的JDBC技術來存取資料庫。
public class MemberDaoImpl_Hibernate implements MemberDao {

	SessionFactory factory;
	
	public MemberDaoImpl_Hibernate() {
		this.factory = HibernateUtils.getSessionFactory();
	}
	
	// 儲存MemberBean物件，將參數mb新增到Member表格內。
	public int saveMember(MemberBean mb) {
		int n = 0;
		Session session = factory.getCurrentSession();
		session.save(mb);
		n++;
		return n;
	}
	// 判斷參數id(會員帳號)是否已經被現有客戶使用，如果是，傳回true，表示此id不能使用，
	// 否則傳回false，表示此id可使用。
	@Override
	public boolean idExists(String id) {
		Session session = factory.getCurrentSession();
		boolean exist = false;
		String hql = "FROM MemberBean m WHERE m.memberId = :mid";
		try {
			MemberBean mb = (MemberBean) session.createQuery(hql)
												.setParameter("mid", id)
												.uniqueResult();
			if (mb != null) {
				exist = true;
			}
		} catch (NoResultException ex) {
			exist = false;
		} catch (NonUniqueResultException ex) {
			exist = true;
		}
		return exist;
	}
	
	// 由參數 id (會員帳號) 到Member表格中 取得某個會員的所有資料，傳回值為一個MemberBean物件，
	// 如果找不到對應的會員資料，傳回值為null。
	@Override
	public MemberBean queryMember(String id) {
		MemberBean mb = null;
		Session session = factory.getCurrentSession();
		String hql = "FROM MemberBean m WHERE m.memberId = :mid";
		try {
			mb = (MemberBean) session.createQuery(hql)
					.setParameter("mid", id)
					.uniqueResult();
		} catch (NoResultException ex) {
			mb = null;
		}
		return mb;
	}
	
	// 檢查使用者在登入時輸入的帳號與密碼是否正確。如果正確，傳回該帳號所對應的MemberBean物件，
	// 否則傳回 null。
	@Override
	public MemberBean checkIDPassword(String userId, String password) {
		MemberBean mb = null;
		Session session = factory.getCurrentSession();
		String hql = "FROM MemberBean m WHERE m.memberId = :mid and m.password = :pswd";
		try {
			mb = (MemberBean) session.createQuery(hql)
							.setParameter("mid", userId)
							.setParameter("pswd", password)
							.uniqueResult();
		} catch (NoResultException ex) {
			mb = null;
		}
		return mb;
	}
	/*
	 * 功能：更新客戶的未付款訂購金額。
	 * 說明：處理客戶訂單時，[訂單的總金額 + 該客戶的未付款餘額]不能超過限額，
	 * 此限額定義在 GlobalService類別的常數: ORDER_AMOUNT_LIMIT
	 * 步驟：
	 * 1. 取出Member表格內的 Member#unpaid_amount欄位(未付款餘額) 
	 * 2. unpaid_amount加上本訂單的總金額後，檢查該數值是否超過限額
	 *    (GlobalService.ORDER_AMOUNT_LIMIT)。 
	 *    如果超過限額， 則
	 *    		該訂單不予處裡， 丟出UnpaidOrderAmountExceedingException，
	 * 	    否則更新Member表格的unpaid_amount欄位: Member#unpaid_amount += currentAmount;
	 */
	@Override
	public void updateUnpaidOrderAmount(OrderBean ob) {
		MemberBean mb = null;
		String hql1 = "FROM MemberBean m WHERE m.memberId = :mid";
		String hql2 = "UPDATE MemberBean m SET m.unpaid_amount = m.unpaid_amount + :totalAmount "
				+ " WHERE m.memberId = :mid";
		Session session = factory.getCurrentSession();
		try {
			mb = (MemberBean) session.createQuery(hql1)
					.setParameter("mid", ob.getMemberId())
					.uniqueResult();
		} catch (NoResultException ex) {
			mb = null;
			throw new MemberNotFoundException("會員: " + ob.getMemberId() + "找不到...");
		}

		if (mb.getUnpaid_amount() + ob.getTotalAmount() > GlobalService.ORDER_AMOUNT_LIMIT) {
			throw new OrderAmountLimitException(
					"會員: " + ob.getMemberId() + "未付款餘額超過限額: " + 
			        (mb.getUnpaid_amount() + ob.getTotalAmount()));
		}

		session.createQuery(hql2).setParameter("totalAmount", ob.getTotalAmount())
								 .setParameter("mid", ob.getMemberId())
								 .executeUpdate();
	}
	
	@Override
	public void setConnection(Connection conn) {
		throw new RuntimeException("不支援setConnection(Connection con)方法...");
	}
}

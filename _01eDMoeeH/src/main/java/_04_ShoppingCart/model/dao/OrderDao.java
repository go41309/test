package _04_ShoppingCart.model.dao;

import java.sql.Connection;
import java.util.List;

import _04_ShoppingCart.model.OrderBean;

public interface OrderDao {

	List<OrderBean> getAllOrders();
	
	List<OrderBean> getMemberOrders(String memberId);
	
	OrderBean getOrder(int orderNo);
	
	void insertOrder(OrderBean ob);
	
	void setConnection(Connection con);

}
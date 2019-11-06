package _01_register.service;

import _01_register.model.MemberBean;
import _04_ShoppingCart.model.OrderBean;

public interface MemberService {
	
	MemberBean checkIDPassword(String userId, String password);
	
	boolean idExists(String id);
	
	MemberBean queryMember(String id);
	
	int saveMember(MemberBean mb);
	
	
	
	void updateUnpaidOrderAmount(OrderBean ob);
}

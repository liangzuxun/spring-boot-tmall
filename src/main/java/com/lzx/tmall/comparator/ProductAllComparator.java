package com.lzx.tmall.comparator;

import java.util.Comparator;

import com.lzx.tmall.pojo.Product;

public class ProductAllComparator implements Comparator<Product>{
	//  o1 - o2 = -1 
	@Override
	public int compare(Product o1, Product o2) {
		// TODO Auto-generated method stub
		return o2.getReviewCount()*o2.getSaleCount() - o1.getReviewCount() * o1.getSaleCount();
		
	}
	
	
}

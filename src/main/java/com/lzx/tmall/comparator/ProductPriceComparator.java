package com.lzx.tmall.comparator;

import java.util.Comparator;

import com.lzx.tmall.pojo.Product;

public class ProductPriceComparator implements Comparator<Product>{

	@Override
	public int compare(Product o1, Product o2) {
		// TODO Auto-generated method stub
		return (int) (o1.getPromotePrice() - o2.getPromotePrice());
	}

}

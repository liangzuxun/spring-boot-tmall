package com.lzx.tmall.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.lzx.tmall.dao.CategoryDAO;
import com.lzx.tmall.pojo.Category;
import com.lzx.tmall.pojo.Product;
import com.lzx.tmall.util.Page4Navigator;

@Service
@CacheConfig(cacheNames="categories")
public class CategoryService {
	@Autowired CategoryDAO categoryDAO;
	
	@Cacheable(key="'categories-page-'+#p0+'-'+#p1")
	public Page4Navigator<Category> list(int start,int size,int navigatePages){
		Sort sort = new Sort(Sort.Direction.DESC,"id");
		Pageable pageable = new PageRequest(start,size,sort);
		Page pageFormJPA = categoryDAO.findAll(pageable);
		return new Page4Navigator<>(pageFormJPA,navigatePages);
				
		
	}
	@Cacheable(key="'categories-all'")
	public List<Category> list(){
		Sort sort = new Sort(Sort.Direction.DESC,"id");
		return categoryDAO.findAll(sort);
	}
	@CacheEvict(allEntries=true)
	public void delete(int id) {
		categoryDAO.delete(id);		
	}
	@CacheEvict(allEntries=true)
	public void add(Category category) {
		categoryDAO.save(category);
	}
	@Cacheable(key="'categories-one-'+#p0")
	public Category get(int id) {
		return categoryDAO.getOne(id);
	}
	@CacheEvict(allEntries=true)
	public void update(Category bean) {
		categoryDAO.save(bean);
	}
	//防止无限递归
	public void removeCategoryFromProduct(Category category) {
		List<Product> products = category.getProducts();
		if(products != null) {
			for(Product product:products) {
				product.setCategory(null);
			}
		}
		List<List<Product>> productsByRow = category.getProductsByRow();
		if(null != productsByRow) {
			for(List<Product> ps:productsByRow) {
				for(Product p :ps) {
					p.setCategory(null);
				}
			}
		}
	}
	public void removeCategoryFromProduct(List<Category> cs) {
		for(Category c:cs) {
			removeCategoryFromProduct(c);
		}
	}
	
}

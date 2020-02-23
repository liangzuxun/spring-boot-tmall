package com.lzx.tmall.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.lzx.tmall.dao.ProductDAO;
import com.lzx.tmall.pojo.Category;
import com.lzx.tmall.pojo.Product;
import com.lzx.tmall.util.Page4Navigator;
import com.lzx.tmall.util.SpringContextUtil;

@Service
@CacheConfig(cacheNames="products")
public class ProductService {
	@Autowired ProductDAO productDAO;
	@Autowired CategoryService categoryService;
	@Autowired ProductImageService productImageService;
	@Autowired OrderItemService orderItemService;
	@Autowired ReviewService reviewService;
	@CacheEvict(allEntries=true)
	public void add(Product bean) {
		productDAO.save(bean);
	}
	@CacheEvict(allEntries=true)
	public void delete(int id) {
		productDAO.delete(id);
	}
	@Cacheable(key="'products-one-'+#p0")
	public Product get(int id) {
		return productDAO.findOne(id);
	}
	@CacheEvict(allEntries=true)
	public void update(Product bean) {
		productDAO.save(bean);
	}
	@Cacheable(key="'products-cid-'+#p0+'-page-'+#p1+'-'+#p2")
	public Page4Navigator<Product> list(int cid,int start,int size,int navigatePages){
		Category category = categoryService.get(cid);
		//排序方式
		Sort sort = new Sort(Sort.Direction.DESC,"id");
		//Pageable 分页工具类 接收3个参数
		Pageable pageable = new PageRequest(start,size,sort);
		//返回分页类结果 
		Page<Product> pageFromJPA = productDAO.findByCategory(category, pageable);
		//二次封装 目的是为了多加一个参数
		//假设当前页码是5 总共有10页 加一个navigatePages = 5 就有[3,4,5,6,7]
		//假设当前页码是5 总共有6页 加一个navigatePages = 7 有[1,2,3,4,5,6]
		//假设当前页码是5 总共有8页 加一个navigatePages = 7 因为5+7/2 > 8 有 [2,3,4,5,6,7,8]
		//假设当前页码是2 总共有8页 加一个navigatePages = 7 因为 2-7/2 < 1 有 [1,2,3,4,5,6,7]
		
		return new Page4Navigator<Product>(pageFromJPA,navigatePages);
		
		
	}
	@Cacheable(key="'products-cid-'+#p0.id")
	public List<Product> listByCategory(Category category){
		return productDAO.findByCategoryOrderById(category);
	}
	public void fill(Category category) {
		//listByCategory不能直接调用它也是一个缓存管理方法因为aop
		ProductService productService = SpringContextUtil.getBean(ProductService.class);
		List<Product> products = productService.listByCategory(category);
		productImageService.setFirstProductImages(products);
		category.setProducts(products);
	}
	public void fill(List<Category> cs) {
		for(Category c:cs) {
			fill(c);
		}
	}
	public void fillByRow(List<Category> cs) {
		int productNumberEachRow = 8;
		for(Category c:cs) {
			List<Product> ps = c.getProducts();
			List<List<Product>> productsByRow = new ArrayList<List<Product>>();
			for(int i = 0;i<ps.size();i+=productNumberEachRow) {
				int size = i+productNumberEachRow;
				size = size>ps.size()?ps.size():size;
				List<Product> productsOfEachRow = ps.subList(i, size);
				productsByRow.add(productsOfEachRow);
			}
			c.setProductsByRow(productsByRow);
		}
	}
	public void setSaleAndReviewNumber(Product product) {
		int saleCount = orderItemService.getSaleCount(product);
		int reviewCount = reviewService.getCount(product);
		product.setSaleCount(saleCount);
		product.setReviewCount(reviewCount);
	}
	public void setSaleAndReviewNumber(List<Product> products) {
		for(Product product:products) {
			setSaleAndReviewNumber(product);
		}
	}
	public List<Product> search(String keyword,int start,int size){
		Sort sort = new Sort(Sort.Direction.DESC,"id");
		Pageable pageable = new PageRequest(start,size,sort);
		List<Product> products = productDAO.findByNameLike("%"+keyword+"%", pageable);
		return products;
	}
}

package com.lzx.tmall.util;

import java.util.List;

import org.springframework.data.domain.Page;

public class Page4Navigator<T> {
	Page<T> pageFormJPA;
	//导航页码数 自己输入的
	int navigatePages;
	//总页数
	int totalPages;
	//当前页码
	int number;
	//所有元素的数量
	long totalElements;
	//一页的size
	int size;
	//当前页码的元素
	int numberOfElements;
	//以数组形式返回内容
	List<T> content;
	
	//是否有内容
	boolean isHasContent;
	//当前页是不是第一页
	boolean first;
	//是不是最后一页
	boolean last;
	//
	boolean isHasNext;
	boolean isHasPrevious;
	//页码数组 返回给前端
	int[] navigatepageNums;
	public Page4Navigator() {
		//
	}
	public Page4Navigator(Page<T> pageFormJPA,int navigatePages) {
		this.pageFormJPA = pageFormJPA;
		this.navigatePages = navigatePages;
		
		totalPages = pageFormJPA.getTotalPages();
		
		number = pageFormJPA.getNumber();
		
		totalElements = pageFormJPA.getTotalElements();
		
		size = pageFormJPA.getSize();
		
		numberOfElements = pageFormJPA.getNumberOfElements();
		
		content = pageFormJPA.getContent();
		
		isHasContent = pageFormJPA.hasContent();
		
		first = pageFormJPA.isFirst();
		
		last = pageFormJPA.isLast();
		
		isHasNext = pageFormJPA.hasNext();
		
		isHasPrevious = pageFormJPA.hasPrevious();
		this.calcNavigatepageNums();
	}
	private void calcNavigatepageNums() {
		int navigatepageNums[];
		int totalPages = this.getTotalPages();
		
		int num = this.getNumber();
		if(totalPages <= navigatePages) {
			navigatepageNums = new int[totalPages];
			for(int i = 0;i<totalPages;i++) {
				navigatepageNums[i]=i+1;
			}
		}else {
			// 1 10
			navigatepageNums = new int[navigatePages];
			int startNum = num - navigatePages/2;
			int endNum = num+navigatePages/2;
			
			if(startNum<1) {
				startNum = 1;
				for(int i = 0;i<navigatePages;i++) {
					navigatepageNums[i] = startNum++;
				}
			}else if(endNum > totalPages) {
				endNum = totalPages;
				for(int i = navigatePages-1;i>=0;i--) {
					navigatepageNums[i] = endNum--;
				}
				
			}else {
				for(int i = 0;i<navigatePages;i++) {
					navigatepageNums[i] = startNum++;
				}
			}
		}
		this.navigatepageNums = navigatepageNums;
	}
//	public Page<T> getPageFormJPA() {
//		return pageFormJPA;
//	}
//	public void setPageFormJPA(Page<T> pageFormJPA) {
//		this.pageFormJPA = pageFormJPA;
//	}
	public int getNavigatePages() {
		return navigatePages;
	}
	public void setNavigatePages(int navigatePages) {
		this.navigatePages = navigatePages;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public long getTotalElements() {
		return totalElements;
	}
	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getNumberOfElements() {
		return numberOfElements;
	}
	public void setNumberOfElements(int numberOfElements) {
		this.numberOfElements = numberOfElements;
	}
	public List<T> getContent() {
		return content;
	}
	public void setContent(List<T> content) {
		this.content = content;
	}
	public boolean isHasContent() {
		return isHasContent;
	}
	public void setHasContent(boolean isHasContent) {
		this.isHasContent = isHasContent;
	}
	public boolean isFirst() {
		return first;
	}
	public void setFirst(boolean first) {
		this.first = first;
	}
	public boolean isLast() {
		return last;
	}
	public void setLast(boolean last) {
		this.last = last;
	}
	public boolean isHasNext() {
		return isHasNext;
	}
	public void setHasNext(boolean isHasNext) {
		this.isHasNext = isHasNext;
	}
	public boolean isHasPrevious() {
		return isHasPrevious;
	}
	public void setHasPrevious(boolean isHasPrevious) {
		this.isHasPrevious = isHasPrevious;
	}
	public int[] getNavigatepageNums() {
		return navigatepageNums;
	}
	public void setNavigatepageNums(int[] navigatepageNums) {
		this.navigatepageNums = navigatepageNums;
	}
	
}

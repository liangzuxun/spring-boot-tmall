package com.lzx.tmall.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Entity
@Table(name="user")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class User{
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="password")
	private String password;
	
	@Column(name="salt")
	private String salt;
	
	@Transient
	private String anonymousName;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public String getAnonymousName() {
		if(null != anonymousName) {
			return anonymousName;
		}
		if(null == name) {
			anonymousName = null;
		}
		else if(name.length()<1) {
			anonymousName = "*";
		}else if(name.length() == 2) {
			anonymousName = name.substring(0,1)+"*";
		}else {
			char[] cs = name.toCharArray();
			for(int i=1;i<cs.length-1;i++) {
				cs[i] ='*';
			}
			anonymousName = new String(cs);
		}
		return anonymousName;
	}
}

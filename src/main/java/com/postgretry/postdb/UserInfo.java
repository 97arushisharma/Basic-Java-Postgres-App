package com.postgretry.postdb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="userinfo")

public class UserInfo implements Serializable {

	private static final long serialVersionUID =22L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="UID")
	private long uid;
	
	@Column(name="Name")
	private String name;
	
	@Column(name="PhoneNo")
	private String phone;
	
	@Column(name="Email")
	private String email;
	
	protected UserInfo() {
		
	}
	public UserInfo(String name, String phone, String email) {
		this.name=name;
		this.phone=phone;
		this.email=email;
	}
	
	
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "UserInfo [uid=" + uid + ", name=" + name + ", phone=" + phone + ", email=" + email + "]";
	}
	
	
}

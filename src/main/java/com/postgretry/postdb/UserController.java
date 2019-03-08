package com.postgretry.postdb;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.hibernate.procedure.ProcedureOutputs;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class UserController {
	
	@Autowired
	UserRepository repos;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@PersistenceContext
	EntityManager entityManager ;
	
	
	
	
	@RequestMapping(value="/enterinfo", method= RequestMethod.GET)
	public ModelAndView store() {
		ModelAndView model = new ModelAndView("store");
		return model;
        //return "store";
	}
	
	@RequestMapping(value="/enterinfo", method= RequestMethod.POST)
	public String storeinfo(@RequestParam("name") String name, @RequestParam("phone") String phone, @RequestParam("email") String email){
		UserInfo user = new UserInfo();
		String q="create table if not exits userinfo(uid int,name varchar(30), email varchar(30), phone_no varchar(15));";
		jdbcTemplate.update(q);
		
		user.setName(name);
		user.setPhone(phone);
		user.setEmail(email);
		repos.save(user);
		return String.format("done: your ID is : %l",user.getUid());
	}
	@RequestMapping("/save")
	public String process() {
		System.out.println("here see this");
		repos.save(new UserInfo("Arushi","9464164800","angel9999@gmail.com"));
		repos.save(new UserInfo("Gyan Ganga","9466564800","knowledge9999@gmail.com"));
		repos.save(new UserInfo("Akshita","9423164800","ghissu9999@gmail.com"));
		
		return "Done";
	}
	@RequestMapping("/finduser")
	public String demo(@RequestParam("uid") long uid,@RequestParam("name") String name, @RequestParam("phone") String phone, @RequestParam("email") String email) {
		System.out.println("here see this");
		
		List<UserInfo> userin = repos.findByUid(uid);
		for(UserInfo userfound : userin) {
			if(userfound.getEmail().contentEquals(email) && userfound.getPhone().contentEquals(phone)) {
				return userfound.toString()+"Already Present";
			}
			else if(userfound.getEmail().contentEquals(email)==false && userfound.getPhone().contentEquals(phone)) {
				
				int c=verify(email);
				if(c==0) {
					return "Not Valid Email";
				}
				else {
					
					String q="update userinfo set email= ? where uid= ?;";
					jdbcTemplate.update( q, new Object[] { email, uid});
					return "Email Updated";
				}
				
			}
			else if(userfound.getEmail().contentEquals(email) && userfound.getPhone().contentEquals(phone)==false) {
				
				String sql="update userinfo set phone_no= ? where uid= ?;";
				jdbcTemplate.update( sql, new Object[] { phone, uid});
				return "Phone No. Updated";
			}
			
			else {
				
				
				
				int c=verify(email);
				if(c==0) {
					return "Not Valid Email";
				}
				else {
					
					String q="update userinfo set email= ? where uid= ?;";
					jdbcTemplate.update( q, new Object[] { email, uid});
					String sql="update userinfo set phone_no= ? where uid= ?;";
					jdbcTemplate.update( sql, new Object[] { phone, uid});
					return "Email and Phone No. Updated";
				}
				
				
			}
		}
		
		return "DEMO";
	}
	@RequestMapping("/findall")
	public String findAll() {
		String result="<html>";
		
		for(UserInfo user : repos.findAll()) {
			result+= "<div>" + user.toString() + "</div>";
		}
		return result+"</html>";
	}
	
	@RequestMapping("/findbyid/{uid}")
	public String findById(@PathVariable("uid") long uid) {
		String result = "";
		result = repos.findOne(uid).toString();
		return result;
	}
	
	public int verify(String email) {
		
		int output;
		System.out.println("Hello World");
		StoredProcedureQuery query = entityManager
				.createStoredProcedureQuery("verifyemail")
				.registerStoredProcedureParameter(
				    "email",
				    String.class,
				    ParameterMode.IN
				)
				.registerStoredProcedureParameter(
				    "output",
				    Integer.class,
				    ParameterMode.OUT
				)
				.setParameter("email", email);
				 
				try {
				    query.execute();
				     
				    output = (int) query
				      .getOutputParameterValue("output");
				 
				    
				} finally {
				    query.unwrap(ProcedureOutputs.class)
				    .release();
				}
				return output;
		
	}
	
	/*Stored procedure for verifying email
	 * CREATE FUNCTION verifyemail(email varchar(30))
		RETURNS INT
		LANGUAGE plpgsql
		AS $$
		BEGIN
			IF email ~ '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$' then
			return 1;
			ELSE return 0;
			END IF;
		END;
		$$;

	 */

}

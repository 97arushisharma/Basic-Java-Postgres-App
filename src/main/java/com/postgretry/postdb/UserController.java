package com.postgretry.postdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;


import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.procedure.ProcedureOutputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class UserController {
	
	@Autowired
	UserRepository repos;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Value("${gmail.username}")
	private String username;
	
	@Value("${gmail.password}")
	private String password;
    
	@PersistenceContext
	EntityManager entityManager ;
	
	private final UserRepository repository;

	    public UserController(UserRepository repository) {
	        this.repository = repository;
	    }
	
	
	@RequestMapping(value="/uploadcsv", method= RequestMethod.GET)
	public ModelAndView uploadcsv() {
		ModelAndView model = new ModelAndView("csvfileupload");
		return model;
	}
	@RequestMapping(value = "/uploadcsv",consumes = "multipart/form-data", method = RequestMethod.POST)
	public String submit(@RequestParam("file") MultipartFile file, ModelMap modelMap) throws FileNotFoundException , IOException{
		
//		modelMap.addAttribute("file", file);
		
        
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile(); 
        FileOutputStream fos = new FileOutputStream(convFile); 
        fos.write(file.getBytes());
        fos.close(); 
        
	    BufferedReader br = Files.newBufferedReader(convFile.toPath(),
                StandardCharsets.US_ASCII);
	    
	    String line = br.readLine(); 
	    while (line != null) { 
	    	UserInfo user = new UserInfo();
	    	String[] attributes = line.split(","); 
	    	user.setName(attributes[0]);
			user.setPhone(attributes[1]);
			user.setEmail(attributes[2]);
			repos.save(user);
	    	line = br.readLine();
	    }


	    
//        //Set the delimiter used in file
//        scanner.useDelimiter(",");
//        String message=" ";
//        while(scanner.hasNext()) {
//        	if(scanner.next()=="\n")
//        	message = message + scanner.next();
//        }
////
//        scanner.close();
//	    ModelAndView model = new ModelAndView("fileuploadview");
        return "Data Stored";
	}
	
	@RequestMapping(value="/enterinfo", method= RequestMethod.GET)
	public ModelAndView store() {
		ModelAndView model = new ModelAndView("store");
		return model;
        //return "store";
	}
	
	@RequestMapping(value="/enterinfo", method= RequestMethod.POST)
	public String storeinfo(@RequestParam("name") String name, @RequestParam("phone") String phone, @RequestParam("email") String email){
		UserInfo user = new UserInfo();
		
		
		user.setName(name);
		user.setPhone(phone);
		user.setEmail(email);
		repos.save(user);
		return String.format("done: your ID is : %d",user.getUid());
	}
	
	@RequestMapping(value="/sendemail", method= RequestMethod.GET)
	public ModelAndView email() {
		ModelAndView model = new ModelAndView("emailpage");
		return model;
        //return "store";
	}
	
	@RequestMapping(value="/sendemail", method=RequestMethod.POST)
	public String email(@RequestParam("name") String name , @RequestParam("address") String address) throws MessagingException, IOException {
		List<UserInfo> userin = repos.findByName(name);
		
		sendmail(userin, address);
		
		return "Email Sent Successfully";
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
					return "Not a Valid Email";
				}
				else {
					
					String q="update userinfo set email= ? where uid= ?;";
					jdbcTemplate.update( q, new Object[] { email, uid});
					return "Email Updated";
				}
				
			}
			else if(userfound.getEmail().contentEquals(email) && userfound.getPhone().contentEquals(phone)==false) {
				int p = verifyp(phone);
				if(p==0) {
					return "Not a valid Phone Number";
				}
				else {
					String sql="update userinfo set phone_no= ? where uid= ?;";
					jdbcTemplate.update( sql, new Object[] { phone, uid});
					return "Phone No. Updated";
				}
				
			}
			
			else {
				
				
				
				int c=verify(email);
				int p = verifyp(phone);
				if(c==0 || p==0) {
					return "Not a Valid Email or Phone Number";
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
	 * CREATE FUNCTION verifyemail(email varchar(30), output int)
	 * return int
		LANGUAGE plpgsql
		AS $$
		BEGIN
			IF email ~ '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$' then
			output=1;
			ELSE output= 0;
			END IF;
		END;
		$$;

	 */
public int verifyp(String phone) {
		
		int output;
		System.out.println("Hello World");
		StoredProcedureQuery query = entityManager
				.createStoredProcedureQuery("phoneno")
				.registerStoredProcedureParameter(
				    "phone",
				    String.class,
				    ParameterMode.IN
				)
				.registerStoredProcedureParameter(
				    "outputp",
				    Integer.class,
				    ParameterMode.OUT
				)
				.setParameter("phone", phone);
				 
				try {
				    query.execute();
				     
				    output = (int) query
				      .getOutputParameterValue("outputp");
				 
				    
				} finally {
				    query.unwrap(ProcedureOutputs.class)
				    .release();
				}
				return output;
		
	}

/*
 *  CREATE OR REPLACE FUNCTION phoneno(in phone varchar(15),out outputp int)
RETURNS int
LANGUAGE plpgsql
AS $$
BEGIN
IF phone ~ '^((?!(0))[0-9][0-9]{9})$' then
outputp=1;
ELSE outputp=0;
END IF;
END;
$$;

*/

private void sendmail(List<UserInfo> userin, String address) throws MessagingException, IOException {
	
	Properties props = new Properties();
	props.put("mail.smtp.auth","true");
	props.put("mail.smtp.starttls.enable","true");
	props.put("mail.smtp.host","smtp.gmail.com");
	props.put("mail.smtp.port", "587");
	
	Session session = Session.getInstance(props, new javax.mail.Authenticator() {
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}
	});
	
	Message msg = new MimeMessage(session);
	msg.setFrom(new InternetAddress(username, false));
	
	msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address));
	msg.setSubject("Trial");
	msg.setContent(userin,"text/html");
	msg.setSentDate(new Date());
	
	MimeBodyPart messageBodyPart = new MimeBodyPart();
	messageBodyPart.setContent(userin.toString(),"text/html");
	Multipart multipart = new MimeMultipart();
	multipart.addBodyPart(messageBodyPart);
	MimeBodyPart attachPart = new MimeBodyPart();
	
	attachPart.attachFile("/home/ar.sharma1/Desktop/South-Korea-1.jpg");
	
	multipart.addBodyPart(attachPart);
	msg.setContent(multipart);
	
	Transport.send(msg);
	
}

}
